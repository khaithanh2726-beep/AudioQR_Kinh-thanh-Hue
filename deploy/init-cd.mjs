import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { access, mkdir, readFile, writeFile, realpath } from 'node:fs/promises'
import { join } from 'node:path'
import { isIP } from 'node:net'

// Khởi tạo mới kho CD có ACL; không cài công cụ hoặc ghi đè cấu hình/khóa đã có.
const [runtimeArg, host] = process.argv.slice(2)
assert.equal(process.platform, 'win32')
assert(runtimeArg && host, 'Usage: init-cd.mjs runtime-directory tailscale-ipv4')
assert(isIP(host) === 4 && host.split('.')[0] === '100' && +host.split('.')[1] >= 64 && +host.split('.')[1] <= 127)
const runtimeDirectory = await realpath(runtimeArg)
const runtime = JSON.parse((await readFile(join(runtimeDirectory, 'runtime.json'), 'utf8')).replace(/^\uFEFF/, ''))
assert.match(runtime.project, /^(audioqr-hue|hueqr-(rehearsal|ops-check)-[a-f0-9]{8})$/)
const publicUrl = new URL(runtime.publicStartUrl)
assert(publicUrl.protocol === 'https:' && !publicUrl.username && !publicUrl.password && !publicUrl.search && !publicUrl.hash)
const directory = join(process.env.USERPROFILE, 'AudioQR-CD')
try { await access(directory); assert.fail('CD directory already exists; inspect and preserve its keys') } catch (error) { if (error.code !== 'ENOENT') throw error }
const identity = JSON.parse(execFileSync('powershell.exe', ['-NoProfile', '-Command',
  '$i=[Security.Principal.WindowsIdentity]::GetCurrent();@{sid=$i.User.Value;name=$i.Name}|ConvertTo-Json -Compress'], { encoding: 'utf8', windowsHide: true, stdio: 'pipe' }))
assert.match(identity.sid, /^S-1-[0-9-]+$/)
const sshUser = identity.name.split('\\').at(-1)
assert.match(sshUser, /^[A-Za-z0-9_.-]+$/)
await mkdir(directory)
execFileSync('icacls.exe', [directory, '/inheritance:r', '/grant:r', `*${identity.sid}:(OI)(CI)F`, '*S-1-5-18:(OI)(CI)F'], { windowsHide: true, stdio: 'pipe' })
execFileSync('ssh-keygen', ['-q', '-t', 'ed25519', '-N', '', '-C', 'hueqr-actions-deploy', '-f', join(directory, 'actions_ed25519')], { windowsHide: true, stdio: 'pipe' })
await mkdir(join(directory, 'inbox'))
await mkdir(join(directory, 'backups'))
await writeFile(join(directory, 'config.json'), JSON.stringify({ runtimeDirectory, nodeExecutable: process.execPath,
  backupRoot: join(directory, 'backups'), sshUser, tailscaleIPv4: host }, null, 2), { flag: 'wx' })
console.log(JSON.stringify({ status: 'CD_DIRECTORY_CREATED', publicKeyPath: join(directory, 'actions_ed25519.pub'),
  privateKeyStoredOutsideRepository: true, administratorSshSetupRequired: true }))
