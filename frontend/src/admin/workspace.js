import { AdminApiError } from './api.js'

const STORAGE_KEY = 'hueqr.admin.place-work'

export function createWorkspace({ api, storage, uuid = () => crypto.randomUUID(), clock = () => performance.now(), decorate = value => value }) {
  const state = decorate({
    session: null, locked: true, places: [], form: null, pending: null,
    editor: null, lastOperation: null, busy: false, message: '', error: '', fieldErrors: {}, storageError: false,
  })
  let generation = 0
  let deadline = 0
  let lastActivity = -Infinity
  let bootstrapQueued = false

  function persist() {
    if (!state.session) return
    try {
      storage.setItem(STORAGE_KEY, JSON.stringify({ accountId: state.session.accountId, form: state.form, editor: state.editor, lastOperation: state.lastOperation, pending: state.pending }))
      state.storageError = false
    } catch {
      state.storageError = true
    }
  }

  function session(data, observedAt) {
    const remaining = Date.parse(data.expiresAt) - Date.parse(data.serverNow)
    if (!data.accountId || !Number.isFinite(remaining) || remaining <= 0) {
      lock()
      throw new AdminApiError(401, { code: 'ADMIN_SESSION_EXPIRED' })
    }
    if (state.session && state.session.accountId !== data.accountId) {
      state.form = null
      state.editor = null
      state.lastOperation = null
      state.pending = null
      state.places = []
      try { storage.removeItem(STORAGE_KEY) } catch { state.storageError = true }
    }
    state.session = data
    // Tính từ lúc bắt đầu đọc để thời gian chờ mạng không kéo dài quyền hiển thị
    deadline = observedAt + remaining
    if (clock() >= deadline) {
      lock()
      throw new AdminApiError(401, { code: 'ADMIN_SESSION_EXPIRED' })
    }
    state.locked = false
  }

  function lock() {
    generation += 1
    bootstrapQueued = false
    state.locked = true
    state.places = []
  }

  function checkExpiry() {
    if (!state.locked && clock() >= deadline) lock()
    return !state.locked
  }

  function guard() {
    if (!checkExpiry()) throw new AdminApiError(401, { code: 'ADMIN_SESSION_EXPIRED' })
  }

  function fail(error) {
    if (error.status === 401) lock()
    state.error = error.code || 'CONNECTION_FAILED'
    state.fieldErrors = error.fieldErrors || {}
    state.missingConditions = error.missingConditions || []
    return error
  }

  async function run(work) {
    if (state.busy) return false
    state.busy = true
    state.error = ''
    state.message = ''
    state.fieldErrors = {}
    state.missingConditions = []
    try {
      await work()
      return true
    } catch (error) {
      fail(error)
      return false
    } finally {
      state.busy = false
      if (bootstrapQueued) {
        bootstrapQueued = false
        await bootstrap()
      }
    }
  }

  function restore() {
    try {
      const saved = JSON.parse(storage.getItem(STORAGE_KEY) || 'null')
      if (!saved) return
      if (saved.accountId !== state.session.accountId) {
        storage.removeItem(STORAGE_KEY)
        return
      }
      if (!state.form && saved.form && typeof saved.form.displayName === 'string') state.form = saved.form
      if (!state.lastOperation && saved.lastOperation) state.lastOperation = saved.lastOperation
      if (!state.editor && saved.editor) state.editor = saved.editor
      if (!state.pending && saved.pending && typeof saved.pending.key === 'string') state.pending = saved.pending
      if (state.form || state.editor) state.message = 'Đã khôi phục công việc trong tab — chưa tự lưu hoặc gửi lại'
    } catch {
      state.storageError = true
    }
  }

  async function bootstrap() {
    if (state.busy) {
      bootstrapQueued = true
      return false
    }
    return run(async () => {
      const id = generation
      const started = clock()
      const { data } = await api.send('/admin/session')
      if (id !== generation) return
      session(data, started)
      restore()
      if (state.editor && !state.pending) await refreshEditor()
      else if (state.form?.placeId && !state.pending) await reviewForm()
      if (!state.form && !state.editor && !state.pending) await loadList()
    })
  }

  async function login(loginName, password) {
    return run(async () => {
      const id = generation
      const started = clock()
      const { data } = await api.send('/auth/login', { method: 'POST', body: { loginName, password } })
      if (id !== generation) return
      session(data, started)
      restore()
      if (state.editor && !state.pending) await refreshEditor()
      else if (state.form?.placeId && !state.pending) await reviewForm()
      if (!state.form && !state.editor && !state.pending) await loadList()
    })
  }

  async function loadList() {
    guard()
    const id = generation
    const { data } = await api.send('/admin/places')
    if (id === generation && checkExpiry()) state.places = data
  }

  async function detail(placeId) {
    guard()
    const id = generation
    const { data, etag } = await api.send(`/admin/places/${encodeURIComponent(placeId)}`)
    if (id !== generation || !checkExpiry()) return
    state.form = { placeId: data.placeId, displayName: data.displayName, original: data.displayName, etag, place: data }
    persist()
  }

  async function reviewForm() {
    guard()
    const form = state.form
    if (!form?.placeId || state.pending) return
    form.reviewRequired = true
    const id = generation
    try {
      const { data, etag } = await api.send(`/admin/places/${encodeURIComponent(form.placeId)}`)
      if (id !== generation || !checkExpiry() || state.form !== form) return
      form.current = data
      form.currentEtag = etag
      form.conflict = etag !== form.etag
      form.deleted = false
      form.reviewRequired = false
      form.place = data
      persist()
    } catch (error) {
      if (error.status === 404 && state.form === form) {
        form.deleted = true
        persist()
      }
      throw error
    }
  }

  function acceptCurrentVersion() {
    guard()
    const form = state.form
    if (state.pending || !form?.currentEtag || form.deleted) return
    form.etag = form.currentEtag
    form.original = form.current.displayName
    form.conflict = false
    persist()
  }

  function newPlace() {
    guard()
    if (state.pending) throw new Error('PENDING_OPERATION')
    state.form = { placeId: null, displayName: '', original: '', etag: null, place: null }
    persist()
  }

  function edit(value) {
    guard()
    if (state.pending || !state.form) return
    state.form.displayName = value
    persist()
  }

  function dirty() {
    return !!((state.form && state.form.displayName !== state.form.original) || (state.editor && JSON.stringify(state.editor.draft) !== state.editor.original))
  }

  function discard() {
    guard()
    if (state.pending) throw new Error('PENDING_OPERATION')
    state.form = null
    state.editor = null
    persist()
  }

  async function applyResult(operation) {
    state.lastOperation = operation
    if (state.pending?.kind) {
      const pending = state.pending
      const kind = pending.kind
      state.pending = null
      const e = state.editor
      if (e && kind.startsWith('tts-') && operation.result?.jobId) {
        e.ttsJobId = operation.result.jobId
      }
      if (e && kind === 'region-save') {
        e.savedRevisionId = operation.result.regionRevisionId
        e.savedHash = operation.result.geometryHash
        e.original = JSON.stringify(e.draft)
      }
      if (e && kind === 'text-save') {
        const original = JSON.parse(e.original)
        const previousTextId = e.draft.baseTextId
        e.draft.body = pending.options.body.body
        e.draft.baseTextId = operation.result.textRevisionId || e.draft.baseTextId
        original.body = e.draft.body
        original.baseTextId = e.draft.baseTextId
        if (e.draft.baseTextId !== previousTextId) {
          for (const field of ['textLocator', 'textLimitations', 'audioLocator', 'audioLimitations']) {
            e.draft[field] = original[field] = ''
          }
          e.audioReview = null
          e.ttsJob = null
          e.ttsJobId = null
        }
        e.original = JSON.stringify(original)
      }
      if (e && (kind === 'text-review' || kind === 'audio-review')) {
        const prefix = kind.split('-')[0], original = JSON.parse(e.original)
        original[prefix + 'Locator'] = pending.options.body.locator
        original[prefix + 'Limitations'] = pending.options.body.limitations
        e.original = JSON.stringify(original)
      }
      persist()
      try {
        if (kind === 'delete') { state.form = null; state.editor = null; await loadList() }
        else if (e) await refreshEditor(kind)
        else await detail(operation.result.placeId)
        state.message = kind === 'delete' ? 'Đã xóa dữ liệu; file dọn riêng khi đủ điều kiện, lịch sử giữ theo chính sách' : 'Lệnh đã thành công; đã đọc lại dữ liệu hiện hành'
      } catch (error) { fail(error); state.message = 'Lệnh đã commit; chưa tải được hiện trạng. Không cần gửi lệnh mới' }
      persist()
      return
    }
    // Receipt chứng minh lệnh đã commit, không chứng minh dữ liệu hiện hành vẫn bằng kết quả cũ
    state.pending = null
    state.form = null
    state.message = operation.result?.duplicateNameWarning
      ? 'Đã lưu địa điểm; có địa điểm trùng tên, dùng mã để phân biệt'
      : 'Đã lưu địa điểm'
    persist()
    try {
      await detail(operation.result.placeId)
    } catch (error) {
      fail(error)
      state.message += ' — chưa tải được dữ liệu hiện hành, không cần gửi lệnh lưu mới'
    }
  }

  async function dispatchPending() {
    guard()
    const pending = state.pending
    if (!pending || pending.accountId !== state.session.accountId) throw new Error('PENDING_OPERATION')
    const id = generation
    const attempted = pending.attempted
    pending.attempted = true
    persist()
    try {
      const { data } = await api.send(pending.path, pending.options)
      if (id !== generation || !checkExpiry()) return
      await applyResult(data)
      try {
        const started = clock()
        const current = await api.send('/admin/session')
        if (id === generation && !state.locked) session(current.data, started)
      } catch (error) {
        if (error.status === 401) lock()
      }
    } catch (error) {
      // Lỗi mạng/5xx có thể xảy ra sau commit; giữ nguyên khóa, payload và precondition để đối chiếu
      if (!attempted && error.status >= 400 && error.status < 500 && error.status !== 401 && error.status !== 408 && error.status !== 429) {
        state.pending = null
        persist()
      }
      throw error
    }
  }

  async function save() {
    return run(async () => {
      guard()
      if (!state.form || state.pending) throw new Error('PENDING_OPERATION')
      const form = state.form
      if (form.deleted || form.conflict || form.reviewRequired) throw new AdminApiError(412, { code: 'VERSION_CONFLICT' })
      const requestGeneration = generation
      const { data: window } = await api.send('/admin/command-windows', { method: 'POST' })
      if (requestGeneration !== generation) return
      guard()
      const key = `${window.windowId}:${uuid()}`
      const path = form.placeId ? `/admin/places/${encodeURIComponent(form.placeId)}` : '/admin/places'
      const options = { method: form.placeId ? 'PATCH' : 'POST', key, body: { displayName: form.displayName } }
      if (form.placeId) {
        if (!form.etag) throw new AdminApiError(400, { code: 'VERSION_REQUIRED' })
        options.etag = form.etag
      }
      state.pending = { accountId: state.session.accountId, key, path, options }
      persist()
      await dispatchPending()
    })
  }

  async function inspect() {
    return run(async () => {
      guard()
      if (!state.pending) return
      const id = generation
      const { data } = await api.send(`/admin/operations/${encodeURIComponent(state.pending.key)}`)
      if (id !== generation || !checkExpiry()) return
      if (data.state === 'COMMITTED' && data.operation) await applyResult(data.operation)
      else state.message = 'Chưa quan sát thấy kết quả; chưa đủ căn cứ kết luận lưu thất bại'
    })
  }

  async function activity(type) {
    if (state.locked || clock() - lastActivity < 1000) return
    lastActivity = clock()
    try {
      guard()
      const id = generation
      const started = clock()
      const { data } = await api.send('/admin/session/activity', { method: 'POST', body: { activityType: type } })
      if (id === generation && !state.locked) session(data, started)
    } catch (error) { fail(error) }
  }

  async function logout() {
    return run(async () => {
      guard()
      try {
        await api.send('/auth/logout', { method: 'POST' })
      } catch (error) {
        lock()
        state.message = 'Chưa xác nhận đã đăng xuất; kiểm tra phiên trước khi tiếp tục'
        throw error
      }
      lock()
      state.session = null
      state.form = null
      state.editor = null
      state.lastOperation = null
      state.pending = null
      try { storage.removeItem(STORAGE_KEY) } catch { state.storageError = true }
      state.message = 'Đã đăng xuất'
    })
  }


  const placePath = id => '/admin/places/' + encodeURIComponent(id)
  const contentPath = e => placePath(e.placeId) + '/content/' + e.language

  async function openEditor(kind, placeId, language = 'vi') {
    return run(async () => {
      guard()
      if (state.pending || dirty()) throw new Error('UNSAVED_WORK')
      state.form = null
      state.editor = { kind, placeId, language, draft: {}, original: '{}', reviewRequired: true }
      persist()
      await refreshEditor('open')
    })
  }

  async function refreshEditor(reason = 'review') {
    guard()
    const e = state.editor, id = generation
    if (!e || state.pending) return
    e.reviewRequired = true
    if (!('vertices' in e.draft) && !('body' in e.draft)) reason = 'open'
    const place = await api.send(placePath(e.placeId))
    let content, region, activeRegion, completeness
    if (e.kind === 'content') {
      content = await api.send(contentPath(e))
      completeness = (await api.send('/admin/content-completeness')).data.find(row => row.placeId === e.placeId && row.language === e.language)
    }
    const rid = e.savedRevisionId || place.data.activeRegionRevisionId
    if (e.kind === 'region' && rid) region = (await api.send(placePath(e.placeId) + '/region-revisions/' + encodeURIComponent(rid))).data
    if (e.kind === 'region' && place.data.activeRegionRevisionId) activeRegion = place.data.activeRegionRevisionId === rid ? region : (await api.send(placePath(e.placeId) + '/region-revisions/' + encodeURIComponent(place.data.activeRegionRevisionId))).data
    let audio
    let tts
    const jobId = e.ttsJobId || content?.data.draftText?.latestTtsJob?.jobId
    if (e.kind === 'content' && jobId) tts = (await api.send('/admin/tts-jobs/' + encodeURIComponent(jobId))).data
    if (e.audioReview) audio = (await api.send('/admin/audio-revisions/' + encodeURIComponent(e.audioReview.audioRevisionId) + '/review-reference')).data
    if (id !== generation || !checkExpiry() || state.editor !== e) return
    e.conflict = reason !== 'open' && !!e.etag && (e.kind === 'region' ? reason === 'review' && e.etag !== place.etag : (reason === 'review' && e.contentEtag !== content.etag) || e.draft.baseTextId !== (content.data.draftText?.textRevisionId || null))
    e.activeRegion = activeRegion
    e.place = place.data
    e.currentEtag = place.etag
    if (!e.conflict) e.etag = place.etag
    if (e.kind === 'content') {
      e.content = content.data
      e.ttsJob = tts || content.data.draftText?.latestTtsJob || null
      e.ttsJobId = e.ttsJob?.jobId || null
      e.candidates = (completeness?.draftAudios || []).filter(a => a.textRevisionId === content.data.draftText?.textRevisionId)
      e.missingConditions = completeness?.draftMissingConditions || []
      e.currentContentEtag = content.etag
      if (!e.conflict) e.contentEtag = content.etag
      e.textReview = content.data.draftText?.reviewReference
      if (audio) e.audioReview = audio
      if (reason === 'open') {
        e.draft = { body: content.data.draftText?.body || '', baseTextId: content.data.draftText?.textRevisionId || null,
          textLocator: e.textReview?.locator || '', textLimitations: e.textReview?.limitations || '', audioLocator: '', audioLimitations: '' }
        e.original = JSON.stringify(e.draft)
      }
    } else if (reason === 'open') {
      e.draft = { vertices: region ? region.polygon.coordinates[0].slice(0, -1) : [], closed: !!region }
      e.savedRevisionId = region?.revisionId || null
      e.savedHash = region?.geometryHash || null
      e.original = JSON.stringify(e.draft)
    }
    e.reviewRequired = false
    persist()
  }

  function editDraft(patch) {
    guard()
    if (state.busy || state.pending || !state.editor) return
    Object.assign(state.editor.draft, patch)
    if (state.editor.kind === 'region') state.editor.preview = null
    persist()
  }

  function acceptEditorVersion() {
    guard()
    const e = state.editor
    if (!e || state.pending || e.reviewRequired) return
    e.etag = e.currentEtag
    e.contentEtag = e.currentContentEtag
    if (e.kind === 'content') e.draft.baseTextId = e.content?.draftText?.textRevisionId || null
    e.conflict = false
    persist()
  }

  async function command(kind, path, method, body, etag) {
    return run(async () => {
      guard()
      if (state.pending) throw new Error('PENDING_OPERATION')
      if (state.editor?.conflict || state.editor?.reviewRequired) throw new AdminApiError(412, { code: 'VERSION_CONFLICT' })
      if (!etag && !kind.startsWith('tts-')) throw new AdminApiError(400, { code: 'VERSION_REQUIRED' })
      const id = generation
      const { data: window } = await api.send('/admin/command-windows', { method: 'POST' })
      if (id !== generation || !checkExpiry()) return
      const key = window.windowId + ':' + uuid()
      state.pending = { accountId: state.session.accountId, kind, key, path, options: { method, body, etag, key } }
      persist()
      await dispatchPending()
    })
  }

  async function chooseAudio(audioId) {
    return run(async () => {
      guard()
      const e = state.editor, id = generation
      if (!e || e.kind !== 'content' || state.pending) return
      const original = JSON.parse(e.original)
      if (e.draft.audioLocator !== original.audioLocator || e.draft.audioLimitations !== original.audioLimitations) throw new AdminApiError(409, { code: 'UNSAVED_AUDIO_REFERENCE' })
      const { data } = await api.send('/admin/audio-revisions/' + encodeURIComponent(audioId) + '/review-reference')
      if (id !== generation || !checkExpiry() || state.editor !== e) return
      if (data.textRevisionId !== e.content.draftText?.textRevisionId || data.language !== e.language) throw new AdminApiError(409, { code: 'AUDIO_TEXT_MISMATCH' })
      e.audioReview = data
      Object.assign(e.draft, { audioLocator: data.locator || '', audioLimitations: data.limitations || '' })
      Object.assign(original, { audioLocator: e.draft.audioLocator, audioLimitations: e.draft.audioLimitations })
      e.original = JSON.stringify(original)
      persist()
    })
  }

  function saveText() {
    const e = state.editor
    if (reviewDraftDirty()) {
      state.error = 'UNSAVED_REVIEW_REFERENCE'
      return Promise.resolve(false)
    }
    return command('text-save', contentPath(e) + '/text-revisions', 'POST', { body: e.draft.body, baseTextId: e.draft.baseTextId }, e.contentEtag)
  }

  const reviewFields = ['textLocator', 'textLimitations', 'audioLocator', 'audioLimitations']

  function reviewDraftDirty() {
    const e = state.editor
    const original = JSON.parse(e.original)
    return reviewFields.some(field => e.draft[field] !== original[field])
  }

  function discardReviewDraft() {
    const original = JSON.parse(state.editor.original)
    editDraft(Object.fromEntries(reviewFields.map(field => [field, original[field]])))
  }

  async function previewAudio(onReady) {
    return run(async () => {
      guard()
      const editor = state.editor
      const audio = editor?.audioReview
      if (state.pending || !audio || audio.textRevisionId !== editor.content.draftText?.textRevisionId) return
      const id = generation
      const { data } = await api.send('/admin/audio-revisions/' + encodeURIComponent(audio.audioRevisionId) + '/preview', { preview: audio })
      if (id === generation && checkExpiry() && state.editor === editor && editor.audioReview === audio) onReady(data)
    })
  }

  function saveReview(type) {
    const e = state.editor, text = type === 'text', ref = text ? e.textReview : e.audioReview
    const rid = text ? e.content.draftText?.textRevisionId : ref?.audioRevisionId
    if (!rid || !ref) return Promise.resolve(false)
    if (text && e.draft.baseTextId !== rid) { state.error = 'VERSION_CONFLICT'; return Promise.resolve(false) }
    if (!text && (ref.textRevisionId !== e.content.draftText?.textRevisionId || ref.language !== e.language)) {
      state.error = 'AUDIO_TEXT_MISMATCH'
      return Promise.resolve(false)
    }
    return command(type + '-review', '/admin/' + type + '-revisions/' + encodeURIComponent(rid) + '/review-reference', 'PUT',
      { locator: e.draft[type + 'Locator'], limitations: e.draft[type + 'Limitations'] }, ref.etag)
  }

  function applyPair() {
    const e = state.editor, t = e.content.draftText, a = e.audioReview
    if (dirty() || !t || !a || e.textReview?.version == null || a.version == null || a.textRevisionId !== t.textRevisionId || a.language !== e.language) {
      state.error = 'PAIR_NOT_READY'; return Promise.resolve(false)
    }
    return command('pair-apply', contentPath(e) + '/active-pair', 'PUT', { textRevisionId: t.textRevisionId, audioRevisionId: a.audioRevisionId,
      expectedTextHash: t.contentHash, expectedMp3Checksum: a.mp3Checksum, textReviewVersion: e.textReview.version, audioReviewVersion: a.version }, e.contentEtag)
  }

  function createTts() {
    const editor = state.editor
    const text = editor?.content?.draftText
    if (!text || editor.draft.body !== text.body || editor.draft.baseTextId !== text.textRevisionId) {
      state.error = 'SAVE_TEXT_FIRST'
      return Promise.resolve(false)
    }
    return command('tts-create', '/admin/text-revisions/' + encodeURIComponent(text.textRevisionId) + '/tts-jobs',
      'POST', { expectedTextHash: text.contentHash })
  }

  function checkTts() {
    const job = state.editor?.ttsJob
    if (!job || job.phase === 'DISCARDED') return Promise.resolve(false)
    return command('tts-check', '/admin/tts-jobs/' + encodeURIComponent(job.jobId) + '/check', 'POST')
  }

  function retryTts() {
    const job = state.editor?.ttsJob
    if (!job || !['REPROCESS_LOCAL', 'CREATE_PROVIDER_ATTEMPT'].includes(job.retryAction)) return Promise.resolve(false)
    return command('tts-retry', '/admin/tts-jobs/' + encodeURIComponent(job.jobId) + '/retry',
      'POST', { expectedPhaseVersion: job.phaseVersion })
  }

  async function readAdmin(path, onResult) {
    return run(async () => {
      guard()
      const id = generation
      const { data } = await api.send(path)
      if (id === generation && checkExpiry()) onResult(data)
    })
  }

  return { state, run, persist, openEditor, refreshEditor: () => run(() => refreshEditor()), editDraft, acceptEditorVersion, command, chooseAudio, previewAudio, saveText, saveReview, reviewDraftDirty, discardReviewDraft, applyPair, createTts, checkTts, retryTts, readAdmin, bootstrap, login, checkExpiry, newPlace, edit, dirty, discard,
    list: () => run(loadList), detail: id => run(() => detail(id)), save, inspect,
    replay: () => run(dispatchPending), review: () => run(reviewForm), acceptCurrentVersion, activity, logout, lock }
}
