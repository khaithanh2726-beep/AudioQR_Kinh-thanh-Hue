import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { createReadStream } from 'node:fs'
import { readFile, writeFile, lstat, unlink } from 'node:fs/promises'
import { resolve, dirname, basename, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { createHash, randomUUID } from 'node:crypto'

// Nhận gói CI qua SSH đã ghim host key; cấu hình/khóa/backup luôn ngoài gói nguồn.
assert.equal(process.platform, 'win32')
const source = process.argv[2]
assert.match(source || '', /^[a-f0-9]{40}$/)
const release = resolve(dirname(fileURLToPath(import.meta.url)), '..')
const cdRoot = resolve(process.env.USERPROFILE, 'AudioQR-CD')
assert.equal(dirname(release), join(cdRoot, 'inbox'))
assert.match(basename(release), new RegExp('^' + source + '-[0-9]+-[0-9]+$'))
for (const path of [cdRoot, join(cdRoot, 'inbox'), release]) assert((await lstat(path)).isDirectory(), 'Refuse symbolic release path')
const config = JSON.parse(await readFile(join(cdRoot, 'config.json'), 'utf8'))
const runtime = JSON.parse(await readFile(join(config.runtimeDirectory, 'runtime.json'), 'utf8'))
const transfer = JSON.parse(await readFile(join(release, 'transfer.json'), 'utf8'))
assert.equal(transfer.source, source)
assert.equal(transfer.image, 'hueqr-local:ci-' + source)
assert.equal(transfer.publicStartUrl, runtime.publicStartUrl)
const url = new URL(transfer.publicStartUrl)
assert(url.protocol === 'https:' && !url.username && !url.password && !url.hash && !url.search)
async function digest(path) {
  assert((await lstat(path)).isFile(), 'Refuse symbolic artifact')
  const hash = createHash('sha256')
  for await (const chunk of createReadStream(path)) hash.update(chunk)
  return hash.digest('hex')
}
const allowed = ['deploy/cd-receive.mjs', 'deploy/update-image.mjs', 'deploy/maintenance.mjs',
  'deploy/operations-backup.mjs', 'deploy/runtime-env.mjs', 'deploy/compose.yaml', 'deploy/Dockerfile']
assert.deepEqual(transfer.files.map(f => f.path).sort(), [...allowed].sort())
for (const file of transfer.files) {
  assert.match(file.sha256, /^[a-f0-9]{64}$/)
  assert.equal(await digest(join(release, file.path)), file.sha256, 'Tool checksum mismatch')
}
assert.match(transfer.imageTarSha256, /^[a-f0-9]{64}$/)
assert.match(transfer.applicationJarSha256, /^[a-f0-9]{64}$/)
assert.equal(await digest(join(release, 'image.tar')), transfer.imageTarSha256)
const docker = args => execFileSync('docker', args, { encoding: 'utf8', windowsHide: true,
  timeout: 300000, maxBuffer: 8 * 1024 * 1024, stdio: ['ignore', 'pipe', 'pipe'] }).trim()
docker(['load', '-i', join(release, 'image.tar')])
const image = JSON.parse(docker(['image', 'inspect', transfer.image]))[0]
assert.equal(image.Config.Labels?.['org.opencontainers.image.revision'], source)
const probe = 'hueqr-cd-image-' + randomUUID().slice(0, 8)
let created = false
try {
  docker(['create', '--name', probe, '--network', 'none', '--entrypoint', 'true', transfer.image])
  created = true
  docker(['cp', probe + ':/app/backend.jar', join(release, 'application.jar')])
  assert.equal(await digest(join(release, 'application.jar')), transfer.applicationJarSha256)
} finally { if (created) docker(['rm', probe]) }

const backup = join(config.backupRoot, 'backup-' + new Date().toISOString().replace(/[:.]/g, '-') + '-' + source.slice(0, 8))
const result = JSON.parse(execFileSync(config.nodeExecutable, [join(release, 'deploy/update-image.mjs'),
  config.runtimeDirectory, transfer.image, source, backup], { encoding: 'utf8', windowsHide: true, timeout: 600000,
  maxBuffer: 8 * 1024 * 1024, stdio: ['ignore', 'pipe', 'pipe'] }))
assert.equal(result.status, 'IMAGE_UPDATE_VERIFIED')
const report = { status: 'CD_DEPLOYMENT_VERIFIED', source, imageId: image.Id,
  artifactChecksumVerified: true, applicationJarChecksumVerified: true, toolChecksumsVerified: true,
  volumesPreserved: result.volumesPreserved, epochAndSigningKeyPreserved: result.epochAndSigningKeyPreserved,
  backupCreated: result.backup.status === 'COMPLETE', localHealthVerified: true, finishedAt: new Date().toISOString() }
await writeFile(join(release, 'result.json'), JSON.stringify(report, null, 2))
// Giữ Hash/báo cáo và ảnh Docker, bỏ đúng hai tệp trung chuyển đã kiểm để tránh dư dung lượng.
await unlink(join(release, 'image.tar'))
await unlink(join(release, 'application.jar'))
console.log(JSON.stringify(report))
