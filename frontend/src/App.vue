<script setup>
import { computed, nextTick, onMounted, onBeforeUnmount, ref, watch } from 'vue'
import GuestMap from './components/GuestMap.vue'
import { mapPreviewEnabled } from './map/local-basemap.js'
import { request, newCredential, ApiError } from './guest/api.js'
import { acquireOwner } from './guest/owner.js'
import { Playback } from './guest/playback.js'
import { languages, messages } from './guest/messages.js'

const STORAGE = 'hueqr-guest-tab-v1'
const screen = ref('CHECKING')
const name = ref('')
const language = ref('vi')
const session = ref(null)
const content = ref(null)
const places = ref([])
const busy = ref(false)
const loading = ref(false)
const notice = ref('')
const retryAction = ref(null)
const revision = ref(0)
const audioElement = ref(null)
const position = ref(0)
const duration = ref(0)
const overlap = ref(false)
const restartOffered = ref(false)
const t = computed(() => messages[language.value] || messages.vi)
let saved = null
let owner = null
let ownerController = null
let player
let timer
let generation = 0
let documentNonce = null
let selectionSeq = 0
let downloadController = null
let pending = null
let serverAnchor = 0
let localAnchor = 0
let mounted = true
const acked = new Set()

const serverNow = () => serverAnchor + Math.max(0, performance.now() - localAnchor)
const active = () => session.value?.state === 'ACTIVE' && serverNow() < Date.parse(session.value.expiresAt)
const expired = computed(() => { revision.value; return !!session.value && (session.value.state === 'EXPIRED' || player?.expired) })
const warning = computed(() => { revision.value; const remaining = Date.parse(session.value?.expiresAt) - serverNow(); return active() && remaining <= 300000 })
const phase = computed(() => { revision.value; return player?.phase || 'EMPTY' })
const canPlay = computed(() => { revision.value; return !!player?.url && (active() || player?.continueCurrent) && ['PAUSED', 'PLAYING', 'ENDED'].includes(phase.value) })
const playLabel = computed(() => phase.value === 'PLAYING' ? t.value.pause : phase.value === 'ENDED' ? t.value.replay : t.value.play)

function syncClock(value) {
  serverAnchor = Math.max(serverAnchor ? serverNow() : 0, Date.parse(value))
  localAnchor = performance.now()
}

function save() {
  sessionStorage.setItem(STORAGE, JSON.stringify(saved))
}

function stopAudio() {
  downloadController?.abort()
  downloadController = null
  player?.reset()
  position.value = 0
  duration.value = 0
}

function checkDeadline() {
  player?.tick()
  if (session.value?.state === 'ACTIVE' && !active()) {
    session.value = { ...session.value, state: 'EXPIRED' }
    downloadController?.abort()
    pending = null
    retryAction.value = null
    notice.value = ''
  }
  revision.value++
}

function errorMessage(error) {
  if (!(error instanceof ApiError)) return t.value.uncertain
  if (error.status === 422) return t.value.invalid
  if (error.code === 'PLACE_UNAVAILABLE' || error.code === 'CONTENT_UNAVAILABLE') return t.value.unavailable
  return t.value.error
}

async function handleError(error, retry) {
  if (error.name === 'AbortError') return
  if (['SESSION_EXPIRED', 'SESSION_ENDED', 'GUEST_CREDENTIAL_INVALID', 'SERVICE_EPOCH_MISMATCH'].includes(error.code)) {
    try {
      const current = await request(`/sessions/${session.value.sessionId}`, { credential: saved.credential })
      syncClock(current.serverNow)
      session.value = current
      if (current.state === 'ENDED') finishEnded()
      else checkDeadline()
    } catch {
      stopAudio()
      notice.value = t.value.lostSession
      screen.value = 'LOST'
    }
    return
  }
  notice.value = errorMessage(error)
  retryAction.value = retry
}

async function loadMap() {
  const result = await request(`/sessions/${session.value.sessionId}/map`, { credential: saved.credential })
  syncClock(result.serverNow)
  places.value = result.places
}

