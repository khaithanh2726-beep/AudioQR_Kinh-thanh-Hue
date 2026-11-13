import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { readFile, writeFile } from 'node:fs/promises'
import { resolve, join } from 'node:path'
import { createHash, randomBytes, randomUUID } from 'node:crypto'

// Chỉ nâng bản restore riêng đã được kiểm, không nhận runtime phục vụ gốc.
const [directoryArg, candidate] = process.argv.slice(2)
assert(directoryArg && candidate, 'Usage: upgrade-v13-rehearsal.mjs private-v13-restore candidate-image')
assert.match(candidate, /^hueqr-local:[A-Za-z0-9_.-]+$/)
const directory = resolve(directoryArg), root = process.cwd()
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
const proof = JSON.parse(await readFile(join(directory, 'restore-result.json'), 'utf8'))
assert.match(runtime.project, /^hueqr-ops-check-[a-f0-9]{8}$/)
assert.equal(proof.status, 'RESTORE_V13_VERIFIED_PRIVATE')
assert.equal(proof.public, false)
const docker = (args, input, env = process.env) => execFileSync('docker', args, {
  input, env, encoding: 'utf8', windowsHide: true, timeout: 120000, maxBuffer: 16 * 1024 * 1024, stdio: ['pipe', 'pipe', 'pipe'],
}).trim()
const sql = query => docker(['exec', '-i', runtime.database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr --default-character-set=utf8mb4 -N -B hueqr'], query)
const originalConfig = await readFile(runtime.configPath, 'utf8')
const settings = Object.fromEntries(originalConfig.trim().split('\n').map(line => { const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)] }))
for (const name of ['HUEQR_TTS_ENABLED', 'HUEQR_TTS_LOCAL_ENABLED', 'HUEQR_CLEANUP_ENABLED', 'HUEQR_RETENTION_ENABLED']) assert.equal(settings[name], 'false')
const compose = (args, config = settings) => docker(['compose', '-p', runtime.project, '--env-file', runtime.configPath,
  '-f', join(root, 'deploy/compose.yaml'), '-f', join(directory, 'limits.yaml'), ...args], undefined, { ...process.env, ...config })
const base = new URL(runtime.url)
assert(['localhost', '127.0.0.1'].includes(base.hostname) && base.protocol === 'http:')
async function ready(expected) {
  for (let i = 0; i < 120; i++) {
    try { const res = await fetch(new URL('/api/v1/auth/csrf', base), { signal: AbortSignal.timeout(2000) }); if (res.status === expected) return } catch {}
    await new Promise(done => setTimeout(done, 1000))
  }
  throw Error('Private application not ready')
}
const credential = randomBytes(32).toString('base64url')
async function guest(path, method = 'GET', body, key) {
  const response = await fetch(new URL('/api/v1/guest' + path, base), { method,
    headers: { 'X-Guest-Credential': credential, ...(body ? { 'Content-Type': 'application/json' } : {}), ...(key ? { 'Idempotency-Key': key } : {}) },
    body: body ? JSON.stringify(body) : undefined, redirect: 'error', signal: AbortSignal.timeout(15000) })
  assert(response.ok, 'Guest API status ' + response.status)
  return response.json()
}
const inventorySql = `SELECT (SELECT COUNT(*) FROM place),(SELECT COUNT(*) FROM content_slot),(SELECT COUNT(*) FROM text_revision),
  (SELECT COUNT(*) FROM audio_revision),(SELECT COUNT(*) FROM content_pair),(SELECT COUNT(*) FROM file_asset);
  SELECT LOWER(HEX(id)),polygon FROM region_revision ORDER BY id;
  SELECT storage_key,LOWER(HEX(checksum)),byte_size FROM file_asset ORDER BY storage_key;`
const schemaSql = 'SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;'
const run = args => execFileSync(process.execPath, args, { cwd: root, windowsHide: true, encoding: 'utf8', timeout: 240000,
  maxBuffer: 8 * 1024 * 1024, stdio: ['ignore', 'pipe', 'pipe'] })
const report = { startedAt: new Date().toISOString(), project: runtime.project, sourceImage: runtime.imageId,
  candidate, providerRequests: 0, servingRuntimeModified: false, status: 'RUNNING' }
