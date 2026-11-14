param([Parameter(Mandatory = $true)][string]$CdDirectory)

$ErrorActionPreference = 'Stop'
$identity = [Security.Principal.WindowsIdentity]::GetCurrent()
$principal = New-Object Security.Principal.WindowsPrincipal($identity)
if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    throw 'Mo PowerShell bang Run as administrator truoc khi chay.'
}

# Chỉ cấu hình SSH đã cài; không tải công cụ, đọc khóa riêng hoặc thay các khóa hiện có.
$cdRoot = (Resolve-Path -LiteralPath $CdDirectory).Path
$config = Get-Content -LiteralPath (Join-Path $cdRoot 'config.json') -Raw | ConvertFrom-Json
if ($identity.Name.Split('\')[-1] -ne $config.sshUser) {
    throw 'Can dung tai khoan Windows da cau hinh cho Docker Desktop.'
}
$service = Get-Service sshd -ErrorAction Stop
$key = (Get-Content -LiteralPath (Join-Path $cdRoot 'actions_ed25519.pub') -Raw).Trim()
if ($key -notmatch '^ssh-ed25519 [A-Za-z0-9+/=]+(?: .*)?$') { throw 'Public key khong hop le.' }

$hostDirectory = Join-Path $env:ProgramData 'ssh'
& "$env:WINDIR\System32\OpenSSH\ssh-keygen.exe" -A
if ($LASTEXITCODE -ne 0) { throw 'Khong tao duoc host key.' }
$authorized = Join-Path $hostDirectory 'administrators_authorized_keys'
if (Test-Path -LiteralPath $authorized) {
    $preserved = Join-Path $cdRoot 'administrators_authorized_keys.before'
    if (-not (Test-Path -LiteralPath $preserved)) { Copy-Item -LiteralPath $authorized -Destination $preserved }
} else {
    New-Item -ItemType File -Path $authorized | Out-Null
}
$body = ($key -split ' ')[1]
if (-not (Select-String -LiteralPath $authorized -SimpleMatch $body -Quiet)) {
    [IO.File]::AppendAllText($authorized, "`nrestrict $key`n", [Text.Encoding]::ASCII)
}
& icacls.exe $authorized '/inheritance:r' '/grant:r' '*S-1-5-32-544:F' '*S-1-5-18:F' | Out-Null
if ($LASTEXITCODE -ne 0) { throw 'Khong dat duoc quyen authorized_keys.' }

# SSH chỉ nhận qua dải Tailscale; không mở WAN/router hoặc Tailscale Funnel.
$ruleName = 'OpenSSH-Server-In-TCP'
$rule = Get-NetFirewallRule -Name $ruleName -ErrorAction SilentlyContinue
if ($rule) {
    Set-NetFirewallRule -Name $ruleName -RemoteAddress '100.64.0.0/10' -Profile Any -Enabled True
} else {
    New-NetFirewallRule -Name $ruleName -DisplayName 'OpenSSH qua Tailscale' -Direction Inbound `
        -Protocol TCP -LocalPort 22 -Action Allow -RemoteAddress '100.64.0.0/10' -Profile Any | Out-Null
}
Set-Service sshd -StartupType Automatic
Start-Service sshd

$hostKey = (Get-Content -LiteralPath (Join-Path $hostDirectory 'ssh_host_ed25519_key.pub') -Raw).Trim()
[IO.File]::WriteAllText((Join-Path $cdRoot 'host_ed25519.pub'), $hostKey + "`n", [Text.Encoding]::ASCII)
$remoteAddresses = @((Get-NetFirewallRule -Name $ruleName | Get-NetFirewallAddressFilter).RemoteAddress)
if ($remoteAddresses.Count -ne 1 -or $remoteAddresses[0] -notin @('100.64.0.0/10', '100.64.0.0/255.192.0.0')) {
    throw 'Firewall khong dung dai Tailscale.'
}
$result = [ordered]@{
    checkedAt = [DateTime]::UtcNow.ToString('o')
    status = 'SSH_SETUP_READY'
    serviceStatus = [string](Get-Service sshd).Status
    sshUser = $config.sshUser
    firewallRule = $ruleName
    remoteAddresses = $remoteAddresses
    privateKeyRead = $false
}
$result | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $cdRoot 'ssh-setup.json') -Encoding UTF8
$result | ConvertTo-Json -Depth 4
