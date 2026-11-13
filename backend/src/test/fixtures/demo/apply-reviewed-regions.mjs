import assert from 'node:assert/strict'
import { readFile, writeFile } from 'node:fs/promises'
import { resolve, join } from 'node:path'
import { pathToFileURL } from 'node:url'
import { createHash } from 'node:crypto'
import { openAdmin } from './content-admin-session.mjs'
import { commandsFor } from './content-admin-commands.mjs'

const directory = resolve(process.argv[2])
const reviewFile = 'docs/05-noi-dung/duyet-vung-demo-v02.json'
const reviewBytes = await readFile(reviewFile), review = JSON.parse(reviewBytes)
assert.equal(review.status, 'APPROVED_FOR_COURSE_DEMO')
assert.equal(review.regions.length, 4)
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
const ledgerFile = join(directory, 'region-application-ledger.json')
let ledger
try { ledger = JSON.parse(await readFile(ledgerFile, 'utf8')) } catch (error) { if (error.code !== 'ENOENT') throw error }
ledger ||= { startedAt: new Date().toISOString(), reviewFile,
  reviewSha256: createHash('sha256').update(reviewBytes).digest('hex'), project: runtime.project, regions: [] }
assert.equal(ledger.project, runtime.project)
assert.equal(ledger.reviewSha256, createHash('sha256').update(reviewBytes).digest('hex'))
const persist = () => writeFile(ledgerFile, JSON.stringify(ledger, null, 2))
const session = await openAdmin(runtime)
try {
  const command = await commandsFor(session, ledger, persist)
  const places = (await session.read('/admin/places')).data
  const { prepareRegion } = await import(pathToFileURL(resolve('frontend/src/admin/geometry.js')))
  for (const region of review.regions) {
    const place = places.find(p => p.displayName === region.name)
    assert(place)
    const path = '/admin/places/' + place.placeId
    let detail = await session.read(path)
    const prepared = await prepareRegion({ closed: true, vertices: region.polygon.coordinates[0].slice(0, -1) })
    assert.equal(prepared.geometryHash, region.geometryHash)
    const saved = await command('save/' + region.placeKey, path + '/region-revisions', 'POST', prepared, detail.etag)
    const reloaded = (await session.read(path + '/region-revisions/' + saved.regionRevisionId)).data
    const checked = await prepareRegion({ closed: true, vertices: reloaded.polygon.coordinates[0].slice(0, -1) })
    assert.equal(checked.geometryHash, region.geometryHash)
    assert.equal(reloaded.geometryHash, region.geometryHash)
    detail = await session.read(path)
    await command('apply/' + region.placeKey, path + '/active-region', 'PUT',
      { savedRevisionId: saved.regionRevisionId, previewedGeometryHash: checked.geometryHash }, detail.etag)
    const final = (await session.read(path)).data
    assert.equal(final.activeRegionRevisionId, saved.regionRevisionId)
    const entry = { placeKey: region.placeKey, placeId: place.placeId, regionRevisionId: saved.regionRevisionId,
      geometryHash: checked.geometryHash, reloadedAndRecomputed: true, status: 'APPLIED_IN_PRIVATE_STAGE' }
    const index = ledger.regions.findIndex(r => r.placeKey === region.placeKey)
    if (index < 0) ledger.regions.push(entry); else ledger.regions[index] = entry
    await persist(); console.log(JSON.stringify(entry))
  }
  ledger.status = 'FOUR_REGIONS_APPLIED_IN_PRIVATE_STAGE'; await persist()
} finally { await session.browser.close() }
