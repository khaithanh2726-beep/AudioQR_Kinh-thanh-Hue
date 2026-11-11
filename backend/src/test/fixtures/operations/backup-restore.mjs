import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { createHash, randomBytes, randomUUID } from 'node:crypto'
import { copyFile, lstat, mkdir, readFile, realpath, writeFile } from 'node:fs/promises'
import { dirname, isAbsolute, join, relative, resolve, sep } from 'node:path'
import { fileURLToPath } from 'node:url'
import { verifySnapshot } from './backup-manifest.mjs'

// Công cụ diễn tập riêng, không phải cơ chế backup live hoặc thay thế OPS_GUARD
const project = resolve(dirname(fileURLToPath(import.meta.url)), '../../../../..')
const [sourceRuntimePath, outputPath] = process.argv.slice(2)
assert(sourceRuntimePath && outputPath, 'Cần runtime thử và thư mục đầu ra mới ngoài repository')
const source = JSON.parse((await readFile(resolve(sourceRuntimePath, 'runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
assert.match(source.container, /^hueqr-e2e-[a-z0-9-]+$/)
assert(isAbsolute(source.audioRoot), 'Kho nguồn phải là đường dẫn tuyệt đối')
const output = resolve(outputPath)
const inside = relative(project, output)
assert(inside.startsWith('..' + sep) || isAbsolute(inside), 'Đầu ra phải ngoài repository')
const audioRoot = await realpath(source.audioRoot)
const audioRelative = relative(audioRoot, output)
assert(audioRelative.startsWith('..' + sep) || isAbsolute(audioRelative), 'Đầu ra phải tách kho audio nguồn')
const docker = (args, options = {}) => execFileSync('docker', args, {
  encoding: 'utf8', stdio: ['pipe', 'pipe', 'pipe'], maxBuffer: 64 * 1024 * 1024, timeout: 120000, ...options,
})
const sql = (container, password, statement) => docker([
  'exec', '-i', '-e', 'MYSQL_PWD', container,
  'mysql', '--default-character-set=utf8mb4', '-u', 'hueqr_smoke', '-N', '-B', 'hueqr_smoke',
], { env: { ...process.env, MYSQL_PWD: password }, input: statement }).trim()
const hash = bytes => createHash('sha256').update(bytes).digest('hex')
const guardSql = `SELECT
  (SELECT COUNT(*) FROM runtime_task) + (SELECT COUNT(*) FROM file_claim)
  + (SELECT COUNT(*) FROM tts_job WHERE phase IN ('QUEUED','DISPATCHING','UNCERTAIN','RESULT_STAGED','CONVERTING'))
  + (SELECT COUNT(*) FROM file_asset WHERE state = 'DELETING')
  + (SELECT COUNT(*) FROM information_schema.processlist WHERE ID <> CONNECTION_ID() AND DB = DATABASE());`
function frozen() {
  if (process.platform !== 'win32') throw new Error('Diễn tập này cần kiểm tiến trình Windows')
  const running = execFileSync('powershell.exe', ['-NoProfile', '-Command',
    "Get-CimInstance Win32_Process | Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -match 'backend-0\\.0\\.1-SNAPSHOT|vn\\.hueqr\\.backend\\.BackendApplication' } | Select-Object -ExpandProperty ProcessId"],
  { encoding: 'utf8', timeout: 30000 }).trim()
  assert.equal(running, '', 'Dừng backend/worker trước diễn tập')
  assert.equal(sql(source.container, source.dbPassword, guardSql), '0', 'Có task/claim/job cần reconcile hoặc kết nối khác')
}
const catalogSql = `SELECT storage_key,LOWER(HEX(checksum)),byte_size,format FROM file_asset f
 WHERE state = 'READY' AND (
 EXISTS(SELECT 1 FROM audio_revision a WHERE a.wav_file_id=f.id OR a.mp3_file_id=f.id)
 OR EXISTS(SELECT 1 FROM tts_job j WHERE j.source_wav_file_id=f.id)
 OR EXISTS(SELECT 1 FROM guest_content_grant g WHERE g.mp3_file_id=f.id)) ORDER BY storage_key;`
const countsSql = `SELECT 'places',COUNT(*) FROM place UNION ALL SELECT 'pairs',COUNT(*) FROM content_pair
 UNION ALL SELECT 'audios',COUNT(*) FROM audio_revision UNION ALL SELECT 'sessions',COUNT(*) FROM guest_session
 UNION ALL SELECT 'views',COUNT(*) FROM place_view;`
await mkdir(output)
const started = Date.now()
let restoreContainer
let sourceLock
try {
  // Khóa riêng chỉ ngăn hai lượt công cụ này, không được diễn giải thành gate cho app live
  sourceLock = await import('node:fs/promises').then(fs => fs.open(join(sourceRuntimePath, 'backup-rehearsal.lock'), 'wx'))
  frozen()
  const createdAt = new Date()
  const catalogText = sql(source.container, source.dbPassword, catalogSql)
  const catalog = catalogText ? catalogText.split('\n').map(line => {
    const [storageKey, sha256, byteSize, format] = line.trim().split('\t')
    assert.match(storageKey, /^[0-9a-f-]{36}\.(wav|mp3)$/)
    assert.match(sha256, /^[0-9a-f]{64}$/)
    assert(['WAV', 'MP3'].includes(format))
    assert(Number.isSafeInteger(Number(byteSize)) && Number(byteSize) > 0)
    return { storageKey, sha256, bytes: Number(byteSize), format }
  }) : []
  assert(catalog.length > 0, 'Cần audio thật được tham chiếu để diễn tập')
  const beforeCounts = sql(source.container, source.dbPassword, countsSql)
  const migrations = sql(source.container, source.dbPassword,
    'SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;')
  const dump = docker(['exec', '-e', 'MYSQL_PWD', source.container, 'mysqldump',
    '-u', 'hueqr_smoke', '--single-transaction', '--no-tablespaces', '--hex-blob',
    '--set-gtid-purged=OFF', '--skip-comments', '--default-character-set=utf8mb4', 'hueqr_smoke'],
  { env: { ...process.env, MYSQL_PWD: source.dbPassword }, encoding: 'buffer' })
  await writeFile(join(output, 'database.sql'), dump, { flag: 'wx' })
  await mkdir(join(output, 'audio'))
  for (const file of catalog) {
    const original = join(audioRoot, file.storageKey)
    assert((await lstat(original)).isFile(), 'Không nhận symlink hoặc thư mục thay audio')
    const bytes = await readFile(original)
    assert.equal(bytes.length, file.bytes, 'Sai kích thước audio nguồn')
    assert.equal(hash(bytes), file.sha256, 'Sai Hash audio nguồn')
    await copyFile(original, join(output, 'audio', file.storageKey))
    assert.equal(hash(await readFile(join(output, 'audio', file.storageKey))), file.sha256)
  }
  frozen()
  assert.equal(sql(source.container, source.dbPassword, catalogSql), catalogText, 'Catalog thay đổi trong snapshot')
  assert.equal(sql(source.container, source.dbPassword, countsSql), beforeCounts)
  const manifest = { kind: 'offline-test-rehearsal', status: 'SNAPSHOT_VERIFIED',
    createdAt: createdAt.toISOString(), expiresAt: new Date(+createdAt + 7 * 86400000).toISOString(),
    sourceContainer: source.container, sourceSchema: 'hueqr_smoke', schema: migrations,
    database: { file: 'database.sql', bytes: dump.length, sha256: hash(dump) }, files: catalog,
    limitation: 'Không có OPS_GUARD hoặc drain live; nguồn thử đã dừng và chỉ nhận job terminal, không có claim/task.' }
  await writeFile(join(output, 'manifest.json'), JSON.stringify(manifest, null, 2))
  await verifySnapshot(output, manifest)

  // Phục hồi vào container mới không publish cổng, không đụng schema/volume nguồn
  restoreContainer = 'hueqr-restore-' + randomUUID().slice(0, 8)
  const password = randomBytes(24).toString('base64url')
  const epoch = randomUUID()
  const privateRuntime = { container: restoreContainer, dbPassword: password,
    serviceEpoch: epoch, signingKeyBase64: randomBytes(32).toString('base64'), audioRoot: join(output, 'audio') }
  await writeFile(join(output, 'private-runtime.json'), JSON.stringify(privateRuntime, null, 2))
  docker(['run', '-d', '--name', restoreContainer, '--memory', '512m', '--cpus', '1',
    '-e', 'MYSQL_RANDOM_ROOT_PASSWORD=yes', '-e', 'MYSQL_DATABASE=hueqr_smoke',
    '-e', 'MYSQL_USER=hueqr_smoke', '-e', 'MYSQL_PASSWORD', 'mysql:8.4.13'],
  { env: { ...process.env, MYSQL_PASSWORD: password } })
  let ready = false
  for (let i = 0; i < 90; i++) {
    try { ready = sql(restoreContainer, password, 'SELECT 1;') === '1' } catch {}
    if (ready) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(ready, 'MySQL restore chưa sẵn sàng')
  await verifySnapshot(output, manifest)
  sql(restoreContainer, password, dump)
  assert.equal(sql(restoreContainer, password, catalogSql), catalogText)
  assert.equal(sql(restoreContainer, password, countsSql), beforeCounts)
  assert.equal(sql(restoreContainer, password,
    'SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;'), migrations)
  assert.equal(sql(restoreContainer, password, guardSql), '0')
  const cases = await readFile(join(dirname(fileURLToPath(import.meta.url)), 'restore-retention-cases.sql'), 'utf8')
  sql(restoreContainer, password, cases)
  const sessionTimes = sql(restoreContainer, password,
    'SELECT HEX(id),started_at,expires_at,ended_at FROM guest_session ORDER BY id;')
  const filter = await readFile(join(dirname(fileURLToPath(import.meta.url)), 'restore-filter.sql'), 'utf8')
  const purged = Number(sql(restoreContainer, password, filter))
  const remainingTimes = sql(restoreContainer, password,
    'SELECT HEX(id),started_at,expires_at,ended_at FROM guest_session ORDER BY id;')
  for (const row of remainingTimes.split('\n').filter(Boolean)) assert(sessionTimes.split('\n').includes(row), 'Mốc phiên gốc bị sửa')
  assert.equal(sql(restoreContainer, password,
    "SELECT display_name FROM guest_session WHERE display_name LIKE 'restore-test-%' ORDER BY display_name;"), 'restore-test-retained')
  assert.equal(sql(restoreContainer, password,
    "SELECT COUNT(*) FROM place_view v JOIN guest_session s ON s.id=v.session_id WHERE s.display_name LIKE 'restore-test-%';"), '1')
  assert(purged >= 3, 'Chưa xóa đủ fixture quá hạn')
  assert.equal(sql(restoreContainer, password,
    'SELECT COUNT(*) FROM guest_session WHERE service_epoch = ' + "'" + epoch + "';"), '0')
  assert.equal(sql(restoreContainer, password,
    'SELECT (SELECT COUNT(*) FROM guest_content_grant)+(SELECT COUNT(*) FROM guest_open_intent)+(SELECT COUNT(*) FROM guest_request_result);'), '0')
  const report = { recordedAt: new Date().toISOString(), status: 'PASSED_OFFLINE_REHEARSAL',
    sourceContainer: source.container, restoreContainer, files: catalog.length, bytes: catalog.reduce((n, f) => n + f.bytes, 0),
    databaseHash: manifest.database.sha256, snapshotCounts: beforeCounts, restoredCounts: sql(restoreContainer, password, countsSql),
    purgedSessions: purged, originalTimestampsPreserved: true, newEpoch: true,
    oldGuestEpochRejectedByComparison: true, guestContextsCleared: true,
    retentionFixture: { insertedSessions: 4, insertedViews: 4, expectedPurged: 3, retainedSessions: 1, retainedViews: 1,
      cases: ['expired', 'boundary 30 days', 'invalid endedAt falls back to expiresAt', 'valid endedAt retains original anchor'] },
    providerEnabled: false, appStarted: false, portPublished: false,
    elapsedSeconds: (Date.now() - started) / 1000,
    limitations: ['Chỉ job terminal và zero claim/task', 'Chưa OPS_GUARD/drain live',
      'Chưa kiểm endpoint với epoch mới hoặc phát audio sau restore'] }
  await writeFile(join(output, 'report.json'), JSON.stringify(report, null, 2))
  console.log(JSON.stringify(report))
} catch (error) {
  await writeFile(join(output, 'FAILED.txt'), String(error.code || error.name))
  console.error('Diễn tập chưa đạt:', error.code || error.message?.split('\n')[0])
  process.exitCode = 1
} finally {
  if (restoreContainer) docker(['stop', restoreContainer])
  if (sourceLock) {
    await sourceLock.close()
    const { unlink } = await import('node:fs/promises')
    await unlink(join(sourceRuntimePath, 'backup-rehearsal.lock'))
  }
}
