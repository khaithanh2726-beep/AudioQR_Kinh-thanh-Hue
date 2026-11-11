import { test, expect } from '@playwright/test'
import { readFile, mkdir } from 'node:fs/promises'
import { join } from 'node:path'
import proposed from '../../src/assets/hue-proposed-regions.json' with { type: 'json' }
import { messages } from '../../src/guest/messages.js'
import { interceptTiles } from './helpers/tile-fixture.mjs'

const evidence = process.env.HUEQR_OSM_EVIDENCE
test.skip(!evidence, 'Cần thư mục bằng chứng bản đồ.')

async function loaded(page) {
  await expect.poll(() => page.locator('.leaflet-tile-loaded').count()).toBeGreaterThan(0)
  await expect(page.locator('.basemap-status')).toHaveCount(0)
}

for (const viewport of [{ width: 1280, height: 900 }, { width: 390, height: 844 }]) {
  test(`OSM layer stays below API-shaped regions and pan/zoom does not select ${viewport.width}`, async ({ page }) => {
    await mkdir(evidence, { recursive: true })
    await page.setViewportSize(viewport)
    const tiles = await interceptTiles(page), api = [], errors = []
    page.on('request', request => { if (new URL(request.url()).pathname.startsWith('/api/')) api.push(request.url()) })
    page.on('pageerror', error => errors.push(error.message))
    await page.goto('/map-preview.html')
    await loaded(page)
    expect(tiles.length).toBeGreaterThan(0)
    expect(tiles.every(url => /^https:\/\/tile\.openstreetmap\.org\/\d+\/\d+\/\d+\.png$/.test(url))).toBe(true)
    await expect(page.locator('.leaflet-local-basemap-pane')).toHaveCount(0)
    await expect(page.locator('.leaflet-overlay-pane path')).toHaveCount(5)
    await expect(page.getByRole('link', { name: 'OpenStreetMap contributors', exact: true })).toBeVisible()
    expect(await page.locator('.leaflet-tile').first().getAttribute('referrerpolicy')).toBe('strict-origin-when-cross-origin')
    for (const [index, place] of proposed.places.entries()) {
      const box = await page.locator('.leaflet-overlay-pane path').nth(index).boundingBox()
      await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2)
      await expect(page.locator('.preview-selection h2')).toHaveText(place.name)
    }
    const first = await page.locator('.leaflet-overlay-pane path').first().boundingBox()
    const x = first.x + first.width / 2, y = first.y + first.height / 2
    await page.mouse.move(x, y)
    await page.mouse.down()
    await page.mouse.move(x + 50, y - 30, { steps: 12 })
    await page.mouse.up()
    await page.getByRole('button', { name: 'Zoom in', exact: true }).click()
    await loaded(page)
    await expect(page.locator('.preview-selection h2')).toHaveText(proposed.places.at(-1).name)
    expect(api).toEqual([])
    expect(errors).toEqual([])
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
    await page.screenshot({ path: join(evidence, `osm-mechanism-fixture-${viewport.width}.png`), fullPage: true })
  })
}

test('failed tiles keep selected region and retry only reloads the basemap', async ({ page }) => {
  let fail = true
  const tiles = await interceptTiles(page, () => fail), api = []
  page.on('request', request => { if (new URL(request.url()).pathname.startsWith('/api/')) api.push(request.url()) })
  await page.goto('/map-preview.html')
  const error = page.locator('.basemap-status[role="alert"]')
  await expect(error).toContainText(messages.vi.mapError)
  const region = await page.locator('.leaflet-overlay-pane path').nth(1).boundingBox()
  await page.mouse.click(region.x + region.width / 2, region.y + region.height / 2)
  await expect(page.locator('.preview-selection h2')).toHaveText('Điện Thái Hòa')
  const before = tiles.length
  fail = false
  await page.getByRole('button', { name: messages.vi.mapRetry, exact: true }).click()
  await loaded(page)
  expect(tiles.length).toBeGreaterThan(before)
  await expect(page.locator('.preview-selection h2')).toHaveText('Điện Thái Hòa')
  expect(api).toEqual([])
})

