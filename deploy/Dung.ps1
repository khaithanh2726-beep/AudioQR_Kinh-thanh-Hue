param([string]$RuntimeDirectory)
$ErrorActionPreference = 'Stop'
# Nguoi van hanh xu ly ban Admin chua luu va dung ngrok truoc khi chay.
$node = (Get-Command node -ErrorAction Stop).Source
$arguments = @((Join-Path $PSScriptRoot 'quick-run.mjs'), 'stop')
if ($RuntimeDirectory) { $arguments += $RuntimeDirectory }
& $node @arguments
if ($LASTEXITCODE -ne 0) { throw 'Dung chua hoan tat; giu nguyen gate va du lieu de doi chieu.' }