async function establishSession(result) {
  syncClock(result.serverNow)
  session.value = result
  name.value = result.displayName
  language.value = result.currentLanguage || result.initialLanguage
  player.session(result.expiresAt)
  saved.sessionId = result.sessionId
  saved.pendingStart = null
  save()
  if (result.state === 'ENDED') { finishEnded(); return }
  screen.value = 'VISIT'
  if (result.state !== 'ACTIVE') { checkDeadline(); return }
  const previous = saved.documentNonce || null
  documentNonce = crypto.randomUUID()
  // Giữ cùng nonce khi phản hồi resume chưa rõ, không quay nonce mỗi lần thử lại
  saved.resumeRequest = { documentNonce, previousDocumentNonce: previous }
  save()
  await restore()
}

async function restore() {
  const result = await request(`/sessions/${session.value.sessionId}/resume`, {
    method: 'POST', credential: saved.credential, body: saved.resumeRequest,
  })
  syncClock(result.session.serverNow)
  session.value = result.session
  documentNonce = result.documentNonce
  selectionSeq = result.selectionSeq
  saved.documentNonce = documentNonce
  save()
  if (result.session.state !== 'ACTIVE') { checkDeadline(); return }
  await loadMap()
  saved.resumeRequest = null
  if (!result.currentOpening && languages.some(item => item.code === saved.language)) language.value = saved.language
  save()
  if (result.content) {
    const memento = saved.memento
    if (memento && (memento.revisionId !== result.content.audio?.revisionId || memento.checksum !== result.content.audio?.checksum)) notice.value = t.value.changedTrack
    await showContent(result.content, ++generation, false, memento)
  }
}

async function start() {
  if (busy.value || !owner?.acquired) return
  busy.value = true
  notice.value = ''
  retryAction.value = null
  try {
    if (!saved.credential) { saved.credential = newCredential(); save() }
    if (!saved.pendingStart) {
      const ticket = saved.ticket || await request('/start-ticket', { method: 'POST', credential: saved.credential })
      saved.ticket = ticket
      saved.pendingStart = { startTicket: ticket.startTicket, displayName: name.value, initialLanguage: language.value }
      save()
    }
    const result = await request('/sessions', {
      method: 'POST', credential: saved.credential, body: saved.pendingStart, key: saved.ticket.requestId,
    })
    await establishSession(result)
  } catch (error) {
    if (error.status === 422) { saved.pendingStart = null; save() }
    if (error.code === 'START_TICKET_EXPIRED') restartOffered.value = true
    await handleError(error, screen.value === 'VISIT' ? retryRestore : start)
  } finally { busy.value = false }
}

async function retryRestore() {
  if (!active() || !saved.resumeRequest || busy.value) return
  busy.value = true
  try { await restore(); notice.value = ''; retryAction.value = null }
  catch (error) { await handleError(error, retryRestore) }
  finally { busy.value = false }
}

async function showContent(result, expectedGeneration, autoplay, memento = null) {
  if (expectedGeneration !== generation || !active()) return
  content.value = result
  language.value = result.language
  saved.language = result.language
  save()
  syncClock(result.serverNow)
  loading.value = false
  await nextTick()
  await new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)))
  // Chỉ ACK sau khi Vue đã đưa đúng văn bản vào document đang hiển thị, không xếp hàng gửi bù offline
  if (expectedGeneration === generation && active() && document.visibilityState === 'visible' && result.text && result.grant?.viewEligible && !acked.has(result.openingId)) {
    acked.add(result.openingId)
    request(`/sessions/${session.value.sessionId}/openings/${result.openingId}/view-ack`, {
      method: 'POST', credential: saved.credential, body: { contentGrantId: result.grant.id },
    }).catch(() => {})
  }
  if (result.audio?.availability === 'READY' && result.grant && expectedGeneration === generation && active()) {
    await loadAudio(result, autoplay, memento)
  }
}

async function loadAudio(result, autoplay, memento = null) {
  if (!active() || result.grant.id !== content.value?.grant?.id) return
  downloadController?.abort()
  downloadController = new AbortController()
  const controller = downloadController
  await player.load(result.audio, () => request(`/sessions/${session.value.sessionId}/content-grants/${result.grant.id}/audio`, {
    credential: saved.credential, signal: controller.signal, binary: true,
  }), { autoplay, memento })
  if (controller === downloadController && phase.value === 'ERROR' && active()) {
    notice.value = t.value.audioError
    retryAction.value = retryAudio
  }
}

