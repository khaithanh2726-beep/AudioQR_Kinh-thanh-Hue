import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { mkdir, readFile, writeFile, lstat, statfs, realpath } from 'node:fs/promises'
import { resolve, relative, isAbsolute, sep, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { createHash } from 'node:crypto'
import { verifyOfflineV13Backup } from './operations-backup.mjs'

// Backup trước nâng V13: chỉ nhận app đã dừng và DB không writer, không giả gate V14.
const [runtimeArg, outputArg] = process.argv.slice(2)
assert(runtimeArg && outputArg, 'Usage: offline-v13-backup.mjs runtime new-backup-directory')
assert.equal(process.platform, 'win32', 'Windows ACL required')
const root = resolve(fileURLToPath(new URL('..', import.meta.url)))
const source = await realpath(runtimeArg), output = resolve(outputArg)
for (const parent of [root, source]) {
  const delta = relative(parent, output)
  assert(delta.startsWith('..' + sep) || isAbsolute(delta), 'Backup must be outside source and repository')
}
const runtime = JSON.parse(await readFile(join(source, 'runtime.json'), 'utf8'))
assert.match(runtime.project, /^hueqr-rehearsal-[a-f0-9]{8}$/)
assert.equal(runtime.application, runtime.project + '-application-1')
assert.equal(runtime.database, runtime.project + '-database-1')
const docker = (args, input, encoding = 'utf8') => execFileSync('docker', args, {
  input, encoding, windowsHide: true, timeout: 120000, maxBuffer: 128 * 1024 * 1024, stdio: ['pipe', 'pipe', 'pipe'],
})
const inspect = name => JSON.parse(docker(['inspect', name]))[0]
const sql = statement => docker(['exec', '-i', runtime.database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql --default-character-set=utf8mb4 -u hueqr -N -B hueqr'], statement).trim()
const schemaSql = 'SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;'
const catalogSql = `SELECT storage_key,LOWER(HEX(checksum)),byte_size,format FROM file_asset f WHERE state='READY' AND (
  EXISTS(SELECT 1 FROM audio_revision a WHERE a.wav_file_id=f.id OR a.mp3_file_id=f.id)
  OR EXISTS(SELECT 1 FROM tts_job j WHERE j.source_wav_file_id=f.id)
  OR EXISTS(SELECT 1 FROM guest_content_grant g WHERE g.mp3_file_id=f.id)) ORDER BY storage_key;`
const hash = bytes => createHash('sha256').update(bytes).digest('hex')
function stopped() {
  const app = inspect(runtime.application), db = inspect(runtime.database)
  for (const [container, service] of [[app, 'application'], [db, 'database']]) {
    assert.equal(container.Config.Labels['com.docker.compose.project'], runtime.project)
    assert.equal(container.Config.Labels['com.docker.compose.service'], service)
  }
  assert.equal(app.State.Running, false, 'Stop app and workers before offline backup')
  assert.equal(app.Image, runtime.imageId, 'Runtime image identity changed')
  assert(db.State.Running)
  assert.equal(db.Config.Image, 'mysql:8.4.13')
  assert.equal(db.HostConfig.PortBindings?.['3306/tcp'], undefined, 'Do not publish MySQL')
  const settings = Object.fromEntries(app.Config.Env.map(line => { const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)] }))
  assert.equal(settings.SPRING_DATASOURCE_URL, 'jdbc:mysql://database:3306/hueqr')
  assert.equal(settings.HUEQR_AUDIO_ROOT, '/data/audio')
  for (const key of ['HUEQR_TTS_ENABLED', 'HUEQR_TTS_LOCAL_ENABLED', 'HUEQR_CLEANUP_ENABLED', 'HUEQR_RETENTION_ENABLED'])
    assert.equal(settings[key], 'false', 'Disable workers before offline maintenance')
  assert.equal(sql(`SELECT (SELECT COUNT(*) FROM runtime_task)+(SELECT COUNT(*) FROM file_claim)
    +(SELECT COUNT(*) FROM tts_job WHERE phase IN ('QUEUED','DISPATCHING','UNCERTAIN','RESULT_STAGED','CONVERTING'))
    +(SELECT COUNT(*) FROM file_asset WHERE state='DELETING');`), '0', 'Unreconciled work; do not clear markers by age')
  assert.equal(sql('SELECT COUNT(*) FROM information_schema.processlist WHERE ID<>CONNECTION_ID() AND DB=DATABASE();'), '0', 'Unexpected database writer')
  const volumes = new Set([...app.Mounts, ...db.Mounts].filter(m => m.Type === 'volume').map(m => m.Name))
  for (const id of docker(['ps', '-q']).trim().split(/\s+/).filter(Boolean)) {
    const other = inspect(id)
    if (other.Id === db.Id) continue
    assert(!other.Mounts.some(m => m.Type === 'volume' && volumes.has(m.Name)), 'Another running container uses source volume')
  }
  return app
}
let locked = false, created = false
try {
  stopped()
  docker(['exec', runtime.database, 'mkdir', '/tmp/hueqr-operations-lock'])
  locked = true
  const schema = sql(schemaSql)
  assert.equal(schema.split('\n').length, 13, 'Only V13 source supported')
  assert.equal(schema.split('\n').at(-1).split('\t')[0], '13')
  const catalog = sql(catalogSql)
  const files = catalog ? catalog.split('\n').map(line => {
    const [storageKey, sha256, size, format] = line.trim().split('\t')
    assert.match(storageKey, /^[0-9a-f-]{36}\.(wav|mp3)$/)
    assert.match(sha256, /^[0-9a-f]{64}$/)
    assert(Number.isSafeInteger(Number(size)) && Number(size) > 0)
    return { storageKey, sha256, bytes: Number(size), format }
  }) : []
  await mkdir(output)
  created = true
  const sid = execFileSync('powershell.exe', ['-NoProfile', '-Command', '[Security.Principal.WindowsIdentity]::GetCurrent().User.Value'], { encoding: 'utf8', windowsHide: true }).trim()
  assert.match(sid, /^S-1-[0-9-]+$/)
  execFileSync('icacls.exe', [output, '/inheritance:r', '/grant:r', `*${sid}:(OI)(CI)F`, '*S-1-5-18:(OI)(CI)F'], { stdio: 'pipe', windowsHide: true })
  const createdAt = new Date()
  const dump = docker(['exec', runtime.database, 'sh', '-c', 'MYSQL_PWD="$MYSQL_PASSWORD" exec mysqldump -u hueqr --single-transaction --no-tablespaces --hex-blob --set-gtid-purged=OFF --skip-comments --default-character-set=utf8mb4 hueqr'], undefined, 'buffer')
  const capacity = await statfs(output, { bigint: true })
  const required = BigInt(dump.length) + files.reduce((sum, file) => sum + BigInt(file.bytes), 0n) + 65536n
  assert(capacity.bavail * capacity.bsize >= required, 'Insufficient backup space')
  await writeFile(join(output, 'database.sql'), dump, { flag: 'wx' })
  await mkdir(join(output, 'audio'))
  for (const file of files) {
    const target = join(output, 'audio', file.storageKey)
    docker(['cp', `${runtime.application}:/data/audio/${file.storageKey}`, target])
    assert((await lstat(target)).isFile())
    const bytes = await readFile(target)
    assert.equal(bytes.length, file.bytes)
    assert.equal(hash(bytes), file.sha256)
  }
  const app = stopped()
  assert.equal(sql(schemaSql), schema)
  assert.equal(sql(catalogSql), catalog)
  const manifest = { kind: 'offline-v13-backup', status: 'COMPLETE', sourceStopped: true, sourceImage: app.Image,
    createdAt: createdAt.toISOString(), expiresAt: new Date(+createdAt + 7 * 86400000).toISOString(), schema,
    database: { file: 'database.sql', bytes: dump.length, sha256: hash(dump) }, files, restoreVerified: false }
  await verifyOfflineV13Backup(output, manifest)
  await writeFile(join(output, 'manifest.json'), JSON.stringify(manifest, null, 2), { flag: 'wx' })
  console.log(JSON.stringify({ status: 'OFFLINE_V13_BACKUP_COMPLETE', files: files.length, restoreVerified: false }))
} catch (error) {
  if (created) await writeFile(join(output, 'FAILED.json'), JSON.stringify({ status: 'FAILED', code: 'BACKUP_NOT_COMPLETE' }))
  throw error
} finally {
  if (locked) docker(['exec', runtime.database, 'rmdir', '/tmp/hueqr-operations-lock'])
}
