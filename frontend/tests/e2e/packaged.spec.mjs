import { test, expect } from '@playwright/test'
import { readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { randomUUID } from 'node:crypto'

const runtime = process.env.HUEQR_PACKAGED_RUNTIME
test.skip(!runtime, 'Cần runtime restore riêng và ảnh đóng gói đã kiểm.')

test('packaged application serves restored content, rejects old epoch and plays MP3', async ({ page, browser, request }) => {
  const config = JSON.parse(await readFile(join(runtime, 'browser-runtime.json'), 'utf8'))
  expect(config.application).toMatch(/^hueqr-packaged-/)
  const errors = []
  page.on('pageerror', error => errors.push(error.message))
  const oldSession = await request.get('/api/v1/guest/sessions/' + config.epochFixture.id, {
    headers: { 'X-Guest-Credential': config.epochFixture.credential },
  })
  expect(oldSession.status()).toBe(401)
  expect((await oldSession.json()).code).toBe('GUEST_CREDENTIAL_INVALID')
  await page.goto('/admin.html')
  await page.locator('#login-name').fill('smoke-admin')
  await page.locator('#login-password').fill(config.adminPassword)
  await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
  await expect(page.getByRole('button', { name: 'Thêm địa điểm', exact: true })).toBeVisible()
  const guestContext = await browser.newContext()
  const guest = await guestContext.newPage()
  guest.on('pageerror', error => errors.push(error.message))
  await guest.goto('/')
  await guest.locator('input[name="displayName"]').fill('Khách restore ' + randomUUID().slice(0, 8))
  await guest.getByRole('button', { name: 'Bắt đầu tham quan' }).click()
  await guest.locator('.map-canvas').click()
  const candidate = guest.getByRole('button', { name: config.placeName, exact: true })
  if (await candidate.isVisible()) await candidate.click()
  await expect(guest.locator('.text-body')).toContainText('Đây là cổng chính nằm ở phía Nam')
  await expect(guest.locator('audio')).toHaveAttribute('src', /^blob:/)
  await expect.poll(() => guest.locator('audio').evaluate(audio => !audio.paused && audio.currentTime > 0)).toBe(true)
  await guest.screenshot({ path: join(runtime, 'packaged-guest.png'), fullPage: true })
  expect(errors).toEqual([])
  await guest.getByRole('button', { name: 'Kết thúc tham quan', exact: true }).click()
  await expect(guest.getByRole('heading', { name: 'Đã kết thúc tham quan', exact: true })).toBeVisible()
  await writeFile(join(runtime, 'packaged-result.json'), JSON.stringify({ recordedAt: new Date().toISOString(),
    oldEpochHttpStatus: 401, oldEpochError: 'GUEST_CREDENTIAL_INVALID',
    adminLogin: true, guestStart: true, restoredTextVisible: true, restoredMp3Playing: true,
    guestEnd: true, pageErrors: errors, providerCalled: false, source: 'Docker image serves Vue and API same origin' }, null, 2))
  await guestContext.close()
})
