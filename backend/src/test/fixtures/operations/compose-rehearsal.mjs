import assert from 'node:assert/strict'
import { execFile } from 'node:child_process'
import { createHash, randomBytes, randomUUID } from 'node:crypto'
import { mkdir, readFile, writeFile } from 'node:fs/promises'
import { createServer } from 'node:net'
import { dirname, isAbsolute, join, relative, resolve, sep } from 'node:path'
import { fileURLToPath } from 'node:url'
import { promisify } from 'node:util'

// Diễn tập chỉ dùng project Compose và volume mới, không nhận cấu hình môi trường phục vụ.
const exec = promisify(execFile)
const repository = resolve(dirname(fileURLToPath(import.meta.url)), '../../../../..')
const [outputArg, candidate, previous] = process.argv.slice(2)
assert(outputArg && candidate && previous, 'Cần thư mục mới ngoài repo và hai ảnh local đã có')
const output = resolve(outputArg)
const distance = relative(repository, output)
assert(distance.startsWith('..' + sep) || isAbsolute(distance), 'Đầu ra phải ngoài repository')
for (const image of [candidate, previous]) assert.match(image, /^hueqr-local:[a-zA-Z0-9_.-]+$/)
assert.notEqual(candidate, previous, 'Cần hai tag khác nhau để diễn tập đổi ảnh')
assert.equal(process.platform, 'win32', 'Cần ACL Windows cho tệp cấu hình riêng')
const docker = async (args, options = {}) => (await exec('docker', args, {
  timeout: 120000, windowsHide: true, maxBuffer: 4 * 1024 * 1024, ...options,
})).stdout.trim()
const inspectImage = async image => JSON.parse(await docker(['image', 'inspect', image]))[0].Id
const images = { previous: await inspectImage(previous), candidate: await inspectImage(candidate) }
assert.notEqual(images.previous, images.candidate, 'Hai ảnh phải có ID khác nhau')
const project = 'hueqr-compose-check-' + randomUUID().slice(0, 8)
const port = await new Promise((done, reject) => {
  const server = createServer().once('error', reject)
  server.listen(0, '127.0.0.1', () => {
    const value = server.address().port
    server.close(() => done(value))
  })
})
await mkdir(output, { recursive: false })
const account = (await exec('whoami.exe', [], { windowsHide: true })).stdout.trim()
await exec('icacls.exe', [output, '/inheritance:r', '/grant:r', `${account}:(OI)(CI)F`, 'SYSTEM:(OI)(CI)F'], { windowsHide: true })
const password = randomBytes(32).toString('hex')
const signingKey = randomBytes(32).toString('base64')
const epoch = randomUUID()
const environment = join(output, 'private.env')
const override = join(output, 'limits.yaml')
const report = { startedAt: new Date().toISOString(), project, images, stages: [], status: 'running',
  scope: 'isolated-compose-no-provider-no-approved-content', port, servicesStopped: false }
await writeFile(environment, [
  `HUEQR_DB_PASSWORD=${password}`, `HUEQR_GUEST_SIGNING_KEY_BASE64=${signingKey}`,
  `HUEQR_SERVICE_EPOCH=${epoch}`, `HUEQR_HTTP_PORT=${port}`,
  'HUEQR_TTS_ENABLED=false', 'HUEQR_TTS_LOCAL_ENABLED=false',
  'HUEQR_CLEANUP_ENABLED=false', 'HUEQR_RETENTION_ENABLED=false',
].join('\n') + '\n', { flag: 'wx' })
await writeFile(override, 'services:\n  database:\n    mem_limit: 384m\n  application:\n    mem_limit: 640m\n', { flag: 'wx' })
const compose = async (image, args) => docker(['compose', '--project-name', project, '--env-file', environment,
  '-f', join(repository, 'deploy/compose.yaml'), '-f', override, ...args],
{ env: { ...process.env, HUEQR_IMAGE: image, HUEQR_TTS_API_KEY: '',
  HUEQR_TTS_ENABLED: 'false', HUEQR_TTS_LOCAL_ENABLED: 'false',
  HUEQR_CLEANUP_ENABLED: 'false', HUEQR_RETENTION_ENABLED: 'false',
  HUEQR_DB_PASSWORD: password, HUEQR_GUEST_SIGNING_KEY_BASE64: signingKey,
  HUEQR_SERVICE_EPOCH: epoch, HUEQR_HTTP_PORT: String(port) } })
