import assert from 'node:assert/strict'
import { readFile, writeFile } from 'node:fs/promises'
import { execFileSync } from 'node:child_process'
import { join, resolve } from 'node:path'

const [directoryArg, credentialSource] = process.argv.slice(2)
assert(directoryArg && credentialSource)
const directory = resolve(directoryArg), root = process.cwd()
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
const proof = JSON.parse(await readFile(join(directory, 'restore-result.json'), 'utf8'))
const account = JSON.parse(await readFile(join(credentialSource, 'browser-private.json'), 'utf8'))
assert.match(runtime.project, /^hueqr-ops-check-[a-f0-9]{8}$/)
assert.equal(proof.status, 'RESTORE_VERIFIED_CLOSED')
assert.equal(runtime.image, 'hueqr-local:20261113-ops-guard-urls')
const docker = (args, input, env = process.env) => execFileSync('docker', args, { input, env, encoding: 'utf8',
  windowsHide: true, timeout: 120000, maxBuffer: 8 * 1024 * 1024, stdio: ['pipe', 'pipe', 'pipe'] })
const original = await readFile(runtime.configPath, 'utf8')
const parse = text => Object.fromEntries(text.split(/\r?\n/).filter(line => /^\s*[A-Z_]+=/.test(line)).map(line => {
  const i = line.indexOf('='); return [line.slice(0, i).trim(), line.slice(i + 1).trim().replace(/^(['"])(.*)\1$/, '$2')]
}))
assert(process.env.HUEQR_PROVIDER_KEY_FILE, 'Set HUEQR_PROVIDER_KEY_FILE outside repository')
const variables = parse(await readFile(process.env.HUEQR_PROVIDER_KEY_FILE, 'utf8'))
const key = variables.HUEQR_TTS_API_KEY || variables.HUE_TTS_API_KEY || variables.GEMINI_API_KEY || variables.GOOGLE_API_KEY
assert(key && !/[\r\n$]/.test(key), 'Use protected provider key without dotenv expansion')
const config = original.replace(/^HUEQR_TTS_ENABLED=false$/m, 'HUEQR_TTS_ENABLED=true')
  .replace(/^HUEQR_TTS_LOCAL_ENABLED=false$/m, 'HUEQR_TTS_LOCAL_ENABLED=true')
  .replace(/^HUEQR_TTS_API_KEY=.*$/m, 'HUEQR_TTS_API_KEY=' + key)
await writeFile(join(directory, 'config-before-generation.env'), original, { flag: 'wx' })
await writeFile(runtime.configPath, config)
const environment = { ...process.env, ...parse(config) }
assert.equal(environment.HUEQR_CLEANUP_ENABLED, 'false')
assert.equal(environment.HUEQR_RETENTION_ENABLED, 'false')
const compose = args => docker(['compose', '-p', runtime.project, '--env-file', runtime.configPath,
  '-f', join(root, 'deploy/compose.yaml'), '-f', join(directory, 'limits.yaml'), ...args], undefined, environment)
const sql = input => docker(['exec', '-i', runtime.database, 'sh', '-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr -N -B hueqr'], input).trim()
compose(['up', '-d', '--no-build', '--pull', 'never', 'database'])
let ready = false
for (let i = 0; i < 90; i++) {
  try { ready = sql('SELECT 1;') === '1' } catch {}
  if (ready) break
  await new Promise(done => setTimeout(done, 1000))
}
assert(ready)
assert.equal(sql('SELECT mode FROM ops_guard WHERE id=1;'), 'FROZEN')
assert.equal(sql("SELECT COUNT(*) FROM guest_session WHERE display_name NOT IN ('Operations recovery fixture','restore-test-retained');"), '0',
  'Do not remove real visitor data; only a freshly restored operations fixture is accepted')
const fixtureSessions = Number(sql('SELECT COUNT(*) FROM guest_session;'))
assert.equal(sql('SELECT (SELECT COUNT(*) FROM ops_activity)+(SELECT COUNT(*) FROM runtime_task)+(SELECT COUNT(*) FROM file_claim);'), '0')
// Làm sạch dữ liệu giả nhận diện được trong bản restore mới, không chạy trên database đang phục vụ.
sql(`START TRANSACTION;
 UPDATE guest_session SET current_opening_id=NULL,current_language=NULL,document_nonce=NULL,selection_seq=0;
 DELETE FROM guest_request_result; DELETE FROM guest_content_grant; DELETE FROM guest_open_intent;
 DELETE FROM place_view; DELETE FROM guest_session; COMMIT;`)
compose(['up', '-d', '--no-build', '--pull', 'never', 'application'])
ready = false
for (let i = 0; i < 90; i++) {
  try { ready = (await fetch(runtime.url + '/api/v1/auth/csrf')).status === 503 } catch {}
  if (ready) break
  await new Promise(done => setTimeout(done, 1000))
}
assert(ready)
execFileSync(process.execPath, ['deploy/maintenance.mjs', directory, 'resume', runtime.maintenanceToken],
  { cwd: root, windowsHide: true, timeout: 120000, stdio: 'pipe' })
await writeFile(join(directory, 'runtime.json'), JSON.stringify({ ...runtime, adminLogin: account.adminLogin,
  adminPassword: account.adminPassword, contentStage: true, providerEnabled: true }, null, 2))
await writeFile(join(directory, 'content-stage-preparation.json'), JSON.stringify({ preparedAt: new Date().toISOString(),
  status: 'READY_FOR_AUTHORIZED_GENERATION', project: runtime.project, public: false,
  removedIdentifiedFixtureSessions: fixtureSessions, providerEnabled: true, localEnabled: true,
  cleanupEnabled: false, retentionEnabled: false, originalRehearsalModified: false }, null, 2))
console.log(JSON.stringify({ status: 'READY_FOR_AUTHORIZED_GENERATION', project: runtime.project, public: false }))
