import assert from 'node:assert/strict'
import { readFile, writeFile, mkdir, access } from 'node:fs/promises'
import { join, resolve } from 'node:path'
import { createHash, randomUUID } from 'node:crypto'
import { openAdmin } from './content-admin-session.mjs'

const [directoryArg, maximumArg = '19'] = process.argv.slice(2)
assert(directoryArg)
const directory = resolve(directoryArg), maximum = Number(maximumArg)
assert(Number.isInteger(maximum) && maximum >= 1 && maximum <= 19)
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
assert(runtime.providerEnabled && runtime.contentStage)
const bundleBytes = await readFile('docs/05-noi-dung/demo-content-v02.json')
const bundle = JSON.parse(bundleBytes), bundleHash = createHash('sha256').update(bundleBytes).digest('hex')
const ledgerFile = join(directory, 'content-generation-ledger.json')
let ledger
try { ledger = JSON.parse(await readFile(ledgerFile, 'utf8')) } catch (error) { if (error.code !== 'ENOENT') throw error }
if (!ledger) ledger = { startedAt: new Date().toISOString(), project: runtime.project,
  sourceBundleSha256: bundleHash, status: 'RUNNING', operations: [], articles: [] }
assert.equal(ledger.project, runtime.project)
assert.equal(ledger.sourceBundleSha256, bundleHash, 'Do not change reviewed text while jobs are in flight')
const persist = () => writeFile(ledgerFile, JSON.stringify(ledger, null, 2))
await mkdir(join(directory, 'generated-audio'), { recursive: true })
const { browser, page, request, read } = await openAdmin(runtime)
let current = 'initialization'
try {
  ledger.status = 'RUNNING'
  delete ledger.error
  delete ledger.failedArticle
  await persist()
  const places = (await read('/admin/places')).data
  const issued = await request('/admin/command-windows', 'POST')
  assert.equal(issued.status, 201)
  const window = issued.data.windowId
  const command = async (name, path, method, payload, etag) => {
    let step = ledger.operations.find(step => step.name === name)
    if (step) {
      if (step.status === 'COMMITTED') return step.response.result
      const lookup = await read('/admin/operations/' + encodeURIComponent(step.key))
      step.lookup = lookup.data
      await persist()
      throw new Error('EXISTING_OPERATION_REQUIRES_RECEIPT_INSPECTION')
    }
    step = { name, path, method, payload, etag, key: window + ':' + randomUUID(), status: 'PENDING' }
    ledger.operations.push(step); await persist()
    const result = await request(path, method, payload, etag, step.key)
    step.httpStatus = result.status; step.response = result.data
    step.status = result.status >= 200 && result.status < 300 ? 'COMMITTED' : 'REJECTED'
    await persist()
    assert.equal(step.status, 'COMMITTED', result.data?.code)
    return result.data.result
  }
  const articles = bundle.articles.filter(a => !a.existingApprovedNgoMonVi)
    .sort((a, b) => (a.language === 'vi' ? 0 : 1) - (b.language === 'vi' ? 0 : 1))
  let generatedThisRun = 0
  for (const article of articles) {
    const name = article.placeKey + '/' + article.language
    current = name
    let entry = ledger.articles.find(entry => entry.name === name)
    if (entry?.status === 'GENERATED') continue
    if (generatedThisRun >= maximum) break
    const place = places.find(place => place.displayName === article.placeName)
    assert(place)
    const path = `/admin/places/${place.placeId}/content/${article.language}`
    let detail = await read(path)
    if (!entry) {
      entry = { name, placeId: place.placeId, language: article.language, textHash: article.textSha256, status: 'PREPARING' }
      ledger.articles.push(entry); await persist()
    }
    if (detail.data.draftText?.contentHash !== article.textSha256) {
      const saved = await command('save/' + name, path + '/text-revisions', 'POST', {
        body: article.body, baseTextId: detail.data.draftText?.textRevisionId || null,
      }, detail.etag)
      entry.textRevisionId = saved.textRevisionId; await persist()
      detail = await read(path)
    }
    assert.equal(detail.data.draftText.contentHash, article.textSha256)
    entry.textRevisionId = detail.data.draftText.textRevisionId
    if (!entry.jobId) {
      const accepted = await command('tts/' + name, '/admin/text-revisions/' + entry.textRevisionId + '/tts-jobs',
        'POST', { expectedTextHash: article.textSha256 })
      entry.jobId = accepted.jobId; entry.status = 'ACCEPTED'; await persist()
    }
    let job
    const start = Date.now()
    while (Date.now() - start < 240000) {
      job = (await read('/admin/tts-jobs/' + entry.jobId)).data
      entry.phase = job.phase; await persist()
      if (['SUCCEEDED', 'FAILED', 'UNCERTAIN', 'DISCARDED'].includes(job.phase)) break
      await new Promise(done => setTimeout(done, 2000))
    }
    entry.job = job; await persist()
    assert.equal(job.phase, 'SUCCEEDED', `Real provider outcome ${job.phase}: ${job.failureCode}; inspect before retry`)
    assert.equal(job.textRevisionId, entry.textRevisionId)
    assert.equal(job.language, article.language)
    assert.equal(job.model, 'gemini-3.8-flash-tts')
    assert.equal(job.voice, 'Charon')
    const response = await page.request.get(runtime.url + '/api/v1/admin/audio-revisions/' + job.result.audioRevisionId + '/preview')
    assert.equal(response.status(), 200)
    assert.match(response.headers()['content-type'], /^audio\/mpeg/)
    const bytes = await response.body()
    const checksum = createHash('sha256').update(bytes).digest('hex')
    assert.equal(checksum, job.result.checksum)
    entry.audioRevisionId = job.result.audioRevisionId; entry.mp3Checksum = checksum
    entry.durationSeconds = job.result.durationSeconds
    entry.audioFile = join('generated-audio', article.placeKey + '-' + article.language + '.mp3')
    await writeFile(join(directory, entry.audioFile), bytes, { flag: 'wx' })
    entry.status = 'GENERATED'; entry.bytes = bytes.length; generatedThisRun++; await persist()
    console.log(JSON.stringify({ article: name, status: entry.status, durationSeconds: entry.durationSeconds }))
  }
  ledger.status = ledger.articles.filter(entry => entry.status === 'GENERATED').length === 19 ? 'ALL_GENERATED_PENDING_AUDIO_REVIEW' : 'PARTIAL_GENERATION_CHECKPOINT'
  await persist()
} catch (error) {
  ledger.status = 'STOPPED_FOR_INSPECTION'; ledger.failedArticle = current; ledger.error = error.message
  await persist(); console.error(JSON.stringify({ status: ledger.status, article: current, reason: error.message })); process.exitCode = 1
} finally { await browser.close() }
