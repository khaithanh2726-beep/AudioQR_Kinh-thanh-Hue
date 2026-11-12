export class AdminApiError extends Error {
  constructor(status, data) {
    super(data?.code || 'REQUEST_FAILED')
    this.status = status
    this.code = data?.code || 'REQUEST_FAILED'
    this.fieldErrors = data?.fieldErrors || {}
    this.missingConditions = data?.missingConditions || []
  }
}

export function createAdminClient(fetcher = globalThis.fetch, timeoutMs = 15000) {
  async function read(path, { method = 'GET', body, key, etag, csrf, preview } = {}) {
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), timeoutMs)
    const headers = { Accept: preview ? 'audio/mpeg' : 'application/json' }
    if (body !== undefined) headers['Content-Type'] = 'application/json'
    if (csrf) headers[csrf.headerName] = csrf.token
    if (key) headers['Idempotency-Key'] = key
    if (etag) headers['If-Match'] = etag
    try {
      const response = await fetcher(`/api/v1${path}`, {
        method, headers, body: body === undefined ? undefined : JSON.stringify(body),
        credentials: 'same-origin', cache: 'no-store', redirect: 'error', signal: controller.signal,
      })
      if (preview && response.ok) {
        const headers = response.headers
        if (response.status !== 200 || headers.get('Content-Type')?.split(';')[0] !== 'audio/mpeg'
          || headers.get('X-Text-Revision-Id') !== preview.textRevisionId
          || headers.get('Content-Language') !== preview.language
          || headers.get('X-Audio-Checksum') !== preview.mp3Checksum) {
          throw new AdminApiError(502, { code: 'INVALID_AUDIO_PREVIEW' })
        }
        const bytes = await response.arrayBuffer()
        const digest = await crypto.subtle.digest('SHA-256', bytes)
        const checksum = Array.from(new Uint8Array(digest), byte => byte.toString(16).padStart(2, '0')).join('')
        if (!bytes.byteLength || checksum !== preview.mp3Checksum) {
          throw new AdminApiError(502, { code: 'INVALID_AUDIO_PREVIEW' })
        }
        return { data: bytes }
      }
      if (response.status === 204 && !preview) return { data: null, etag: null }
      const data = await response.json().catch(() => null)
      if (!response.ok) throw new AdminApiError(response.status, data)
      if (data === null) throw new AdminApiError(502, { code: 'INVALID_RESPONSE' })
      return { data, etag: response.headers.get('ETag') }
    } finally {
      clearTimeout(timer)
    }
  }

  async function send(path, options = {}) {
    const method = options.method || 'GET'
    if (method === 'GET') return read(path, options)
    // Đọc token của session hiện tại, không tự phát lại lệnh khi CSRF hoặc xác thực lỗi
    const { data: csrf } = await read('/auth/csrf')
    return read(path, { ...options, csrf })
  }

  return { send }
}
