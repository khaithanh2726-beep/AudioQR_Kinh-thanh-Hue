import L from 'leaflet'
import { basemapConfig } from './config.js'
import { addLocalBasemap } from './local-basemap.js'

export async function addBasemap(map, { changed = () => {}, stillMounted = () => true } = {}) {
  const config = basemapConfig(import.meta.env)
  const report = state => { if (stillMounted()) changed(state) }
  report('loading')
  if (config.mode === 'offline') {
    await addLocalBasemap(map, stillMounted)
    report('ready')
    return { dispose() {}, retry: null }
  }

  const failed = new Set()
  const layer = L.tileLayer(config.url, {
    maxZoom: 19, maxNativeZoom: 19, updateWhenIdle: true, updateWhenZooming: false, keepBuffer: 0,
    referrerPolicy: 'strict-origin-when-cross-origin',
    attribution: '© <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noopener noreferrer">OpenStreetMap contributors</a>',
  })
  const settled = () => report(failed.size ? 'error' : 'ready')
  layer.on('loading', () => report('loading'))
  layer.on('tileerror', event => { failed.add(event.tile); report('error') })
  layer.on('tileload', event => { failed.delete(event.tile) })
  layer.on('tileunload', event => {
    failed.delete(event.tile)
    if (!layer.isLoading()) settled()
  })
  layer.on('load', settled)
  map.attributionControl?.setPrefix(false)
  layer.addTo(map)
  return {
    retry() { failed.clear(); report('loading'); layer.redraw() },
    dispose() { layer.off(); map.removeLayer(layer) },
  }
}
