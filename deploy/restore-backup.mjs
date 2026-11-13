import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { mkdir, readFile, writeFile } from 'node:fs/promises'
import { resolve, join, relative, isAbsolute, sep } from 'node:path'
import { randomBytes, randomUUID } from 'node:crypto'
import { createServer } from 'node:net'
import { fileURLToPath } from 'node:url'
import { verifyOperationsBackup, verifyOfflineV13Backup } from './operations-backup.mjs'

const [backupArg, outputArg, image] = process.argv.slice(2)
assert(backupArg && outputArg && image, 'Usage: restore-backup.mjs backup new-runtime image')
assert.equal(process.platform, 'win32', 'Windows ACL required')
assert.match(image, /^hueqr-local:[A-Za-z0-9_.-]+$/)
const root = resolve(fileURLToPath(new URL('..', import.meta.url)))
const backup = resolve(backupArg), output = resolve(outputArg)
for (const parent of [root, backup]) {
  const delta = relative(parent, output)
  assert(delta.startsWith('..' + sep) || isAbsolute(delta), 'Restore must use a new directory outside source and repository')
}
const manifest = JSON.parse(await readFile(join(backup, 'manifest.json'), 'utf8'))
const offlineV13 = manifest.kind === 'offline-v13-backup'
const verify = offlineV13 ? verifyOfflineV13Backup : verifyOperationsBackup
await verify(backup, manifest)
const docker = (args, input, env = process.env) => execFileSync('docker', args, { input, env, encoding: 'utf8', windowsHide: true,
  maxBuffer: 128 * 1024 * 1024, timeout: 120000, stdio: ['pipe', 'pipe', 'pipe'] })
const imageId = JSON.parse(docker(['image', 'inspect', image]))[0].Id
assert.equal(imageId, manifest.sourceImage, 'Use exact source image; schema upgrade requires a separate reviewed plan')
await mkdir(output)
const sid = execFileSync('powershell.exe', ['-NoProfile', '-Command', '[Security.Principal.WindowsIdentity]::GetCurrent().User.Value'],
  { encoding: 'utf8', windowsHide: true }).trim()
assert.match(sid, /^S-1-[0-9-]+$/)
execFileSync('icacls.exe', [output, '/inheritance:r', '/grant:r', `*${sid}:(OI)(CI)F`, '*S-1-5-18:(OI)(CI)F'], { stdio: 'pipe', windowsHide: true })
const project = 'hueqr-ops-check-' + randomUUID().slice(0, 8)
const application = `${project}-application-1`, database = `${project}-database-1`
const port = await new Promise((done, reject) => {
  const server = createServer().once('error', reject)
  server.listen(0, '127.0.0.1', () => { const port = server.address().port; server.close(() => done(port)) })
})
const epoch = randomUUID(), key = randomBytes(32).toString('base64'), password = randomBytes(32).toString('hex')
const token = randomUUID(), configPath = join(output, 'private.env'), limits = join(output, 'limits.yaml')
const restoreEnvironment = { ...process.env, HUEQR_IMAGE: image, HUEQR_DB_PASSWORD: password,
  HUEQR_GUEST_SIGNING_KEY_BASE64: key, HUEQR_SERVICE_EPOCH: epoch, HUEQR_HTTP_PORT: String(port),
  HUEQR_TTS_ENABLED: 'false', HUEQR_TTS_API_KEY: '', HUEQR_TTS_LOCAL_ENABLED: 'false',
  HUEQR_CLEANUP_ENABLED: 'false', HUEQR_RETENTION_ENABLED: 'false', HUEQR_PUBLIC_START_URL: '' }
await writeFile(configPath, [`HUEQR_IMAGE=${image}`, `HUEQR_DB_PASSWORD=${password}`,
  `HUEQR_GUEST_SIGNING_KEY_BASE64=${key}`, `HUEQR_SERVICE_EPOCH=${epoch}`, `HUEQR_HTTP_PORT=${port}`,
  'HUEQR_TTS_ENABLED=false', 'HUEQR_TTS_API_KEY=', 'HUEQR_TTS_LOCAL_ENABLED=false',
  'HUEQR_CLEANUP_ENABLED=false', 'HUEQR_RETENTION_ENABLED=false', 'HUEQR_PUBLIC_START_URL=',
].join('\n') + '\n', { flag: 'wx' })
await writeFile(limits, 'services:\n  database:\n    mem_limit: 384m\n  application:\n    mem_limit: 640m\n')
await writeFile(join(output, 'runtime.json'), JSON.stringify({ project, application, database, image, imageId,
  url: `http://localhost:${port}`, configPath, serviceEpoch: epoch, signingKeyBase64: key,
  maintenanceToken: token }, null, 2), { flag: 'wx' })
