import assert from 'node:assert/strict'
import { readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { createHash, randomBytes, randomUUID } from 'node:crypto'

// API smoke trên runtime riêng đã rà; tạo đúng một phiên/lượt kiểm, không sửa nội dung.
const [runtimeDirectory, output] = process.argv.slice(2)
assert(runtimeDirectory && output, 'Usage: post-deploy-check.mjs private-runtime new-report.json')
const runtime = JSON.parse((await readFile(join(runtimeDirectory, 'runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
assert.match(runtime.project, /^hueqr-(rehearsal|ops-check)-[a-f0-9]{8}$/)
const origin = new URL(runtime.url)
assert(origin.protocol === 'http:' && ['localhost', '127.0.0.1'].includes(origin.hostname))
assert(runtime.adminLogin && runtime.adminPassword, 'Reviewed Admin credential required in private runtime')
const hash = value => createHash('sha256').update(value).digest('hex')
const cookies = new Map()
const report = { kind: 'post-deploy-api-check', startedAt: new Date().toISOString(), status: 'RUNNING',
  project: runtime.project, browserOrDeviceVerified: false, providerRequests: 0, checks: [] }
let csrf, session, credential, stage = 'admin', ended = false
async function request(path, { admin = false, method = 'GET', body, headers = {} } = {}) {
  const response = await fetch(new URL(path, origin), { method, redirect: 'error', signal: AbortSignal.timeout(15000),
    headers: { ...(body === undefined ? {} : { 'Content-Type': 'application/json' }),
      ...(admin ? { Cookie: [...cookies].map(([k, v]) => `${k}=${v}`).join('; '), ...(csrf ? { [csrf.headerName]: csrf.token } : {}) } : {}), ...headers },
    body: body === undefined ? undefined : JSON.stringify(body) })
  if (admin) for (const cookie of response.headers.getSetCookie()) {
    const pair = cookie.split(';', 1)[0], split = pair.indexOf('=')
    cookies.set(pair.slice(0, split), pair.slice(split + 1))
  }
  return response
}
async function json(path, options, expected = 200) {
  const response = await request(path, options); assert.equal(response.status, expected, path)
  assert.match(response.headers.get('cache-control') || '', /no-store/)
  return response.json()
}
try {
  csrf = await json('/api/v1/auth/csrf', { admin: true })
  await json('/api/v1/auth/login', { admin: true, method: 'POST', body: { loginName: runtime.adminLogin, password: runtime.adminPassword } })
  csrf = await json('/api/v1/auth/csrf', { admin: true })
  await json('/api/v1/admin/session', { admin: true })
  const places = await json('/api/v1/admin/places', { admin: true })
  const place = places.find(p => p.visible && p.activeRegionRevisionId && p.canShow)
  assert(place, 'No reviewed visible place')
  const region = await json(`/api/v1/admin/places/${place.placeId}/region-revisions/${place.activeRegionRevisionId}`, { admin: true })
  assert(region)
  const content = await json(`/api/v1/admin/places/${place.placeId}/content/vi`, { admin: true })
  assert(content.appliedPair && content.appliedPair.missingConditions.length === 0)
  report.checks.push('admin-login-session', 'applied-region-and-pair')
  stage = 'guest'
  credential = randomBytes(32).toString('base64url')
  const headers = { 'X-Guest-Credential': credential }
  const ticket = await json('/api/v1/guest/start-ticket', { method: 'POST', headers }, 201)
  const start = { method: 'POST', headers: { ...headers, 'Idempotency-Key': ticket.requestId },
    body: { startTicket: ticket.startTicket, displayName: 'Kiểm vận hành', initialLanguage: 'vi' } }
  session = await json('/api/v1/guest/sessions', start, 201)
  const replay = await json('/api/v1/guest/sessions', start)
  assert.equal(replay.sessionId, session.sessionId); assert.equal(replay.expiresAt, session.expiresAt)
  const base = `/api/v1/guest/sessions/${session.sessionId}`
  const documentNonce = randomUUID()
  await json(base + '/resume', { method: 'POST', headers, body: { documentNonce, previousDocumentNonce: null } })
  const map = await json(base + '/map', { headers })
  assert(map.places.some(p => p.id === place.placeId))
  const opened = await json(base + '/openings', { method: 'POST', headers: { ...headers, 'Idempotency-Key': randomUUID() },
    body: { placeId: place.placeId, language: 'vi', documentNonce, selectionSeq: 1 } })
  assert.equal(opened.text.hash, content.appliedPair.textHash)
  assert.equal(hash(Buffer.from(opened.text.body)), opened.text.hash)
  assert.equal(opened.audio.checksum, content.appliedPair.mp3Checksum)
  const audioPath = base + `/content-grants/${opened.grant.id}/audio`
  const audio = await request(audioPath, { headers })
  assert.equal(audio.status, 200); assert.match(audio.headers.get('content-type') || '', /audio\/mpeg/)
  const bytes = Buffer.from(await audio.arrayBuffer())
  assert.equal(bytes.length, opened.audio.byteSize); assert.equal(hash(bytes), opened.audio.checksum)
  assert.equal(audio.headers.get('x-audio-checksum'), opened.audio.checksum)
  const ackPath = base + `/openings/${opened.openingId}/view-ack`, ackBody = { contentGrantId: opened.grant.id }
  const ack = await json(ackPath, { method: 'POST', headers, body: ackBody }, 201)
  const ackAgain = await json(ackPath, { method: 'POST', headers, body: ackBody })
  assert.equal(ack.viewId, ackAgain.viewId)
  report.checks.push('guest-start-replay', 'map-text', 'full-mp3-byte-hash', 'view-ack-replay')
  stage = 'errors'
  const denied = await request(audioPath, { headers: { 'X-Guest-Credential': randomBytes(32).toString('base64url') } })
  assert(denied.status >= 400 && denied.status < 500); await denied.arrayBuffer()
  const ranged = await request(audioPath, { headers: { ...headers, Range: 'bytes=0-15' } })
  assert(ranged.status >= 400 && ranged.status < 500); assert.equal((await ranged.json()).code, 'FULL_DOWNLOAD_REQUIRED')
  const endedSession = await json(base + '/end', { method: 'POST', headers: { ...headers, 'Idempotency-Key': randomUUID() } })
  assert.equal(endedSession.state, 'ENDED'); ended = true
  const afterEnd = await request(audioPath, { headers })
  assert(afterEnd.status >= 400 && afterEnd.status < 500); await afterEnd.arrayBuffer()
  report.checks.push('wrong-credential-denied', 'range-denied', 'ended-session-audio-denied')
  report.audioBytes = bytes.length; report.audioSha256 = opened.audio.checksum
  report.testSessionsCreated = 1; report.testViewsCreated = 1
  report.status = 'PASSED'
} catch (error) {
  report.status = 'FAILED'; report.stage = stage; report.errorName = error.name; process.exitCode = 1
} finally {
  if (session && !ended) {
    try { await request(`/api/v1/guest/sessions/${session.sessionId}/end`, { method: 'POST', headers: {
      'X-Guest-Credential': credential, 'Idempotency-Key': randomUUID() } }) } catch {}
  }
  try { await request('/api/v1/auth/logout', { admin: true, method: 'POST' }) } catch {}
  report.finishedAt = new Date().toISOString()
  await writeFile(output, JSON.stringify(report, null, 2), { flag: 'wx' })
}
console.log(JSON.stringify(report))