async function retryAudio() {
  if (!active() || !content.value?.grant) return
  notice.value = ''
  retryAction.value = null
  await loadAudio(content.value, false)
}

async function select(place) {
  if (!active() || busy.value) return
  if (!loading.value && content.value?.place.id === place.id && content.value.language === language.value) {
    if (phase.value !== 'PLAYING') await player.toggle()
    return
  }
  generation++
  stopAudio()
  content.value = null
  loading.value = true
  notice.value = ''
  retryAction.value = null
  pending = { method: 'POST', suffix: '/openings', key: crypto.randomUUID(), body: {
    placeId: place.id, language: language.value, documentNonce, selectionSeq: ++selectionSeq,
  }, generation, autoplay: true }
  await sendSelection()
}

async function changeLanguage(event) {
  const next = event.target.value
  if (!languages.some(item => item.code === next)) return
  if (saved) { saved.language = next; save() }
  if (screen.value !== 'VISIT') { language.value = next; return }
  if (!active() || busy.value || loading.value) return
  language.value = next
  if (!content.value) return
  const previous = content.value
  generation++
  stopAudio()
  content.value = null
  loading.value = true
  notice.value = ''
  retryAction.value = null
  pending = { method: 'PUT', suffix: `/openings/${previous.openingId}/language`, key: crypto.randomUUID(),
    etag: `"opening:${previous.openingId}:${previous.openingVersion}"`, body: {
      language: next, documentNonce, selectionSeq: ++selectionSeq,
    }, generation, autoplay: false }
  await sendSelection()
}

async function sendSelection() {
  const action = pending
  if (!action || !active()) return
  retryAction.value = null
  try {
    const result = await request(`/sessions/${session.value.sessionId}${action.suffix}`, {
      method: action.method, credential: saved.credential, body: action.body, key: action.key, etag: action.etag,
    })
    if (action.generation !== generation) return
    pending = null
    await showContent(result, action.generation, action.autoplay)
  } catch (error) {
    if (action.generation !== generation) return
    loading.value = false
    await handleError(error, sendSelection)
  }
}

function finishEnded() {
  generation++
  stopAudio()
  saved = null
  sessionStorage.removeItem(STORAGE)
  content.value = null
  pending = null
  retryAction.value = null
  notice.value = ''
  screen.value = 'ENDED'
}

async function end() {
  if (busy.value || !active()) return
  busy.value = true
  saved.endKey ||= crypto.randomUUID()
  save()
  try {
    const result = await request(`/sessions/${session.value.sessionId}/end`, {
      method: 'POST', credential: saved.credential, key: saved.endKey,
    })
    if (result.state === 'ENDED') finishEnded()
  } catch (error) { await handleError(error, end) }
  finally { busy.value = false }
}

async function newSession() {
  const prefill = screen.value === 'VISIT' && expired.value ? { name: name.value, language: language.value } : null
  generation++
  stopAudio()
  owner?.release()
  ownerController?.abort()
  sessionStorage.removeItem(STORAGE)
  saved = { ownerId: crypto.randomUUID() }
  save()
  ownerController = new AbortController()
  owner = await acquireOwner(saved.ownerId, () => {}, ownerController.signal)
  session.value = null
  content.value = null
  places.value = []
  pending = null
  acked.clear()
  name.value = prefill?.name || ''
  language.value = prefill?.language || 'vi'
  notice.value = ''
  retryAction.value = null
  restartOffered.value = false
  screen.value = 'START'
}

