param(
    [Parameter(Mandatory = $true)][string]$ConfigPath,
    [string]$DailyAt = '23:00',
    [switch]$ValidateOnly
)
$ErrorActionPreference = 'Stop'
if ($DailyAt -notmatch '^([01][0-9]|2[0-3]):[0-5][0-9]$') { throw 'DailyAt must be HH:mm' }
$resolvedConfig = (Resolve-Path -LiteralPath $ConfigPath).Path
$config = Get-Content -LiteralPath $resolvedConfig -Raw -Encoding UTF8 | ConvertFrom-Json
$node = (Get-Command node.exe -ErrorAction Stop).Source
$runner = Join-Path $PSScriptRoot 'scheduled-operations.mjs'
& $node $runner $resolvedConfig check
if ($LASTEXITCODE -ne 0) { throw 'Operations configuration check failed' }
if ($ValidateOnly) { return }
# Dùng tài khoản Windows đang đăng nhập; không lưu mật khẩu hoặc chạy quyền cao.
$account = [Security.Principal.WindowsIdentity]::GetCurrent().Name
$taskName = 'AudioQR-Operations-' + $config.project
if (Get-ScheduledTask -TaskName $taskName -ErrorAction SilentlyContinue) {
    throw 'Task exists; inspect it before replacing its schedule'
}
# Ghim ban cong cu rieng: chuyen nhanh Git khong lam hong task da dang ky.
$toolRoot = Join-Path (Split-Path $resolvedConfig -Parent) 'tools'
$toolNames = @('scheduled-operations.mjs', 'operations-retention.mjs', 'operations-backup.mjs', 'maintenance.mjs')
$sourceSignature = ($toolNames | ForEach-Object { $_ + ':' + (Get-FileHash -LiteralPath (Join-Path $PSScriptRoot $_) -Algorithm SHA256).Hash }) -join "`n"
$sha = [Security.Cryptography.SHA256]::Create()
try { $version = ([BitConverter]::ToString($sha.ComputeHash([Text.Encoding]::UTF8.GetBytes($sourceSignature)))).Replace('-', '').ToLowerInvariant().Substring(0, 16) }
finally { $sha.Dispose() }
$versionRoot = Join-Path $toolRoot $version
$pinnedDeploy = Join-Path $versionRoot 'deploy'
foreach ($directory in @($toolRoot, $versionRoot, $pinnedDeploy)) {
    New-Item -ItemType Directory -Path $directory -Force | Out-Null
    if ((Get-Item -LiteralPath $directory).Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'Refuse linked tools directory' }
}
$manifest = @()
foreach ($name in $toolNames) {
    $sourceFile = Join-Path $PSScriptRoot $name
    $targetFile = Join-Path $pinnedDeploy $name
    $sourceHash = (Get-FileHash -LiteralPath $sourceFile -Algorithm SHA256).Hash
    if (Test-Path -LiteralPath $targetFile) {
        if ((Get-Item -LiteralPath $targetFile).Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'Refuse linked tool file' }
        if ((Get-FileHash -LiteralPath $targetFile -Algorithm SHA256).Hash -ne $sourceHash) { throw 'Pinned tool differs; review before replacing' }
    } else { Copy-Item -LiteralPath $sourceFile -Destination $targetFile }
    if ((Get-FileHash -LiteralPath $targetFile -Algorithm SHA256).Hash -ne $sourceHash) { throw 'Tool copy checksum mismatch' }
    $manifest += @{ file = $name; sha256 = $sourceHash.ToLowerInvariant() }
}
$manifest | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $versionRoot 'manifest.json') -Encoding UTF8
$runner = Join-Path $pinnedDeploy 'scheduled-operations.mjs'
& $node $runner $resolvedConfig check
if ($LASTEXITCODE -ne 0) { throw 'Pinned operations configuration check failed' }
$action = New-ScheduledTaskAction -Execute $node -Argument ('"{0}" "{1}" run' -f $runner, $resolvedConfig) -WorkingDirectory $versionRoot
$trigger = New-ScheduledTaskTrigger -Daily -At ([datetime]::ParseExact($DailyAt, 'HH:mm', [cultureinfo]::InvariantCulture))
$principal = New-ScheduledTaskPrincipal -UserId $account -LogonType Interactive -RunLevel Limited
# Không chạy bù khi mở máy: tránh bất ngờ ngừng dịch vụ giữa buổi trình diễn.
$settings = New-ScheduledTaskSettingsSet -MultipleInstances IgnoreNew -ExecutionTimeLimit ([timespan]::Zero) -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries
Register-ScheduledTask -TaskName $taskName -Action $action -Trigger $trigger -Principal $principal -Settings $settings -Description 'AudioQR: guarded backup and retention; logged-in user only.' | Select-Object TaskName,State
