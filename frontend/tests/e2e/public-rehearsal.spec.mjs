import { test, expect } from '@playwright/test'
import { readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { createHash, randomUUID } from 'node:crypto'
import { interceptTiles } from './helpers/tile-fixture.mjs'

const directory = process.env.HUEQR_PUBLIC_REHEARSAL_RUNTIME
test.skip(!directory, 'Cần bản tập dượt HTTPS có URL và bộ Ngọ Môn đã áp dụng.')

async function config() {
  const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
  expect(runtime.project).toMatch(/^hueqr-rehearsal-[a-f0-9]{8}$/)
  expect(new URL(runtime.publicStartUrl).protocol).toBe('https:')
  expect(new URL(process.env.HUEQR_E2E_URL).origin).toBe(new URL(runtime.publicStartUrl).origin)
  return runtime
}

async function visit(page, path, form) {
  await page.goto(path, { waitUntil: 'domcontentloaded' })
  const warning = page.getByRole('button', { name: 'Visit Site', exact: true })
  await expect.poll(async () => await form.isVisible() || await warning.isVisible(), { timeout: 30000 }).toBe(true)
  const interstitial = await warning.isVisible()
  if (interstitial) await warning.click()
  await expect(form).toBeVisible()
  return interstitial
}

test('HTTPS Admin generates and exports the QR for the configured public start URL', async ({ page }) => {
  const runtime = await config()
  const interstitial = await visit(page, '/admin.html', page.locator('#login-name'))
  await page.locator('#login-name').fill(runtime.adminLogin)
  await page.locator('#login-password').fill(runtime.adminPassword)
  await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
  await expect(page.locator('.admin-place')).toHaveCount(5)
  await page.getByRole('button', { name: 'QR chung', exact: true }).click()
  await expect(page.locator('.qr-url')).toHaveText(runtime.publicStartUrl)
  const image = page.getByRole('img', { name: 'QR mở trang bắt đầu tham quan' })
  await expect(image).toHaveAttribute('src', /^data:image\/png;base64,/)
  const download = page.waitForEvent('download')
  await page.getByRole('link', { name: 'Tải PNG', exact: true }).click()
  await (await download).saveAs(join(directory, 'qr-kinh-thanh-hue.png'))
  const bytes = await readFile(join(directory, 'qr-kinh-thanh-hue.png'))
  expect(bytes.subarray(0, 8).toString('hex')).toBe('89504e470d0a1a0a')
  expect(bytes.readUInt32BE(16)).toBe(512)
  expect(bytes.readUInt32BE(20)).toBe(512)
  const decoded = await image.evaluate(async element => {
    if (!('BarcodeDetector' in window)) return { supported: false }
    const formats = await BarcodeDetector.getSupportedFormats()
    if (!formats.includes('qr_code')) return { supported: false }
    const bitmap = await createImageBitmap(await (await fetch(element.src)).blob())
    return { supported: true, values: (await new BarcodeDetector({ formats: ['qr_code'] }).detect(bitmap)).map(code => code.rawValue) }
  })
  if (decoded.supported) expect(decoded.values).toContain(runtime.publicStartUrl)
  await page.screenshot({ path: join(directory, 'https-admin-qr.png'), fullPage: true })
  // Kiểm đúng trạng thái do nút In tạo ra, thay hộp thoại in hệ điều hành bằng marker trong browser kiểm.
  await page.evaluate(() => { window.print = () => { window.__qrPrintRequested = true } })
  await page.getByRole('button', { name: 'In QR và đường dẫn', exact: true }).click()
  await expect(page.locator('.qr-sheet')).toHaveClass(/qr-printing/)
  expect(await page.evaluate(() => window.__qrPrintRequested)).toBe(true)
  await page.emulateMedia({ media: 'print' })
  await page.screenshot({ path: join(directory, 'qr-print-preview.png'), fullPage: true })
  await writeFile(join(directory, 'https-qr-result.json'), JSON.stringify({ checkedAt: new Date().toISOString(),
    passed: true, url: runtime.publicStartUrl, interstitial, pngBytes: bytes.length,
    pngHash: createHash('sha256').update(bytes).digest('hex'), dimensions: [512, 512], decoded,
    printRequested: true, printCssCaptured: true, physicalPhoneScanVerified: false }, null, 2))
})

test('HTTPS guest starts a Vietnamese visit, selects Ngo Mon, reads and plays the approved MP3, then ends', async ({ browser }) => {
  const runtime = await config()
  const context = await browser.newContext({ baseURL: runtime.publicStartUrl, viewport: { width: 390, height: 844 } })
  try {
    const page = await context.newPage()
    const errors = []
    page.on('pageerror', error => errors.push(error.message))
    await interceptTiles(page)
    const interstitial = await visit(page, '/', page.locator('input[name="displayName"]'))
    expect(await page.evaluate(() => isSecureContext)).toBe(true)
    await page.locator('input[name="displayName"]').fill('Tập dượt HTTPS ' + randomUUID().slice(0, 8))
    const mapReady = page.waitForResponse(response => /\/api\/v1\/guest\/sessions\/[^/]+\/map$/.test(new URL(response.url()).pathname))
    await page.getByRole('button', { name: 'Bắt đầu tham quan', exact: true }).click()
    const mapResponse = await mapReady
    expect(mapResponse.status()).toBe(200)
    expect(mapResponse.headers()['content-type']).toContain('application/json')
    const mapHeaders = await mapResponse.request().allHeaders()
    expect(mapHeaders['ngrok-skip-browser-warning']).toBe('1')
    expect(mapHeaders.cookie).toBeUndefined()
    expect((await mapResponse.json()).places).toHaveLength(1)
    await expect(page.locator('.map-canvas')).toBeVisible()
    const polygon = page.locator('.map-canvas .leaflet-overlay-pane path')
    await expect(polygon).toHaveCount(1)
    await page.locator('.map-canvas').click()
    const source = await readFile('../docs/05-noi-dung/thuyet-minh-vi-v01.md', 'utf8')
    const text = source.split('## 1. Cổng Ngọ Môn')[1].split('<a id="thai-hoa">')[0].trim()
    await expect(page.locator('.text-body')).toHaveText(text)
    const audio = page.locator('audio')
    await expect(audio).toHaveAttribute('src', /^blob:/)
    const checksum = await audio.evaluate(async element => {
      const bytes = await (await fetch(element.src)).arrayBuffer()
      return [...new Uint8Array(await crypto.subtle.digest('SHA-256', bytes))].map(n => n.toString(16).padStart(2, '0')).join('')
    })
    expect(checksum).toBe('b236c64424078f960a7dc164d17294b804fd1ff5ffbb800b1f2f988165011354')
    await expect.poll(() => audio.evaluate(element => !element.paused && element.currentTime > 0)).toBe(true)
    const playedSeconds = await audio.evaluate(element => element.currentTime)
    await page.screenshot({ path: join(directory, 'https-guest-ngo-mon.png'), fullPage: true })
    await page.getByRole('button', { name: 'Kết thúc tham quan', exact: true }).click()
    await expect(page.getByRole('heading', { name: 'Đã kết thúc tham quan', exact: true })).toBeVisible()
    expect(errors).toEqual([])
    await writeFile(join(directory, 'https-guest-result.json'), JSON.stringify({ checkedAt: new Date().toISOString(),
      passed: true, url: runtime.publicStartUrl, interstitial, secureContext: true, mapPlaces: 1,
      textHash: createHash('sha256').update(text).digest('hex'), mp3Checksum: checksum, playedSeconds,
      ngrokHeaderSent: true, guestCookieSent: false, mapContentType: mapResponse.headers()['content-type'],
      endedThroughUi: true, pageErrors: errors, tileFixture: true, realPhoneVerified: false }, null, 2))
  } finally { await context.close() }
})
