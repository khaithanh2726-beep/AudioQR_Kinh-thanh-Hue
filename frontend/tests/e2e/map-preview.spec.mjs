import { test, expect } from '@playwright/test'
import { readFile, mkdir, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import proposed from '../../src/assets/hue-proposed-regions.json' with { type: 'json' }

const runtimePath = process.env.HUEQR_MAP_PREVIEW_RUNTIME
const evidence = process.env.HUEQR_MAP_PREVIEW_EVIDENCE
test.skip(!runtimePath || !evidence, 'Cần database và bản xem trước riêng.')
test.skip(process.env.HUEQR_MAP_BASEMAP !== 'offline', 'Ca lưu trữ phải chạy rõ chế độ offline, không gọi tile ngoài.')

for (const viewport of [{ width: 1280, height: 900 }, { width: 390, height: 844 }]) {
  test(`offline map preview renders and selects five separate regions ${viewport.width}`, async ({ page }) => {
    await mkdir(evidence, { recursive: true })
    await page.setViewportSize(viewport)
    const requests = [], errors = []
    page.on('request', request => requests.push(request.url()))
    page.on('pageerror', error => errors.push(error.message))
    await page.goto('/map-preview.html')
    await expect(page.getByRole('heading', { name: 'Xem trước bản đồ Kinh thành Huế', exact: true })).toBeVisible()
    await expect(page.locator('.leaflet-local-basemap-pane path')).toHaveCount(651)
    await expect(page.locator('.leaflet-overlay-pane path')).toHaveCount(5)
    await expect(page.locator('.map-legend li')).toHaveCount(5)
    for (const [index, place] of proposed.places.entries()) {
      const box = await page.locator('.leaflet-overlay-pane path').nth(index).boundingBox()
      await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2)
      await expect(page.locator('.preview-selection h2')).toHaveText(place.name)
      await expect(page.locator('.map-legend li').nth(index)).toHaveClass(/legend-selected/)
    }
    await expect(page.getByRole('link', { name: 'OpenStreetMap contributors', exact: true })).toBeVisible()
    expect(requests.filter(url => new URL(url).origin !== new URL(page.url()).origin)).toEqual([])
    expect(requests.filter(url => url.includes('/api/v1/'))).toEqual([])
    expect(errors).toEqual([])
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
    await page.screenshot({ path: join(evidence, `map-preview-${viewport.width}.png`), fullPage: true })
  })
}

test('clean preview database exposes drafts and completeness handles error/retry honestly', async ({ page }) => {
  const config = JSON.parse(await readFile(join(runtimePath, 'runtime.json'), 'utf8'))
  await page.goto('/admin.html')
  await page.locator('#login-name').fill(config.adminLogin)
  await page.locator('#login-password').fill(config.adminPassword)
  await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
  await expect(page.locator('.admin-place')).toHaveCount(5)
  for (const place of proposed.places) await expect(page.locator('.admin-place').filter({ hasText: place.name })).toHaveCount(1)
  await page.route('**/api/v1/admin/content-completeness', route => route.abort())
  await page.getByRole('button', { name: 'Mức đầy đủ', exact: true }).click()
  await expect(page.getByText('Chưa tải được mức đầy đủ.', { exact: false })).toBeVisible()
  await expect(page.getByText('Đang đọc mức đầy đủ…', { exact: true })).not.toBeVisible()
  await page.screenshot({ path: join(evidence, 'completeness-failed.png'), fullPage: true })
  await page.unroute('**/api/v1/admin/content-completeness')
  await page.getByRole('button', { name: 'Tải lại mức đầy đủ', exact: true }).click()
  await expect(page.locator('tbody tr')).toHaveCount(5)
  await expect(page.getByText('Bộ chuẩn có 0/20 cặp', { exact: false })).toBeVisible()
  await expect(page.getByText('Có nháp', { exact: true })).toHaveCount(5)
  await expect(page.getByText('Chưa áp dụng cặp', { exact: true })).toHaveCount(20)
  await page.screenshot({ path: join(evidence, 'completeness-retried.png'), fullPage: true })
  await page.route('**/api/v1/admin/content-completeness', route => route.abort())
  await page.getByRole('button', { name: 'Tải lại mức đầy đủ', exact: true }).click()
  await expect(page.getByText('Bảng dưới thuộc lần tải trước.', { exact: false })).toBeVisible()
  await expect(page.locator('tbody tr')).toHaveCount(5)
  await expect(page.getByText('Đang đọc mức đầy đủ…', { exact: true })).not.toBeVisible()
  await writeFile(join(evidence, 'assertions.json'), JSON.stringify({
    previewOnly: true, standardPlaces: 5, languageSlots: 20, vietnameseDrafts: 5, appliedPairs: 0,
    errorClearsLoading: true, retryLoadsData: true, staleDataLabelled: true, fakeApprovals: false,
  }, null, 2))
})
