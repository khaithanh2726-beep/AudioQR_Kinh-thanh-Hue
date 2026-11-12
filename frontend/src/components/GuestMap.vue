<script setup>
import { onMounted, onBeforeUnmount, ref, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { addBasemap } from '../map/basemap.js'
import BasemapStatus from './BasemapStatus.vue'
import { messages } from '../guest/messages.js'

const props = defineProps({ places: { type: Array, default: () => [] }, selected: String, disabled: Boolean, cancelLabel: String, overlapLabel: String, labels: { type: Object, default: () => messages.vi } })
const emit = defineEmits(['select', 'overlap'])
const container = ref(null)
const candidates = ref([])
const basemapState = ref('loading')
const canRetryBasemap = ref(false)
let basemap
let map
let layer
let resize
let fittedIds = ''
let mounted = true
let pin
let clickedPoint = null
let candidatePoint = null

function paintPin() {
  if (!clickedPoint) { pin?.remove(); pin = null; return }
  if (!pin) pin = L.marker(clickedPoint.latlng, {
    pane: 'click-pin', interactive: false, keyboard: false,
    icon: L.divIcon({ className: 'map-click-pin', iconSize: [28, 40], iconAnchor: [14, 40],
      html: '<svg viewBox="0 0 28 40" width="28" height="40" aria-hidden="true"><path d="M14 39C11 33 1 23 1 14a13 13 0 0 1 26 0c0 9-10 19-13 25Z" fill="#1875b8" stroke="#fff" stroke-width="2"/><circle cx="14" cy="14" r="5" fill="#fff"/></svg>' }),
  }).addTo(map)
  else pin.setLatLng(clickedPoint.latlng)
  const element = pin.getElement()
  element.setAttribute('role', 'img')
  element.setAttribute('aria-label', props.labels.mapPin)
  element.title = props.labels.mapPin
}

function cancelCandidates() { candidates.value = []; candidatePoint = null }

function inRing(point, ring) {
  let inside = false
  for (let i = 0, j = ring.length - 1; i < ring.length; j = i++) {
    const a = ring[i]
    const b = ring[j]
    const cross = (point[0] - a[0]) * (b[1] - a[1]) - (point[1] - a[1]) * (b[0] - a[0])
    if (Math.abs(cross) < 1e-12 && point[0] >= Math.min(a[0], b[0]) && point[0] <= Math.max(a[0], b[0]) && point[1] >= Math.min(a[1], b[1]) && point[1] <= Math.max(a[1], b[1])) return true
    if ((a[1] > point[1]) !== (b[1] > point[1]) && point[0] < (b[0] - a[0]) * (point[1] - a[1]) / (b[1] - a[1]) + a[0]) inside = !inside
  }
  return inside
}

function contains(polygon, point) {
  const rings = polygon?.type === 'Polygon' ? polygon.coordinates : null
  return rings?.length && inRing(point, rings[0]) && !rings.slice(1).some(ring => inRing(point, ring))
}

function choose(place, point = candidatePoint) {
  if (props.disabled) return
  if (point) { clickedPoint = { placeId: place.id, latlng: point }; paintPin() }
  cancelCandidates()
  emit('select', place)
}

function render() {
  if (!map) return
  if (clickedPoint && !props.places.some(place => place.id === clickedPoint.placeId && contains(place.polygon, [clickedPoint.latlng.lng, clickedPoint.latlng.lat]))) {
    clickedPoint = null
    paintPin()
  }
  if (layer) map.removeLayer(layer)
  layer = L.featureGroup()
  for (const [index, place] of props.places.entries()) {
    if (place.mapSpaceKey !== 'WGS84_LON_LAT' || place.polygon?.type !== 'Polygon') continue
    const selected = place.id === props.selected
    const shape = L.geoJSON(place.polygon, { interactive: false, style: { color: '#970b0b', weight: selected ? 4 : 2, fillColor: '#b43a3a', fillOpacity: selected ? 0.38 : 0.18 } })
    const label = document.createElement('span')
    label.textContent = `${selected ? '✓ ' : ''}${index + 1}`
    label.setAttribute('aria-label', place.name)
    label.title = place.name
    shape.bindTooltip(label, { permanent: true, direction: 'center', className: 'place-label place-number' })
    layer.addLayer(shape)
  }
  layer.addTo(map)
  const ids = props.places.map(place => `${place.id}:${JSON.stringify(place.polygon)}`).join(',')
  if (layer.getLayers().length && fittedIds !== ids) {
    map.fitBounds(layer.getBounds(), { padding: [24, 24], maxZoom: 17, animate: false })
    fittedIds = ids
  }
}

onMounted(() => {
  map = L.map(container.value, { attributionControl: true, scrollWheelZoom: true, zoomSnap: .5 }).setView([16.469, 107.577], 16)
  const pinPane = map.createPane('click-pin')
  pinPane.style.zIndex = '700'
  pinPane.style.pointerEvents = 'none'
  void addBasemap(map, { stillMounted: () => mounted, changed: state => { basemapState.value = state } }).then(controller => {
    if (!mounted) return
    basemap = controller
    canRetryBasemap.value = !!controller.retry
  }).catch(() => { if (mounted) basemapState.value = 'error' })
  // Nền bản đồ không tự tạo vùng; vùng chạm vẫn chỉ lấy từ API.
  map.on('click', event => {
    if (props.disabled || candidates.value.length) return
    const matches = props.places.filter(place => place.mapSpaceKey === 'WGS84_LON_LAT' && contains(place.polygon, [event.latlng.lng, event.latlng.lat]))
    if (matches.length === 1) choose(matches[0], event.latlng)
    else if (matches.length > 1) { candidatePoint = event.latlng; candidates.value = matches }
  })
  resize = new ResizeObserver(() => map.invalidateSize())
  resize.observe(container.value)
  render()
})
watch(() => [props.places, props.selected], render)
watch(() => props.selected, selected => {
  if (selected && clickedPoint && selected !== clickedPoint.placeId) { clickedPoint = null; paintPin() }
})
watch(() => props.labels.mapPin, () => { if (pin) paintPin() })
watch(() => candidates.value.length > 0, value => emit('overlap', value))
watch(() => props.disabled, disabled => {
  if (!map) return
  for (const handler of ['dragging', 'touchZoom', 'doubleClickZoom', 'scrollWheelZoom', 'boxZoom', 'keyboard']) {
    if (disabled) map[handler]?.disable()
    else map[handler]?.enable()
  }
  if (disabled) cancelCandidates()
})
onBeforeUnmount(() => { mounted = false; resize?.disconnect(); basemap?.dispose(); map?.remove() })
</script>

<template>
  <div class="map-wrapper geographic-map">
    <div ref="container" class="map-canvas" :inert="disabled || candidates.length > 0" />
    <div v-if="candidates.length" class="map-shade">
      <section role="dialog" aria-modal="true" :aria-label="overlapLabel" class="overlap-box">
        <p class="overlap-title">{{ overlapLabel }}</p>
        <div class="overlap-list">
          <button v-for="place in candidates" :key="place.id" @click="choose(place)">{{ place.name }}</button>
        </div>
        <button class="quiet" @click="cancelCandidates">{{ cancelLabel }}</button>
      </section>
    </div>
    <slot />
  </div>
  <BasemapStatus :state="basemapState" :labels="labels" :can-retry="canRetryBasemap" :disabled="disabled || candidates.length > 0" :inert="disabled || candidates.length > 0" @retry="basemap?.retry()" />
  <ol v-if="places.length" class="map-legend" :aria-label="labels.map">
    <li v-for="(place,index) in places" :key="place.id" :class="{ 'legend-selected': place.id === selected }"><span>{{ index + 1 }}{{ place.id === selected ? ' ✓' : '' }}</span>{{ place.name }}</li>
  </ol>
</template>