test('Guest can show the real tile layer with no proposal areas and translate map errors', async ({ page }) => {
  let fail = true
  await interceptTiles(page, () => fail)
  const session = { sessionId: 'map-fixture', displayName: 'Khách kiểm bản đồ', initialLanguage: 'vi', currentLanguage: 'vi', state: 'ACTIVE', serverNow: new Date().toISOString(), expiresAt: new Date(Date.now() + 8 * 3600000).toISOString() }
  const unexpected = []
  await page.addInitScript(() => sessionStorage.setItem('hueqr-guest-tab-v1', JSON.stringify({ ownerId: 'map-owner', sessionId: 'map-fixture', credential: 'ui-fixture-only' })))
  await page.route('**/api/v1/guest/**', async route => {
    const path = new URL(route.request().url()).pathname
    let payload
    if (path.endsWith('/resume')) payload = { session, documentNonce: 'map-document', selectionSeq: 0, currentOpening: null, content: null }
    else if (path.endsWith('/map')) payload = { places: [], serverNow: session.serverNow }
    else if (path.endsWith('/sessions/map-fixture')) payload = session
    else { unexpected.push(path); await route.abort(); return }
    await route.fulfill({ json: payload })
  })
  await page.goto('/')
  await expect(page.locator('.basemap-status[role="alert"]')).toBeVisible()
  await expect(page.locator('.leaflet-overlay-pane path')).toHaveCount(0)
  for (const language of ['vi', 'en', 'fr', 'ru']) {
    await page.locator('.language-field select').selectOption(language)
    await expect(page.locator('.basemap-status')).toContainText(messages[language].mapError)
    await expect(page.getByRole('button', { name: messages[language].mapRetry, exact: true })).toBeVisible()
  }
  fail = false
  await page.getByRole('button', { name: messages.ru.mapRetry, exact: true }).click()
  await loaded(page)
  expect(unexpected).toEqual([])
})

test('real Guest API starts a visit without exposing unapproved preview areas', async ({ page }) => {
  test.skip(!process.env.HUEQR_MAP_PREVIEW_RUNTIME, 'Cần backend preview thử riêng.')
  await interceptTiles(page)
  const starts = []
  page.on('request', request => {
    if (request.method() === 'POST' && new URL(request.url()).pathname === '/api/v1/guest/sessions') starts.push(request.url())
  })
  await page.goto('/')
  await page.locator('input[name="displayName"]').fill('Khách kiểm nền bản đồ')
  await page.getByRole('button', { name: messages.vi.start, exact: true }).click()
  await expect(page.locator('.visit-page')).toBeVisible()
  await loaded(page)
  await expect(page.locator('.map-empty')).toContainText(messages.vi.noMap)
  await expect(page.locator('.leaflet-overlay-pane path')).toHaveCount(0)
  expect(starts).toHaveLength(1)
  await page.getByRole('button', { name: messages.vi.end, exact: true }).click()
  await expect(page.getByRole('heading', { name: messages.vi.ended, exact: true })).toBeVisible()
})

test('Admin region editor uses the same tile background without saving geometry', async ({ page }) => {
  const runtime = process.env.HUEQR_MAP_PREVIEW_RUNTIME
  test.skip(!runtime, 'Cần Admin thử riêng.')
  const config = JSON.parse(await readFile(join(runtime, 'runtime.json'), 'utf8'))
  let fail = true
  await interceptTiles(page, () => fail)
  const saves = []
  page.on('request', request => {
    if (request.method() !== 'GET' && /\/(region-revisions|active-region)$/.test(new URL(request.url()).pathname)) saves.push(request.url())
  })
  await page.goto('/admin.html')
  await page.locator('#login-name').fill(config.adminLogin)
  await page.locator('#login-password').fill(config.adminPassword)
  await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
  await expect(page.locator('.admin-place')).toHaveCount(5)
  await page.locator('.admin-place').first().getByRole('button', { name: 'Vùng tương tác', exact: true }).click()
  await expect(page.locator('.region-map')).toBeVisible()
  await expect(page.locator('.basemap-status[role="alert"]')).toBeVisible()
  fail = false
  await page.getByRole('button', { name: messages.vi.mapRetry, exact: true }).click()
  await loaded(page)
  await expect(page.getByText('Không có phần chưa lưu', { exact: true })).toBeVisible()
  expect(saves).toEqual([])
})
