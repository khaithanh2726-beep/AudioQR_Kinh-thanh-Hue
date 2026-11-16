param([string]$RuntimeDirectory, [switch]$MoTrang)
$ErrorActionPreference = 'Stop'
$node = (Get-Command node -ErrorAction Stop).Source
$docker = (Get-Command docker -ErrorAction Stop).Source
$ready = $false
Write-Host 'Mo Docker Desktop va cho Linux Engine san sang...'
for ($i = 0; $i -lt 90; $i++) {
    try {
        $engine = & $docker info --format '{{.OSType}}' 2>$null
        if ($LASTEXITCODE -eq 0 -and $engine -eq 'linux') { $ready = $true; break }
    } catch { }
    Start-Sleep -Seconds 1
}
if (!$ready) { throw 'Docker chua san sang. Mo Docker Desktop roi chay lai.' }
$arguments = @((Join-Path $PSScriptRoot 'quick-run.mjs'), 'start')
if ($RuntimeDirectory) { $arguments += $RuntimeDirectory }
& $node @arguments
if ($LASTEXITCODE -ne 0) { throw 'Khoi dong chua hoan tat; xem thong bao, khong xoa volume hoac gate.' }
if ($MoTrang) {
    if (!$RuntimeDirectory) {
        $config = Get-Content -LiteralPath (Join-Path $env:USERPROFILE 'AudioQR-CD\config.json') -Raw | ConvertFrom-Json
        $RuntimeDirectory = $config.runtimeDirectory
    }
    $runtime = Get-Content -LiteralPath (Join-Path $RuntimeDirectory 'runtime.json') -Raw | ConvertFrom-Json
    Start-Process $runtime.url
    Start-Process ([uri]::new([uri]$runtime.url, '/admin.html').AbsoluteUri)
}
