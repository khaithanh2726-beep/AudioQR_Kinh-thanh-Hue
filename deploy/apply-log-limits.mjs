import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { readFile, writeFile, access } from 'node:fs/promises'
import { resolve, join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'
import { randomUUID } from 'node:crypto'
import { parseRuntimeEnv } from './runtime-env.mjs'
import { plainDirectory } from './operations-retention.mjs'

// Recreate cùng ảnh/volume, có backup và gate; không sửa tay file log của Docker daemon.
const [runtimeArg, backupArg] = process.argv.slice(2)
assert(runtimeArg && backupArg, 'Usage: apply-log-limits.mjs runtime new-backup-directory')
const root = resolve(dirname(fileURLToPath(import.meta.url)), '..'), directory = await plainDirectory(runtimeArg)
const runtime = JSON.parse((await readFile(join(directory, 'runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
const settings = parseRuntimeEnv(await readFile(runtime.configPath, 'utf8'))
assert.equal(settings.HUEQR_IMAGE, runtime.image)
assert.equal(settings.HUEQR_TTS_ENABLED, 'false'); assert.equal(settings.HUEQR_TTS_LOCAL_ENABLED, 'false')
const docker = (args, input) => execFileSync('docker', args, { input, encoding: 'utf8', windowsHide: true,
  timeout: 180000, stdio: ['pipe', 'pipe', 'pipe'], maxBuffer: 4 * 1024 * 1024, env: { ...process.env, ...settings } }).trim()
const inspect = name => JSON.parse(docker(['inspect', name]))[0]
const sql = statement => docker(['exec', '-i', runtime.database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr -N -B hueqr'], statement)
const originalApp = inspect(runtime.application), originalDb = inspect(runtime.database)
assert(originalApp.State.Running && originalDb.State.Running)
const volumes = value => value.Mounts.filter(m => m.Type === 'volume').map(m => m.Name + ':' + m.Destination).sort()
const schemaSql = 'SELECT version,checksum,success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank;'
const schema = sql(schemaSql)
const files = ['-f', join(root, 'deploy/compose.yaml')]
try { await access(join(directory, 'limits.yaml')); files.push('-f', join(directory, 'limits.yaml')) } catch (e) { if (e.code !== 'ENOENT') throw e }
const compose = args => docker(['compose', '-p', runtime.project, '--env-file', runtime.configPath, ...files, ...args])
const token = randomUUID()
const maintenance = (action, ...args) => JSON.parse(execFileSync(process.execPath,
  [join(root, 'deploy/maintenance.mjs'), directory, action, ...args], { encoding: 'utf8', windowsHide: true,
    timeout: 600000, maxBuffer: 4 * 1024 * 1024, stdio: ['ignore', 'pipe', 'pipe'] }))
const report = { kind: 'docker-log-policy', status: 'RUNNING', startedAt: new Date().toISOString(), operationId: token }
try {
  assert.equal(maintenance('status').mode, 'RUNNING')
  maintenance('quiesce', token)
  let drained = false
  for (let i = 0; i < 60; i++) {
    const state = maintenance('status'); assert.equal(state.owner, token)
    drained = ['activities', 'tasks', 'claims', 'deleting'].every(k => state[k] === 0)
    if (drained) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(drained); maintenance('freeze', token)
  maintenance('backup', token, resolve(backupArg))
  compose(['up', '-d', '--no-deps', '--no-build', '--pull', 'never', 'database'])
  let ready = false
  for (let i = 0; i < 90; i++) {
    try { ready = sql('SELECT 1;') === '1' } catch {}
    if (ready) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(ready); assert.equal(sql(schemaSql), schema)
  assert.equal(sql('SELECT mode FROM ops_guard WHERE id=1;'), 'FROZEN')
  compose(['up', '-d', '--no-deps', '--no-build', '--pull', 'never', 'application'])
  for (const [name, previous] of [[runtime.application, originalApp], [runtime.database, originalDb]]) {
    const current = inspect(name)
    assert.equal(current.Image, previous.Image); assert.deepEqual(volumes(current), volumes(previous))
    assert.deepEqual(current.HostConfig.LogConfig, { Type: 'local', Config: { 'max-file': '3', 'max-size': '10m' } })
  }
  const env = Object.fromEntries(inspect(runtime.application).Config.Env.map(line => { const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)] }))
  assert.equal(env.HUEQR_GUEST_SIGNING_KEY_BASE64, runtime.signingKeyBase64)
  assert.equal(env.HUEQR_SERVICE_EPOCH, runtime.serviceEpoch)
  let closed = false
  for (let i = 0; i < 120; i++) {
    try { closed = (await fetch(new URL('/api/v1/auth/csrf', runtime.url), { signal: AbortSignal.timeout(2000) })).status === 503 } catch {}
    if (closed) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(closed); maintenance('resume', token)
  assert.equal((await fetch(new URL('/api/v1/auth/csrf', runtime.url), { signal: AbortSignal.timeout(5000) })).status, 200)
  Object.assign(report, { status: 'LOG_POLICY_VERIFIED', schemaAndVolumesPreserved: true, keysPreserved: true,
    policy: { driver: 'local', maxSize: '10m', maxFiles: 3 }, reopened: true })
} catch {
  report.status = 'FAILED'; report.code = 'KEEP_GATE_FOR_OPERATOR_REVIEW'; process.exitCode = 1
} finally {
  report.finishedAt = new Date().toISOString()
  await writeFile(join(directory, 'log-policy-' + token + '.json'), JSON.stringify(report, null, 2), { flag: 'wx' })
}
console.log(JSON.stringify(report))
