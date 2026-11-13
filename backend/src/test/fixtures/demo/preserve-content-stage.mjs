import assert from 'node:assert/strict'
import { readFile, writeFile } from 'node:fs/promises'
import { execFileSync } from 'node:child_process'
import { join, resolve } from 'node:path'

const directory = resolve(process.argv[2]), root = process.cwd()
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
assert(runtime.contentStage && /^hueqr-ops-check-[a-f0-9]{8}$/.test(runtime.project))
const browser = JSON.parse(await readFile(join(directory, 'admin-review-browser.json'), 'utf8'))
assert.equal(browser.status, 'PASSED')
assert.equal(browser.cases, 14)
const docker = (args, input, env = process.env) => execFileSync('docker', args, { input, env, encoding: 'utf8',
  windowsHide: true, timeout: 120000, maxBuffer: 8 * 1024 * 1024, stdio: ['pipe', 'pipe', 'pipe'] })
const sql = input => docker(['exec', '-i', runtime.database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr -N -B hueqr'], input).trim()
const activeJobs = Number(sql("SELECT COUNT(*) FROM tts_job WHERE phase NOT IN ('SUCCEEDED','FAILED','DISCARDED');"))
assert.equal(activeJobs, 0, 'Uncertain or active provider outcomes must be inspected before shutdown')
const maintain = action => execFileSync(process.execPath, ['deploy/maintenance.mjs', directory, action, runtime.maintenanceToken],
  { windowsHide: true, timeout: 120000, stdio: 'pipe' })
maintain('quiesce')
maintain('freeze')
const original = await readFile(runtime.configPath, 'utf8')
const flags = ['HUEQR_TTS_ENABLED', 'HUEQR_TTS_LOCAL_ENABLED', 'HUEQR_CLEANUP_ENABLED', 'HUEQR_RETENTION_ENABLED']
let config = original
for (const flag of flags) config = config.replace(new RegExp('^' + flag + '=.*$', 'm'), flag + '=false')
config = config.replace(/^HUEQR_TTS_API_KEY=.*$/m, 'HUEQR_TTS_API_KEY=')
await writeFile(runtime.configPath, config)
const settings = Object.fromEntries(config.split(/\r?\n/).filter(line => /^[A-Z_]+=/.test(line))
  .map(line => { const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)] }))
assert(flags.every(flag => settings[flag] === 'false'))
assert.equal(settings.HUEQR_TTS_API_KEY, '')
docker(['compose', '-p', runtime.project, '--env-file', runtime.configPath, '-f', join(root, 'deploy/compose.yaml'),
  '-f', join(directory, 'limits.yaml'), 'create', '--no-build', '--pull', 'never', '--force-recreate', 'application'],
undefined, { ...process.env, ...settings })
const app = JSON.parse(docker(['inspect', runtime.application]))[0]
assert.equal(app.State.Running, false)
const actual = Object.fromEntries(app.Config.Env.map(line => { const i = line.indexOf('='); return [line.slice(0, i), line.slice(i + 1)] }))
assert(flags.every(flag => actual[flag] === 'false'))
assert.equal(actual.HUEQR_TTS_API_KEY, '')
await writeFile(join(directory, 'runtime.json'), JSON.stringify({ ...runtime, providerEnabled: false }, null, 2))
const destination = directory + '-backup'
execFileSync(process.execPath, ['deploy/maintenance.mjs', directory, 'backup', runtime.maintenanceToken, destination],
  { windowsHide: true, timeout: 120000, stdio: 'pipe' })
const manifest = JSON.parse(await readFile(join(destination, 'manifest.json'), 'utf8'))
assert.equal(manifest.status, 'COMPLETE')
assert.equal(manifest.files.length, 30)
const newPairsApplied = Number(sql('SELECT COUNT(*) FROM content_pair;'))
docker(['stop', '--timeout', '30', runtime.database])
await writeFile(join(directory, 'content-preservation.json'), JSON.stringify({ recordedAt: new Date().toISOString(),
  status: 'FROZEN_BACKED_UP_ALL_WORKERS_DISABLED', files: manifest.files.length, backupVerified: true,
  restoreOfThisSnapshotTested: false, providerKeyRemovedFromStageConfiguration: true,
  newPairsApplied, applicationStopped: true, databaseStopped: true, originalServingRuntimeModified: false }, null, 2))
console.log(JSON.stringify({ status: 'FROZEN_BACKED_UP_ALL_WORKERS_DISABLED', files: manifest.files.length }))
