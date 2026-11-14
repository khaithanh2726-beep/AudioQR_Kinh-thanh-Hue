import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { mkdir, readFile, writeFile } from 'node:fs/promises'
import { join, resolve, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'
import { plainDirectory, disjoint } from './operations-retention.mjs'

const [runtimeArg, outputArg, backupRootArg] = process.argv.slice(2)
assert.equal(process.platform, 'win32', 'Windows ACL required')
assert(runtimeArg && outputArg, 'Usage: init-operations.mjs runtime new-private-directory [existing-protected-backup-root]')
const repository = resolve(dirname(fileURLToPath(import.meta.url)), '..')
const runtimeDirectory = await plainDirectory(runtimeArg), output = resolve(outputArg)
await plainDirectory(dirname(output))
disjoint(repository, output); disjoint(runtimeDirectory, output)
const runtime = JSON.parse((await readFile(join(runtimeDirectory, 'runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
assert.match(runtime.project, /^hueqr-(rehearsal|ops-check)-[a-f0-9]{8}$/)
let backupRoot
if (backupRootArg) {
  backupRoot = await plainDirectory(backupRootArg)
  for (const path of [repository, runtimeDirectory, output]) disjoint(path, backupRoot)
}
const sid = execFileSync('powershell.exe', ['-NoProfile', '-Command', '[Security.Principal.WindowsIdentity]::GetCurrent().User.Value'],
  { encoding: 'utf8', windowsHide: true, stdio: 'pipe' }).trim()
assert.match(sid, /^S-1-[0-9-]+$/)
await mkdir(output)
execFileSync('icacls.exe', [output, '/inheritance:r', '/grant:r', `*${sid}:(OI)(CI)F`, '*S-1-5-18:(OI)(CI)F'], { windowsHide: true, stdio: 'pipe' })
if (!backupRoot) { backupRoot = join(output, 'backups'); await mkdir(backupRoot) }
const logRoot = join(output, 'logs'); await mkdir(logRoot)
const configPath = join(output, 'config.json')
await writeFile(configPath, JSON.stringify({ version: 1, repositoryDirectory: repository, project: runtime.project, runtimeDirectory, backupRoot,
  logRoot, retentionDays: 7 }, null, 2), { flag: 'wx' })
console.log(JSON.stringify({ status: 'OPERATIONS_INITIALIZED', project: runtime.project, configPath }))