const container = async service => {
  const id = await compose(candidate, ['ps', '--all', '-q', service])
  assert.match(id, /^[a-f0-9]{12,64}$/)
  return JSON.parse(await docker(['inspect', id]))[0]
}
const sql = async statement => {
  const db = await container('database')
  // Chỉ truyền mật khẩu trong môi trường tiến trình, không đưa vào argv hoặc báo cáo.
  const child = exec('docker', ['exec', '-i', '-e', 'MYSQL_PWD', db.Id,
    'mysql', '--default-character-set=utf8mb4', '-u', 'hueqr', '-N', '-B', 'hueqr'],
  { env: { ...process.env, MYSQL_PWD: password }, timeout: 30000, windowsHide: true })
  child.child.stdin.end(statement)
  return (await child).stdout.trim()
}
const pause = () => new Promise(done => setTimeout(done, 1000))
const ready = async () => {
  for (let i = 0; i < 120; i++) {
    try {
      const response = await fetch(`http://127.0.0.1:${port}/api/v1/auth/csrf`, { signal: AbortSignal.timeout(2000) })
      if (response.ok && (await response.json()).token) return
    } catch {}
    await pause()
  }
  throw new Error('APPLICATION_NOT_READY')
}
const marker = randomBytes(48).toString('hex')
const markerPath = '/data/audio/.compose-rehearsal-checkpoint'
const snapshot = async (name, image) => {
  await ready()
  const app = await container('application'), db = await container('database')
  assert.equal(app.Image, images[image])
  assert.deepEqual(db.HostConfig.PortBindings || {}, {})
  for (const binding of Object.values(app.HostConfig.PortBindings).flat()) assert.equal(binding.HostIp, '127.0.0.1')
  const env = Object.fromEntries(app.Config.Env.map(line => { const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)] }))
  for (const flag of ['HUEQR_TTS_ENABLED', 'HUEQR_TTS_LOCAL_ENABLED', 'HUEQR_CLEANUP_ENABLED', 'HUEQR_RETENTION_ENABLED']) assert.equal(env[flag], 'false')
  assert.equal(env.HUEQR_SERVICE_EPOCH, epoch)
  assert.equal(env.HUEQR_GUEST_SIGNING_KEY_BASE64, signingKey)
  assert.equal(await docker(['exec', app.Id, 'cat', markerPath]), marker)
  const counts = await sql('SELECT (SELECT COUNT(*) FROM place), (SELECT COUNT(*) FROM content_slot), (SELECT COUNT(*) FROM content_pair), (SELECT COUNT(*) FROM place WHERE visible=TRUE);')
  assert.equal(counts, '5\t20\t0\t0')
  const migrations = await sql('SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;')
  for (const page of ['/', '/admin.html']) assert((await fetch(`http://127.0.0.1:${port}${page}`)).ok)
  const state = { name, imageId: app.Image, counts: { places: 5, slots: 20, pairs: 0, visible: 0 },
    mysqlVolume: db.Mounts.find(m => m.Destination === '/var/lib/mysql')?.Name,
    audioVolume: app.Mounts.find(m => m.Destination === '/data/audio')?.Name,
    migrationHash: createHash('sha256').update(migrations).digest('hex'),
    audioMarkerHash: createHash('sha256').update(marker).digest('hex'),
    stableRuntimeKeys: true, loopbackOnly: true, workersDisabled: true }
  assert(state.mysqlVolume?.startsWith(project + '_'))
  assert(state.audioVolume?.startsWith(project + '_'))
  const baseline = report.stages[0]
  if (baseline) for (const key of ['mysqlVolume', 'audioVolume', 'migrationHash', 'audioMarkerHash']) assert.equal(state[key], baseline[key])
  report.stages.push(state)
  console.log('PASS ' + name)
}
let stage = 'configuration', created = false
try {
  assert.equal(await docker(['ps', '-aq', '--filter', `label=com.docker.compose.project=${project}`]), '')
  await compose(previous, ['config', '--quiet'])
  stage = 'first-start'
  created = true
  await compose(previous, ['up', '-d', '--no-build', '--pull', 'never'])
  await ready()
  const seed = await readFile(join(repository, 'backend/src/main/resources/db/seed/five-places.sql'), 'utf8')
  await sql(seed)
  await sql(seed)
  const app = await container('application')
  const child = exec('docker', ['exec', '-i', app.Id, 'sh', '-c', `cat > ${markerPath}`], { windowsHide: true })
  child.child.stdin.end(marker)
  await child
  await snapshot('initial-seed-idempotent', 'previous')
  stage = 'restart'
  await compose(previous, ['restart'])
  await snapshot('restart-persistence', 'previous')
  stage = 'update'
  await compose(candidate, ['up', '-d', '--no-build', '--pull', 'never', '--no-deps', '--force-recreate', 'application'])
  await snapshot('application-update', 'candidate')
  stage = 'rollback'
  await compose(previous, ['up', '-d', '--no-build', '--pull', 'never', '--no-deps', '--force-recreate', 'application'])
  await snapshot('application-rollback', 'previous')
  report.status = 'passed'
} catch {
  report.status = 'failed'
  report.failedStage = stage
  console.error('FAIL ' + stage + '; inspect isolated containers without sharing their environment')
  process.exitCode = 1
} finally {
  if (created) {
    try { await compose(previous, ['stop', '--timeout', '30']); report.servicesStopped = true }
    catch { report.stopFailed = true; process.exitCode = 1 }
  }
  report.finishedAt = new Date().toISOString()
  await writeFile(join(output, 'report.json'), JSON.stringify(report, null, 2))
  console.log('Report: ' + join(output, 'report.json'))
}
