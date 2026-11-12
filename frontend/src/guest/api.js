export class ApiError extends Error {
  constructor(status, payload) {
    super(payload?.code || 'REQUEST_FAILED')
    this.status = status
    this.code = payload?.code || 'REQUEST_FAILED'
    this.fieldErrors = payload?.fieldErrors || {}
  }
}

export function guestTransportHeaders(env = {}, origin) {
  if (env.VITE_NGROK_SKIP_BROWSER_WARNING !== 'true') return {}
  try {
    const url = new URL(env.VITE_PUBLIC_START_URL)
    if (url.protocol === 'https:' && url.origin === origin && !url.username && !url.password && !url.hash && !url.search) {
      return { 'ngrok-skip-browser-warning': '1' }
    }
  } catch {}
  return {}
}

export async function request(path, { credential, method = 'GET', body, key, etag, signal, binary = false } = {}) {
  const headers = { ...guestTransportHeaders(import.meta.env || {}, globalThis.location?.origin),
    Accept: binary ? 'audio/mpeg' : 'application/json' }
  if (credential) headers['X-Guest-Credential'] = credential
  if (key) headers['Idempotency-Key'] = key
  if (etag) headers['If-Match'] = etag
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  const response = await fetch(`/api/v1/guest${path}`, {
    method, headers, body: body === undefined ? undefined : JSON.stringify(body),
    credentials: 'omit', cache: 'no-store', redirect: 'error', signal,
  })
  if (!response.ok) {
    const payload = await response.json().catch(() => null)
    throw new ApiError(response.status, payload)
  }
  if (binary) {
    if (response.status !== 200 || response.headers.get('Content-Type')?.split(';')[0] !== 'audio/mpeg') {
      throw new ApiError(503, { code: 'MEDIA_NOT_READY' })
    }
    return response.arrayBuffer()
  }
  if (response.headers.get('Content-Type')?.split(';')[0] !== 'application/json') {
    throw new ApiError(503, { code: 'INVALID_API_RESPONSE' })
  }
  return response.json()
}

export function newCredential() {
  const bytes = crypto.getRandomValues(new Uint8Array(32))
  return btoa(String.fromCharCode(...bytes)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}
