import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { mkdir, readFile, writeFile, lstat, statfs } from 'node:fs/promises'
import { resolve, relative, isAbsolute, sep, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { createHash } from 'node:crypto'
import { verifyOperationsBackup } from './operations-backup.mjs'

const [runtimePath, action, token, destination] = process.argv.slice(2)
const actions = new Set(['status', 'quiesce', 'freeze', 'backup', 'resume'])
assert(runtimePath && actions.has(action), 'Usage: maintenance.mjs runtime status|quiesce|freeze|backup|resume [owner-uuid] [new-backup-directory]')
if (action !== 'status') assert.match(token || '', /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/)
const root = resolve(fileURLToPath(new URL('..', import.meta.url)))
const runtime = JSON.parse((await readFile(join(runtimePath, 'runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
assert.match(runtime.project, /^(audioqr-hue|hueqr-(ops-check|rehearsal)-[a-z0-9]+)$/)
assert.equal(runtime.application, `${runtime.project}-application-1`)
assert.equal(runtime.database, `${runtime.project}-database-1`)
const docker = (args, input, encoding = 'utf8') => execFileSync('docker', args, {
  input, encoding, windowsHide: true, maxBuffer: 128 * 1024 * 1024, timeout: 120000,
  stdio: ['pipe', 'pipe', 'pipe'],
})
const inspect = name => JSON.parse(docker(['inspect', name]))[0]
function containers() {
  const application = inspect(runtime.application), database = inspect(runtime.database)
  for (const [value, service] of [[application, 'application'], [database, 'database']]) {
    assert.equal(value.Config.Labels['com.docker.compose.project'], runtime.project)
    assert.equal(value.Config.Labels['com.docker.compose.service'], service)
  }
  assert(database.State.Running, 'Database must remain running')
  assert.equal(database.HostConfig.PortBindings?.['3306/tcp'], undefined, 'Do not publish MySQL')
  const settings = value => Object.fromEntries(value.Config.Env.map(line => {
    const split = line.indexOf('='); return [line.slice(0, split), line.slice(split + 1)]
  }))
  assert.equal(settings(application).HUEQR_AUDIO_ROOT, '/data/audio')
  assert.equal(settings(application).SPRING_DATASOURCE_URL, 'jdbc:mysql://database:3306/hueqr')
  assert.equal(settings(database).MYSQL_DATABASE, 'hueqr')
  assert.equal(settings(database).MYSQL_USER, 'hueqr')
  return { application, database }
}
const sql = statement => docker(['exec', '-i', runtime.database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql --default-character-set=utf8mb4 -u hueqr -N -B hueqr'], statement).trim()
function state() {
  const rows = sql(`SELECT mode,COALESCE(owner_token,'') FROM ops_guard WHERE id=1;
    SELECT (SELECT COUNT(*) FROM ops_activity), (SELECT COUNT(*) FROM runtime_task),
    (SELECT COUNT(*) FROM file_claim), (SELECT COUNT(*) FROM file_asset WHERE state='DELETING');`).split('\n')
  assert.equal(rows.length, 2, 'Guard must exist and have exactly one row')
  const [mode, owner = ''] = rows[0].split('\t')
  const [activities, tasks, claims, deleting] = rows[1].split('\t').map(Number)
  assert(['RUNNING', 'QUIESCING', 'FROZEN'].includes(mode))
  return { mode, owner, activities, tasks, claims, deleting }
}
function idle(value) {
  for (const key of ['activities', 'tasks', 'claims', 'deleting']) assert.equal(value[key], 0, `Not drained: ${key}`)
}
function owned(value, mode) {
  assert.equal(value.owner, token, 'Another maintenance operation owns the guard')
  if (mode) assert.equal(value.mode, mode)
}
function frozen() {
  const current = containers()
  assert.equal(current.application.State.Running, false, 'Application must be stopped after drain')
  const value = state(); owned(value, 'FROZEN'); idle(value)
  assert.equal(sql('SELECT COUNT(*) FROM information_schema.processlist WHERE ID<>CONNECTION_ID() AND DB=DATABASE();'), '0',
    'Unexpected database connection; keep frozen and inspect writer')
  // Không có container khác dùng cùng volume; thao tác SQL/DDL thủ công vẫn thuộc quy trình vận hành.
  const protectedVolumes = new Set([...current.application.Mounts, ...current.database.Mounts].filter(m => m.Type === 'volume').map(m => m.Name))
  const running = docker(['ps', '-q']).trim().split(/\s+/).filter(Boolean)
  for (const id of running) {
    const other = inspect(id)
    if ([current.application.Id, current.database.Id].includes(other.Id)) continue
    assert(!other.Mounts.some(m => m.Type === 'volume' && protectedVolumes.has(m.Name)), 'Another container shares protected storage')
  }
  return value
}
const hash = bytes => createHash('sha256').update(bytes).digest('hex')
const catalogSql = `SELECT storage_key,LOWER(HEX(checksum)),byte_size,format FROM file_asset f WHERE state='READY' AND (
  EXISTS(SELECT 1 FROM audio_revision a WHERE a.wav_file_id=f.id OR a.mp3_file_id=f.id)
  OR EXISTS(SELECT 1 FROM tts_job j WHERE j.source_wav_file_id=f.id)
  OR EXISTS(SELECT 1 FROM guest_content_grant g WHERE g.mp3_file_id=f.id)) ORDER BY storage_key;`
const migrationsSql = 'SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;'
let locked = false
try {
containers()
if (action !== 'status') {
  // Khóa ở chính container DB, hai thư mục runtime cùng trỏ tới DB cũng không chạy chồng.
  // Không tự tháo khóa cũ theo tuổi; tiến trình chết cần đối chiếu trước khi xử lý riêng.
  docker(['exec', runtime.database, 'mkdir', '/tmp/hueqr-operations-lock'])
  locked = true
}
if (action === 'quiesce') {
  sql(`UPDATE ops_guard SET mode='QUIESCING',owner_token='${token}',changed_at=UTC_TIMESTAMP(6) WHERE id=1 AND mode='RUNNING' AND owner_token IS NULL;`)
  const current = state(); owned(current, 'QUIESCING')
  console.log(JSON.stringify(current))
} else if (action === 'freeze') {
  const current = state(); owned(current, 'QUIESCING'); idle(current)
  // Đã drain trước stop; timeout/kill không được dùng thay bằng chứng task kết thúc.
  docker(['stop', '--timeout', '30', runtime.application])
  const stopped = containers().application
  assert.equal(stopped.State.Running, false)
  assert([0, 143].includes(stopped.State.ExitCode), 'Unclean stop; do not claim frozen')
  idle(state())
  sql(`UPDATE ops_guard SET mode='FROZEN',changed_at=UTC_TIMESTAMP(6) WHERE id=1 AND mode='QUIESCING' AND owner_token='${token}';`)
  console.log(JSON.stringify(frozen()))
} else if (action === 'resume') {
  const current = state(); owned(current)
  assert(['QUIESCING', 'FROZEN'].includes(current.mode))
  if (!containers().application.State.Running) {
    docker(['start', runtime.application])
    const origin = new URL(runtime.url)
    assert(['localhost', '127.0.0.1'].includes(origin.hostname) && origin.protocol === 'http:')
    let ready = false
    for (let attempt = 0; attempt < 90; attempt++) {
      try {
        const response = await fetch(new URL('/api/v1/auth/csrf', origin), { redirect: 'error', signal: AbortSignal.timeout(2000) })
        ready = response.status === 503 && (await response.json()).code === 'SERVICE_UNAVAILABLE'
      } catch {}
      if (ready) break
      await new Promise(done => setTimeout(done, 1000))
    }
    assert(ready, 'Application not ready with closed guard; not reopening')
  }
  sql(`UPDATE ops_guard SET mode='RUNNING',owner_token=NULL,changed_at=UTC_TIMESTAMP(6) WHERE id=1 AND owner_token='${token}' AND mode IN ('QUIESCING','FROZEN');`)
  assert.equal(state().mode, 'RUNNING')
  console.log(JSON.stringify({ mode: 'RUNNING' }))
} else if (action === 'backup') {
  assert(destination, 'New directory outside repository required')
  frozen()
  const output = resolve(destination)
  for (const parent of [root, resolve(runtimePath)]) {
    const rel = relative(parent, output)
    assert(rel.startsWith('..' + sep) || isAbsolute(rel), 'Backup must be outside repository and runtime')
  }
  await mkdir(output)
  try {
    assert.equal(process.platform, 'win32', 'Configure protected backup ownership for this operating system first')
    const sid = execFileSync('powershell.exe', ['-NoProfile', '-Command', '[Security.Principal.WindowsIdentity]::GetCurrent().User.Value'],
      { encoding: 'utf8', windowsHide: true }).trim()
    assert.match(sid, /^S-1-[0-9-]+$/)
    execFileSync('icacls.exe', [output, '/inheritance:r', '/grant:r', `*${sid}:(OI)(CI)F`, '*S-1-5-18:(OI)(CI)F'], { windowsHide: true, stdio: 'pipe' })
    const createdAt = new Date(), catalogText = sql(catalogSql), migrations = sql(migrationsSql)
    assert.match(migrations, /(?:^|\n)14\t/)
    const files = catalogText ? catalogText.split('\n').map(line => {
      const [storageKey, sha256, size, format] = line.trim().split('\t')
      assert.match(storageKey, /^[0-9a-f-]{36}\.(wav|mp3)$/)
      assert.match(sha256, /^[0-9a-f]{64}$/)
      assert(['WAV', 'MP3'].includes(format))
      assert(Number.isSafeInteger(Number(size)) && Number(size) > 0)
      return { storageKey, sha256, bytes: Number(size), format }
    }) : []
    const dump = docker(['exec', runtime.database, 'sh', '-c',
      'MYSQL_PWD="$MYSQL_PASSWORD" exec mysqldump -u hueqr --single-transaction --no-tablespaces --hex-blob --set-gtid-purged=OFF --skip-comments --default-character-set=utf8mb4 hueqr'], undefined, 'buffer')
    const capacity = await statfs(output, { bigint: true })
    const available = capacity.bavail * capacity.bsize
    const required = BigInt(dump.length) + files.reduce((sum, file) => sum + BigInt(file.bytes), 0n) + 65536n
    assert(available >= required, 'Insufficient backup space; keep frozen and choose another protected destination')
    await writeFile(join(output, 'database.sql'), dump, { flag: 'wx' })
    await mkdir(join(output, 'audio'))
    for (const file of files) {
      const target = join(output, 'audio', file.storageKey)
      docker(['cp', `${runtime.application}:/data/audio/${file.storageKey}`, target])
      assert((await lstat(target)).isFile(), 'Refuse directory or symlink instead of audio')
      const bytes = await readFile(target)
      assert.equal(bytes.length, file.bytes)
      assert.equal(hash(bytes), file.sha256)
    }
    frozen()
    assert.equal(sql(catalogSql), catalogText, 'Catalog changed during backup')
    assert.equal(sql(migrationsSql), migrations, 'Schema changed during backup')
    const manifest = { kind: 'quiesced-backup', status: 'COMPLETE', checkpoint: token,
      createdAt: createdAt.toISOString(), expiresAt: new Date(+createdAt + 7 * 86400000).toISOString(),
      sourceImage: containers().application.Image, schema: migrations,
      database: { file: 'database.sql', bytes: dump.length, sha256: hash(dump) }, files,
      restoreVerified: false, backupDurationMs: Date.now() - +createdAt,
      disk: { availableBytesAtCopy: available.toString(), requiredBytes: required.toString() } }
    await verifyOperationsBackup(output, manifest)
    await writeFile(join(output, 'manifest.json'), JSON.stringify(manifest, null, 2), { flag: 'wx' })
    console.log(JSON.stringify({ status: 'COMPLETE', files: files.length, restoreVerified: false }))
  } catch (error) {
    await writeFile(join(output, 'FAILED.json'), JSON.stringify({ status: 'FAILED', code: 'BACKUP_NOT_COMPLETE' }))
    throw error
  }
} else console.log(JSON.stringify(state()))
} finally {
  if (locked) docker(['exec', runtime.database, 'rmdir', '/tmp/hueqr-operations-lock'])
}
