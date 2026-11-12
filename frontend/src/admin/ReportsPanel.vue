<script setup>
import { computed, nextTick, ref } from 'vue'
const props = defineProps({ work: Object })
const day = () => new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Saigon', year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date())
const fromDate = ref(day()), toDate = ref(day()), status = ref(''), searchNameOrId = ref('')
const mode = ref('sessions'), result = ref(null), detail = ref(null), detailId = ref(''), detailError = ref('')
const loadedFilters = ref('')
const detailDialog = ref(null)
function closeDetail() {
  detailDialog.value?.close()
  detailId.value = ''
  detail.value = null
}
const filterKey = computed(() => JSON.stringify([mode.value, fromDate.value, toDate.value, status.value, searchNameOrId.value]))
const stale = computed(() => !!result.value && filterKey.value !== loadedFilters.value)
const states = { ACTIVE: 'Còn hiệu lực', ENDED: 'Đã kết thúc', EXPIRED: 'Đã hết hạn' }
const stamp = value => value ? new Intl.DateTimeFormat('vi-VN', { dateStyle: 'short', timeStyle: 'medium', timeZone: 'Asia/Saigon' }).format(new Date(value)) : '—'
async function load() {
  if (fromDate.value > toDate.value) {
    props.work.state.error = 'DATE_RANGE_INVALID'
    return
  }
  const current = filterKey.value
  const query = new URLSearchParams({ fromDate: fromDate.value, toDate: toDate.value })
  if (mode.value === 'sessions') {
    if (status.value) query.set('status', status.value)
    if (searchNameOrId.value) query.set('searchNameOrId', searchNameOrId.value)
  }
  void props.work.activity('SELECT')
  await props.work.readAdmin('/admin/' + (mode.value === 'sessions' ? 'guest-sessions' : 'statistics') + '?' + query, data => {
    if (filterKey.value === current) {
      result.value = data
      loadedFilters.value = current
    }
  })
}
function switchMode(value) {
  mode.value = value
  result.value = null
  void props.work.activity('SELECT')
}
async function show(id) {
  detailId.value = id
  detail.value = null
  detailError.value = ''
  await nextTick()
  if (!detailDialog.value.open) detailDialog.value.showModal()
  void props.work.activity('SELECT')
  const ok = await props.work.readAdmin('/admin/guest-sessions/' + encodeURIComponent(id), data => {
    if (detailId.value === id) detail.value = data
  })
  if (!ok) detailError.value = 'Chưa đọc được phiên. Phiên có thể không còn được giữ.'
}
</script>

