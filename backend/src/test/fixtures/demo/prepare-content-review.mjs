import assert from 'node:assert/strict'
import { readFile, writeFile } from 'node:fs/promises'
import { createHash } from 'node:crypto'
import { pathToFileURL } from 'node:url'
import { resolve } from 'node:path'

const root = process.cwd()
const hash = bytes => createHash('sha256').update(bytes).digest('hex')
const patch = JSON.parse(await readFile('docs/05-noi-dung/dieu-chinh-demo-v02.json', 'utf8'))
const regions = JSON.parse(await readFile('frontend/src/assets/hue-proposed-regions.json', 'utf8'))
const osm = JSON.parse(await readFile('docs/04-giao-dien/design-assets/hue-osm-source.json', 'utf8'))
const { prepareRegion } = await import(pathToFileURL(resolve(root, 'frontend/src/admin/geometry.js')))
const keys = ['ngo-mon', 'thai-hoa', 'tu-cam-thanh', 'cuu-dinh', 'dien-tho']
const articles = [], textSources = []
for (const language of ['vi', 'en', 'fr', 'ru']) {
  const file = `docs/05-noi-dung/thuyet-minh-${language}-v01.md`
  const source = await readFile(file, 'utf8')
  const sections = [...source.matchAll(/<a id="([a-z-]+)"><\/a>\s*## \d+\. [^\r\n]+\r?\n\r?\n([\s\S]*?)(?=\r?\n<a id=|$)/g)]
  assert.equal(sections.length, 5)
  textSources.push({ file, sha256: hash(Buffer.from(source)) })
  for (const section of sections) {
    const placeKey = section[1], body = patch.overrides[placeKey]?.[language] || section[2].trim()
    assert(keys.includes(placeKey) && body.length > 100)
    articles.push({ placeKey, placeName: regions.places[keys.indexOf(placeKey)].name, language, body,
      textSha256: hash(Buffer.from(body)), changedFromV01: body !== section[2].trim(),
      existingApprovedNgoMonVi: placeKey === 'ngo-mon' && language === 'vi',
      textReview: 'REVIEWED_FOR_COURSE_DEMO', audioReview: 'PENDING_ACTUAL_AUDIO' })
  }
}
function contains(point, ring) {
  let inside = false
  for (let i = 0, j = ring.length - 1; i < ring.length; j = i++) {
    const [xi, yi] = ring[i], [xj, yj] = ring[j]
    if ((yi > point[1]) !== (yj > point[1]) && point[0] < (xj - xi) * (point[1] - yi) / (yj - yi) + xi) inside = !inside
  }
  return inside
}
const checkedRegions = []
for (const place of regions.places) {
  const prepared = await prepareRegion({ closed: true, vertices: place.polygon.coordinates[0].slice(0, -1) })
  assert(contains(place.anchor, prepared.polygon.coordinates[0]), 'Anchor outside touch region')
  const localElements = osm.elements.filter(element => element.tags?.name && (
    element.tags.name === place.name || place.id === 'MAP-Z04' && /đỉnh|urn/i.test(element.tags.name)))
  checkedRegions.push({ placeKey: keys[place.number - 1], name: place.name, anchor: place.anchor, ...prepared,
    anchorInside: true, localOsmReferences: localElements.map(e => ({ type: e.type, id: e.id, name: e.tags.name })),
    reviewScope: 'TOUCH_SELECTION_FOR_COURSE_DEMO_NOT_SURVEYED_HERITAGE_BOUNDARY',
    alreadyApprovedNgoMon: place.number === 1 })
}
const result = { preparedAt: new Date().toISOString(), scope: 'course-demo',
  reviewMethod: 'source and language review under operator delegation; audio approval requires actual output checks',
  limitations: ['Not expert historical or native-language certification', 'Touch regions are not surveyed or legal boundaries'],
  sources: patch.sources, originalTextFiles: textSources,
  originalRegionFile: { file: regions.source.path, sha256: regions.source.sha256 }, articles, regions: checkedRegions }
await writeFile('docs/05-noi-dung/demo-content-v02.json', JSON.stringify(result, null, 2))
console.log(JSON.stringify({ articles: articles.length, changed: articles.filter(a => a.changedFromV01).length,
  pendingNewAudio: articles.filter(a => !a.existingApprovedNgoMonVi).length, regions: checkedRegions.length }))
