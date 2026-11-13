import { test, expect } from '@playwright/test'
import { readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { interceptTiles } from './helpers/tile-fixture.mjs'

const directory = process.env.HUEQR_OPS_RESTORE_RUNTIME
test.skip(!directory, 'Separate verified restore runtime required')

test('restored Admin reads preserved text and plays exact MP3 without creating TTS or applying a pair', async ({ page }) => {
  const runtime = JSON.parse(await readFile(join(directory, 'browser-private.json'), 'utf8'))
  const restored = JSON.parse(await readFile(join(directory, 'restore-result.json'), 'utf8'))
  expect(runtime.project).toMatch(/^hueqr-ops-check-[a-f0-9]{8}$/)
  expect(restored.status).toBe('RESTORE_VERIFIED_CLOSED')
  expect(process.env.HUEQR_E2E_URL).toBe(runtime.url)
  const writes = []
  page.on('request', request => {
    const path = new URL(request.url()).pathname
    if (request.method() !== 'GET' && path.startsWith('/api/v1/admin/') && !path.endsWith('/session/activity')) writes.push(path)
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
  await page.getByRole('button', { name: 'Chọn kết quả để nghe và đối chiếu', exact: true }).click()
  await page.getByRole('button', { name: 'Tải để nghe thử', exact: true }).click()
  const audio = page.locator('audio')
  await expect(audio).toHaveAttribute('src', /^blob:/)
  const checksum = await audio.evaluate(async element => {
    const bytes = await (await fetch(element.src)).arrayBuffer()
    return [...new Uint8Array(await crypto.subtle.digest('SHA-256', bytes))].map(n => n.toString(16).padStart(2, '0')).join('')
  })
  expect(checksum).toBe(runtime.expectedMp3Hash)
  await audio.evaluate(element => element.play())
  await expect.poll(() => audio.evaluate(element => element.currentTime)).toBeGreaterThan(0)
  expect(writes).toEqual([])
  await page.screenshot({ path: join(directory, 'restored-admin-audio.png'), fullPage: true })
  await writeFile(join(directory, 'browser-result.json'), JSON.stringify({ status: 'PASSED', adminLogin: true,
    places: 5, textLoaded: true, mp3Checksum: checksum, actualPlayback: true, administrativeWrites: writes,
    tileFixture: true, realPhoneVerified: false, providerRequests: 0 }, null, 2))
})
