import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { createHash, randomBytes, randomUUID } from 'node:crypto'
import { readFile, writeFile } from 'node:fs/promises'
import { join, resolve } from 'node:path'
import { createServer } from 'node:net'

const [restorePath, originalRuntimePath] = process.argv.slice(2)
assert(restorePath && originalRuntimePath)
const root = resolve(restorePath)
const config = JSON.parse((await readFile(join(root, 'private-runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
assert.match(config.container, /^hueqr-restore-[0-9a-f]{8}$/)
const source = JSON.parse((await readFile(join(resolve(originalRuntimePath), 'runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
assert.match(source.container, /^hueqr-e2e-[a-z0-9-]+$/)
const result = JSON.parse(await readFile(join(resolve(originalRuntimePath), 'e2e-result.json'), 'utf8'))
const docker = (args, options = {}) => execFileSync('docker', args, {
  encoding: 'utf8', stdio: ['pipe', 'pipe', 'pipe'], timeout: 120000, ...options,
})
await new Promise((done, reject) => {
  const server = createServer().once('error', reject)
  server.listen(18081, '127.0.0.1', () => server.close(done))
})
docker(['start', config.container])
const sql = statement => docker(['exec', '-i', '-e', 'MYSQL_PWD', config.container,
  'mysql', '-u', 'hueqr_smoke', '-N', 'hueqr_smoke'],
{ env: { ...process.env, MYSQL_PWD: config.dbPassword }, input: statement }).trim()
let ready = false
for (let i = 0; i < 60; i++) {
  try { ready = sql('SELECT 1;') === '1' } catch {}
  if (ready) break
  await new Promise(done => setTimeout(done, 1000))
}
assert(ready, 'MySQL restore chưa sẵn sàng')
const fixtureId = randomUUID(), credentialBytes = randomBytes(32)
const oldEpoch = '00000000-0000-4000-8000-000000000001'
assert.notEqual(config.serviceEpoch, oldEpoch)
// Phiên fixture còn hiệu lực chứng minh từ chối do epoch cũ, không do hết hạn
sql(`INSERT INTO guest_session (id,start_request_key,credential_hash,service_epoch,start_fingerprint,
start_ticket_issued_at,start_ticket_expires_at,start_ticket_digest,display_name,initial_language,started_at,expires_at)
VALUES(UUID_TO_BIN('${fixtureId}'),UUID_TO_BIN(UUID()),UNHEX('${createHash('sha256').update(credentialBytes).digest('hex')}'),
'${oldEpoch}',RANDOM_BYTES(32),UTC_TIMESTAMP(6),UTC_TIMESTAMP(6)+INTERVAL 24 HOUR,RANDOM_BYTES(32),
'Restore epoch fixture','vi',UTC_TIMESTAMP(6),UTC_TIMESTAMP(6)+INTERVAL 8 HOUR);`)
const ip = docker(['inspect', '--format', '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}', config.container]).trim()
assert.match(ip, /^\d+\.\d+\.\d+\.\d+$/)
const application = 'hueqr-packaged-' + randomUUID().slice(0, 8)
const env = { ...process.env,
  SPRING_DATASOURCE_PASSWORD: config.dbPassword,
  HUEQR_GUEST_SIGNING_KEY_BASE64: config.signingKeyBase64,
  HUEQR_SERVICE_EPOCH: config.serviceEpoch }
docker(['run', '-d', '--name', application, '--memory', '768m', '--cpus', '2',
  '-p', '127.0.0.1:18081:8080', '--mount', `type=bind,source=${config.audioRoot},target=/data/audio`,
  '-e', `SPRING_DATASOURCE_URL=jdbc:mysql://${ip}:3306/hueqr_smoke`, '-e', 'SPRING_DATASOURCE_USERNAME=hueqr_smoke',
  '-e', 'SPRING_DATASOURCE_PASSWORD', '-e', 'HUEQR_GUEST_SIGNING_KEY_BASE64', '-e', 'HUEQR_SERVICE_EPOCH',
  '-e', 'HUEQR_AUDIO_ROOT=/data/audio', '-e', 'HUEQR_TTS_ENABLED=false', '-e', 'HUEQR_TTS_LOCAL_ENABLED=false',
  '-e', 'HUEQR_CLEANUP_ENABLED=false', '-e', 'HUEQR_RETENTION_ENABLED=false', 'hueqr-local:20261111'], { env })
await writeFile(join(root, 'browser-runtime.json'), JSON.stringify({ application,
  databaseContainer: config.container, adminPassword: source.adminPassword,
  placeName: result.placeName, epochFixture: { id: fixtureId, credential: credentialBytes.toString('base64url') } }, null, 2))
let appReady = false
for (let i = 0; i < 90; i++) {
  try { appReady = (await fetch('http://127.0.0.1:18081/api/v1/auth/csrf')).ok } catch {}
  if (appReady) break
  await new Promise(done => setTimeout(done, 1000))
}
assert(appReady, 'Ứng dụng đóng gói chưa sẵn sàng; đối chiếu log container thử')
console.log(JSON.stringify({ application, databaseContainer: config.container, ready: true,
  url: 'http://localhost:18081', providerEnabled: false }))