<template>
  <section>
    <div :inert="!!detailId">
      <h2>Phiên và thống kê</h2>
      <div class="admin-toolbar">
        <button :disabled="work.state.busy" @click="switchMode('sessions')">Phiên khách</button>
        <button :disabled="work.state.busy" @click="switchMode('statistics')">Thống kê</button>
      </div>
      <form @submit.prevent="load">
        <fieldset :disabled="work.state.busy" @change="work.activity('SELECT')">
          <legend>Khoảng ngày — Asia/Saigon</legend>
          <label>Từ ngày <input v-model="fromDate" type="date" required></label>
          <label>Đến ngày <input v-model="toDate" type="date" required></label>
          <template v-if="mode === 'sessions'">
            <label>Trạng thái <select v-model="status"><option value="">Tất cả</option><option v-for="(label, code) in states" :key="code" :value="code">{{ label }}</option></select></label>
            <label>Tên hoặc mã phiên <input v-model="searchNameOrId"></label>
          </template>
          <button>Xem dữ liệu</button>
          <button type="button" @click="fromDate = day(); toDate = day(); load()">Về hôm nay</button>
        </fieldset>
      </form>
      <p>Chỉ dữ liệu còn được giữ theo chính sách 30 ngày. Số phiên và lượt xem được tính riêng.</p>
      <p v-if="stale" role="status">Bộ lọc đã thay đổi. Bảng dưới thuộc lần tải trước.</p>
      <p v-if="!result">Chọn khoảng ngày và bấm Xem dữ liệu.</p>
      <template v-else-if="mode === 'sessions'">
        <p v-if="!result.sessions.length">Không có phiên còn được giữ phù hợp bộ lọc.</p>
        <div class="admin-table-wrap"><table v-if="result.sessions.length">
          <thead><tr><th>Mã phiên</th><th>Tên</th><th>Ngôn ngữ ban đầu</th><th>Bắt đầu</th><th>Trạng thái</th><th></th></tr></thead>
          <tbody><tr v-for="session in result.sessions" :key="session.sessionId">
            <td>{{ session.sessionId }}</td><td>{{ session.displayName }}</td><td>{{ session.initialLanguage }}</td><td>{{ stamp(session.startedAt) }}</td><td>{{ states[session.state] }}</td>
            <td><button :disabled="work.state.busy" @click="show(session.sessionId)">Xem</button></td>
          </tr></tbody>
        </table></div>
      </template>
      <template v-else>
        <h3>Phiên bắt đầu trong khoảng — tổng {{ result.sessionTotal }}</h3>
        <table><thead><tr><th>Ngày bắt đầu</th><th>Số phiên</th></tr></thead><tbody><tr v-for="row in result.sessionsByDay" :key="row.date"><td>{{ row.date }}</td><td>{{ row.sessions }}</td></tr></tbody></table>
        <p v-if="!result.sessionsByDay.length">Không có phiên phù hợp.</p>
        <h3>Lượt xem trong khoảng — tổng {{ result.viewTotal }}</h3>
        <div class="admin-table-wrap"><table><thead><tr><th>Ngày xem</th><th>Địa điểm</th><th>Ngôn ngữ</th><th>Lượt</th></tr></thead>
          <tbody><tr v-for="row in result.viewsByPlaceLanguageDay" :key="row.date + row.placeId + row.language"><td>{{ row.date }}</td><td>{{ row.placeName }} · {{ row.placeId }} {{ row.placeDeleted ? '(đã xóa)' : '' }}</td><td>{{ row.language }}</td><td>{{ row.views }}</td></tr></tbody>
        </table></div>
        <p v-if="!result.viewsByPlaceLanguageDay.length">Không có lượt xem phù hợp.</p>
        <p>Phiên nhóm theo ngày bắt đầu. Lượt xem nhóm theo ngày xem, địa điểm và ngôn ngữ đã ghi nhận.</p>
      </template>
    </div>
    <dialog ref="detailDialog" aria-labelledby="session-detail-heading" class="admin-dialog admin-session-dialog" @cancel.prevent="closeDetail">
        <h3 id="session-detail-heading">Chi tiết phiên {{ detailId }}</h3>
        <button autofocus @click="closeDetail">Đóng</button>
        <p v-if="detailError" role="alert">{{ detailError }} <button :disabled="work.state.busy" @click="show(detailId)">Thử lại</button></p>
        <p v-else-if="!detail">Đang tải phiên…</p>
        <template v-else>
          <p>{{ detail.displayName }} · {{ detail.initialLanguage }} · {{ states[detail.state] }}</p>
          <p>Bắt đầu {{ stamp(detail.startedAt) }} · hết hạn {{ stamp(detail.expiresAt) }} · kết thúc chủ động {{ stamp(detail.endedAt) }}</p>
          <h4>Lượt xem đã tính</h4>
          <p v-if="!detail.views.length">Chưa có lượt xem được tính.</p>
          <ul><li v-for="view in detail.views" :key="view.viewId">{{ stamp(view.viewedAt) }} · {{ view.placeName }} · {{ view.placeId }} {{ view.placeDeleted ? '(đã xóa)' : '' }} · {{ view.language }}</li></ul>
        </template>
    </dialog>
  </section>
</template>
