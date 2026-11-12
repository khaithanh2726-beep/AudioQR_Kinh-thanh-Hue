<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { createAdminClient } from './api.js'
import { createWorkspace } from './workspace.js'
import './admin.css'
import RegionEditor from './RegionEditor.vue'
import ContentEditor from './ContentEditor.vue'
import ReportsPanel from './ReportsPanel.vue'
import QrPanel from './QrPanel.vue'
import CompletenessPanel from './CompletenessPanel.vue'

const unavailableStorage = { getItem() { throw new Error() }, setItem() { throw new Error() }, removeItem() { throw new Error() } }
let storage
try { storage = window.sessionStorage } catch { storage = unavailableStorage }
const work = createWorkspace({ api: createAdminClient(), storage, decorate: reactive })
const state = work.state
const loginName = ref('')
const password = ref('')
const confirmation = ref(null)
const page = ref('places')
const errors = {
  ADMIN_LOGIN_FAILED: 'Tài khoản hoặc mật khẩu không đúng',
  ADMIN_SESSION_EXPIRED: 'Phiên quản trị đã hết hạn. Vui lòng đăng nhập lại',
  CONNECTION_FAILED: 'Không kết nối được. Vui lòng kiểm tra mạng',
  INVALID_RESPONSE: 'Phản hồi chưa hợp lệ. Vui lòng kiểm tra lại kết quả',
  VERSION_CONFLICT: 'Địa điểm đã thay đổi. Hãy giữ phần đang soạn và tải lại để đối chiếu',
  VERSION_REQUIRED: 'Chưa có phiên bản dữ liệu. Hãy tải lại địa điểm trước khi lưu',
  VALIDATION_FAILED: 'Dữ liệu chưa hợp lệ. Vui lòng kiểm tra các trường',
  FORBIDDEN: 'Không có quyền thực hiện thao tác này',
  CSRF_INVALID: 'Thông tin bảo vệ yêu cầu chưa hợp lệ. Vui lòng kiểm tra phiên',
  COMMAND_WINDOW_EXPIRED: 'Thời hạn tra cứu yêu cầu đã hết. Cần đối chiếu dữ liệu trước thao tác mới',
  SERVICE_EPOCH_CHANGED: 'Dịch vụ đã thay đổi. Cần đối chiếu kết quả trước thao tác mới',
  LOCATION_NOT_FOUND: 'Địa điểm không còn tồn tại',
  KEY_REUSED: 'Yêu cầu không khớp thao tác đã gửi. Cần kiểm tra kết quả',
  UNSAVED_REVIEW_REFERENCE: 'Lưu hoặc bỏ tham chiếu đang soạn trước khi lưu bản văn mới.',
  INVALID_AUDIO_PREVIEW: 'Bản nghe thử không khớp audio đã chọn.',
  MEDIA_NOT_READY: 'Audio chưa sẵn sàng để nghe thử.',
  SAVE_TEXT_FIRST: 'Lưu bản văn đang soạn trước khi tạo audio.',
  DATE_RANGE_INVALID: 'Ngày kết thúc phải từ ngày bắt đầu trở đi.',
}
const errorText = computed(() => errors[state.error] || (state.error ? 'Chưa thực hiện được yêu cầu. Vui lòng kiểm tra lại' : ''))
const nameLength = computed(() => {
  const name = state.form?.displayName || ''
  if (!Intl.Segmenter) return null
  const normalized = name.replace(/[\p{Zs}\t\n\r\f\v]+/gu, ' ').trim()
  return [...new Intl.Segmenter('vi', { granularity: 'grapheme' }).segment(normalized)].length
})
Object.assign(errors, { UNSAVED_AUDIO_REFERENCE: 'Cần ghi hoặc bỏ tham chiếu audio đang soạn trước khi chọn audio khác', PAIR_NOT_READY: 'Cần lưu phần đang soạn và đối chiếu đủ hai hồ sơ đúng cặp', AUDIO_TEXT_MISMATCH: 'Audio không khớp bản văn hoặc ngôn ngữ', GEOMETRY_HASH_MISMATCH: 'Hình đã thay đổi; cần xem trước đúng hình trước khi lưu/áp dụng' })
const conditions = {
  PLACE_INFORMATION_INVALID: 'Thông tin địa điểm chưa hợp lệ',
  REGION_NOT_APPLIED: 'Chưa có vùng hợp lệ được xem xét và áp dụng',
  REGION_INVALID: 'Vùng hiện hành chưa hợp lệ',
  VI_PAIR_NOT_APPLIED: 'Chưa có cặp văn bản và audio tiếng Việt được duyệt, áp dụng',
}

