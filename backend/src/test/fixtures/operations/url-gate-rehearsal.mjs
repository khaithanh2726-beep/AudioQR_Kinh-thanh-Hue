import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { readFile, writeFile } from 'node:fs/promises'
import { resolve, join } from 'node:path'
import { randomUUID } from 'node:crypto'

const [sourceArg, image, backup, restore] = process.argv.slice(2)
assert(sourceArg && image && backup && restore)
assert.match(image, /^hueqr-local:[a-zA-Z0-9_.-]+$/)
const directory = resolve(sourceArg), root = process.cwd()
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
assert.match(runtime.project, /^hueqr-ops-check-[a-f0-9]{8}$/)
const docker = (args, env = process.env) => execFileSync('docker', args, { env, encoding: 'utf8', windowsHide: true,
  maxBuffer: 8 * 1024 * 1024, timeout: 120000, stdio: ['pipe', 'pipe', 'pipe'] })
const run = (args, env = process.env) => execFileSync(process.execPath, args, { cwd: root, env, encoding: 'utf8', windowsHide: true,
  maxBuffer: 8 * 1024 * 1024, timeout: 600000, stdio: ['pipe', 'pipe', 'pipe'] })
const token = (await readFile(join(directory, 'normalization-token.txt'), 'utf8')).trim()
const before = await readFile(runtime.configPath, 'utf8')
const changed = before.replace(/^HUEQR_IMAGE=.*$/m, 'HUEQR_IMAGE=' + image)
assert.notEqual(changed, before)
await writeFile(join(directory, 'config-before-url-fix.env'), before, { flag: 'wx' })
await writeFile(runtime.configPath, changed)
const environment = { ...process.env, ...Object.fromEntries(changed.trim().split('\n').map(line => {
  const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)]
})) }
runtime.image = image
runtime.imageId = JSON.parse(docker(['image', 'inspect', image]))[0].Id
await writeFile(join(directory, 'runtime.json'), JSON.stringify(runtime, null, 2))
const compose = args => docker(['compose', '-p', runtime.project, '--env-file', runtime.configPath,
  '-f', join(root, 'deploy/compose.yaml'), '-f', join(directory, 'limits.yaml'), ...args], environment)
const report = { startedAt: new Date().toISOString(), sourceProject: runtime.project, imageId: runtime.imageId,
  status: 'RUNNING', providerRequests: 0, productionUpdated: false }
let target
try {
  compose(['up', '-d', '--no-build', '--pull', 'never'])
  let closed = false
  for (let i = 0; i < 90; i++) {
    try { closed = (await fetch(runtime.url + '/api/v1/auth/csrf')).status === 503 } catch {}
    if (closed) break
    await new Promise(done => setTimeout(done, 1000))
  }
  assert(closed)
  assert.equal((await fetch(runtime.url + '/%61pi/v1/auth/csrf')).status, 503)
  run(['deploy/maintenance.mjs', directory, 'resume', token])
  assert.equal((await fetch(runtime.url + '/api/v1/auth/csrf')).status, 200)
  assert.equal((await fetch(runtime.url + '/%61pi/v1/auth/csrf')).status, 200)
  report.paths = { closedNormal: 503, closedEncoded: 503, openNormal: 200, openEncoded: 200 }
  const next = randomUUID()
  for (const action of ['quiesce', 'freeze']) run(['deploy/maintenance.mjs', directory, action, next])
  run(['deploy/maintenance.mjs', directory, 'backup', next, backup])
  run(['deploy/restore-backup.mjs', backup, restore, image], { ...process.env,
    HUEQR_TTS_ENABLED: 'true', HUEQR_TTS_LOCAL_ENABLED: 'true', HUEQR_CLEANUP_ENABLED: 'true',
    HUEQR_RETENTION_ENABLED: 'true', HUEQR_TTS_API_KEY: '' })
  target = JSON.parse(await readFile(join(restore, 'runtime.json'), 'utf8'))
  const proof = JSON.parse(await readFile(join(restore, 'restore-result.json'), 'utf8'))
  assert.equal(proof.status, 'RESTORE_VERIFIED_CLOSED')
  assert.equal(proof.imageId, runtime.imageId)
  const manifest = JSON.parse(await readFile(join(backup, 'manifest.json'), 'utf8'))
  const privateBrowser = JSON.parse(await readFile(join(directory, 'browser-private.json'), 'utf8'))
  await writeFile(join(restore, 'browser-private.json'), JSON.stringify({ ...target,
    adminLogin: privateBrowser.adminLogin, adminPassword: privateBrowser.adminPassword,
    expectedMp3Hash: manifest.files.find(f => f.format === 'MP3').sha256 }, null, 2))
  // RestoreEnvironment ép worker tắt ngay cả khi biến môi trường của shell có giá trị khác.
  const config = JSON.parse(docker(['inspect', target.application]))[0].Config.Env
  for (const name of ['HUEQR_TTS_ENABLED', 'HUEQR_TTS_LOCAL_ENABLED', 'HUEQR_CLEANUP_ENABLED', 'HUEQR_RETENTION_ENABLED'])
    assert(config.includes(name + '=false'))
  report.finalBackupComplete = manifest.status === 'COMPLETE'
  report.finalRestore = { project: target.project, verifiedClosed: true, files: manifest.files.length,
    allWorkersDisabled: true, shellWorkerOverridesIgnored: true }
  report.status = 'PASSED'
} catch (error) {
  report.status = 'FAILED'; report.error = error.name
  process.exitCode = 1
} finally {
  report.finishedAt = new Date().toISOString()
  await writeFile(join(directory, 'url-gate-result.json'), JSON.stringify(report, null, 2))
  console.log(JSON.stringify(report))
  for (const name of [runtime.application, runtime.database].filter(Boolean)) {
    try { docker(['stop', '--timeout', '30', name]) } catch {}
  }
}
