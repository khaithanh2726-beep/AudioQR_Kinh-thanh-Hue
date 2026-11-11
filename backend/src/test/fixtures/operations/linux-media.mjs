import assert from 'node:assert/strict'
import { execFileSync } from 'node:child_process'
import { createHash } from 'node:crypto'
import { mkdir, readFile, writeFile } from 'node:fs/promises'
import { resolve, join } from 'node:path'

const [inputDirectory, outputDirectory] = process.argv.slice(2)
assert(inputDirectory && outputDirectory)
const output = resolve(outputDirectory)
await mkdir(output, { recursive: true })
// Truyền script qua stdin để PowerShell không tách đối số có dấu cách hoặc Unicode
const script = `set -eu
id
java -version
ffmpeg -version | head -1
ffprobe -version | head -1
mkdir -p '/output/Âm thanh Huế'
ffmpeg -nostdin -hide_banner -loglevel error -xerror -i /input/vi-01.wav -map 0:a:0 -vn -sn -dn -c:a libmp3lame -ar 24000 -ac 1 -f mp3 'pipe:1' > '/output/Âm thanh Huế/Ngọ Môn.mp3'
ffprobe -v error -show_entries stream=codec_name,codec_type,sample_rate,channels:format=duration -of json '/output/Âm thanh Huế/Ngọ Môn.mp3' > /output/probe.json
ffmpeg -nostdin -v error -xerror -i '/output/Âm thanh Huế/Ngọ Môn.mp3' -map 0:a:0 -f null -
`
const stdout = execFileSync('docker', ['run', '--rm', '-i', '--network', 'none',
  '--mount', `type=bind,source=${resolve(inputDirectory)},target=/input,readonly`,
  '--mount', `type=bind,source=${output},target=/output`, '--entrypoint', 'sh', 'hueqr-local:20261111'],
{ input: script, encoding: 'utf8', timeout: 120000, stdio: ['pipe', 'pipe', 'pipe'] })
await writeFile(join(output, 'tools.log'), stdout)
const probe = JSON.parse(await readFile(join(output, 'probe.json'), 'utf8'))
assert.equal(probe.streams.length, 1)
assert.equal(probe.streams[0].codec_name, 'mp3')
assert.equal(probe.streams[0].sample_rate, '24000')
assert.equal(probe.streams[0].channels, 1)
assert(Number(probe.format.duration) > 0)
const bytes = await readFile(join(output, 'Âm thanh Huế', 'Ngọ Môn.mp3'))
const report = { status: 'PASSED', image: 'hueqr-local:20261111', network: 'none', nonRoot: true,
  file: 'Âm thanh Huế/Ngọ Môn.mp3', bytes: bytes.length,
  sha256: createHash('sha256').update(bytes).digest('hex'), probe, fullDecodeExit: 0 }
await writeFile(join(output, 'report.json'), JSON.stringify(report, null, 2))
console.log(JSON.stringify(report))