const compose = args => docker(['compose', '-p', project, '--env-file', configPath,
  '-f', join(root, 'deploy/compose.yaml'), '-f', limits, ...args], undefined, restoreEnvironment)
const sql = statement => docker(['exec', '-i', database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql --default-character-set=utf8mb4 -u hueqr -N -B hueqr'], statement).trim()
let created = false
try {
  created = true
  compose(['up', '-d', '--no-build', '--pull', 'never', 'database'])
  let ready = false
  for (let attempt = 0; attempt < 120; attempt++) {
    try { ready = sql('SELECT 1;') === '1' } catch {}
    if (ready) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(ready, 'New database not ready')
  assert.equal(sql('SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE();'), '0')
  await verify(backup, manifest)
  sql(await readFile(join(backup, 'database.sql')))
  assert.equal(sql('SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;'), manifest.schema)
  if (!offlineV13) assert.equal(sql('SELECT mode FROM ops_guard WHERE id=1;'), 'FROZEN')
  assert.equal(sql(`SELECT ${offlineV13 ? '' : '(SELECT COUNT(*) FROM ops_activity)+'}(SELECT COUNT(*) FROM runtime_task)+(SELECT COUNT(*) FROM file_claim);`), '0',
    'Unreconciled activity: keep restore stopped; never clear claims by age')
  const catalog = sql(`SELECT storage_key,LOWER(HEX(checksum)),byte_size,format FROM file_asset f WHERE state='READY' AND (
    EXISTS(SELECT 1 FROM audio_revision a WHERE a.wav_file_id=f.id OR a.mp3_file_id=f.id)
    OR EXISTS(SELECT 1 FROM tts_job j WHERE j.source_wav_file_id=f.id)
    OR EXISTS(SELECT 1 FROM guest_content_grant g WHERE g.mp3_file_id=f.id)) ORDER BY storage_key;`)
  assert.equal(catalog, manifest.files.map(f => `${f.storageKey}\t${f.sha256}\t${f.bytes}\t${f.format}`).join('\n'))
  sql(await readFile(join(root, 'deploy/restore-retention.sql'), 'utf8'))
  if (!offlineV13) sql(`UPDATE ops_guard SET owner_token='${token}',changed_at=UTC_TIMESTAMP(6) WHERE id=1 AND mode='FROZEN';`)
  compose(['create', '--no-build', '--pull', 'never', 'application'])
  for (const file of manifest.files) docker(['cp', join(backup, 'audio', file.storageKey), `${application}:/data/audio/${file.storageKey}`])
  // Kiểm lại bytes từ volume đích trước khi chạy app; không suy từ Hash bản nguồn.
  const probe = join(output, 'audio-verification')
  await mkdir(probe)
  for (const file of manifest.files) docker(['cp', `${application}:/data/audio/${file.storageKey}`, join(probe, file.storageKey)])
  await mkdir(join(probe, 'audio'))
  for (const file of manifest.files) {
    const bytes = await readFile(join(probe, file.storageKey))
    await writeFile(join(probe, 'audio', file.storageKey), bytes)
  }
  await writeFile(join(probe, 'database.sql'), await readFile(join(backup, 'database.sql')))
  await verify(probe, manifest)
  compose(['up', '-d', '--no-build', '--pull', 'never', 'application'])
  let closed = false
  for (let attempt = 0; attempt < 120; attempt++) {
    try {
      const response = await fetch(`http://localhost:${port}/api/v1/auth/csrf`, { signal: AbortSignal.timeout(2000) })
      const body = await response.json()
      closed = offlineV13 ? response.status === 200 && typeof body.token === 'string'
        : response.status === 503 && body.code === 'SERVICE_UNAVAILABLE'
    } catch {}
    if (closed) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(closed, 'Restored application must satisfy private startup check')
  const status = offlineV13 ? 'RESTORE_V13_VERIFIED_PRIVATE' : 'RESTORE_VERIFIED_CLOSED'
  await writeFile(join(output, 'restore-result.json'), JSON.stringify({ status, project,
    imageId, files: manifest.files.length, gate: offlineV13 ? 'NOT_AVAILABLE_V13' : 'FROZEN', epochChanged: true,
    retentionApplied: true, public: false, guestAdminFlowVerified: false }, null, 2))
  console.log(JSON.stringify({ status, project, files: manifest.files.length }))
} catch (error) {
  await writeFile(join(output, 'FAILED.json'), JSON.stringify({ status: 'FAILED', code: 'RESTORE_NOT_VERIFIED' }))
  throw error
} finally {
  if (created) compose(['stop'])
}
