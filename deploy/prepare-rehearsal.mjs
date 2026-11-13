import assert from 'node:assert/strict'
import { execFile } from 'node:child_process'
import { createHash, randomBytes, randomUUID } from 'node:crypto'
import { copyFile, mkdir, readFile, realpath, writeFile } from 'node:fs/promises'
import { createServer } from 'node:net'
import { dirname, isAbsolute, join, relative, resolve, sep } from 'node:path'
import { fileURLToPath } from 'node:url'
import { promisify } from 'node:util'

// Chỉ chuẩn bị bản tập dợt từ bộ kiểm provider đã dừng, không nhận database phục vụ.
const exec = promisify(execFile)
const repository = resolve(dirname(fileURLToPath(import.meta.url)), '..')
const [sourceArg, outputArg, image] = process.argv.slice(2)
assert(sourceArg && outputArg && image, 'Cần runtime provider, thư mục mới ngoài repo và ảnh đã build')
assert.equal(process.platform, 'win32', 'Cần ACL Windows để giữ cấu hình riêng')
assert.match(image, /^hueqr-local:[A-Za-z0-9_.-]+$/)
const javaVersion = await exec('java', ['-version'], { windowsHide: true })
assert.match(javaVersion.stderr, /version "21[."]/, 'Cần Java 21 đã cấu hình trong PATH')
const source = await realpath(sourceArg), output = resolve(outputArg)
function outside(parent, child) {
  const delta = relative(parent, child)
  return delta.startsWith('..' + sep) || isAbsolute(delta)
}
assert(outside(repository, output) && outside(source, output), 'Đích phải tách repo và runtime nguồn')
const json = async file => JSON.parse(await readFile(file, 'utf8'))
const origin = await json(join(source, 'provider-runtime.json'))
const proof = await json(join(source, 'provider-verification.json'))
const provider = await json(join(source, 'provider-result.json'))
assert.match(origin.project, /^hueqr-compose-check-[a-f0-9]{8}$/)
assert.equal(proof.status, 'passed')
assert.equal(proof.containersStopped, true)
assert.equal(provider.status, 'passed')
assert.equal(provider.providerCreateRequests, 1)
assert.equal(proof.files.length, 2)
const docker = async (args, options = {}) => (await exec('docker', args, {
  timeout: 120000, windowsHide: true, maxBuffer: 8 * 1024 * 1024, ...options,
})).stdout.trim()
const inspect = async name => JSON.parse(await docker(['inspect', name]))[0]
const originalDb = await inspect(origin.database), originalApp = await inspect(origin.application)
for (const container of [originalDb, originalApp]) {
  assert.equal(container.Config.Labels['com.docker.compose.project'], origin.project)
  assert.equal(container.State.Running, false, 'Dừng toàn bộ nguồn trước khi chuẩn bị bản sao')
}
assert.equal(originalDb.Config.Image, 'mysql:8.4.13')
const originalEnv = Object.fromEntries(originalDb.Config.Env.map(line => {
  const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)]
}))
assert.equal(originalEnv.MYSQL_DATABASE, 'hueqr')
assert.equal(originalEnv.MYSQL_USER, 'hueqr')
const imageId = JSON.parse(await docker(['image', 'inspect', image]))[0].Id
const port = 18083
await new Promise((done, reject) => {
  const server = createServer().once('error', reject)
  server.listen(port, '127.0.0.1', () => server.close(done))
})
await mkdir(output)
const account = (await exec('whoami.exe', [], { windowsHide: true })).stdout.trim()
await exec('icacls.exe', [output, '/inheritance:r', '/grant:r', `${account}:(OI)(CI)F`, 'SYSTEM:(OI)(CI)F'], { windowsHide: true })
const project = 'hueqr-rehearsal-' + randomUUID().slice(0, 8)
const password = randomBytes(32).toString('hex')
const signingKey = randomBytes(32).toString('base64'), epoch = randomUUID()
const adminPassword = randomBytes(24).toString('base64url')
const configPath = join(output, 'private.env'), limits = join(output, 'limits.yaml')
await writeFile(configPath, [`HUEQR_IMAGE=${image}`, `HUEQR_DB_PASSWORD=${password}`,
  `HUEQR_GUEST_SIGNING_KEY_BASE64=${signingKey}`, `HUEQR_SERVICE_EPOCH=${epoch}`, `HUEQR_HTTP_PORT=${port}`,
  'HUEQR_PUBLIC_START_URL=', 'HUEQR_TTS_ENABLED=false', 'HUEQR_TTS_LOCAL_ENABLED=false',
  'HUEQR_CLEANUP_ENABLED=false', 'HUEQR_RETENTION_ENABLED=false', 'HUEQR_TTS_API_KEY=',
].join('\n') + '\n', { flag: 'wx' })
await writeFile(limits, 'services:\n  database:\n    mem_limit: 384m\n  application:\n    mem_limit: 640m\n', { flag: 'wx' })
const env = { ...process.env, HUEQR_IMAGE: image, HUEQR_DB_PASSWORD: password,
  HUEQR_GUEST_SIGNING_KEY_BASE64: signingKey, HUEQR_SERVICE_EPOCH: epoch, HUEQR_HTTP_PORT: String(port),
  HUEQR_PUBLIC_START_URL: '', HUEQR_TTS_ENABLED: 'false', HUEQR_TTS_LOCAL_ENABLED: 'false',
  HUEQR_CLEANUP_ENABLED: 'false', HUEQR_RETENTION_ENABLED: 'false', HUEQR_TTS_API_KEY: '' }
