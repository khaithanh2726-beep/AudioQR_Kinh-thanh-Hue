<script setup>
import { computed, onMounted, ref } from 'vue'
const props = defineProps({ work: Object })
const emit = defineEmits(['open'])
const entries = ref(null), standardOnly = ref(true)
const loading = ref(false), loadError = ref(false)
const standardIds = new Set([
  '039d5ff2-da6b-5283-b7ed-5196dd4dcb2e', '68667731-5be9-592f-bf28-3e3f0e81e4ef',
  '5a3bc1b0-7e12-593e-9570-ce3ed726dbae', '0ccc056f-6203-5da3-b4b3-18613615ea91',
  '9b30736e-75fd-5c62-916e-ad8e2b11747a',
])
const languages = ['vi', 'en', 'fr', 'ru']
const rows = computed(() => {
  const groups = new Map()
  for (const entry of entries.value || []) {
    if (standardOnly.value && !standardIds.has(entry.placeId)) continue
    if (!groups.has(entry.placeId)) groups.set(entry.placeId, { id: entry.placeId, name: entry.displayName, content: {} })
    groups.get(entry.placeId).content[entry.language] = entry.content
  }
  return [...groups.values()]
})
const standardPairs = computed(() => (entries.value || []).filter(entry => standardIds.has(entry.placeId)
  && entry.content.appliedPair?.textReviewReference?.version != null
  && entry.content.appliedPair?.audioReviewReference?.version != null
  && entry.content.appliedPair.missingConditions.length === 0).length)
async function load() {
  if (loading.value) return
  loading.value = true
  loadError.value = false
  try {
    loadError.value = !await props.work.readAdmin('/admin/content-completeness', data => { entries.value = data })
  } finally { loading.value = false }
}
onMounted(load)
</script>
<template>
  <section>
    <h2>Mức đầy đủ nội dung</h2>
    <p v-if="entries">Bộ chuẩn có {{ standardPairs }}/20 cặp đã áp dụng đủ điều kiện tại lần đọc gần nhất.</p>
    <label class="completeness-filter"><input v-model="standardOnly" type="checkbox" @change="work.activity('SELECT')">Chỉ năm địa điểm chuẩn</label>
    <button :disabled="work.state.busy || loading" @click="load(); work.activity('SELECT')">Tải lại mức đầy đủ</button>
    <p v-if="loading" role="status">Đang đọc mức đầy đủ…</p>
    <p v-else-if="loadError" role="alert">Chưa tải được mức đầy đủ. Bấm Tải lại mức đầy đủ để thử lại. {{ entries ? 'Bảng dưới thuộc lần tải trước.' : '' }}</p>
    <p v-else-if="entries && !rows.length">Chưa có địa điểm phù hợp.</p>
    <div class="admin-table-wrap"><table v-if="rows.length"><thead><tr><th>Địa điểm</th><th v-for="lang in languages" :key="lang">{{ lang }}</th></tr></thead>
      <tbody><tr v-for="row in rows" :key="row.id"><th class="completeness-place">{{ row.name }}<small>{{ row.id }}</small></th><td v-for="lang in languages" :key="lang">
        <p>{{ row.content[lang]?.draftText ? 'Có nháp' : 'Chưa có nháp' }}</p>
        <p>{{ row.content[lang]?.appliedPair ? 'Đã áp dụng cặp' : 'Chưa áp dụng cặp' }}</p>
        <button :disabled="work.state.busy || !!work.state.pending" @click="emit('open',row.id,lang)">Mở</button>
      </td></tr></tbody>
    </table></div>
  </section>
</template>

<style scoped>
.completeness-filter { display: flex; align-items: center; gap: .5rem; margin: .75rem 0; }
.completeness-filter input[type="checkbox"] { width: auto; margin: 0; }
.completeness-place small { display: block; margin-top: .25rem; color: #66756c; font-size: .75rem; font-weight: 400; overflow-wrap: anywhere; }
</style>
