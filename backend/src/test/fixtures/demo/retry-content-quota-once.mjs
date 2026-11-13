import assert from 'node:assert/strict'
import { readFile, writeFile } from 'node:fs/promises'
import { join, resolve } from 'node:path'
import { randomUUID } from 'node:crypto'
import { openAdmin } from './content-admin-session.mjs'

const directory = resolve(process.argv[2])
const file = join(directory, 'content-generation-ledger.json')
const ledger = JSON.parse(await readFile(file, 'utf8'))
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
const entry = ledger.articles.find(a => a.name === ledger.failedArticle)
assert.equal(entry?.job?.failureCode, 'PROVIDER_QUOTA')
assert.equal(entry.job.outcomeUncertain, false)
assert.equal(entry.job.retryAction, 'CREATE_PROVIDER_ATTEMPT')
assert(Date.now() - Date.parse(entry.job.terminalAt) >= 120000, 'Wait at least two minutes before one inspected retry')
assert(!entry.quotaRetry, 'At most one inspected quota retry; no automatic repeat')
const { browser, request, read } = await openAdmin(runtime)
try {
  const actual = (await read('/admin/tts-jobs/' + entry.jobId)).data
  assert.equal(actual.phase, 'FAILED')
  assert.equal(actual.failureCode, 'PROVIDER_QUOTA')
  const window = await request('/admin/command-windows', 'POST')
  assert.equal(window.status, 201)
  entry.quotaRetry = { requestedAt: new Date().toISOString(), originalJobId: entry.jobId,
    reason: 'one deliberate attempt after cooldown; daily versus per-minute quota not exposed by contract',
    key: window.data.windowId + ':' + randomUUID(), status: 'PENDING' }
  await writeFile(file, JSON.stringify(ledger, null, 2))
  const response = await request('/admin/tts-jobs/' + entry.jobId + '/retry', 'POST',
    { expectedPhaseVersion: actual.phaseVersion }, undefined, entry.quotaRetry.key)
  entry.quotaRetry.response = response.data
  entry.quotaRetry.httpStatus = response.status
  entry.quotaRetry.status = response.status === 202 ? 'COMMITTED' : 'REQUIRES_INSPECTION'
  if (response.status === 202) {
    entry.jobId = response.data.result.jobId
    entry.status = 'ACCEPTED'
    delete entry.job
  }
  await writeFile(file, JSON.stringify(ledger, null, 2))
  assert.equal(response.status, 202)
  console.log(JSON.stringify({ article: entry.name, status: 'ONE_QUOTA_RETRY_ACCEPTED' }))
} finally { await browser.close() }
