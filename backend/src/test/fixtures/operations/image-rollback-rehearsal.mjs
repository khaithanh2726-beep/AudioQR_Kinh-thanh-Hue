import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { readFile, writeFile, mkdir } from 'node:fs/promises'
import { resolve, join, relative, isAbsolute, sep } from 'node:path'
import { randomUUID, createHash } from 'node:crypto'
import { parseRuntimeEnv } from '../../../../../deploy/runtime-env.mjs'

// Gây lỗi Java thật trên bản restore riêng để kiểm nhánh quay ảnh cũ cùng schema.
const [directoryArg, outputArg] = process.argv.slice(2)
assert(directoryArg && outputArg, 'Usage: image-rollback-rehearsal.mjs private-restore new-output')
const root = process.cwd(), directory = resolve(directoryArg), output = resolve(outputArg)
for (const parent of [root, directory]) {
  const delta = relative(parent, output)
  assert(delta.startsWith('..' + sep) || isAbsolute(delta))
}
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
const proof = JSON.parse(await readFile(join(directory, 'restore-result.json'), 'utf8'))
assert.match(runtime.project, /^hueqr-ops-check-[a-f0-9]{8}$/)
assert.equal(proof.public, false)
const config = await readFile(runtime.configPath, 'utf8'), settings = parseRuntimeEnv(config)
const docker = (args, input) => execFileSync('docker', args, { input, env: { ...process.env, ...settings },
  encoding: 'utf8', windowsHide: true, timeout: 120000, maxBuffer: 8 * 1024 * 1024, stdio: ['pipe', 'pipe', 'pipe'] }).trim()
const compose = args => docker(['compose', '-p', runtime.project, '--env-file', runtime.configPath,
  '-f', join(root, 'deploy/compose.yaml'), '-f', join(directory, 'limits.yaml'), ...args])
const sql = q => docker(['exec', '-i', runtime.database, 'sh', '-c', 'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr -N -B hueqr'], q)
const image = JSON.parse(docker(['image', 'inspect', runtime.image]))[0]
const source = image.Config.Labels?.['org.opencontainers.image.revision']
assert.match(source, /^[a-f0-9]{40}$/)
assert.match(runtime.image, /^hueqr-local:[A-Za-z0-9_.-]+$/)
await mkdir(output)
const faultImage = 'hueqr-local:fault-' + randomUUID().slice(0, 8)
const recipe = `FROM ${runtime.image}\nENTRYPOINT ["java","-hueqr-invalid-rehearsal-option","-jar","/app/backend.jar"]\n`
const report = { startedAt: new Date().toISOString(), status: 'RUNNING', project: runtime.project,
  faultInjection: 'INVALID_JAVA_OPTION', candidateExpectedToFail: true, providerRequests: 0, servingRuntimeModified: false }
const hash = bytes => createHash('sha256').update(bytes).digest('hex')
try {
  compose(['up', '-d', '--no-build', '--pull', 'never', 'database'])
  let ready = false
  for (let i = 0; i < 90; i++) { try { ready = sql('SELECT 1;') === '1' } catch {} if (ready) break; await new Promise(r => setTimeout(r, 1000)) }
  assert(ready)
  assert.equal(sql('SELECT mode FROM ops_guard;'), 'RUNNING')
  const inventoryQuery = 'SELECT storage_key,LOWER(HEX(checksum)),byte_size FROM file_asset ORDER BY storage_key;'
  const inventory = sql(inventoryQuery)
  compose(['up', '-d', '--no-build', '--pull', 'never', 'application'])
  ready = false
  for (let i = 0; i < 120; i++) { try { ready = (await fetch(runtime.url + '/api/v1/auth/csrf')).status === 200 } catch {} if (ready) break; await new Promise(r => setTimeout(r, 1000)) }
  assert(ready)
  docker(['build', '--pull=false', '--network=none', '-t', faultImage, '-'], recipe)
  let failed
  try {
    execFileSync(process.execPath, ['deploy/update-image.mjs', directory, faultImage, source, join(output, 'backup')],
      { cwd: root, windowsHide: true, timeout: 600000, maxBuffer: 8 * 1024 * 1024, encoding: 'utf8', stdio: ['ignore', 'pipe', 'pipe'] })
  } catch (error) {
    assert.equal(error.status, 1)
    failed = JSON.parse(error.stdout)
  }
  assert.equal(failed?.status, 'FAILED')
  assert.equal(failed.stage, 'candidate')
  assert.equal(failed.previousImageRestored, true)
  assert.equal(failed.schemaChanged, false)
  assert.equal(sql('SELECT mode FROM ops_guard;'), 'RUNNING')
  assert.equal(JSON.parse(docker(['inspect', runtime.application]))[0].Image, runtime.imageId)
  assert.equal(hash(await readFile(runtime.configPath)), hash(config))
  assert.equal(sql(inventoryQuery), inventory)
  assert.equal((await fetch(runtime.url + '/api/v1/auth/csrf', { headers: { Connection: 'close' } })).status, 200)
  report.status = 'EXPECTED_FAILURE_AND_ROLLBACK_PASSED'
  report.previousImageRestored = true
  report.schemaAndConfigUnchanged = true
  report.realFileMetadataPreserved = true
  await writeFile(join(output, 'update-expected-failure.json'), JSON.stringify(failed, null, 2))
} catch (error) {
  report.status = 'FAILED'; report.error = error.name; process.exitCode = 1
} finally {
  try { compose(['stop']) } catch { report.stopFailed = true; process.exitCode = 1 }
  try { docker(['image', 'rm', faultImage]); report.faultImageRemoved = true } catch { report.faultImageRemoved = false }
  report.finishedAt = new Date().toISOString()
  await writeFile(join(output, 'result.json'), JSON.stringify(report, null, 2))
  console.log(JSON.stringify(report))
}
