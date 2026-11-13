import assert from 'node:assert/strict'
import { pathToFileURL } from 'node:url'
import { resolve } from 'node:path'

export async function openAdmin(runtime) {
  assert(runtime.contentStage && runtime.project.startsWith('hueqr-ops-check-'))
  const { chromium } = await import(pathToFileURL(resolve('frontend/node_modules/@playwright/test/index.mjs')))
  const browser = await chromium.launch({ executablePath: 'C:/Program Files/Google/Chrome/Application/chrome.exe', headless: true })
  const page = await browser.newPage()
  await page.goto(runtime.url + '/admin.html')
  await page.locator('#login-name').fill(runtime.adminLogin)
  await page.locator('#login-password').fill(runtime.adminPassword)
  await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
  await page.getByRole('button', { name: 'Thêm địa điểm', exact: true }).waitFor()
  const request = (path, method = 'GET', payload, etag, key) => page.evaluate(async input => {
    const { path, method, payload, etag, key } = input
    const headers = { Accept: 'application/json' }
    if (method !== 'GET') {
      const csrf = await (await fetch('/api/v1/auth/csrf', { cache: 'no-store' })).json()
      headers[csrf.headerName] = csrf.token
    }
    if (payload !== undefined) headers['Content-Type'] = 'application/json'
    if (etag) headers['If-Match'] = etag
    if (key) headers['Idempotency-Key'] = key
    const response = await fetch('/api/v1' + path, { method, headers, credentials: 'same-origin',
      cache: 'no-store', redirect: 'error', body: payload === undefined ? undefined : JSON.stringify(payload) })
    return { status: response.status, etag: response.headers.get('ETag'), data: await response.json() }
  }, { path, method, payload, etag, key })
  const read = async path => {
    const result = await request(path)
    assert.equal(result.status, 200, `Read failed: ${path} ${result.data?.code}`)
    return result
  }
  return { browser, page, request, read }
}
