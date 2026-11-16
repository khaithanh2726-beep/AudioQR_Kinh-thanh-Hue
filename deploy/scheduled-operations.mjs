import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { mkdir, readFile, writeFile, rmdir } from 'node:fs/promises'
import { join, resolve, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'
import { randomUUID } from 'node:crypto'
import { plainDirectory, disjoint, pruneBackups, pruneLogs } from './operations-retention.mjs'
import { verifyOperationsBackup } from './operations-backup.mjs'

const repository = resolve(dirname(fileURLToPath(import.meta.url)), '..')
const [configArg, mode = 'run'] = process.argv.slice(2)
assert(configArg && ['run', 'check'].includes(mode), 'Usage: scheduled-operations.mjs config.json [run|check]')
const configPath = resolve(configArg)
await plainDirectory(dirname(configPath))
const config = JSON.parse((await readFile(configPath, 'utf8')).replace(/^\uFEFF/, ''))
assert.equal(config.version, 1)
assert.equal(config.retentionDays, 7)
const runtimeDirectory = await plainDirectory(config.runtimeDirectory)
const sourceRepository = await plainDirectory(config.repositoryDirectory)
const backupRoot = await plainDirectory(config.backupRoot), logRoot = await plainDirectory(config.logRoot)
for (const path of [runtimeDirectory, backupRoot, logRoot]) { disjoint(repository, path); disjoint(sourceRepository, path) }
disjoint(runtimeDirectory, backupRoot); disjoint(runtimeDirectory, logRoot); disjoint(backupRoot, logRoot)
const runtime = JSON.parse((await readFile(join(runtimeDirectory, 'runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
assert.equal(runtime.project, config.project, 'Schedule must target the reviewed project')
assert.match(runtime.project, /^(audioqr-hue|hueqr-(rehearsal|ops-check)-[a-f0-9]{8})$/)
const child = (file, args) => execFileSync(process.execPath, [join(repository, 'deploy', file), ...args], {
  cwd: repository, encoding: 'utf8', windowsHide: true, timeout: 600000,
  stdio: ['ignore', 'pipe', 'pipe'], maxBuffer: 4 * 1024 * 1024,
}).trim()
const maintenance = (action, ...args) => JSON.parse(child('maintenance.mjs', [runtimeDirectory, action, ...args]))
if (mode === 'check') {
  console.log(JSON.stringify({ status: 'CONFIG_VERIFIED', project: config.project, retentionDays: 7, changes: false }))
} else {
  const id = randomUUID(), lock = join(logRoot, '.run-lock'), backup = join(backupRoot, 'backup-' + id)
  const report = { kind: 'scheduled-operations', startedAt: new Date().toISOString(), project: runtime.project,
    status: 'RUNNING', stage: 'lock', backupComplete: false, reopened: false, providerRequests: 0 }
  let locked = false
  try {
    await mkdir(lock); locked = true
    await writeFile(join(lock, 'owner.json'), JSON.stringify({ pid: process.pid, id, startedAt: report.startedAt }), { flag: 'wx' })
    report.logs = await pruneLogs(logRoot)
    report.stage = 'preflight'
    const applicationRunning = execFileSync('docker', ['inspect', '--format', '{{.State.Running}}', runtime.application], {
      encoding: 'utf8', windowsHide: true, timeout: 30000, stdio: ['ignore', 'pipe', 'pipe'],
    }).trim()
    assert.equal(applicationRunning, 'true', 'Do not start a stopped application from the backup schedule')
    assert.equal(maintenance('status').mode, 'RUNNING', 'Do not take over existing maintenance')
    report.stage = 'quiesce'
    maintenance('quiesce', id)
    report.closedAt = new Date().toISOString()
    let drained = false
    for (let i = 0; i < 60; i++) {
      const state = maintenance('status')
      assert.equal(state.owner, id)
      drained = ['activities', 'tasks', 'claims', 'deleting'].every(key => state[key] === 0)
      if (drained) break
      await new Promise(done => setTimeout(done, 1000))
    }
    assert(drained, 'Drain incomplete; keep closed for operator review')
    report.stage = 'freeze'; maintenance('freeze', id)
    report.stage = 'backup'; maintenance('backup', id, backup)
    const manifest = JSON.parse(await readFile(join(backup, 'manifest.json'), 'utf8'))
    await verifyOperationsBackup(backup, manifest)
    report.backupComplete = true
    report.files = manifest.files.length
    report.backupName = 'backup-' + id
    report.stage = 'resume'; maintenance('resume', id)
    const health = await fetch(new URL('/api/v1/auth/csrf', runtime.url), { signal: AbortSignal.timeout(5000) })
    assert.equal(health.status, 200)
    report.reopened = true
    report.reopenedAt = new Date().toISOString()
    report.closedDurationMs = Date.parse(report.reopenedAt) - Date.parse(report.closedAt)
    report.stage = 'retention'
    report.backups = await pruneBackups(backupRoot, backup)
    report.stage = 'complete'; report.status = 'SUCCESS'
  } catch {
    report.status = 'FAILED'
    report.code = 'OPERATIONS_REQUIRES_REVIEW'
    // Không in stderr/config/token; owner được lưu trong kho riêng để đối chiếu sau crash.
    report.operationId = id
    process.exitCode = 1
  } finally {
    report.finishedAt = new Date().toISOString()
    await writeFile(join(logRoot, 'operations-' + id + '.json'), JSON.stringify(report, null, 2), { flag: 'wx' })
    if (locked) {
      const { unlink } = await import('node:fs/promises')
      await unlink(join(lock, 'owner.json')); await rmdir(lock)
    }
  }
  console.log(JSON.stringify(report))
}
