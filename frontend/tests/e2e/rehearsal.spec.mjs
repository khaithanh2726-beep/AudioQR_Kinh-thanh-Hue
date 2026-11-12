import { test, expect } from '@playwright/test'
import { readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { createHash } from 'node:crypto'
import { interceptTiles } from './helpers/tile-fixture.mjs'

const directory = process.env.HUEQR_REHEARSAL_RUNTIME
test.skip(!directory, 'Cần bản tập dợt riêng đã chuẩn bị.')

test('rehearsal serves the guest form and hides the proposal preview page', async ({ page }) => {
  const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
  expect(runtime.project).toMatch(/^hueqr-rehearsal-[a-f0-9]{8}$/)
  expect(process.env.HUEQR_E2E_URL).toBe(runtime.url)
  await page.goto('/')
  await expect(page.locator('input[name="displayName"]')).toBeVisible()
  await expect(page.getByRole('button', { name: 'Bắt đầu tham quan', exact: true })).toBeVisible()
  const response = await page.request.get('/map-preview.html')
  expect(response.status()).toBe(404)
  await page.screenshot({ path: join(directory, 'guest-ready.png'), fullPage: true })
})

test('rehearsal Admin previews the preserved provider MP3 without generating or applying anything', async ({ page }) => {
  const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
  const prepared = JSON.parse(await readFile(join(directory, 'preparation.json'), 'utf8'))
  expect(runtime.project).toMatch(/^hueqr-rehearsal-[a-f0-9]{8}$/)
  expect(prepared.status).toBe('ready-local-awaiting-approval-https')
  const writes = []
  page.on('request', request => {
    if (request.method() !== 'GET' && request.url().includes('/api/v1/admin/')
        && !request.url().endsWith('/session/activity')) writes.push(new URL(request.url()).pathname)
  })
  await interceptTiles(page)
  await page.goto('/admin.html')
  await page.locator('#login-name').fill(runtime.adminLogin)
  await page.locator('#login-password').fill(runtime.adminPassword)
  await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
  await expect(page.locator('.admin-place')).toHaveCount(5)
  await page.locator('.admin-place').filter({ hasText: 'Cổng Ngọ Môn' })
    .getByRole('button', { name: 'Nội dung và cặp', exact: true }).click()
  await expect(page.locator('#text-body')).not.toHaveValue('')
  const source = await readFile('../docs/05-noi-dung/thuyet-minh-vi-v01.md', 'utf8')
  const body = source.split('## 1. Cổng Ngọ Môn')[1].split('<a id="thai-hoa">')[0].trim()
  const textHash = createHash('sha256').update(await page.locator('#text-body').inputValue()).digest('hex')
  expect(textHash).toBe(createHash('sha256').update(body).digest('hex'))
  await page.getByRole('button', { name: 'Chọn kết quả để nghe và đối chiếu', exact: true }).click()
  await page.getByRole('button', { name: 'Tải để nghe thử', exact: true }).click()
  const audio = page.locator('audio')
  await expect(audio).toHaveAttribute('src', /^blob:/)
  const checksum = await audio.evaluate(async element => {
    const bytes = await (await fetch(element.src)).arrayBuffer()
    return [...new Uint8Array(await crypto.subtle.digest('SHA-256', bytes))].map(n => n.toString(16).padStart(2, '0')).join('')
  })
  expect(checksum).toBe(prepared.files.find(file => file.storageKey.endsWith('.mp3')).sha256)
  await audio.evaluate(element => element.play())
  await expect.poll(() => audio.evaluate(element => element.currentTime > 0 && !element.paused)).toBe(true)
  await audio.evaluate(element => element.pause())
  await expect(page.getByRole('button', { name: 'Áp dụng văn bản và audio', exact: true })).toBeDisabled()
  await page.screenshot({ path: join(directory, 'admin-audio-ready.png'), fullPage: true })
  await page.getByRole('button', { name: 'Về danh sách', exact: true }).click()
  await page.getByRole('button', { name: 'QR chung', exact: true }).click()
  await expect(page.getByText('Chưa có URL triển khai. QR sẽ sẵn sàng khi cấu hình đường dẫn trang bắt đầu.', { exact: true })).toBeVisible()
  expect(writes).toEqual([])
  await writeFile(join(directory, 'browser-verification.json'), JSON.stringify({
    checkedAt: new Date().toISOString(), passed: true, cases: 2, providerCalled: false,
    sourceTextHash: textHash, sourceMp3Hash: checksum, playbackStarted: true, pairApplyDisabled: true,
    adminBusinessWrites: writes.length, qrConfigured: false, actualTilesTested: false,
  }, null, 2))
})
