import assert from 'node:assert/strict'
import { execFileSync, spawnSync } from 'node:child_process'
import { writeFile } from 'node:fs/promises'
import { randomUUID } from 'node:crypto'

// Sinh log trên container không mạng/volume; không tạo tải hoặc dọn log bản phục vụ.
const [image, output] = process.argv.slice(2)
assert.match(image || '', /^hueqr-local:[A-Za-z0-9_.-]+$/)
assert(output, 'Usage: docker-log-rotation.mjs local-image new-report.json')
const name = 'hueqr-log-check-' + randomUUID().slice(0, 8)
const docker = args => execFileSync('docker', args, { encoding: 'utf8', windowsHide: true,
  timeout: 180000, maxBuffer: 64 * 1024 * 1024, stdio: ['ignore', 'pipe', 'pipe'] }).trim()
const report = { kind: 'docker-log-rotation', startedAt: new Date().toISOString(), status: 'RUNNING',
  generatedLines: 45000, network: 'none', volumes: 0, servingModified: false }
let created = false
try {
  docker(['create', '--name', name, '--network', 'none', '--label', 'vn.hueqr.purpose=log-rotation-check',
    '--log-driver', 'local', '--log-opt', 'max-size=10m', '--log-opt', 'max-file=3', '--entrypoint', 'awk', image,
    'BEGIN { for(i=0;i<45000;i++) printf "%06d %01000d%c", i, i, 10 }'])
  created = true
  docker(['start', name])
  const exitCode = docker(['wait', name])
  if (exitCode !== '0') {
    const failedLogs = spawnSync('docker', ['logs', name], { encoding: 'utf8', windowsHide: true, timeout: 10000 })
    report.generatorOutput = (failedLogs.stdout + failedLogs.stderr).slice(0, 300)
  }
  assert.equal(exitCode, '0')
  const container = JSON.parse(docker(['inspect', name]))[0]
  assert.equal(container.HostConfig.NetworkMode, 'none'); assert.equal(container.Mounts.length, 0)
  const logs = docker(['logs', name]), lines = logs.split('\n')
  report.observedLines = lines.length
  report.observedBytes = Buffer.byteLength(logs)
  report.firstLineLength = lines[0]?.length
  assert(!/^000000 /m.test(logs), 'Oldest log must have rotated away')
  assert(/^044999 /m.test(logs), 'Latest log must remain readable')
  assert(lines.length < report.generatedLines)
  assert(Buffer.byteLength(logs) <= 30 * 1024 * 1024, 'Retained payload exceeds configured three-file budget')
  Object.assign(report, { status: 'PASSED', retainedLines: lines.length, retainedBytes: Buffer.byteLength(logs),
    oldestRemoved: true, latestPreserved: true, policy: container.HostConfig.LogConfig })
} catch (error) {
  report.status = 'FAILED'; report.errorName = error.name; report.failure = error.message.slice(0, 200); process.exitCode = 1
} finally {
  if (created) {
    const container = JSON.parse(docker(['inspect', name]))[0]
    assert.equal(container.Name, '/' + name)
    assert.equal(container.Config.Labels['vn.hueqr.purpose'], 'log-rotation-check')
    if (container.State.Running) docker(['stop', name])
    docker(['rm', name])
    report.ownContainerRemoved = true
  }
  report.finishedAt = new Date().toISOString()
  await writeFile(output, JSON.stringify(report, null, 2), { flag: 'wx' })
}
console.log(JSON.stringify(report))