async function login() {
  const secret = password.value
  password.value = ''
  await work.login(loginName.value, secret)
}

function navigate(action, logout = false) {
  if (state.busy) return
  if (state.pending) {
    state.message = 'Cần kiểm tra kết quả lưu đang chờ trước khi chuyển công việc'
    return
  }
  if (work.dirty()) confirmation.value = { action, logout }
  else action()
}

function confirmDiscard() {
  const { action, deletion } = confirmation.value
  confirmation.value = null
  if (!deletion && work.dirty()) work.discard()
  action()
}

function input(event) {
  work.edit(event.target.value)
  void work.activity('EDIT')
}

function unload(event) {
  if (work.dirty() || state.pending) {
    event.preventDefault()
    event.returnValue = ''
  }
}

function open(kind, placeId, language = 'vi') {
  navigate(async () => {
    work.discard()
    page.value = 'places'
    await work.openEditor(kind, placeId, language)
    void work.activity('SELECT')
  })
}
function changeLanguage(event) { const id = state.editor.placeId; open('content', id, event.target.value) }
function setVisibility() { const f = state.form; navigate(() => work.command('visibility', '/admin/places/' + encodeURIComponent(f.placeId) + '/visibility', 'PUT', { visible: !f.place.visible }, f.etag)) }
function deletePlace() { const f = state.form; navigate(() => { state.form = { ...f, displayName: f.original }; work.persist(); confirmation.value = { deletion: true, action: () => work.command('delete', '/admin/places/' + encodeURIComponent(f.placeId), 'DELETE', undefined, f.etag) } }) }
function scrollActivity(event) { if (event.isTrusted && document.visibilityState === 'visible' && (event.type === 'wheel' || ['ArrowDown','ArrowUp','PageDown','PageUp','Home','End'].includes(event.key))) void work.activity('VIEW_SCROLL') }
let timer
function onVisible() {
  work.lock()
  if (document.visibilityState === 'visible') {
    void work.bootstrap()
  }
}
function onPageShow(event) {
  if (event.persisted) {
    work.lock()
    void work.bootstrap()
  }
}
watch(() => state.locked, value => { if (value) confirmation.value = null })
onMounted(() => {
  void work.bootstrap()
  timer = setInterval(() => work.checkExpiry(), 1000)
  window.addEventListener('beforeunload', unload)
  window.addEventListener('pageshow', onPageShow)
  document.addEventListener('visibilitychange', onVisible)
  window.addEventListener('wheel', scrollActivity, { passive: true })
  window.addEventListener('keydown', scrollActivity)
})
onBeforeUnmount(() => {
  clearInterval(timer)
  window.removeEventListener('beforeunload', unload)
  window.removeEventListener('pageshow', onPageShow)
  document.removeEventListener('visibilitychange', onVisible)
  window.removeEventListener('wheel', scrollActivity)
  window.removeEventListener('keydown', scrollActivity)
})
</script>

