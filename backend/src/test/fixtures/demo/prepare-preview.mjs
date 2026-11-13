import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { randomBytes, randomUUID, createHash } from 'node:crypto'
import { mkdir, readFile, writeFile } from 'node:fs/promises'
import { dirname, isAbsolute, join, relative, resolve, sep } from 'node:path'
import { fileURLToPath, pathToFileURL } from 'node:url'
import { createServer } from 'node:net'

const root = resolve(dirname(fileURLToPath(import.meta.url)), '../../../../..')
const [sourcePath, outputPath] = process.argv.slice(2)
assert(sourcePath && outputPath, 'Cần runtime thử nguồn và thư mục riêng mới đã giới hạn ACL')
const source = JSON.parse((await readFile(join(sourcePath, 'private-runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
const browserSource = JSON.parse(await readFile(join(sourcePath, 'browser-runtime.json'), 'utf8'))
assert.match(source.container, /^hueqr-restore-[0-9a-f]{8}$/)
const output = resolve(outputPath), distance = relative(root, output)
assert(distance.startsWith('..' + sep) || isAbsolute(distance), 'Runtime không được nằm trong repository')
await mkdir(output)
await mkdir(join(output, 'audio'))
const docker = (args, options = {}) => execFileSync('docker', args, {
  encoding: 'utf8', stdio: ['pipe', 'pipe', 'pipe'], timeout: 120000, ...options,
})
const suffix = randomUUID().slice(0, 8)
const runtime = { databaseContainer: 'hueqr-preview-mysql-' + suffix,
  application: 'hueqr-map-preview-' + suffix, database: 'hueqr_preview', username: 'hueqr_preview',
  dbPassword: randomBytes(24).toString('base64url'), serviceEpoch: randomUUID(),
  signingKeyBase64: randomBytes(32).toString('base64'), audioRoot: join(output, 'audio'),
  adminLogin: 'preview-admin', adminPassword: browserSource.adminPassword,
  url: 'http://127.0.0.1:18082', image: 'hueqr-local:20261111-map-preview' }
await writeFile(join(output, 'runtime.json'), JSON.stringify(runtime, null, 2))
await new Promise((done, reject) => {
  const server = createServer().once('error', reject)
  server.listen(18082, '127.0.0.1', () => server.close(done))
})
const adminHash = docker(['exec', '-i', '-e', 'MYSQL_PWD', source.container,
  'mysql', '-u', 'hueqr_smoke', '-N', 'hueqr_smoke'], {
  env: { ...process.env, MYSQL_PWD: source.dbPassword },
  input: "SELECT HEX(password_hash) FROM admin_account WHERE login_name='smoke-admin' AND enabled=TRUE;",
}).trim()
assert.match(adminHash, /^[0-9A-F]+$/)
const sql = statement => docker(['exec', '-i', '-e', 'MYSQL_PWD', runtime.databaseContainer,
  'mysql', '--default-character-set=utf8mb4', '-u', runtime.username, '-N', runtime.database], {
  env: { ...process.env, MYSQL_PWD: runtime.dbPassword }, input: statement,
}).trim()
docker(['run', '-d', '--name', runtime.databaseContainer, '--memory', '384m', '--cpus', '1',
  '-e', 'MYSQL_RANDOM_ROOT_PASSWORD=yes', '-e', 'MYSQL_DATABASE=' + runtime.database,
  '-e', 'MYSQL_USER=' + runtime.username, '-e', 'MYSQL_PASSWORD', 'mysql:8.4.13',
  '--innodb-buffer-pool-size=64M', '--max-connections=40', '--performance-schema=OFF'],
{ env: { ...process.env, MYSQL_PASSWORD: runtime.dbPassword } })
let ready = false
for (let i = 0; i < 90; i++) {
  try { ready = sql('SELECT 1;') === '1' } catch {}
  if (ready) break
  await new Promise(done => setTimeout(done, 1000))
}
assert(ready, 'MySQL xem trước chưa sẵn sàng')
const ip = docker(['inspect', '--format', '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}', runtime.databaseContainer]).trim()
assert.match(ip, /^\d+\.\d+\.\d+\.\d+$/)
const env = { ...process.env, SPRING_DATASOURCE_PASSWORD: runtime.dbPassword,
  HUEQR_GUEST_SIGNING_KEY_BASE64: runtime.signingKeyBase64, HUEQR_SERVICE_EPOCH: runtime.serviceEpoch }
docker(['run', '-d', '--name', runtime.application, '--memory', '640m', '--cpus', '1',
  '-p', '127.0.0.1:18082:8080', '--mount', `type=bind,source=${runtime.audioRoot},target=/data/audio`,
  '-e', `SPRING_DATASOURCE_URL=jdbc:mysql://${ip}:3306/${runtime.database}`,
  '-e', 'SPRING_DATASOURCE_USERNAME=' + runtime.username, '-e', 'SPRING_DATASOURCE_PASSWORD',
  '-e', 'HUEQR_GUEST_SIGNING_KEY_BASE64', '-e', 'HUEQR_SERVICE_EPOCH', '-e', 'JAVA_TOOL_OPTIONS=-Xmx320m',
  '-e', 'HUEQR_AUDIO_ROOT=/data/audio', '-e', 'HUEQR_TTS_ENABLED=false', '-e', 'HUEQR_TTS_LOCAL_ENABLED=false',
  '-e', 'HUEQR_CLEANUP_ENABLED=false', '-e', 'HUEQR_RETENTION_ENABLED=false', runtime.image], { env })
ready = false
for (let i = 0; i < 120; i++) {
  try { ready = (await fetch(runtime.url + '/api/v1/auth/csrf', { signal: AbortSignal.timeout(2000) })).ok } catch {}
  if (ready) break
  await new Promise(done => setTimeout(done, 1000))
}
assert(ready, 'Ứng dụng xem trước chưa sẵn sàng; giữ container để đối chiếu log')
assert.equal(sql('SELECT DATABASE();'), 'hueqr_preview')
assert.equal(sql('SELECT COUNT(*) FROM place;'), '0', 'Chỉ seed database mới trống')
sql(await readFile(join(root, 'backend/src/main/resources/db/seed/five-places.sql'), 'utf8'))
sql(`INSERT INTO admin_account(id,login_name,password_hash,enabled)
VALUES(UUID_TO_BIN(UUID()),'preview-admin',UNHEX('${adminHash}'),TRUE);`)

const proposal = JSON.parse(await readFile(join(root, 'frontend/src/assets/hue-proposed-regions.json'), 'utf8'))
const ids = ['039d5ff2-da6b-5283-b7ed-5196dd4dcb2e', '68667731-5be9-592f-bf28-3e3f0e81e4ef',
  '5a3bc1b0-7e12-593e-9570-ce3ed726dbae', '0ccc056f-6203-5da3-b4b3-18613615ea91', '9b30736e-75fd-5c62-916e-ad8e2b11747a']
const text = await readFile(join(root, 'docs/05-noi-dung/thuyet-minh-vi-v01.md'), 'utf8')
const articles = [...text.matchAll(/## \d+\. ([^\r\n]+)\r?\n\r?\n([\s\S]*?)(?=\r?\n\r?\n<a|$)/g)]
assert.equal(articles.length, 5)
const { chromium } = await import(pathToFileURL(join(root, 'frontend/node_modules/@playwright/test/index.mjs')).href)
const browser = await chromium.launch({ executablePath: 'C:/Program Files/Google/Chrome/Application/chrome.exe', headless: true })
const drafts = []
try {
  const page = await browser.newPage()
  await page.goto(runtime.url + '/admin.html')
  await page.locator('#login-name').fill(runtime.adminLogin)
  await page.locator('#login-password').fill(runtime.adminPassword)
  await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
  await page.getByRole('button', { name: 'Thêm địa điểm', exact: true }).waitFor()
  for (const [index, article] of articles.entries()) {
    assert.equal(article[1], proposal.places[index].name)
    const body = article[2].trim()
    const result = await page.evaluate(async ({ placeId, body }) => {
      const request = async (path, method = 'GET', payload, etag, key) => {
        const headers = { Accept: 'application/json' }
        if (method !== 'GET') {
          const csrf = await (await fetch('/api/v1/auth/csrf', { credentials: 'same-origin' })).json()
          headers[csrf.headerName] = csrf.token
        }
        if (payload !== undefined) headers['Content-Type'] = 'application/json'
        if (etag) headers['If-Match'] = etag
        if (key) headers['Idempotency-Key'] = key
        const response = await fetch('/api/v1' + path, { method, headers, credentials: 'same-origin',
          body: payload === undefined ? undefined : JSON.stringify(payload) })
        const data = await response.json()
        if (!response.ok) throw new Error('API ' + response.status + ' ' + data.code)
        return { data, etag: response.headers.get('ETag') }
      }
      const path = '/admin/places/' + placeId + '/content/vi'
      const slot = await request(path)
      const window = await request('/admin/command-windows', 'POST')
      return (await request(path + '/text-revisions', 'POST', { body, baseTextId: null },
        slot.etag, window.data.windowId + ':' + crypto.randomUUID())).data.result
    }, { placeId: ids[index], body })
    assert.equal(result.contentHash, createHash('sha256').update(body).digest('hex'))
    drafts.push({ placeId: ids[index], name: article[1], textRevisionId: result.textRevisionId, contentHash: result.contentHash })
  }
} finally { await browser.close() }
const counts = sql(`SELECT 'places',COUNT(*) FROM place UNION ALL SELECT 'slots',COUNT(*) FROM content_slot
 UNION ALL SELECT 'draft_texts',COUNT(*) FROM text_revision UNION ALL SELECT 'visible',COUNT(*) FROM place WHERE visible=TRUE
 UNION ALL SELECT 'applied',COUNT(*) FROM content_slot WHERE active_pair_id IS NOT NULL
 UNION ALL SELECT 'reviews',COUNT(*) FROM text_review_ref;`)
const report = { preparedAt: new Date().toISOString(), url: runtime.url, mapPreviewUrl: runtime.url + '/map-preview.html',
  application: runtime.application, databaseContainer: runtime.databaseContainer, schema: runtime.database,
  counts, drafts, proposedRegions: 5, regionApproval: 'pending', textApproval: 'pending',
  providerEnabled: false, fakeApprovalsCreated: false, activePairsCreated: false, sourceDatabaseModified: false }
await writeFile(join(output, 'manifest.json'), JSON.stringify(report, null, 2))
console.log(JSON.stringify(report))
