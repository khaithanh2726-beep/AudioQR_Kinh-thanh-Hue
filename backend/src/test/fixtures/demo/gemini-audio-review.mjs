import assert from 'node:assert/strict'
import { readFile, writeFile, open } from 'node:fs/promises'
import { createHash } from 'node:crypto'

const [audioPath, language, output, requestedModel = 'gemini-3.8-flash'] = process.argv.slice(2)
assert(audioPath && ['vi', 'en', 'fr', 'ru'].includes(language) && output,
  'Usage: gemini-audio-review.mjs audio language new-private-result')
const bytes = await readFile(audioPath)
assert(process.env.HUEQR_PROVIDER_KEY_FILE, 'Set HUEQR_PROVIDER_KEY_FILE to a protected file outside repository')
const source = await readFile(process.env.HUEQR_PROVIDER_KEY_FILE, 'utf8')
const values = Object.fromEntries(source.split(/\r?\n/).filter(line => /^\s*[A-Z_]+=/.test(line)).map(line => {
  const i = line.indexOf('='); return [line.slice(0, i).trim(), line.slice(i + 1).trim().replace(/^(['"])(.*)\1$/, '$2')]
}))
const key = values.HUEQR_TTS_API_KEY || values.HUE_TTS_API_KEY || values.GEMINI_API_KEY || values.GOOGLE_API_KEY
assert(key, 'Provider key must stay outside repository')
const lock = await open(output + '.attempt', 'wx'); await lock.close()
assert(/^[a-z0-9.-]+$/.test(requestedModel))
const model = requestedModel
const report = { startedAt: new Date().toISOString(), model, expectedLanguage: language,
  audioSha256: createHash('sha256').update(bytes).digest('hex'), status: 'REQUEST_STARTED',
  method: 'blind audio transcription; no reference narration supplied', humanListeningClaimed: false }
await writeFile(output, JSON.stringify(report, null, 2), { flag: 'wx' })
try {
  const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent`, {
    method: 'POST', headers: { 'x-goog-api-key': key, 'Content-Type': 'application/json' },
    body: JSON.stringify({ contents: [{ role: 'user', parts: [
      { text: `Transcribe this entire short narration verbatim in its original language. Expected language code: ${language}. Do not translate, summarize or invent missing words. Vietnamese heritage names can occur. Return JSON only with fields: detectedLanguage (ISO code), transcript (all audible words), endsMidSentence (boolean), intelligibleSpeech (boolean), concerns (array of short factual observations about audible problems). Write unambiguous years and counts as digits. This is an automated audio audit, not an expert certification.` },
      { inlineData: { mimeType: 'audio/mpeg', data: bytes.toString('base64') } },
    ] }], generationConfig: { responseMimeType: 'application/json', temperature: 0, maxOutputTokens: 8192 } }),
    signal: AbortSignal.timeout(120000),
  })
  const body = await response.json()
  report.httpStatus = response.status
  if (!response.ok) {
    report.providerError = { code: body.error?.code, status: body.error?.status,
      message: String(body.error?.message || '').replaceAll(key, '[REDACTED]') }
    report.status = 'PROVIDER_REJECTED'
    process.exitCode = 1
  } else {
    report.finishReason = body.candidates?.[0]?.finishReason
    const text = body.candidates?.[0]?.content?.parts?.filter(part => part.text && !part.thought).map(part => part.text).join('')
    assert.equal(report.finishReason, 'STOP')
    report.analysis = JSON.parse(text)
    assert.equal(typeof report.analysis.transcript, 'string')
    assert.equal(typeof report.analysis.endsMidSentence, 'boolean')
    assert.equal(typeof report.analysis.intelligibleSpeech, 'boolean')
    report.usage = body.usageMetadata
    report.status = 'TRANSCRIBED_PENDING_COMPARISON'
  }
} catch (error) {
  report.status = 'OUTCOME_REQUIRES_INSPECTION'; report.error = error.name; process.exitCode = 1
} finally {
  report.finishedAt = new Date().toISOString()
  await writeFile(output, JSON.stringify(report, null, 2))
  console.log(JSON.stringify({ status: report.status, model, language, httpStatus: report.httpStatus,
    finishReason: report.finishReason, providerError: report.providerError }))
}
