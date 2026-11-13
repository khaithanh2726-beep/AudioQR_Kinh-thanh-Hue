import { test } from 'node:test'
import assert from 'node:assert/strict'
import { ApiError, guestTransportHeaders, request } from '../src/guest/api.js'

const origin = 'https://demo.ngrok-free.dev'
const configured = { VITE_NGROK_SKIP_BROWSER_WARNING: 'true', VITE_PUBLIC_START_URL: origin + '/' }

test('ngrok transport header is opt-in and absent from normal builds', () => {
  assert.deepEqual(guestTransportHeaders({}, origin), {})
  assert.deepEqual(guestTransportHeaders({ ...configured, VITE_NGROK_SKIP_BROWSER_WARNING: 'false' }, origin), {})
})

test('the configured HTTPS origin can receive the public ngrok transport header', () => {
  assert.deepEqual(guestTransportHeaders(configured, origin), { 'ngrok-skip-browser-warning': '1' })
})

test('another origin or localhost does not inherit the deployment header', () => {
  for (const current of ['https://other.ngrok-free.dev', 'http://localhost:18083', undefined]) {
    assert.deepEqual(guestTransportHeaders(configured, current), {})
  }
})

test('invalid or insecure public URLs cannot enable the header', () => {
  for (const url of ['http://demo.ngrok-free.dev/', origin + '/#x', origin + '/?token=x', 'https://user:pass@demo.ngrok-free.dev/', 'bad']) {
    assert.deepEqual(guestTransportHeaders({ ...configured, VITE_PUBLIC_START_URL: url }, origin), {})
  }
})

test('guest API requests still omit cookies and keep the credential in its dedicated header', async t => {
  let options
  t.mock.method(globalThis, 'fetch', async (url, input) => {
    assert.equal(url, '/api/v1/guest/sessions/fixture/map')
    options = input
    return new Response(JSON.stringify({ places: [] }), { headers: { 'Content-Type': 'application/json' } })
  })
  assert.deepEqual(await request('/sessions/fixture/map', { credential: 'fixture-guest-credential' }), { places: [] })
  assert.equal(options.credentials, 'omit')
  assert.equal(options.redirect, 'error')
  assert.equal(options.headers['X-Guest-Credential'], 'fixture-guest-credential')
  assert.equal(options.headers.Cookie, undefined)
})

test('an HTTP 200 gateway warning cannot be accepted as successful JSON', async t => {
  t.mock.method(globalThis, 'fetch', async () => new Response('Gateway warning', { headers: { 'Content-Type': 'text/plain' } }))
  await assert.rejects(request('/sessions/fixture/map'), error => error instanceof ApiError
    && error.status === 503 && error.code === 'INVALID_API_RESPONSE')
})