const compose = args => docker(['compose', '-p', project, '--env-file', configPath,
  '-f', join(repository, 'deploy/compose.yaml'), '-f', limits, ...args], { env })
async function sql(container, dbPassword, statement) {
  const command = exec('docker', ['exec', '-i', '-e', 'MYSQL_PWD', container, 'mysql',
    '--default-character-set=utf8mb4', '-u', 'hueqr', '-N', '-B', 'hueqr'],
  { env: { ...process.env, MYSQL_PWD: dbPassword }, timeout: 60000, windowsHide: true, maxBuffer: 8 * 1024 * 1024 })
  command.child.stdin.end(statement)
  return (await command).stdout.trim()
}
const pause = () => new Promise(done => setTimeout(done, 1000))
async function databaseReady(container, dbPassword) {
  for (let i = 0; i < 120; i++) {
    try { if (await sql(container, dbPassword, 'SELECT 1;') === '1') return } catch {}
    await pause()
  }
  throw new Error('DATABASE_NOT_READY')
}
const report = { startedAt: new Date().toISOString(), project, image, imageId,
  status: 'preparing', public: false, sourceProject: origin.project, files: [] }
let stage = 'source', sourceStarted = false, targetCreated = false
try {
  await docker(['start', origin.database]); sourceStarted = true
  await databaseReady(origin.database, originalEnv.MYSQL_PASSWORD)
  // Chỉ nhận bộ nguồn một job thật, không phiên khách/cặp/duyệt hoặc tác vụ đang xử lý.
  const guard = await sql(origin.database, originalEnv.MYSQL_PASSWORD, `SELECT
    (SELECT COUNT(*) FROM place), (SELECT COUNT(*) FROM content_slot),
    (SELECT COUNT(*) FROM tts_job), (SELECT COUNT(*) FROM tts_job WHERE phase='SUCCEEDED'),
    (SELECT COUNT(*) FROM content_pair)+(SELECT COUNT(*) FROM place WHERE visible=TRUE),
    (SELECT COUNT(*) FROM guest_session)+(SELECT COUNT(*) FROM runtime_task)+(SELECT COUNT(*) FROM file_claim),
    (SELECT COUNT(*) FROM text_review_ref)+(SELECT COUNT(*) FROM audio_review_ref);`)
  assert.equal(guard, '5\t20\t1\t1\t0\t0\t0')
  assert.equal((await inspect(origin.application)).State.Running, false)
  const dump = await docker(['exec', '-e', 'MYSQL_PWD', origin.database, 'mysqldump', '-u', 'hueqr',
    '--single-transaction', '--no-tablespaces', '--hex-blob', '--set-gtid-purged=OFF', '--skip-comments',
    '--default-character-set=utf8mb4', 'hueqr'], { env: { ...process.env, MYSQL_PWD: originalEnv.MYSQL_PASSWORD } })
  report.dumpHash = createHash('sha256').update(dump).digest('hex')
  await docker(['stop', origin.database]); sourceStarted = false
  const audio = join(output, 'audio-import')
  await mkdir(audio)
  for (const file of proof.files) {
    assert.match(file.storageKey, /^[a-f0-9-]{36}\.(wav|mp3)$/)
    const input = await realpath(join(source, 'provider-audio', file.storageKey))
    assert.equal(outside(join(source, 'provider-audio'), input), false)
    const bytes = await readFile(input)
    assert.equal(bytes.length, file.bytes)
    assert.equal(createHash('sha256').update(bytes).digest('hex'), file.sha256)
    await copyFile(input, join(audio, file.storageKey))
    report.files.push({ storageKey: file.storageKey, sha256: file.sha256, bytes: file.bytes })
  }
  stage = 'target-database'
  assert.equal(await docker(['ps', '-aq', '--filter', `label=com.docker.compose.project=${project}`]), '')
  await compose(['config', '--quiet'])
  targetCreated = true
  await compose(['up', '-d', '--no-build', '--pull', 'never', 'database'])
  const database = await compose(['ps', '-q', 'database'])
  await databaseReady(database, password)
  assert.equal(await sql(database, password, 'SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE();'), '0')
  await sql(database, password, dump)
  stage = 'admin'
  const helper = join(output, 'HashRehearsalPassword.java')
  await writeFile(helper, 'import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; class HashRehearsalPassword { public static void main(String[] a) throws Exception { byte[] b=System.in.readAllBytes(); try { System.out.print("{bcrypt}"+new BCryptPasswordEncoder().encode(new String(b,java.nio.charset.StandardCharsets.UTF_8))); } finally { java.util.Arrays.fill(b,(byte)0); } } }')
  const m2 = join(process.env.USERPROFILE, '.m2/repository')
  const classpath = [join(m2, 'org/springframework/security/spring-security-crypto/7.1.2/spring-security-crypto-7.1.2.jar'),
    join(m2, 'commons-logging/commons-logging/1.3.6/commons-logging-1.3.6.jar')].join(';')
  const hashCommand = exec('java', ['-cp', classpath, helper], { timeout: 60000, windowsHide: true })
  hashCommand.child.stdin.end(adminPassword)
  const hash = (await hashCommand).stdout.trim()
  assert.match(hash, /^\{bcrypt\}\$2[aby]\$/)
  await sql(database, password, `START TRANSACTION; UPDATE admin_account SET enabled=FALSE;
    INSERT INTO admin_account(id,login_name,password_hash,enabled) VALUES(UUID_TO_BIN(UUID()),'hue-admin',
    CONVERT(UNHEX('${Buffer.from(hash).toString('hex')}') USING ascii),TRUE); COMMIT;`)
  stage = 'audio'
  await compose(['create', '--no-build', '--pull', 'never', 'application'])
  const application = await compose(['ps', '--all', '-q', 'application'])
  const app = await inspect(application), db = await inspect(database)
  assert.equal(app.Image, imageId)
  const volume = app.Mounts.find(m => m.Destination === '/data/audio')?.Name
  assert(volume?.startsWith(project + '_'))
  assert.equal(Object.keys(db.HostConfig.PortBindings || {}).length, 0)
  for (const bindings of Object.values(app.HostConfig.PortBindings)) for (const bind of bindings) assert.equal(bind.HostIp, '127.0.0.1')
  assert(!audio.includes(','))
  for (const file of report.files) {
    await docker(['run', '--rm', '--network', 'none', '--mount', `type=volume,source=${volume},target=/data/audio`,
      '--mount', `type=bind,source=${audio},target=/source,readonly`, '--entrypoint', 'cp', image,
      '/source/' + file.storageKey, '/data/audio/' + file.storageKey])
  }
  stage = 'application'
  await compose(['start', 'application'])
  let ready = false
  for (let i = 0; i < 120; i++) {
    try { ready = (await fetch(`http://127.0.0.1:${port}/api/v1/auth/csrf`, { signal: AbortSignal.timeout(2000) })).ok } catch {}
    if (ready) break
    await pause()
  }
  assert(ready, 'APPLICATION_NOT_READY')
  assert.equal((await fetch(`http://127.0.0.1:${port}/map-preview.html`)).status, 404,
    'Ảnh tập dợt không được chứa trang preview')
  for (const file of report.files) {
    const actual = await docker(['exec', application, 'sha256sum', '/data/audio/' + file.storageKey])
    assert.equal(actual.split(/\s/)[0], file.sha256)
  }
  report.counts = await sql(database, password, `SELECT (SELECT COUNT(*) FROM place),
    (SELECT COUNT(*) FROM content_slot),(SELECT COUNT(*) FROM text_revision),
    (SELECT COUNT(*) FROM audio_revision),(SELECT COUNT(*) FROM content_pair),
    (SELECT COUNT(*) FROM place WHERE visible=TRUE);`)
  assert.equal(report.counts, '5\t20\t1\t1\t0\t0')
  const runtime = { project, image, imageId, application: app.Name.slice(1), database: db.Name.slice(1),
    url: `http://localhost:${port}`, port, adminLogin: 'hue-admin', adminPassword, configPath,
    serviceEpoch: epoch, signingKeyBase64: signingKey, dbPassword: password }
  await writeFile(join(output, 'runtime.json'), JSON.stringify(runtime, null, 2), { flag: 'wx' })
  report.status = 'ready-local-awaiting-approval-https'
  report.url = runtime.url
  report.workersEnabled = false
  report.previewPageDisabled = true
  console.log('READY ' + runtime.url + '; credentials stay in private runtime.json')
} catch {
  report.status = 'failed'; report.failedStage = stage; process.exitCode = 1
  if (targetCreated) {
    try { await compose(['stop', '--timeout', '30']); report.failedTargetStopped = true } catch { report.stopFailed = true }
  }
  console.error('PREPARATION_FAILED ' + stage + '; inspect only this isolated project')
} finally {
  if (sourceStarted) {
    try { await docker(['stop', origin.database]) } catch { report.sourceStopFailed = true; process.exitCode = 1 }
  }
  report.finishedAt = new Date().toISOString()
  await writeFile(join(output, 'preparation.json'), JSON.stringify(report, null, 2))
}
