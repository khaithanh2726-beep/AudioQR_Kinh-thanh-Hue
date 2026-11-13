import { execFile } from 'node:child_process'
import { promisify } from 'node:util'
import { readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { randomUUID } from 'node:crypto'

const exec = promisify(execFile)
const runtime = process.env.HUEQR_MAP_PREVIEW_RUNTIME
if (!runtime) throw new Error('HUEQR_MAP_PREVIEW_RUNTIME_REQUIRED')
const manifestPath = join(runtime, 'manifest.json')
let manifest = {}
try { manifest = JSON.parse(await readFile(manifestPath, 'utf8')) } catch {}
const previous = process.env.HUEQR_PREVIOUS_PREVIEW_APP || manifest.application || 'hueqr-map-preview-507da98b'
const database = 'hueqr-preview-mysql-91b4feca'
const network = 'hueqr-preview-91b4feca'
const image = 'hueqr-local:20261111-osm-map'
const application = `hueqr-osm-preview-${randomUUID().slice(0, 8)}`
const report = { checkedAt: new Date().toISOString(), previous, application, database, network, image, url: 'http://127.0.0.1:18082', status: 'preparing' }
let stage = 'inspect', created = false, stoppedPrevious = false
const docker = async args => (await exec('docker', args, { timeout: 30000, windowsHide: true, maxBuffer: 1024 * 1024 })).stdout.trim()
const inspect = async name => JSON.parse(await docker(['inspect', name]))[0]
const pause = () => new Promise(resolve => setTimeout(resolve, 2000))

try {
  const old = await inspect(previous), db = await inspect(database)
  const env = Object.fromEntries(old.Config.Env.map(value => { const i = value.indexOf('='); return [value.slice(0, i), value.slice(i + 1)] }))
  if (!env.SPRING_DATASOURCE_URL?.endsWith('/hueqr_preview') || old.HostConfig.Memory > 1024 ** 3
      || db.Config.Image !== 'mysql:8.4.13' || !old.Mounts.length) throw new Error('NOT_THE_ISOLATED_PREVIEW')
  for (const flag of ['HUEQR_TTS_ENABLED', 'HUEQR_TTS_LOCAL_ENABLED', 'HUEQR_CLEANUP_ENABLED', 'HUEQR_RETENTION_ENABLED']) {
    if (env[flag] !== 'false') throw new Error('WORKER_MUST_REMAIN_DISABLED')
  }
  await docker(['image', 'inspect', image])
  stage = 'network'
  try { await docker(['network', 'inspect', network]) }
  catch { await docker(['network', 'create', '--label', 'hueqr.scope=preview', network]) }
  if (!db.NetworkSettings.Networks[network]) await docker(['network', 'connect', '--alias', 'database', network, database])
  stage = 'database'
  if (db.State.Status !== 'running') await docker(['start', database])
  const dbEnv = Object.fromEntries(db.Config.Env.map(value => { const i = value.indexOf('='); return [value.slice(0, i), value.slice(i + 1)] }))
  let ready = false
  for (let i = 0; i < 30; i++) {
    try {
      await docker(['exec', '--env', `MYSQL_PWD=${dbEnv.MYSQL_PASSWORD}`, database, 'mysql', '-u', dbEnv.MYSQL_USER, '-D', 'hueqr_preview', '-N', '-s', '-e', 'SELECT 1'])
      ready = true; break
    } catch { await pause() }
  }
  if (!ready) throw new Error('DATABASE_NOT_READY')
  report.countsBefore = await docker(['exec', '--env', `MYSQL_PWD=${dbEnv.MYSQL_PASSWORD}`, database, 'mysql', '-u', dbEnv.MYSQL_USER, '-D', 'hueqr_preview', '-N', '-s', '-e', 'SELECT COUNT(*) FROM place; SELECT COUNT(*) FROM content_slot; SELECT COUNT(*) FROM text_revision;'])
  console.log('Database preview sẵn sàng; giữ nguyên dữ liệu và kho audio.')
  stage = 'create'
  env.SPRING_DATASOURCE_URL = 'jdbc:mysql://database:3306/hueqr_preview'
  const args = ['create', '--name', application, '--network', network, '--memory', String(old.HostConfig.Memory), '--publish', '127.0.0.1:18082:8080', '--label', 'hueqr.scope=preview']
  for (const [key, value] of Object.entries(env)) args.push('--env', `${key}=${value}`)
  for (const mount of old.Mounts) {
    if (!['bind', 'volume'].includes(mount.Type)) throw new Error('UNEXPECTED_MOUNT_TYPE')
    const source = mount.Type === 'volume' ? mount.Name : mount.Source
    if ([source, mount.Destination].some(value => value.includes(','))) throw new Error('UNSUPPORTED_MOUNT_PATH')
    args.push('--mount', `type=${mount.Type},source=${source},target=${mount.Destination}${mount.RW ? '' : ',readonly'}`)
  }
  args.push(image)
  await docker(args)
  created = true
  if (old.State.Status === 'running') { await docker(['stop', previous]); stoppedPrevious = true }
  stage = 'start'
  await docker(['start', application])
  ready = false
  for (let i = 0; i < 45; i++) {
    try {
      const r = await fetch(report.url + '/api/v1/auth/csrf', { signal: AbortSignal.timeout(2000) })
      if (r.ok) { ready = true; break }
    } catch {}
    if (i % 10 === 0) console.log('Đang chờ ứng dụng preview khởi động…')
    await pause()
  }
  if (!ready) throw new Error('APPLICATION_NOT_READY')
  const current = await inspect(application)
  report.imageId = current.Image
  report.status = 'ready'
  report.workersEnabled = false
  report.sameAudioMounts = true
  report.sameDatabase = true
  await writeFile(manifestPath, JSON.stringify({ ...manifest, application, image, network, basemap: 'osm' }, null, 2))
  console.log(JSON.stringify(report))
} catch (error) {
  report.status = 'failed'
  report.failedStage = stage
  report.error = error.code || 'PREVIEW_REFRESH_FAILED'
  if (created) { try { await docker(['stop', application]) } catch {} }
  if (stoppedPrevious) {
    report.rollbackAttempted = true
    try { await docker(['start', previous]); report.previousRestarted = true } catch { report.previousRestarted = false }
  }
  console.error(`Chưa làm mới được preview tại bước ${stage}; giữ nguyên database/audio và container trước.`)
  process.exitCode = 1
} finally {
  await writeFile(join(runtime, 'osm-refresh.json'), JSON.stringify(report, null, 2))
}
