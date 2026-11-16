import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { readFile, writeFile, mkdir, access } from 'node:fs/promises'
import { resolve, join, relative, isAbsolute, sep } from 'node:path'
import { randomUUID, createHash } from 'node:crypto'
import { fileURLToPath } from 'node:url'
import { parseRuntimeEnv } from './runtime-env.mjs'

// Chỉ cập nhật V14 cùng schema: backup trước, gate đóng lúc đổi ảnh; lỗi không hạ migration.
const [directoryArg, candidate, expectedSource, backupArg] = process.argv.slice(2)
assert(directoryArg && candidate && expectedSource && backupArg,
  'Usage: update-image.mjs runtime candidate-image source-sha new-backup-directory')
assert.match(candidate, /^hueqr-local:[A-Za-z0-9_.-]+$/)
assert.match(expectedSource, /^[a-f0-9]{40}$/)
const root = resolve(fileURLToPath(new URL('..', import.meta.url))), directory = resolve(directoryArg)
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
assert.match(runtime.project, /^(audioqr-hue|hueqr-(ops-check|rehearsal)-[a-f0-9]{8})$/)
const originalConfig = await readFile(runtime.configPath, 'utf8')
const settings = parseRuntimeEnv(originalConfig)
assert.equal(settings.HUEQR_IMAGE, runtime.image, 'Configuration/image identity mismatch')
assert.equal(settings.HUEQR_TTS_ENABLED, 'false', 'Provider must remain off during image update')
assert.equal(settings.HUEQR_TTS_LOCAL_ENABLED, 'false', 'Converter must remain off during image update')
const backup = resolve(backupArg)
for (const parent of [root, directory]) {
  const delta = relative(parent, backup)
  assert(delta.startsWith('..' + sep) || isAbsolute(delta), 'Backup must be outside runtime and repository')
}
try { await access(backup); assert.fail('Backup directory must be new') } catch (error) { if (error.code !== 'ENOENT') throw error }
const docker = (args, input, env = process.env) => execFileSync('docker', args, {
  input, env, encoding: 'utf8', windowsHide: true, timeout: 120000, maxBuffer: 16 * 1024 * 1024, stdio: ['pipe', 'pipe', 'pipe'],
}).trim()
const inspect = name => JSON.parse(docker(['inspect', name]))[0]
const image = JSON.parse(docker(['image', 'inspect', candidate]))[0]
assert.equal(image.Config.Labels?.['org.opencontainers.image.revision'], expectedSource, 'Candidate source label mismatch')
assert(image.Config.Entrypoint?.includes('/app/backend.jar'), 'Only packaged application image accepted')
const sql = statement => docker(['exec', '-i', runtime.database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr --default-character-set=utf8mb4 -N -B hueqr'], statement)
const schemaSql = 'SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;'
const schema = sql(schemaSql)
assert.equal(schema.split('\n').at(-1).split('\t')[0], '14', 'V13 upgrade uses the separate offline procedure')
assert.equal(sql('SELECT mode FROM ops_guard WHERE id=1;'), 'RUNNING')
const originalApp = inspect(runtime.application), originalDb = inspect(runtime.database)
assert.equal(originalApp.Image, runtime.imageId)
assert(originalApp.State.Running && originalDb.State.Running)
const volumes = c => c.Mounts.filter(m => m.Type === 'volume').map(m => m.Name + ':' + m.Destination).sort()
const originalVolumes = { application: volumes(originalApp), database: volumes(originalDb) }
const composeFiles = ['-f', join(root, 'deploy/compose.yaml')]
try { await access(join(directory, 'limits.yaml')); composeFiles.push('-f', join(directory, 'limits.yaml')) } catch (error) { if (error.code !== 'ENOENT') throw error }
const compose = (args, config) => docker(['compose', '-p', runtime.project, '--env-file', runtime.configPath,
  ...composeFiles, ...args], undefined, { ...process.env, ...config })
const run = args => execFileSync(process.execPath, args, { cwd: root, encoding: 'utf8', windowsHide: true,
  timeout: 300000, maxBuffer: 8 * 1024 * 1024, stdio: ['ignore', 'pipe', 'pipe'] }).trim()
const maintenance = (action, ...args) => JSON.parse(run(['deploy/maintenance.mjs', directory, action, ...args]))
const token = randomUUID(), operation = join(directory, 'image-update-' + token)
await mkdir(operation)
await writeFile(join(operation, 'previous.env'), originalConfig, { flag: 'wx' })
await writeFile(join(operation, 'recovery-private.json'), JSON.stringify({ runtimePath: directory, maintenanceToken: token, previousImage: runtime.image,
  candidate, expectedSource, backup, schema }, null, 2), { flag: 'wx' })
const origin = new URL(runtime.url)
assert(['localhost', '127.0.0.1'].includes(origin.hostname) && origin.protocol === 'http:')
async function closedReady() {
  for (let i = 0; i < 120; i++) {
    try {
      const response = await fetch(new URL('/api/v1/auth/csrf', origin), { redirect: 'error', signal: AbortSignal.timeout(2000) })
      if (response.status === 503 && (await response.json()).code === 'SERVICE_UNAVAILABLE') return
    } catch {}
    await new Promise(done => setTimeout(done, 1000))
  }
  throw Error('CANDIDATE_NOT_READY_WITH_CLOSED_GATE')
}
async function openReady() {
  for (let i = 0; i < 15; i++) {
    try {
      const response = await fetch(new URL('/api/v1/auth/csrf', origin), { redirect: 'error', signal: AbortSignal.timeout(2000) })
      if (response.status === 200) return
    } catch {}
    await new Promise(done => setTimeout(done, 1000))
  }
  throw Error('APPLICATION_NOT_READY_AFTER_RESUME')
}
const report = { startedAt: new Date().toISOString(), status: 'RUNNING', candidateImage: image.Id, source: expectedSource,
  previousImage: originalApp.Image, schemaChanged: false, epochChanged: false, providerRequests: 0 }
let stage = 'quiesce', frozen = false, switched = false
try {
  maintenance('quiesce', token)
  let drained = false
  for (let i = 0; i < 60; i++) {
    const state = maintenance('status')
    assert.equal(state.owner, token)
    drained = ['activities', 'tasks', 'claims', 'deleting'].every(key => state[key] === 0)
    if (drained) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(drained, 'DRAIN_NOT_COMPLETE')
  maintenance('freeze', token); frozen = true
  stage = 'backup'
  report.backup = maintenance('backup', token, backup)
  stage = 'candidate'
  await writeFile(runtime.configPath, originalConfig.replace(/^HUEQR_IMAGE=.*$/m, 'HUEQR_IMAGE=' + candidate))
  switched = true
  compose(['up', '-d', '--no-deps', '--no-build', '--pull', 'never', 'application'], { ...settings, HUEQR_IMAGE: candidate })
  await closedReady()
  assert.equal(sql(schemaSql), schema, 'SCHEMA_CHANGED_KEEP_FROZEN')
  assert.equal(inspect(runtime.application).Image, image.Id)
  assert.deepEqual(volumes(inspect(runtime.application)), originalVolumes.application)
  assert.deepEqual(volumes(inspect(runtime.database)), originalVolumes.database)
  const currentEnv = Object.fromEntries(inspect(runtime.application).Config.Env.map(line => { const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)] }))
  assert.equal(currentEnv.HUEQR_SERVICE_EPOCH, runtime.serviceEpoch)
  assert.equal(currentEnv.HUEQR_GUEST_SIGNING_KEY_BASE64, runtime.signingKeyBase64)
  stage = 'resume'
  maintenance('resume', token)
  await openReady()
  await writeFile(join(directory, 'runtime.json'), JSON.stringify({ ...runtime, image: candidate, imageId: image.Id }, null, 2))
  report.status = 'IMAGE_UPDATE_VERIFIED'
  report.volumesPreserved = true
  report.epochAndSigningKeyPreserved = true
} catch (error) {
  report.status = 'FAILED'; report.stage = stage; report.error = error.name
  // Chỉ quay ảnh cũ khi schema không đổi và gate vẫn thuộc đúng owner; không ép xóa lease.
  if (frozen && switched) {
    try {
      report.schemaChanged = sql(schemaSql) !== schema
      const state = maintenance('status')
      assert(!report.schemaChanged && state.owner === token && state.mode === 'FROZEN')
      await writeFile(runtime.configPath, originalConfig)
      compose(['up', '-d', '--no-deps', '--no-build', '--pull', 'never', 'application'], settings)
      await closedReady()
      maintenance('resume', token)
      report.previousImageRestored = true
    } catch { report.previousImageRestored = false }
  }
  process.exitCode = 1
} finally {
  report.finishedAt = new Date().toISOString()
  report.configurationSha256 = createHash('sha256').update(await readFile(runtime.configPath)).digest('hex')
  await writeFile(join(operation, 'result.json'), JSON.stringify(report, null, 2))
  console.log(JSON.stringify({ ...report, operationDirectory: operation }))
}