async function boot() {
  generation++
  pending = null
  content.value = null
  places.value = []
  screen.value = 'CHECKING'
  stopAudio()
  try {
    saved = JSON.parse(sessionStorage.getItem(STORAGE) || 'null') || { ownerId: crypto.randomUUID() }
    ownerController?.abort()
    ownerController = new AbortController()
    owner = await acquireOwner(saved.ownerId, () => { notice.value = t.value.ownerWaiting }, ownerController.signal)
    if (!mounted) { owner.release(); return }
    if (!owner.acquired) {
      sessionStorage.removeItem(STORAGE)
      saved = { ownerId: crypto.randomUUID() }
      owner = await acquireOwner(saved.ownerId, () => {}, ownerController.signal)
    }
    save()
    notice.value = ''
    if (!saved.sessionId) {
      name.value = saved.pendingStart?.displayName || ''
      language.value = saved.pendingStart?.initialLanguage || 'vi'
      screen.value = 'START'
      return
    }
    const result = await request(`/sessions/${saved.sessionId}`, { credential: saved.credential })
    if (saved.resumeRequest && result.state === 'ACTIVE') {
      session.value = result
      name.value = result.displayName
      language.value = result.currentLanguage || result.initialLanguage
      syncClock(result.serverNow)
      player.session(result.expiresAt)
      screen.value = 'VISIT'
      await restore()
    } else await establishSession(result)
  } catch (error) {
    if (error.name === 'AbortError') return
    notice.value = error.message === 'OWNER_UNSUPPORTED' ? t.value.unsupported : t.value.lostSession
    screen.value = error.message === 'OWNER_UNSUPPORTED' ? 'UNSUPPORTED' : 'LOST'
  }
}

function pageHide() {
  const memento = player?.memento()
  if (saved && memento) { saved.memento = memento; save() }
  generation++
  stopAudio()
  owner?.release()
  ownerController?.abort()
  owner = null
  screen.value = 'CHECKING'
}

function pageShow(event) {
  if (event.persisted) boot()
}

onMounted(() => {
  player = new Playback({ audio: audioElement.value, now: serverNow,
    createUrl: blob => URL.createObjectURL(blob), revokeUrl: url => URL.revokeObjectURL(url),
    makeBlob: bytes => new Blob([bytes], { type: 'audio/mpeg' }),
    checksum: async bytes => Array.from(new Uint8Array(await crypto.subtle.digest('SHA-256', bytes)), x => x.toString(16).padStart(2, '0')).join(''),
    changed: () => { revision.value++ },
  })
  audioElement.value.ontimeupdate = () => {
    checkDeadline()
    position.value = audioElement.value.currentTime || 0
    duration.value = Number.isFinite(audioElement.value.duration) ? audioElement.value.duration : 0
    const memento = player.memento()
    if (saved && active() && memento) { saved.memento = memento; save() }
  }
  timer = setInterval(checkDeadline, 250)
  window.addEventListener('pagehide', pageHide)
  window.addEventListener('pageshow', pageShow)
  document.addEventListener('visibilitychange', checkDeadline)
  boot()
})
watch(language, value => { document.documentElement.lang = value })
onBeforeUnmount(() => {
  mounted = false
  clearInterval(timer)
  pageHide()
  window.removeEventListener('pagehide', pageHide)
  window.removeEventListener('pageshow', pageShow)
  document.removeEventListener('visibilitychange', checkDeadline)
})
</script>

