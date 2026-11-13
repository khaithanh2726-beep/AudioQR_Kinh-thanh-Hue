import { test, expect } from '@playwright/test'
import { mkdir } from 'node:fs/promises'
import { join } from 'node:path'
import proposed from '../../src/assets/hue-proposed-regions.json' with { type: 'json' }
import { messages } from '../../src/guest/messages.js'
import { interceptTiles } from './helpers/tile-fixture.mjs'

const evidence = process.env.HUEQR_THEME_EVIDENCE
test.skip(!evidence, 'Cần thư mục bằng chứng giao diện.')

test('start page uses the reference palette and renders four languages', async ({ page }) => {
  await mkdir(evidence, { recursive: true })
  await page.setViewportSize({ width: 390, height: 844 })
  await page.goto('/')
  for (const language of ['vi', 'en', 'fr', 'ru']) {
    await page.getByRole('combobox').selectOption(language)
    await expect(page.locator('.start-card h1')).toHaveText(messages[language].title)
    await expect(page.locator('.primary')).toHaveCSS('background-color', 'rgb(151, 11, 11)')
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
    await page.screenshot({ path: join(evidence, `start-${language}.png`), fullPage: true })
  }
})

// Phản hồi cố định chỉ ép trạng thái UI để kiểm hình học; không chứng minh API/audio tích hợp.
for (const viewport of [{ width: 1280, height: 800 }, { width: 960, height: 600 }, { width: 390, height: 844 }, { width: 320, height: 568 }]) {
  test(`reference theme keeps map, heading and controls above scrolling text ${viewport.width}`, async ({ page }) => {
    await mkdir(evidence, { recursive: true })
    await page.setViewportSize(viewport)
    await interceptTiles(page)
    const errors = [], unexpected = []
    page.on('pageerror', error => errors.push(error.message))
    const session = { sessionId: 'theme-fixture', displayName: 'Khách kiểm giao diện', initialLanguage: 'vi', currentLanguage: 'vi', state: 'ACTIVE', serverNow: new Date().toISOString(), expiresAt: new Date(Date.now() + 8 * 3600000).toISOString() }
    const content = { openingId: 'theme-opening', openingVersion: 1, language: 'vi', place: proposed.places[1], serverNow: session.serverNow, text: { body: 'Điện Thái Hòa — thuyết minh tiếng Việt có dấu.\n'.repeat(80) }, audio: { availability: 'NOT_READY' } }
    await page.addInitScript(() => sessionStorage.setItem('hueqr-guest-tab-v1', JSON.stringify({ ownerId: 'theme-owner', sessionId: 'theme-fixture', credential: 'ui-fixture-only' })))
    await page.route('**/api/v1/guest/**', async route => {
      const path = new URL(route.request().url()).pathname
      let payload
      if (path.endsWith('/resume')) payload = { session, documentNonce: 'theme-document', selectionSeq: 1, currentOpening: { id: content.openingId }, content }
      else if (path.endsWith('/map')) payload = { places: proposed.places, serverNow: session.serverNow }
      else if (path.endsWith('/sessions/theme-fixture')) payload = session
      else { unexpected.push(path); await route.abort(); return }
      await route.fulfill({ json: payload })
    })
    await page.goto('/')
    await expect(page.locator('.narration-section h1')).toHaveText('Điện Thái Hòa')
    await expect(page.locator('.masthead')).toHaveCSS('background-color', 'rgb(151, 11, 11)')
    await expect(page.locator('.narration-section h1')).toHaveCSS('color', 'rgb(151, 11, 11)')
    const map = await page.locator('.map-wrapper').boundingBox()
    const title = await page.locator('.narration-section h1').boundingBox()
    const controls = await page.locator('.player-controls').boundingBox()
    const article = await page.locator('.narration').boundingBox()
    expect(map.height).toBeGreaterThanOrEqual(96)
    expect(title.y).toBeGreaterThanOrEqual(map.y + map.height)
    expect(controls.y).toBeGreaterThanOrEqual(title.y + title.height)
    expect(article.y).toBeGreaterThanOrEqual(controls.y + controls.height)
    expect(article.height).toBeGreaterThan(32)
    expect(article.y + article.height).toBeLessThanOrEqual(viewport.height)
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth && document.documentElement.scrollHeight <= innerHeight + 1)).toBe(true)
    await page.locator('.narration').hover()
    await page.mouse.wheel(0, 500)
    await expect.poll(() => page.locator('.narration').evaluate(element => element.scrollTop)).toBeGreaterThan(0)
    expect(await page.evaluate(() => scrollY)).toBe(0)
    await expect(page.getByRole('button', { name: 'Kết thúc tham quan', exact: true })).toBeInViewport()
    await page.screenshot({ path: join(evidence, `visit-fixture-${viewport.width}.png`), fullPage: true })
    expect(errors).toEqual([])
    expect(unexpected).toEqual([])
  })
}
