import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { readFile, writeFile } from 'node:fs/promises'
import { resolve, join } from 'node:path'
import { randomUUID, randomBytes, createHash } from 'node:crypto'

const [sourceArg, backupArg, restoreArg, image] = process.argv.slice(2)
assert(sourceArg && backupArg && restoreArg && image)
const root = process.cwd(), source = resolve(sourceArg), backup = resolve(backupArg), restore = resolve(restoreArg)
const runtime = JSON.parse(await readFile(join(source, 'runtime.json'), 'utf8'))
assert.match(runtime.project, /^hueqr-rehearsal-[a-f0-9]{8}$/)
assert.equal(runtime.image, image)
const prep = JSON.parse(await readFile(join(source, 'preparation.json'), 'utf8'))
assert.equal(prep.counts, '5\t20\t1\t1\t0\t0')
assert.equal(prep.public, false)
const docker = (args, input) => execFileSync('docker', args, { input, encoding: 'utf8', windowsHide: true,
  maxBuffer: 8 * 1024 * 1024, timeout: 120000, stdio: ['pipe', 'pipe', 'pipe'] })
const sql = (db, statement) => docker(['exec', '-i', db, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql --default-character-set=utf8mb4 -u hueqr -N -B hueqr'], statement).trim()
const run = args => execFileSync(process.execPath, args, { cwd: root, encoding: 'utf8', windowsHide: true,
  maxBuffer: 8 * 1024 * 1024, timeout: 600000, stdio: ['pipe', 'pipe', 'pipe'] }).trim()
const maintenance = (action, owner = token, dest) => run(['deploy/maintenance.mjs', source, action, owner, ...(dest ? [dest] : [])])
const token = randomUUID(), fixture = randomUUID()
async function startGuest(url, credential, key, startTicket) {
  if (!startTicket) {
    const issued = await fetch(new URL('/api/v1/guest/start-ticket', url), { method: 'POST', headers: { 'X-Guest-Credential': credential } })
    assert.equal(issued.status, 201)
    const ticket = await issued.json()
    startTicket = ticket.startTicket
    key = ticket.requestId
  }
  const response = await fetch(new URL('/api/v1/guest/sessions', url), { method: 'POST', headers: {
    'Content-Type': 'application/json', 'X-Guest-Credential': credential, 'Idempotency-Key': key,
  }, body: JSON.stringify({ startTicket, displayName: 'Operations recovery fixture', initialLanguage: 'vi' }) })
  return { response, key, startTicket }
}
const report = { startedAt: new Date().toISOString(), sourceProject: runtime.project, status: 'RUNNING',
  providerRequests: 0, originalRehearsalModified: false }
let restored
let stage = 'guard'
try {
  const originalConfigHash = createHash('sha256').update(await readFile(runtime.configPath)).digest('hex')
  assert.equal(sql(runtime.database, 'SELECT mode FROM ops_guard WHERE id=1;'), 'RUNNING')
  let sourceReady = false
  for (let i = 0; i < 90; i++) {
    try { sourceReady = (await fetch(new URL('/api/v1/auth/csrf', runtime.url))).status === 200 } catch {}
    if (sourceReady) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(sourceReady, 'Test source must be ready before creating the fixture session')
  const credential = randomBytes(32).toString('base64url')
  const accepted = await startGuest(runtime.url, credential)
  assert.equal(accepted.response.status, 201)
  const session = await accepted.response.json()
  await writeFile(join(source, 'rights-private.json'), JSON.stringify({ credential, key: accepted.key,
    startTicket: accepted.startTicket, session }, null, 2))
  sql(runtime.database, `INSERT INTO ops_activity(id,instance_id,kind,started_at) VALUES('${fixture}','${fixture}','HTTP','2000-01-01');`)
  maintenance('quiesce')
  assert.equal((await fetch(new URL('/api/v1/auth/csrf', runtime.url))).status, 503)
  assert.throws(() => maintenance('freeze'))
  assert.equal(JSON.parse(docker(['inspect', runtime.application]))[0].State.Running, true)
  assert.throws(() => maintenance('resume', randomUUID()))
  docker(['exec', runtime.database, 'mkdir', '/tmp/hueqr-operations-lock'])
  try {
    assert.throws(() => maintenance('resume'))
    assert.equal(sql(runtime.database, 'SELECT mode FROM ops_guard WHERE id=1;'), 'QUIESCING')
  } finally { docker(['exec', runtime.database, 'rmdir', '/tmp/hueqr-operations-lock']) }
  assert.equal(sql(runtime.database, 'SELECT COUNT(*) FROM ops_activity;'), '1')
  // Chỉ tháo lease fixture do chính ca kiểm tạo, không xóa claim/lease thực theo timeout.
  sql(runtime.database, `DELETE FROM ops_activity WHERE id='${fixture}';`)
  maintenance('freeze')
  report.guard = { blocksApi: true, blocksFreezeWithActivity: true, rejectsWrongOwner: true, noTimeoutReap: true }
  stage = 'backup'
  maintenance('backup', token, backup)
  const manifest = JSON.parse(await readFile(join(backup, 'manifest.json'), 'utf8'))
  assert.equal(manifest.status, 'COMPLETE')
  assert.equal(manifest.files.length, 2)
  maintenance('resume')
  assert.equal((await fetch(new URL('/api/v1/auth/csrf', runtime.url))).status, 200)
  assert.equal(createHash('sha256').update(await readFile(runtime.configPath)).digest('hex'), originalConfigHash)
  const continued = await fetch(new URL(`/api/v1/guest/sessions/${session.sessionId}`, runtime.url), { headers: { 'X-Guest-Credential': credential } })
  assert.equal(continued.status, 200)
  const afterBackup = await continued.json()
  for (const field of ['sessionId', 'startedAt', 'expiresAt', 'endedAt']) assert.equal(afterBackup[field], session[field])
  report.backup = { status: 'COMPLETE', files: 2, reopened: true, runtimeKeysAndEpochPreserved: true }
  report.backup.guestSessionAndOriginalDeadlinePreserved = true
  stage = 'restore'
  run(['deploy/restore-backup.mjs', backup, restore, image])
  restored = JSON.parse(await readFile(join(restore, 'runtime.json'), 'utf8'))
  const proof = JSON.parse(await readFile(join(restore, 'restore-result.json'), 'utf8'))
  assert.equal(proof.status, 'RESTORE_VERIFIED_CLOSED')
  assert.notEqual(restored.serviceEpoch, runtime.serviceEpoch)
  assert.notEqual(restored.signingKeyBase64, runtime.signingKeyBase64)
  docker(['start', restored.database])
  let ready = false
  for (let i = 0; i < 90; i++) {
    try { ready = sql(restored.database, 'SELECT 1;') === '1' } catch {}
    if (ready) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(ready)
  stage = 'retention-fixtures-on-restore-only'
  assert.equal(sql(restored.database, 'SELECT mode FROM ops_guard WHERE id=1;'), 'FROZEN')
  sql(restored.database, await readFile(join(root, 'backend/src/test/fixtures/operations/restore-retention-cases.sql'), 'utf8'))
  const anchor = sql(restored.database, "SELECT started_at,expires_at,ended_at FROM guest_session WHERE display_name='restore-test-retained';")
  sql(restored.database, await readFile(join(root, 'deploy/restore-retention.sql'), 'utf8'))
  assert.equal(sql(restored.database, "SELECT COUNT(*) FROM guest_session WHERE display_name LIKE 'restore-test-%';"), '1')
  assert.equal(sql(restored.database, "SELECT COUNT(*) FROM place_view v JOIN guest_session s ON s.id=v.session_id WHERE s.display_name LIKE 'restore-test-%';"), '1')
  assert.equal(sql(restored.database, "SELECT started_at,expires_at,ended_at FROM guest_session WHERE display_name='restore-test-retained';"), anchor)
  stage = 'reopen-restored'
  run(['deploy/maintenance.mjs', restore, 'resume', restored.maintenanceToken])
  assert.equal((await fetch(new URL('/api/v1/auth/csrf', restored.url))).status, 200)
  const oldAccess = await fetch(new URL(`/api/v1/guest/sessions/${session.sessionId}`, restored.url), { headers: { 'X-Guest-Credential': credential } })
  assert.equal(oldAccess.status, 401)
  assert.equal((await oldAccess.json()).code, 'GUEST_CREDENTIAL_INVALID')
  const oldStart = await startGuest(restored.url, credential, accepted.key, accepted.startTicket)
  assert([401, 403].includes(oldStart.response.status), 'Old start ticket must not restore access or create another session')
  const oldHistory = sql(restored.database, `SELECT started_at,expires_at,COALESCE(ended_at,'NULL') FROM guest_session WHERE id=UNHEX(REPLACE('${session.sessionId}','-',''));`)
  assert(oldHistory.endsWith('\tNULL'), 'Restore must not fabricate endedAt for retained history')
  const fresh = await startGuest(restored.url, randomBytes(32).toString('base64url'))
  assert.equal(fresh.response.status, 201)
  assert.notEqual((await fresh.response.json()).sessionId, session.sessionId)
  // Credential browser chỉ trong runtime riêng đã giới hạn ACL, không chép vào báo cáo.
  await writeFile(join(restore, 'browser-private.json'), JSON.stringify({ ...restored,
    adminLogin: runtime.adminLogin, adminPassword: runtime.adminPassword,
    expectedMp3Hash: manifest.files.find(f => f.format === 'MP3').sha256 }, null, 2))
  report.restore = { project: restored.project, filesVerifiedFromTarget: 2, epochChanged: true, reopened: true,
    retentionFixturesAddedAfterImport: 4, expiredFixturesPurged: 3, retainedFixture: 1, originalAnchorsPreserved: true,
    oldGuestAccessRejected: true, oldStartTicketRejected: true, retainedHistoryNotEnded: true, freshStartAccepted: true }
  report.status = 'PASSED_API_AND_STORAGE'
} catch (error) {
  report.status = 'FAILED'; report.stage = stage; report.error = error.name
  if (typeof error.actual === 'number') report.actualStatus = error.actual
  if (typeof error.expected === 'number') report.expectedStatus = error.expected
  process.exitCode = 1
} finally {
  report.finishedAt = new Date().toISOString()
  await writeFile(join(source, 'maintenance-rehearsal-result.json'), JSON.stringify(report, null, 2))
  console.log(JSON.stringify(report))
  // Giữ app phục hồi để kiểm browser khi đạt; nếu lỗi thì dừng đúng bộ thử, không đụng bản tập dượt gốc.
  if (report.status === 'FAILED') {
    for (const name of [runtime.application, runtime.database, restored?.application, restored?.database].filter(Boolean)) {
      try { docker(['stop', '--timeout', '30', name]) } catch {}
    }
  }
}
