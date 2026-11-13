import { test, expect } from '@playwright/test'
import { mkdir } from 'node:fs/promises'
import { join } from 'node:path'
import proposed from '../../src/assets/hue-proposed-regions.json' with { type: 'json' }
import { interceptTiles } from './helpers/tile-fixture.mjs'

const evidence = process.env.HUEQR_PIN_EVIDENCE
test.skip(!evidence, 'Cần thư mục bằng chứng ghim bản đồ.')
const point = async (page, index) => {
  const box = await page.locator('.leaflet-overlay-pane path').nth(index).boundingBox()
  return { x: box.x + box.width / 2, y: box.y + box.height / 2 }
}
async function tip(page) {
  const box = await page.locator('.map-click-pin').boundingBox()
  return { x: box.x + box.width / 2, y: box.y + box.height }
}
async function expectPin(page, position) {
  await expect(page.locator('.map-click-pin')).toHaveCount(1)
  const actual = await tip(page)
  expect(Math.abs(actual.x - position.x)).toBeLessThanOrEqual(1)
  expect(Math.abs(actual.y - position.y)).toBeLessThanOrEqual(1)
}

for (const viewport of [{ width: 1280, height: 900 }, { width: 390, height: 844 }]) {
  test(`one pin marks the clicked coordinate, not the region centre ${viewport.width}`, async ({ page }) => {
    await mkdir(evidence, { recursive: true })
    await page.setViewportSize(viewport)
    await interceptTiles(page)
    await page.goto('/map-preview.html')
    await expect(page.locator('.leaflet-overlay-pane path')).toHaveCount(5)
    await expect(page.locator('.map-click-pin')).toHaveCount(0)
    await expect(page.locator('.preview-places')).toHaveCount(0)
    const p = await point(page, 2)
    await page.mouse.click(p.x - 4, p.y + 3)
    await expectPin(page, { x: p.x - 4, y: p.y + 3 })
    await expect(page.locator('.preview-selection h2')).toHaveText('Tử Cấm Thành')
    await page.mouse.click(p.x + 4, p.y - 3)
    await expectPin(page, { x: p.x + 4, y: p.y - 3 })
    const saved = await tip(page)
    const map = await page.locator('.map-canvas').boundingBox()
    await page.mouse.click(map.x + 5, map.y + map.height / 2)
    await expectPin(page, saved)
    await page.mouse.move(p.x, p.y)
    await page.mouse.down()
    await page.mouse.move(p.x + 50, p.y + 30, { steps: 12 })
    await page.mouse.up()
    await expect.poll(async () => Math.abs((await tip(page)).x - saved.x)).toBeGreaterThan(20)
    await expect(page.locator('.map-click-pin')).toHaveCount(1)
    await expect(page.locator('.preview-selection h2')).toHaveText('Tử Cấm Thành')
    await page.screenshot({ path: join(evidence, `pin-fixture-${viewport.width}.png`), fullPage: true })
  })
}

test('overlap cancel keeps the old pin; choosing a candidate uses the original clicked coordinate', async ({ page }) => {
  await interceptTiles(page)
  const places = [proposed.places[0], { ...proposed.places[2], name: 'Vùng B' }, { ...proposed.places[2], id: 'overlap-c', name: 'Vùng C' }]
  const session = { sessionId: 'pin-fixture', displayName: 'Khách kiểm ghim', initialLanguage: 'vi', currentLanguage: 'vi', state: 'ACTIVE', serverNow: new Date().toISOString(), expiresAt: new Date(Date.now() + 8 * 3600000).toISOString() }
  const openings = []
  await page.addInitScript(() => sessionStorage.setItem('hueqr-guest-tab-v1', JSON.stringify({ ownerId: 'pin-owner', sessionId: 'pin-fixture', credential: 'fixture-only' })))
  await page.route('**/api/v1/guest/**', async route => {
    const path = new URL(route.request().url()).pathname
    let payload
    if (path.endsWith('/resume')) payload = { session, documentNonce: 'pin-document', selectionSeq: 0 }
    else if (path.endsWith('/map')) payload = { places, serverNow: session.serverNow }
    else if (path.endsWith('/openings')) {
      const body = route.request().postDataJSON()
      openings.push(body)
      payload = { openingId: 'opening-' + openings.length, openingVersion: 1, language: 'vi', serverNow: session.serverNow, place: places.find(p => p.id === body.placeId), text: { body: 'Bài kiểm ghim.' }, audio: { availability: 'NOT_READY' } }
    } else if (path.endsWith('/sessions/pin-fixture')) payload = session
    else { await route.abort(); return }
    await route.fulfill({ json: payload })
  })
  await page.goto('/')
  await expect(page.locator('.leaflet-overlay-pane path')).toHaveCount(3)
  const a = await point(page, 0)
  await page.mouse.click(a.x, a.y)
  await expect(page.locator('.narration-section h1')).toHaveText(places[0].name)
  await expectPin(page, a)
  const b = await point(page, 1)
  await page.mouse.click(b.x + 3, b.y)
  const dialog = page.getByRole('dialog')
  await expect(dialog).toBeVisible()
  await expectPin(page, a)
  await dialog.getByRole('button', { name: 'Hủy', exact: true }).click()
  await expectPin(page, a)
  expect(openings).toHaveLength(1)
  await page.mouse.click(b.x + 3, b.y)
  await dialog.getByRole('button', { name: 'Vùng B', exact: true }).click()
  await expect(page.locator('.narration-section h1')).toHaveText('Vùng B')
  await expectPin(page, { x: b.x + 3, y: b.y })
  expect(openings).toHaveLength(2)
})
