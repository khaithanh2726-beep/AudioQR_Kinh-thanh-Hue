export const OSM_TILE_URL = 'https://tile.openstreetmap.org/{z}/{x}/{y}.png'

export function basemapConfig(env = {}) {
  const mode = env.VITE_MAP_BASEMAP || 'osm'
  if (!['osm', 'offline'].includes(mode)) throw new Error('INVALID_BASEMAP_MODE')
  if (mode === 'offline') return { mode }
  const url = env.VITE_OSM_TILE_URL || OSM_TILE_URL
  const parsed = new URL(url)
  if (parsed.protocol !== 'https:' || parsed.username || parsed.password || parsed.search || parsed.hash
      || !['{z}', '{x}', '{y}'].every(part => url.includes(part))) throw new Error('INVALID_TILE_URL')
  return { mode, url }
}