let stage = 'source'
try {
  compose(['up', '-d', '--no-build', '--pull', 'never', 'database'])
  let dbReady = false
  for (let i = 0; i < 90; i++) { try { dbReady = sql('SELECT 1;') === '1' } catch {} if (dbReady) break; await new Promise(r => setTimeout(r, 1000)) }
  assert(dbReady)
  const baselineSchema = sql(schemaSql)
  assert.equal(baselineSchema.split('\n').at(-1).split('\t')[0], '13')
  const inventory = sql(inventorySql)
  compose(['up', '-d', '--no-build', '--pull', 'never', 'application'])
  await ready(200)
  const ticket = await guest('/start-ticket', 'POST')
  const session = await guest('/sessions', 'POST', { startTicket: ticket.startTicket, displayName: 'Upgrade rehearsal', initialLanguage: 'vi' }, ticket.requestId)
  const documentNonce = randomUUID()
  await guest(`/sessions/${session.sessionId}/resume`, 'POST', { documentNonce, previousDocumentNonce: null })
  const beforeMap = await guest(`/sessions/${session.sessionId}/map`)
  assert(beforeMap.places.length > 0, 'Need approved serving content in private restore')
  stage = 'migration'
  compose(['stop', 'application'])
  const next = { ...settings, HUEQR_IMAGE: candidate }
  await writeFile(runtime.configPath, originalConfig.replace(/^HUEQR_IMAGE=.*$/m, 'HUEQR_IMAGE=' + candidate))
  compose(['up', '-d', '--no-build', '--pull', 'never', 'application'], next)
  await ready(200)
  const schema = sql(schemaSql)
  assert(schema.startsWith(baselineSchema + '\n'))
  assert.equal(schema.split('\n').length, 14)
  assert.equal(schema.split('\n').at(-1).split('\t')[0], '14')
  assert.equal(sql(inventorySql), inventory, 'Business data/regions/files changed during migration')
  const afterMap = await guest(`/sessions/${session.sessionId}/map`)
  assert.deepEqual(afterMap.places, beforeMap.places)
  const status = await guest(`/sessions/${session.sessionId}`)
  assert.equal(status.state, 'ACTIVE')
  assert.equal(status.startedAt, session.startedAt)
  assert.equal(status.expiresAt, session.expiresAt)
  stage = 'guest-audio'
  const content = await guest(`/sessions/${session.sessionId}/openings`, 'POST', {
    placeId: afterMap.places[0].id, language: 'vi', documentNonce, selectionSeq: 1,
  }, randomUUID())
  assert(content.text?.body && content.audio?.checksum && content.grant?.id)
  const response = await fetch(new URL(`/api/v1/guest/sessions/${session.sessionId}/content-grants/${content.grant.id}/audio`, base), {
    headers: { 'X-Guest-Credential': credential }, signal: AbortSignal.timeout(15000) })
  assert.equal(response.status, 200)
  assert.equal(response.headers.get('content-type')?.split(';')[0], 'audio/mpeg')
  const bytes = Buffer.from(await response.arrayBuffer())
  assert.equal(bytes.length, content.audio.byteSize)
  assert.equal(createHash('sha256').update(bytes).digest('hex'), content.audio.checksum)
  await guest(`/sessions/${session.sessionId}/openings/${content.openingId}/view-ack`, 'POST', { contentGrantId: content.grant.id })
  await guest(`/sessions/${session.sessionId}/end`, 'POST', undefined, randomUUID())
  stage = 'gate'
  const owner = randomUUID()
  run(['deploy/maintenance.mjs', directory, 'quiesce', owner])
  await ready(503)
  run(['deploy/maintenance.mjs', directory, 'freeze', owner])
  run(['deploy/maintenance.mjs', directory, 'resume', owner])
  await ready(200)
  assert.equal(sql(inventorySql), inventory)
  const application = JSON.parse(docker(['inspect', runtime.application]))[0]
  const actualEnv = Object.fromEntries(application.Config.Env.map(line => { const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)] }))
  assert.equal(actualEnv.HUEQR_SERVICE_EPOCH, runtime.serviceEpoch)
  assert.equal(actualEnv.HUEQR_GUEST_SIGNING_KEY_BASE64, runtime.signingKeyBase64)
  await writeFile(join(directory, 'runtime.json'), JSON.stringify({ ...runtime, image: candidate, imageId: application.Image }, null, 2))
  report.status = 'V13_TO_V14_PRIVATE_REHEARSAL_PASSED'
  Object.assign(report, { schemaBefore: 13, schemaAfter: 14, inventoryUnchanged: true, regionHashesPreserved: true,
    oldSessionStillValid: true, epochAndSigningKeyPreserved: true, approvedViTextReadable: true,
    mp3DownloadedAndHashVerified: true, mp3Bytes: bytes.length, browserPlaybackVerified: false,
    gateCloseFreezeResumeVerified: true, candidateImageId: application.Image })
} catch (error) {
  report.status = 'FAILED'; report.stage = stage; report.error = error.name
  if (typeof error.actual === 'number') report.actualStatus = error.actual
  process.exitCode = 1
} finally {
  // Sau migration không tự quay V13 trên schema V14; giữ bản thử dừng để đối chiếu.
  try { compose(['stop']) } catch { report.stopFailed = true; process.exitCode = 1 }
  report.finishedAt = new Date().toISOString()
  await writeFile(join(directory, 'upgrade-result.json'), JSON.stringify(report, null, 2))
  console.log(JSON.stringify(report))
}
