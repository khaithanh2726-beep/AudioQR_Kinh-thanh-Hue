import { execFile } from 'node:child_process'
import { promisify } from 'node:util'

// Chỉ đọc điều kiện máy; không cài, đăng nhập, mở cổng hoặc khởi động dịch vụ.
const exec = promisify(execFile)
async function command(name, args) {
  try {
    const result = await exec(name, args, { windowsHide: true, timeout: 30000, maxBuffer: 1024 * 1024 })
    return { available: true, output: (result.stdout || result.stderr).trim() }
  } catch {
    return { available: false, output: '' }
  }
}

const [node, docker, compose, engine, services] = await Promise.all([
  command(process.execPath, ['--version']),
  command('docker', ['--version']),
  command('docker', ['compose', 'version', '--short']),
  command('docker', ['info', '--format', '{{.OSType}}']),
  process.platform === 'win32' ? command('powershell.exe', ['-NoProfile', '-Command', `
    $sshServer = Get-Service sshd -ErrorAction SilentlyContinue
    $tailscale = Get-Service Tailscale -ErrorAction SilentlyContinue
    @{
      sshClientAvailable = $null -ne (Get-Command ssh.exe -ErrorAction SilentlyContinue)
      sshServerInstalled = $null -ne $sshServer
      sshServerRunning = $null -ne $sshServer -and $sshServer.Status -eq 'Running'
      tailscaleInstalled = $null -ne $tailscale -or $null -ne (Get-Command tailscale.exe -ErrorAction SilentlyContinue)
      tailscaleServiceRunning = $null -ne $tailscale -and $tailscale.Status -eq 'Running'
    } | ConvertTo-Json -Compress
  `]) : Promise.resolve({ available: false, output: '' }),
])
const cd = services.available ? JSON.parse(services.output) : {
  sshClientAvailable: false, sshServerInstalled: false, sshServerRunning: false,
  tailscaleInstalled: false, tailscaleServiceRunning: false,
}
const linuxEngine = engine.available && engine.output === 'linux'
const missingConditions = []
if (!linuxEngine) missingConditions.push('DOCKER_LINUX_ENGINE_NOT_READY')
if (!compose.available) missingConditions.push('COMPOSE_NOT_READY')
if (!services.available) missingConditions.push('WINDOWS_SERVICE_CHECK_UNAVAILABLE')
if (!cd.sshServerRunning) missingConditions.push('WINDOWS_OPENSSH_SERVER_NOT_RUNNING')
if (!cd.tailscaleServiceRunning) missingConditions.push('TAILSCALE_SERVICE_NOT_RUNNING')

// Dịch vụ đang chạy chưa chứng minh SSH qua mạng riêng, quyền Docker hoặc CD đạt.
const tools = Object.fromEntries(Object.entries({ node, docker, compose }).map(([name, result]) =>
  [name, { available: result.available, version: result.output.split(/\r?\n/)[0] || null }]))
console.log(JSON.stringify({ checkedAt: new Date().toISOString(), readOnly: true, tools,
  dockerLinuxEngine: linuxEngine, isolatedContainerChecksReady: linuxEngine && compose.available,
  cd: { ...cd, remoteSshVerified: false, remoteDockerContextVerified: false, deploymentVerified: false },
  missingConditions }, null, 2))
if (process.argv.includes('--require-cd') && missingConditions.length) process.exitCode = 1
