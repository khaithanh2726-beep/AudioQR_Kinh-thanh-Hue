import test from 'node:test'
import assert from 'node:assert/strict'
import { parseRuntimeEnv } from '../../../../../deploy/runtime-env.mjs'

test('reads Windows CRLF, BOM, comments and blank lines without empty environment keys', () => {
  assert.deepEqual(parseRuntimeEnv('\uFEFFHUEQR_IMAGE=hueqr-local:test\r\n\r\n# configuration\r\nHUEQR_TTS_ENABLED=false\r\n'),
    { HUEQR_IMAGE: 'hueqr-local:test', HUEQR_TTS_ENABLED: 'false' })
})
test('preserves concrete signing values, empty values and quoted paths', () => {
  assert.deepEqual(parseRuntimeEnv('HUEQR_GUEST_SIGNING_KEY_BASE64=abc==\nHUEQR_TTS_API_KEY=\nLOG_ROOT="C:/Data With Spaces/logs"\n'),
    { HUEQR_GUEST_SIGNING_KEY_BASE64: 'abc==', HUEQR_TTS_API_KEY: '', LOG_ROOT: 'C:/Data With Spaces/logs' })
})
test('refuses malformed keys, quotes and unexpanded configuration', () => {
  for (const text of ['NO_EQUALS', '=bad', 'BAD KEY=value', 'KEY="unclosed', 'KEY=${SECRET}', 'KEY=bad\0value'])
    assert.throws(() => parseRuntimeEnv(text))
  assert.deepEqual(parseRuntimeEnv("KEY='literal$'").KEY, 'literal$')
})