<template>
  <div class="admin-shell">
    <header class="admin-header" :inert="!!confirmation">
      <div><span class="admin-kicker">AUDIOQR KINH THÀNH HUẾ</span><h1>Quản trị địa điểm</h1></div>
      <div v-if="!state.locked" class="admin-account">
        <span>{{ state.session?.loginName }}</span>
        <button :disabled="state.busy || !!state.pending" @click="navigate(work.logout, true)">Đăng xuất</button>
      </div>
    </header>

    <main v-if="!state.locked" class="admin-main" :aria-busy="state.busy" :inert="!!confirmation">
      <nav class="admin-toolbar" aria-label="Chức năng quản trị">
        <button :disabled="state.busy || !!state.pending" @click="navigate(() => { work.discard(); page = 'places'; work.list(); work.activity('SELECT') })">Địa điểm</button>
        <button :disabled="state.busy || !!state.pending" @click="navigate(() => { work.discard(); page = 'reports'; work.activity('SELECT') })">Phiên và thống kê</button>
        <button :disabled="state.busy || !!state.pending" @click="navigate(() => { work.discard(); page = 'completeness'; work.activity('SELECT') })">Mức đầy đủ</button>
        <button :disabled="state.busy || !!state.pending" @click="navigate(() => { work.discard(); page = 'qr'; work.activity('SELECT') })">QR chung</button>
      </nav>
      <p v-if="state.busy" role="status">Đang tải hoặc xử lý…</p>
      <ul v-if="state.missingConditions?.length"><li v-for="condition in state.missingConditions" :key="condition">{{ conditions[condition] || condition }}</li></ul>
      <p v-if="state.message" role="status" class="admin-notice">{{ state.message }}</p>
      <p v-if="errorText" role="alert" class="admin-error">{{ errorText }}</p>
      <p v-if="state.storageError" role="alert" class="admin-error">Chưa giữ tạm được công việc. Nội dung có thể mất khi tải lại hoặc đóng tab</p>

      <p v-if="state.lastOperation" role="status">Thao tác gần nhất đã được xác nhận thành công. Dữ liệu hiện hành có thể thay đổi sau thời điểm đó.</p>
      <section v-if="state.pending" class="admin-pending" aria-labelledby="pending-heading">
        <h2 id="pending-heading">Chưa xác nhận đã lưu</h2>
        <p>Giữ nguyên phần đang soạn. Kiểm tra kết quả trước khi gửi lại</p>
        <button :disabled="state.busy" @click="work.inspect">Kiểm tra kết quả</button>
        <button :disabled="state.busy" @click="work.replay">Gửi lại đúng yêu cầu đang chờ</button>
      </section>

      <ReportsPanel v-if="page === 'reports'" :work="work" />
      <QrPanel v-else-if="page === 'qr'" :work="work" />
      <CompletenessPanel v-else-if="page === 'completeness'" :work="work" @open="(id, lang) => open('content', id, lang)" />
      <section v-else-if="state.editor" class="admin-editor">
        <h2>{{ state.editor.place?.displayName || state.editor.placeId }} — {{ state.editor.kind === 'region' ? 'Vùng tương tác' : 'Nội dung và cặp' }}</h2>
        <button :disabled="state.busy || !!state.pending" @click="navigate(async () => { work.discard(); await work.list() })">Về danh sách</button>
        <button :disabled="state.busy || !!state.pending" @click="work.refreshEditor">Đối chiếu hiện trạng</button>
        <p v-if="state.editor.reviewRequired" role="alert">Chưa đọc/đối chiếu xong dữ liệu hiện hành; thao tác ghi đang bị khóa</p>
        <div v-if="state.editor.conflict" role="alert"><p>Phiên bản đã đổi. Bản đang soạn được giữ; đối chiếu dữ liệu hiện hành trước khi tiếp tục.</p><button :disabled="state.busy || !!state.pending" @click="work.acceptEditorVersion">Giữ phần soạn, dùng phiên bản hiện hành</button></div>
        <p>{{ work.dirty() ? 'Có phần chưa lưu' : 'Không có phần chưa lưu' }}</p>
        <label v-if="state.editor.kind === 'content'">Ngôn ngữ <select :value="state.editor.language" :disabled="state.busy || !!state.pending" @change="changeLanguage"><option v-for="language in ['vi','en','fr','ru']" :key="language">{{ language }}</option></select></label>
        <RegionEditor v-if="state.editor.kind === 'region' && state.editor.draft.vertices" :key="state.editor.placeId" :work="work" />
        <ContentEditor v-if="state.editor.kind === 'content' && state.editor.content" :key="state.editor.placeId + state.editor.language" :work="work" />
        <ul><li v-for="(error, field) in state.fieldErrors" :key="field">{{ field }}: {{ error }}</li></ul>
      </section>
      <section v-else-if="!state.form">
        <div class="admin-toolbar">
          <h2>Địa điểm</h2>
          <button :disabled="state.busy || !!state.pending" @click="work.list">Tải lại danh sách</button>
          <button class="admin-primary" :disabled="state.busy || !!state.pending" @click="work.newPlace">Thêm địa điểm</button>
        </div>
        <p v-if="state.busy" role="status">Đang tải hoặc xử lý…</p>
        <p v-else-if="!state.places.length && !state.error">Chưa có địa điểm</p>
        <ul class="admin-places">
          <li v-for="place in state.places" :key="place.placeId" class="admin-place">
            <div><h3>{{ place.displayName }}</h3><small>Mã: {{ place.placeId }}</small></div>
            <span>{{ place.visible ? 'Đang hiển thị' : 'Đang ẩn' }}</span>
            <span>{{ place.activeRegionRevisionId ? 'Đã áp dụng vùng' : 'Chưa áp dụng vùng' }}</span>
            <button :disabled="state.busy || !!state.pending" @click="work.detail(place.placeId); work.activity('SELECT')">Sửa thông tin</button>
            <button :disabled="state.busy || !!state.pending" @click="open('region',place.placeId)">Vùng tương tác</button>
            <button :disabled="state.busy || !!state.pending" @click="open('content',place.placeId)">Nội dung và cặp</button>
          </li>
        </ul>
      </section>

      <section v-else class="admin-editor">
        <div class="admin-toolbar"><h2>{{ state.form.placeId ? 'Sửa địa điểm' : 'Thêm địa điểm' }}</h2>
          <button :disabled="state.busy || !!state.pending" @click="navigate(async () => { work.discard(); await work.list() })">Về danh sách</button>
        </div>
        <p>Mã: {{ state.form.placeId || 'Hệ thống cấp sau khi lưu thành công' }}</p>
        <p>Trạng thái: {{ state.form.place?.visible ? 'Đang hiển thị' : 'Đang ẩn' }}</p>
        <p v-if="state.form.deleted" role="alert" class="admin-error">Địa điểm đã bị xóa. Không thể lưu phần đang soạn vào địa điểm này</p>
        <section v-if="state.form.conflict" class="admin-pending">
          <h3>Dữ liệu đã thay đổi</h3>
          <p>Tên hiện đã lưu: {{ state.form.current?.displayName }}</p>
          <p>Tên đang soạn được giữ bên dưới. Đối chiếu trước khi chọn tiếp tục</p>
          <button :disabled="state.busy" @click="work.acceptCurrentVersion">Giữ tên đang soạn và dùng phiên bản mới để lưu</button>
        </section>
        <form @submit.prevent="work.save">
          <label for="place-name">Tên địa điểm</label>
          <input id="place-name" :value="state.form.displayName" :disabled="state.busy || !!state.pending" aria-describedby="name-help name-error" @input="input">
          <p id="name-help">Bắt buộc, tối đa 100 ký tự hiển thị<span v-if="nameLength !== null"> — {{ nameLength }}/100</span></p>
          <p id="name-error" class="admin-error" role="alert">{{ state.fieldErrors.displayName }}</p>
          <p>{{ work.dirty() ? 'Chưa lưu vào hệ thống' : 'Không có thay đổi chưa lưu' }}</p>
          <div class="admin-toolbar">
            <button type="button" :disabled="state.busy || !!state.pending" @click="navigate(async () => { work.discard(); await work.list() })">Bỏ thay đổi</button>
            <button type="button" :disabled="state.busy || !!state.pending" @click="work.review">Đối chiếu dữ liệu hiện hành</button>
            <button class="admin-primary" :disabled="state.busy || !!state.pending || state.form.deleted || state.form.conflict || state.form.reviewRequired">{{ state.busy ? 'Đang xử lý…' : 'Lưu địa điểm' }}</button>
          </div>
        </form>
        <template v-if="state.form.place">
          <h3>Điều kiện hiển thị hiện hành</h3>
          <button :disabled="state.busy || !!state.pending || state.form.deleted || state.form.conflict || state.form.reviewRequired" @click="setVisibility">{{ state.form.place.visible ? 'Ẩn địa điểm' : 'Bật hiển thị' }}</button>
          <button :disabled="state.busy || !!state.pending || state.form.deleted || state.form.conflict || state.form.reviewRequired" @click="deletePlace">Xóa địa điểm</button>
          <p v-if="state.form.place.canShow">Đủ điều kiện tại lần đọc gần nhất</p>
          <ul v-else><li v-for="condition in state.form.place.missingConditions" :key="condition">{{ conditions[condition] || 'Còn điều kiện chưa đáp ứng; cần kiểm tra dữ liệu địa điểm' }}</li></ul>
        </template>
      </section>
    </main>

    <section v-if="state.locked" class="admin-login" aria-labelledby="login-heading">
      <h2 id="login-heading">{{ state.session ? 'Đăng nhập lại để tiếp tục' : 'Đăng nhập quản trị' }}</h2>
      <p v-if="state.session">Nội dung quản trị đã được che. Công việc chưa lưu chỉ phục hồi cho đúng tài khoản</p>
      <p v-if="state.message" role="status">{{ state.message }}</p>
      <p v-if="errorText" role="alert" class="admin-error">{{ errorText }}</p>
      <form @submit.prevent="login">
        <label for="login-name">Tài khoản</label><input id="login-name" v-model="loginName" autocomplete="username" required :disabled="state.busy">
        <label for="login-password">Mật khẩu</label><input id="login-password" v-model="password" type="password" autocomplete="current-password" required :disabled="state.busy">
        <button class="admin-primary" :disabled="state.busy">{{ state.busy ? 'Đang kiểm tra…' : 'Đăng nhập' }}</button>
      </form>
      <button :disabled="state.busy" @click="work.bootstrap">Kiểm tra phiên hiện tại</button>
    </section>

    <div v-if="confirmation && !state.locked" class="admin-backdrop">
      <section role="dialog" aria-modal="true" aria-labelledby="discard-heading" class="admin-dialog">
        <h2 id="discard-heading">{{ confirmation.deletion ? 'Xóa địa điểm?' : 'Bỏ phần đang soạn?' }}</h2>
        <p v-if="confirmation.deletion">Xóa {{ state.form?.original }} — {{ state.form?.placeId }} cùng dữ liệu riêng. Lịch sử giữ 30 ngày; file audio dọn riêng khi đủ điều kiện. Hồ sơ duyệt bên ngoài và dữ liệu dùng chung được giữ.</p>
        <p v-else>Thay đổi chưa lưu sẽ bị bỏ. Dữ liệu đã lưu trong hệ thống được giữ nguyên</p>
        <button autofocus @click="confirmation = null">{{ confirmation.deletion ? 'Hủy' : 'Quay lại soạn' }}</button>
        <button @click="confirmDiscard">{{ confirmation.deletion ? 'Xóa địa điểm' : confirmation.logout ? 'Đăng xuất và bỏ thay đổi' : 'Bỏ thay đổi và tiếp tục' }}</button>
      </section>
    </div>
  </div>
</template>