<template>
  <audio ref="audioElement" preload="none" />
  <div class="page" :class="{ 'visit-page': screen === 'VISIT' }">
    <header class="masthead" :inert="overlap">
      <a class="brand" href="/" @click.prevent><span class="brand-mark">H</span><span>AudioQR<small>KINH THÀNH HUẾ</small></span></a>
      <p v-if="mapPreviewEnabled" class="map-preview-note">Bản xem trước · Vùng tham quan chờ duyệt</p>
      <div v-if="screen === 'VISIT'" class="visit-actions">
        <span class="visitor-name">{{ session.displayName }}</span>
        <button class="end-button" :disabled="busy || expired" @click="end">{{ busy ? t.ending : t.end }}</button>
      </div>
      <label v-if="screen !== 'START'" class="language-field">{{ t.language }}
        <select :value="language" :disabled="busy || loading || expired || screen === 'CHECKING' || (screen === 'START' && !!saved?.pendingStart)" @change="changeLanguage">
          <option v-for="item in languages" :key="item.code" :value="item.code">{{ item.name }}</option>
        </select>
      </label>
    </header>

    <main v-if="screen === 'START'" class="start-card">
      <p class="eyebrow">AudioQR · Huế</p>
      <h1>{{ t.title }}</h1><p class="intro">{{ t.subtitle }}</p>
      <form @submit.prevent="start">
        <label>{{ t.name }}<input v-model="name" name="displayName" autocomplete="nickname" required :disabled="busy || !!saved?.pendingStart"></label>
        <label>{{ t.language }}
          <select :value="language" :disabled="busy || !!saved?.pendingStart" @change="changeLanguage">
            <option v-for="item in languages" :key="item.code" :value="item.code">{{ item.name }}</option>
          </select>
        </label>
        <button class="primary" :disabled="busy">{{ busy ? t.starting : t.start }} <span aria-hidden="true">→</span></button>
      </form>
    </main>

    <main v-else-if="screen === 'VISIT'" class="visit-layout">
      <section class="map-section" :aria-label="t.map">
        <GuestMap :places="places" :selected="content?.place.id" :disabled="expired || busy" :labels="t" :cancel-label="t.cancel" :overlap-label="t.overlap" @select="select" @overlap="overlap = $event">
          <div v-if="expired" class="map-shade expiry-shade"><section class="expiry-box" role="alert"><h2>{{ t.expired }}</h2><button class="primary" @click="newSession">{{ t.newSession }}</button></section></div>
          <p v-else-if="!places.length" class="map-empty">{{ t.noMap }}</p>
        </GuestMap>
        <p v-if="warning" class="warning" role="status">{{ t.warning }}</p>
      </section>

      <section class="narration-section" :inert="overlap">
        <h1>{{ content?.place.name || t.choose }}</h1>
        <div v-if="content" class="player-controls">
          <button class="play-button" :aria-label="playLabel" :title="playLabel" :disabled="!canPlay" @click="player.toggle()">{{ phase === 'PLAYING' ? 'Ⅱ' : phase === 'ENDED' ? '↻' : '▶' }}</button>
          <span class="time">{{ Math.floor(position / 60) }}:{{ String(Math.floor(position % 60)).padStart(2, '0') }}</span>
          <input class="seek" type="range" min="0" :max="duration || 1" step="0.1" :value="position" :aria-label="t.seek" :disabled="expired || !canPlay" @input="player.seek(Number($event.target.value))">
          <span class="time">{{ Math.floor(duration / 60) }}:{{ String(Math.floor(duration % 60)).padStart(2, '0') }}</span>
        </div>
        <p v-if="loading || phase === 'LOADING'" class="inline-state" role="status">{{ t.loading }}</p>
        <p v-else-if="content?.text && content.audio?.availability === 'NOT_READY'" class="inline-state">{{ t.noAudio }}</p>
        <article class="narration" tabindex="0" :aria-label="t.text">
          <p v-if="content?.text" class="text-body">{{ content.text.body }}</p>
          <div v-else-if="content"><p>{{ t.noText }}</p><button v-for="code in content.availableLanguages" :key="code" class="quiet" :disabled="expired || loading" @click="changeLanguage({ target: { value: code } })">{{ languages.find(item => item.code === code)?.name }}</button></div>
          <p v-else>{{ loading ? t.loading : t.subtitle }}</p>
        </article>
      </section>
    </main>

    <main v-else-if="screen === 'CHECKING'" class="start-card" role="status"><p>{{ notice || t.checking }}</p></main>
    <main v-else-if="screen === 'UNSUPPORTED'" class="start-card" role="alert"><p>{{ notice }}</p></main>
    <main v-else-if="screen === 'LOST'" class="start-card"><p role="alert">{{ notice }}</p><button class="primary" @click="newSession">{{ t.newSession }}</button></main>
    <div v-else-if="screen === 'ENDED'" class="end-shade"><section class="end-box" role="dialog" aria-modal="true"><h1>{{ t.ended }}</h1><button class="primary" @click="newSession">{{ t.newSession }}</button></section></div>

    <aside v-if="notice && ['START', 'VISIT'].includes(screen)" class="notice" :inert="overlap" role="alert"><span>{{ notice }}</span><button v-if="retryAction" class="quiet" :disabled="busy" @click="retryAction()">{{ t.retry }}</button><button v-if="restartOffered" class="quiet" :disabled="busy" @click="newSession">{{ t.newSession }}</button></aside>
    <footer>AudioQR · Kinh thành Huế</footer>
  </div>
</template>
