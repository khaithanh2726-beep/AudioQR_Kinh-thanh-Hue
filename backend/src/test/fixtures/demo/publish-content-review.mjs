import assert from 'node:assert/strict'
import { readFile, writeFile, mkdir } from 'node:fs/promises'
import { join, resolve } from 'node:path'
import { createHash } from 'node:crypto'

const directory = resolve(process.argv[2])
const output = 'docs/06-kiem-thu/content-review-20261113'
const bundleBytes = await readFile('docs/05-noi-dung/demo-content-v02.json')
const bundle = JSON.parse(bundleBytes)
const generation = JSON.parse(await readFile(join(directory, 'content-generation-ledger.json'), 'utf8'))
const technical = JSON.parse(await readFile(join(directory, 'audio-technical-check.json'), 'utf8'))
const regions = JSON.parse(await readFile(join(directory, 'region-application-ledger.json'), 'utf8'))
assert.equal(regions.status, 'FOUR_REGIONS_APPLIED_IN_PRIVATE_STAGE')
assert.equal(generation.sourceBundleSha256, createHash('sha256').update(bundleBytes).digest('hex'))
const rows = [], transcripts = []
for (const source of bundle.articles) {
  const name = source.placeKey + '/' + source.language
  const entry = generation.articles.find(a => a.name === name)
  const row = { article: name, textSha256: source.textSha256,
    textReview: 'SOURCE_AND_LANGUAGE_REVIEW_FOR_COURSE_DEMO_NOT_EXPERT_CERTIFICATION' }
  if (source.existingApprovedNgoMonVi) {
    row.status = 'PREVIOUSLY_APPROVED_SEPARATE_RECORD'
    row.locator = 'docs/05-noi-dung/duyet-ngo-mon-tap-duot-20261112.md'
  } else if (entry?.status === 'GENERATED') {
    const analysis = JSON.parse(await readFile(join(directory, 'audio-review', name.replace('/', '-') + '.json'), 'utf8'))
    const checked = technical.results.find(t => t.article === name)
    assert.equal(analysis.status, 'TRANSCRIBED_PENDING_COMPARISON')
    assert.equal(analysis.audioSha256, entry.mp3Checksum)
    assert.equal(analysis.analysis.detectedLanguage, source.language)
    assert.equal(analysis.analysis.intelligibleSpeech, true)
    assert.equal(analysis.analysis.endsMidSentence, false)
    assert.equal(checked?.mp3Checksum, entry.mp3Checksum)
    assert.equal(checked.status, 'TECHNICAL_CHECK_PASSED')
    Object.assign(row, { textRevisionId: entry.textRevisionId, audioRevisionId: entry.audioRevisionId,
      jobId: entry.jobId, model: entry.job.model, voice: entry.job.voice,
      mp3Checksum: entry.mp3Checksum, bytes: entry.bytes, durationSeconds: entry.durationSeconds,
      technicalCheck: 'PASSED_FULL_DECODE', automatedAudioCheck: 'COMPLETE_FILE_BLIND_TRANSCRIPTION',
      status: name === 'thai-hoa/fr' ? 'PRONUNCIATION_REQUIRES_LISTENING' : 'AI_COMPARISON_PASSED_PENDING_D09_LISTENING',
      comparisonNote: name === 'thai-hoa/fr'
        ? 'Term trung thiem diep oc transcribed as thong diem diep oc; cannot distinguish pronunciation issue from ASR error. Second model returned 503. Do not approve automatically.'
        : 'Agent compared full transcript with source: meaning, counts, dates, ending and language agree; spelling of proper names, punctuation, numeric notation and homophones can differ.' })
    transcripts.push({ article: name, audioSha256: analysis.audioSha256, model: analysis.model,
      httpStatus: analysis.httpStatus, finishReason: analysis.finishReason, method: analysis.method,
      humanListeningClaimed: false, analysis: analysis.analysis, usage: analysis.usage })
  } else {
    row.status = source.language === 'ru' ? 'BLOCKED_TTS_QUOTA_NO_VALID_AUDIO' : 'NOT_GENERATED'
    if (entry?.job) row.providerOutcome = { jobId: entry.job.jobId, phase: entry.job.phase,
      failureCode: entry.job.failureCode, outcomeUncertain: entry.job.outcomeUncertain,
      inspectedRetryFailed: Boolean(entry.quotaRetry) }
  }
  rows.push(row)
}
const result = { recordedAt: new Date().toISOString(), scope: 'private course-demo staging only',
  status: 'PARTIAL_REVIEW_QUOTA_AND_LISTENING_PENDING', sourceBundleSha256: generation.sourceBundleSha256,
  originalServingRuntimeModified: false, newPairsApplied: 0, externalBillingEnabled: false,
  limitations: ['AI is not expert historical or native-language certification.',
    'No human full-length listening is claimed. D09 approval of the new audio remains pending.',
    'No substituted model, placeholder audio or fallback language is counted as successful TTS.'],
  counts: { totalTargetPairs: 20, previouslyApproved: 1, requestedNewAudio: 19,
    newAudioGenerated: rows.filter(r => r.audioRevisionId).length,
    aiComparisonPassed: rows.filter(r => r.status === 'AI_COMPARISON_PASSED_PENDING_D09_LISTENING').length,
    pronunciationPending: rows.filter(r => r.status === 'PRONUNCIATION_REQUIRES_LISTENING').length,
    quotaBlocked: rows.filter(r => r.status === 'BLOCKED_TTS_QUOTA_NO_VALID_AUDIO').length,
    regionsAppliedInPrivateStage: regions.regions.length },
  regionApplications: regions.regions, articles: rows }
assert.deepEqual(result.counts, { totalTargetPairs: 20, previouslyApproved: 1, requestedNewAudio: 19,
  newAudioGenerated: 14, aiComparisonPassed: 13, pronunciationPending: 1, quotaBlocked: 5, regionsAppliedInPrivateStage: 4 })
await mkdir(output, { recursive: true })
await writeFile(join(output, 'result.json'), JSON.stringify(result, null, 2))
await writeFile(join(output, 'audio-transcripts.json'), JSON.stringify({ transcripts }, null, 2))
await writeFile(join(output, 'audio-technical-check.json'), JSON.stringify(technical, null, 2))
console.log(JSON.stringify({ status: result.status, counts: result.counts }))
