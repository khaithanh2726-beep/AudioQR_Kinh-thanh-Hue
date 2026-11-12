import { readFile, writeFile, mkdir } from 'node:fs/promises'
import { createHash } from 'node:crypto'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const root = resolve(dirname(fileURLToPath(import.meta.url)), '../..')
const sourcePath = 'docs/04-giao-dien/design-assets/hue-osm-source.json'
const bytes = await readFile(resolve(root, sourcePath))
const source = JSON.parse(bytes.toString('utf8'))
const features = []
for (const element of source.elements) {
  if (element.type !== 'way' || !Array.isArray(element.geometry) || element.geometry.length < 2) continue
  const coordinates = element.geometry.map(point => [point.lon, point.lat])
  if (coordinates.some(point => point.some(value => !Number.isFinite(value)))) throw new Error('Tọa độ nguồn không hợp lệ')
  const tags = element.tags || {}
  const closed = coordinates.length >= 4 && coordinates[0].every((value, index) => value === coordinates.at(-1)[index])
  const water = tags.natural === 'water' || !!tags.water || !!tags.waterway
  const building = !!tags.building
  const area = closed && (water || building || tags.area === 'yes')
  const kind = water ? 'water' : building ? 'building' : tags.barrier ? 'wall' : tags.highway ? 'road' : 'landmark'
  features.push({ type: 'Feature', id: 'way/' + element.id,
    properties: { kind, roadType: tags.highway || null },
    geometry: area ? { type: 'Polygon', coordinates: [coordinates] } : { type: 'LineString', coordinates } })
}
const output = resolve(root, 'frontend/src/assets/hue-basemap.json')
await mkdir(dirname(output), { recursive: true })
await writeFile(output, JSON.stringify({ type: 'FeatureCollection',
  source: { path: sourcePath, sha256: createHash('sha256').update(bytes).digest('hex'),
    license: source.license, licenseUrl: source.license_url, snapshot: source.timestamp_osm_base,
    approval: 'preview-only; not verified heritage boundaries' }, features }) + '\n')
const regionsPath = 'docs/04-giao-dien/design-assets/hue-touch-regions-proposal.geojson'
const regionBytes = await readFile(resolve(root, regionsPath))
const regions = JSON.parse(regionBytes.toString('utf8'))
await writeFile(resolve(root, 'frontend/src/assets/hue-proposed-regions.json'), JSON.stringify({
  source: { path: regionsPath, sha256: createHash('sha256').update(regionBytes).digest('hex'), approval: 'pending' },
  places: regions.features.map(feature => ({ id: feature.properties.id, name: feature.properties.name,
    number: feature.properties.number, anchor: feature.properties.anchor,
    mapSpaceKey: 'WGS84_LON_LAT', polygon: feature.geometry })) }) + '\n')
console.log(JSON.stringify({ features: features.length, output: 'frontend/src/assets/hue-basemap.json' }))
