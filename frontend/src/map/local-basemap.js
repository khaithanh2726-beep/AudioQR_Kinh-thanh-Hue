import L from 'leaflet'

export const mapPreviewEnabled = import.meta.env.VITE_MAP_PREVIEW === 'true'

export async function addLocalBasemap(map, stillMounted = () => true) {
  const { default: data } = await import('../assets/hue-basemap.json')
  if (!stillMounted()) return
  const pane = map.createPane('local-basemap')
  pane.style.zIndex = '200'
  pane.style.pointerEvents = 'none'
  L.geoJSON(data, { pane: 'local-basemap', interactive: false, style(feature) {
    const kind = feature.properties.kind
    if (kind === 'water') return { color: '#a6ccd2', fillColor: '#c9e1e4', fillOpacity: .85, weight: 1 }
    if (kind === 'building') return { color: '#ad9f89', fillColor: '#d8cfbd', fillOpacity: .9, weight: .7 }
    if (kind === 'wall') return { color: '#79836d', weight: 2, fillOpacity: 0 }
    if (kind === 'road') return { color: feature.properties.roadType === 'footway' ? '#fffdf5' : '#d6c9ae', weight: feature.properties.roadType === 'footway' ? 2 : 4, fillOpacity: 0 }
    return { color: '#a1aa91', weight: 1, fillOpacity: 0 }
  } }).addTo(map)
  map.attributionControl?.setPrefix(false)
  map.attributionControl?.addAttribution('© <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noopener noreferrer">OpenStreetMap contributors</a> · trích 31/10/2026')
}
