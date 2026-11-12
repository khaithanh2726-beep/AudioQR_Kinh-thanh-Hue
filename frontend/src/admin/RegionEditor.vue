<script setup>
import { onMounted, onBeforeUnmount, ref, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { prepareRegion } from './geometry.js'
import { addBasemap } from '../map/basemap.js'
import BasemapStatus from '../components/BasemapStatus.vue'
import { messages } from '../guest/messages.js'
const props = defineProps({ work: Object })
const e = () => props.work.state.editor
const host = ref(null), mode = ref('move'), error = ref(''), previewing = ref(false)
let map, layers
let basemap
const basemapState = ref('loading'), canRetryBasemap = ref(false)
let mounted = true
const disabled = () => props.work.state.busy || props.work.state.pending || props.work.state.locked || previewing.value
function patch(draft) { if (!disabled()) { props.work.editDraft(draft); void props.work.activity('DRAW') } }
function paint() {
  if (!map || !e()?.draft.vertices) return
  layers.clearLayers()
  if (e().activeRegion) L.geoJSON(e().activeRegion.polygon, { style: { color: '#655943', dashArray: '7 5', fillOpacity: 0.06 } }).bindTooltip('Vùng đang áp dụng', { permanent: true }).addTo(layers)
  const vertices = e().draft.vertices, points = vertices.map(([x,y]) => [y,x])
  if (points.length > 1) (e().draft.closed ? L.polygon(points) : L.polyline(points)).bindTooltip('Bản đang sửa', { permanent: true }).addTo(layers)
  points.forEach((p, index) => {
    const marker = L.marker(p, { draggable: mode.value === 'edit' && !disabled(), icon: L.divIcon({ className: 'region-vertex', html: String(index + 1), iconSize: [24,24] }) }).addTo(layers)
    marker.on('dragend', () => { const pos = marker.getLatLng(); const next = vertices.map(p => [...p]); next[index] = [pos.lng, pos.lat]; patch({ vertices: next }) })
  })
}
async function preview() {
  error.value = ''; previewing.value = true
  const editor = e(), snapshot = JSON.stringify(editor.draft)
  try {
    const data = await prepareRegion(editor.draft)
    if (props.work.state.locked || e() !== editor || snapshot !== JSON.stringify(editor.draft)) return
    editor.preview = data
    props.work.persist()
    void props.work.activity('SELECT')
  } catch (err) { error.value = err.message }
  finally { previewing.value = false }
}
function save() {
  const editor = e()
  if (!editor.preview) return
  void props.work.command('region-save', '/admin/places/' + encodeURIComponent(editor.placeId) + '/region-revisions', 'POST', editor.preview, editor.etag)
}
function apply() {
  const editor = e()
  if (!editor.preview || editor.preview.geometryHash !== editor.savedHash || JSON.stringify(editor.draft) !== editor.original) return
  void props.work.command('region-apply', '/admin/places/' + encodeURIComponent(editor.placeId) + '/active-region', 'PUT', { savedRevisionId: editor.savedRevisionId, previewedGeometryHash: editor.preview.geometryHash }, editor.etag)
}
onMounted(() => {
  map = L.map(host.value, { center: [16.469,107.578], zoom: 16 }); layers = L.layerGroup().addTo(map)
  void addBasemap(map, { stillMounted: () => mounted, changed: state => { basemapState.value = state } }).then(controller => {
    if (!mounted) return
    basemap = controller
    canRetryBasemap.value = !!controller.retry
  }).catch(() => { if (mounted) basemapState.value = 'error' })
  map.on('click', event => { if (mode.value === 'draw' && !e().draft.closed && !disabled()) patch({ vertices: [...e().draft.vertices, [event.latlng.lng,event.latlng.lat]] }) })
  if (e().draft.vertices?.length) map.fitBounds(e().draft.vertices.map(([x,y]) => [y,x]), { maxZoom: 17 })
  paint()
})
watch(() => [e()?.draft, e()?.activeRegion, mode.value, disabled()], paint, { deep: true })
onBeforeUnmount(() => { mounted = false; basemap?.dispose(); map?.remove() })
</script>
<template>
  <section>
    <h3>Vùng tương tác</h3>
    <p>Nền bản đồ chỉ hỗ trợ đối chiếu vị trí. Tọa độ WGS84; hình vẽ không xác nhận ranh giới di tích. Backend tiếp tục kiểm hình học khi lưu.</p>
    <fieldset :disabled="disabled()">
      <label>Chế độ <select v-model="mode"><option value="move">Di chuyển</option><option value="draw">Vẽ</option><option value="edit">Sửa đỉnh</option></select></label>
      <button @click="patch({ closed: true })" :disabled="e().draft.vertices.length < 3">Khép vùng</button>
      <button @click="patch({ vertices: e().draft.vertices.slice(0,-1), closed: false })">Bỏ đỉnh cuối</button>
      <button @click="patch({ vertices: [], closed: false })">Vẽ lại</button>
    </fieldset>
    <div ref="host" class="region-map" aria-label="Trình vẽ vùng tương tác"></div>
    <BasemapStatus :state="basemapState" :labels="messages.vi" :can-retry="canRetryBasemap" :disabled="!!disabled()" @retry="basemap?.retry()" />
    <fieldset :disabled="disabled()">
      <legend>Tọa độ đỉnh — có thể sửa bằng bàn phím</legend>
      <div v-for="(p,i) in e().draft.vertices" :key="i">
        <label :for="'lng-'+i">Kinh độ {{ i+1 }}</label><input :id="'lng-'+i" type="number" step="any" :value="p[0]" @change="patch({ vertices: e().draft.vertices.map((v,j) => j === i ? [Number($event.target.value),v[1]] : v) })">
        <label :for="'lat-'+i">Vĩ độ {{ i+1 }}</label><input :id="'lat-'+i" type="number" step="any" :value="p[1]" @change="patch({ vertices: e().draft.vertices.map((v,j) => j === i ? [v[0],Number($event.target.value)] : v) })">
      </div>
      <button @click="patch({ vertices: [...e().draft.vertices, [107.578,16.469]], closed: false })">Thêm đỉnh để nhập tọa độ</button>
      <button @click="preview">Xem trước đúng hình hiện tại</button>
      <p v-if="e().preview">Đang xem trước hình đã khép; mã Hash: {{ e().preview.geometryHash }}</p>
      <button :disabled="!e().preview || e().reviewRequired || e().conflict" @click="save">Lưu bản sửa</button>
      <button :disabled="!e().savedRevisionId || !e().preview || e().preview.geometryHash !== e().savedHash || JSON.stringify(e().draft) !== e().original || e().conflict || e().reviewRequired" @click="apply">Áp dụng đúng bản đã xem và lưu</button>
    </fieldset>
    <p v-if="error" role="alert">{{ error }}</p>
    <p>Bản đã lưu: {{ e().savedRevisionId || 'Chưa có' }}. Vùng đang áp dụng: {{ e().place?.activeRegionRevisionId || 'Chưa có' }}.</p>
  </section>
</template>
