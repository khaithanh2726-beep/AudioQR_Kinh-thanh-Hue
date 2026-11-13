import assert from 'node:assert/strict'
import { readFile, writeFile } from 'node:fs/promises'
import { join, resolve } from 'node:path'
import { promisify } from 'node:util'
import { execFile } from 'node:child_process'
const exec = promisify(execFile)
const directory = resolve(process.argv[2])
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
assert(runtime.contentStage)
const ledger = JSON.parse(await readFile(join(directory, 'content-generation-ledger.json'), 'utf8'))
const inspected = JSON.parse((await exec('docker', ['inspect', runtime.project + '-application-1', '--format', '{{json .Image}}'])).stdout)
assert.equal(inspected, 'sha256:1e77e1e14ff8a8f4975c8efb16ffbc6ba108e7a884c681c0cbf9c55ed1798488')
let previous = []
try { previous = JSON.parse(await readFile(join(directory, 'audio-technical-check.json'), 'utf8')).results }
catch (error) { if (error.code !== 'ENOENT') throw error }
const results = []
for (const article of ledger.articles.filter(a => a.status === 'GENERATED')) {
  const checked = previous.find(p => p.article === article.name && p.mp3Checksum === article.mp3Checksum)
  if (checked?.status === 'TECHNICAL_CHECK_PASSED') { results.push(checked); continue }
  assert(/^[a-z-]+\/(vi|en|fr|ru)$/.test(article.name))
  const file = '/review/' + article.name.replace('/', '-') + '.mp3'
  const base = ['run', '--rm', '--network', 'none', '--read-only', '--cap-drop', 'ALL',
    '--mount', `type=bind,source=${join(directory, 'generated-audio')},target=/review,readonly`]
  const probe = JSON.parse((await exec('docker', [...base, '--entrypoint', 'ffprobe', inspected,
    '-v', 'error', '-show_format', '-show_streams', '-of', 'json', file], { maxBuffer: 1024 * 1024 })).stdout)
  assert.equal(probe.streams.length, 1)
  assert.equal(probe.streams[0].codec_name, 'mp3')
  assert.equal(Number(probe.streams[0].sample_rate), 24000)
  assert.equal(probe.streams[0].channels, 1)
  assert(Math.abs(Number(probe.format.duration) - Number(article.durationSeconds)) < 0.1)
  const decoded = await exec('docker', [...base, '--entrypoint', 'ffmpeg', inspected,
    '-nostdin', '-v', 'error', '-xerror', '-i', file, '-f', 'null', '-'], { maxBuffer: 1024 * 1024 })
  assert.equal(decoded.stderr.trim(), '')
  results.push({ article: article.name, mp3Checksum: article.mp3Checksum,
    codec: 'mp3', sampleRate: 24000, channels: 1, durationSeconds: Number(probe.format.duration),
    fullDecodeExitCode: 0, status: 'TECHNICAL_CHECK_PASSED' })
  console.log(JSON.stringify(results.at(-1)))
}
await writeFile(join(directory, 'audio-technical-check.json'), JSON.stringify({ checkedAt: new Date().toISOString(),
  image: inspected, humanListeningClaimed: false, results }, null, 2))
