import assert from 'node:assert/strict'
import { readFile, writeFile, mkdir } from 'node:fs/promises'
import { resolve, join } from 'node:path'
import { spawn } from 'node:child_process'

const [directoryArg] = process.argv.slice(2)
assert(directoryArg)
const directory = resolve(directoryArg)
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
assert(runtime.contentStage && runtime.project.startsWith('hueqr-ops-check-'))
const ledger = JSON.parse(await readFile(join(directory, 'content-generation-ledger.json'), 'utf8'))
await mkdir(join(directory, 'audio-review'), { recursive: true })
const summary = { startedAt: new Date().toISOString(), model: 'gemini-3.5-flash',
  method: 'blind transcription, reference text not sent; comparison remains separate', results: [] }
for (const article of ledger.articles.filter(a => a.status === 'GENERATED')) {
  const output = join(directory, 'audio-review', article.name.replace('/', '-') + '.json')
  let existing
  try { existing = JSON.parse(await readFile(output, 'utf8')) } catch (error) { if (error.code !== 'ENOENT') throw error }
  if (!existing) {
    const code = await new Promise((done, reject) => {
      const child = spawn(process.execPath, ['backend/src/test/fixtures/demo/gemini-audio-review.mjs',
        join(directory, article.audioFile), article.language, output, summary.model], { stdio: 'inherit' })
      child.once('error', reject); child.once('exit', done)
    })
    existing = JSON.parse(await readFile(output, 'utf8'))
    if (code !== 0) {
      summary.results.push({ article: article.name, status: existing.status })
      summary.status = 'STOPPED_FOR_PROVIDER_INSPECTION'
      break
    }
  }
  assert.equal(existing.audioSha256, article.mp3Checksum)
  summary.results.push({ article: article.name, status: existing.status })
  if (existing.status !== 'TRANSCRIBED_PENDING_COMPARISON') {
    summary.status = 'STOPPED_FOR_PROVIDER_INSPECTION'; break
  }
}
summary.finishedAt = new Date().toISOString()
summary.status ||= 'AVAILABLE_AUDIO_TRANSCRIBED_PENDING_COMPARISON'
await writeFile(join(directory, 'audio-review', 'batch-summary.json'), JSON.stringify(summary, null, 2))
console.log(JSON.stringify(summary))
