import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { readFile, writeFile } from 'node:fs/promises'
import { resolve, join } from 'node:path'
import { randomUUID, createHash } from 'node:crypto'

const [directoryArg] = process.argv.slice(2)
assert(directoryArg)
const directory = resolve(directoryArg), root = process.cwd()
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
const restored = JSON.parse(await readFile(join(directory, 'restore-result.json'), 'utf8'))
assert.match(runtime.project, /^hueqr-ops-check-[a-f0-9]{8}$/)
assert.equal(restored.status, 'RESTORE_VERIFIED_CLOSED')
assert.equal(runtime.image, 'hueqr-local:20261113-ops-guard-urls')
const docker = (args, input, env = process.env) => execFileSync('docker', args, { input, env, encoding: 'utf8',
  windowsHide: true, timeout: 120000, maxBuffer: 8 * 1024 * 1024, stdio: ['pipe', 'pipe', 'pipe'] })
const run = args => execFileSync(process.execPath, args, { cwd: root, encoding: 'utf8', windowsHide: true,
  timeout: 180000, maxBuffer: 8 * 1024 * 1024, stdio: ['pipe', 'pipe', 'pipe'] })
const sql = statement => docker(['exec', '-i', runtime.database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr --default-character-set=utf8mb4 -N -B hueqr'], statement).trim()
const originalConfig = await readFile(runtime.configPath, 'utf8')
const baselineEnv = Object.fromEntries(originalConfig.trim().split('\n').map(line => {
  const split = line.indexOf('='); return [line.slice(0, split), line.slice(split + 1)]
}))
assert.equal(baselineEnv.HUEQR_TTS_ENABLED, 'false')
assert.equal(baselineEnv.HUEQR_TTS_LOCAL_ENABLED, 'false')
const changed = originalConfig.replace(/^HUEQR_CLEANUP_ENABLED=false$/m, 'HUEQR_CLEANUP_ENABLED=true')
  .replace(/^HUEQR_RETENTION_ENABLED=false$/m, 'HUEQR_RETENTION_ENABLED=true')
const compose = (args, settings = baselineEnv) => docker(['compose', '-p', runtime.project, '--env-file', runtime.configPath,
  '-f', join(root, 'deploy/compose.yaml'), '-f', join(directory, 'limits.yaml'), ...args], undefined, { ...process.env, ...settings })
const wait = ms => new Promise(done => setTimeout(done, ms))
const count = () => sql("SELECT COUNT(*) FROM guest_session WHERE display_name LIKE 'scheduler-test-%';")
const token = randomUUID(), orphan = randomUUID(), protectedId = randomUUID()
const payload = Buffer.from('synthetic-unreferenced-cleanup-fixture')
const digest = createHash('sha256').update(payload).digest('hex')
const report = { startedAt: new Date().toISOString(), project: runtime.project, status: 'RUNNING',
  providerRequests: 0, syntheticAssetsAreNarration: false, productionUpdated: false }
let stage = 'setup'
try {
  compose(['up', '-d', '--no-build', '--pull', 'never', 'database'])
  let ready = false
  for (let i = 0; i < 90; i++) {
    try { ready = sql('SELECT 1;') === '1' } catch {}
    if (ready) break
    await wait(1000)
  }
  assert(ready)
  run(['deploy/maintenance.mjs', directory, 'quiesce', token])
  assert.equal(count(), '0', 'Only fresh scheduler fixtures may be used')
  const businessBefore = sql('SELECT (SELECT COUNT(*) FROM place),(SELECT COUNT(*) FROM content_slot),(SELECT COUNT(*) FROM audio_revision),(SELECT COUNT(*) FROM text_revision);')
  const existingSessions = sql('SELECT COUNT(*) FROM guest_session;')
  const realFiles = sql(`SELECT storage_key,LOWER(HEX(checksum)) FROM file_asset f WHERE
    EXISTS(SELECT 1 FROM audio_revision a WHERE a.wav_file_id=f.id OR a.mp3_file_id=f.id) ORDER BY storage_key;`)
    .split('\n').map(line => line.split('\t'))
  sql((await readFile(join(root, 'backend/src/test/fixtures/operations/restore-retention-cases.sql'), 'utf8'))
    .replaceAll('restore-test-', 'scheduler-test-'))
  const anchor = sql("SELECT started_at,expires_at,ended_at FROM guest_session WHERE display_name='scheduler-test-retained';")
  const localFile = join(directory, 'synthetic-cleanup-fixture.bin')
  await writeFile(localFile, payload)
  for (const [id, applied] of [[orphan, false], [protectedId, true]]) {
    const key = id + '.wav'
    docker(['cp', localFile, `${runtime.application}:/data/audio/${key}`])
    sql(`INSERT INTO file_asset(id,storage_key,format,checksum,byte_size,duration,state,ever_applied,
      last_unapplied_at,orphan_eligible_since,ready_at) VALUES(UNHEX(REPLACE('${id}','-','')),'${key}','WAV',
      UNHEX('${digest}'),${payload.length},1,'READY',${applied ? 1 : 0},
      ${applied ? 'UTC_TIMESTAMP(6)-INTERVAL 7 HOUR' : 'NULL'},UTC_TIMESTAMP(6)-INTERVAL 26 HOUR,UTC_TIMESTAMP(6)-INTERVAL 26 HOUR);`)
  }
  await writeFile(runtime.configPath, changed)
  const enabled = { ...baselineEnv, HUEQR_CLEANUP_ENABLED: 'true', HUEQR_RETENTION_ENABLED: 'true' }
  compose(['up', '-d', '--no-build', '--pull', 'never', 'application'], enabled)
  ready = false
  for (let i = 0; i < 90; i++) {
    try { ready = (await fetch(runtime.url + '/api/v1/auth/csrf')).status === 503 } catch {}
    if (ready) break
    await wait(1000)
  }
  assert(ready)
  stage = 'closed-scheduler'
  // Ba khoảng 25 giây vượt một nhịp mặc định 60 giây, không thay hạn nghiệp vụ.
  for (let i = 0; i < 3; i++) await wait(25000)
  assert.equal(count(), '4')
  assert.equal(sql(`SELECT state FROM file_asset WHERE storage_key='${orphan}.wav';`), 'READY')
  report.closed = { nominalWaitMs: 75000, fixtureSessionsStillPresent: 4, orphanNotDeleted: true }
  stage = 'open-scheduler'
  run(['deploy/maintenance.mjs', directory, 'resume', token])
  let swept = false
  for (let i = 0; i < 60; i++) {
    swept = count() === '1' && sql(`SELECT state FROM file_asset WHERE storage_key='${orphan}.wav';`) === 'DELETED'
    if (swept) break
    await wait(3000)
  }
  assert(swept, 'Scheduler did not reach observed completion within the test timeout')
  let orphanExists = true
  try { docker(['exec', runtime.application, 'test', '-e', `/data/audio/${orphan}.wav`]) } catch { orphanExists = false }
  assert.equal(orphanExists, false)
  assert.equal(sql(`SELECT state FROM file_asset WHERE storage_key='${protectedId}.wav';`), 'READY')
  docker(['exec', runtime.application, 'test', '-f', `/data/audio/${protectedId}.wav`])
  assert.equal(sql("SELECT started_at,expires_at,ended_at FROM guest_session WHERE display_name='scheduler-test-retained';"), anchor)
  assert.equal(sql("SELECT COUNT(*) FROM guest_session WHERE display_name NOT LIKE 'scheduler-test-%';"), existingSessions)
  assert.equal(sql('SELECT (SELECT COUNT(*) FROM place),(SELECT COUNT(*) FROM content_slot),(SELECT COUNT(*) FROM audio_revision),(SELECT COUNT(*) FROM text_revision);'), businessBefore)
  for (const [key, expected] of realFiles)
    assert.equal(docker(['exec', runtime.application, 'sha256sum', '/data/audio/' + key]).split(/\s/)[0], expected)
  report.open = { expiredFixtureSessionsPurged: 3, retainedFixture: 1, originalAnchorsPreserved: true,
    orphanState: 'DELETED', orphanActuallyAbsent: true, sevenHourProtectedAssetKept: true,
    realAudioHashesPreserved: realFiles.length, existingSessionsPreserved: true }
  report.status = 'PASSED'
} catch (error) {
  report.status = 'FAILED'; report.stage = stage; report.error = error.name; process.exitCode = 1
} finally {
  try { compose(['stop']) } catch { report.stopFailed = true; process.exitCode = 1 }
  await writeFile(runtime.configPath, originalConfig)
  report.savedWorkerConfigurationRestored = true
  try {
    // Recreate khi dừng để docker start về sau cũng không dùng lại cờ worker thử.
    compose(['up', '--no-start', '--no-deps', '--force-recreate', '--no-build', '--pull', 'never', 'application'])
    report.stoppedContainerWorkerConfigurationRestored = true
  } catch {
    report.stoppedContainerWorkerConfigurationRestored = false
    process.exitCode = 1
  }
  report.finishedAt = new Date().toISOString()
  await writeFile(join(directory, 'scheduler-result.json'), JSON.stringify(report, null, 2))
  console.log(JSON.stringify(report))
}
