import { test, expect } from '@playwright/test'
import { mkdir, readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { randomUUID } from 'node:crypto'
import { messages } from '../../src/guest/messages.js'

const evidence = process.env.HUEQR_UI_EVIDENCE
const runtime = process.env.HUEQR_PACKAGED_RUNTIME
test.skip(!evidence || !runtime, 'Cần backend thử riêng và thư mục bằng chứng UI.')

async function fontReport(page, selector) {
  const client = await page.context().newCDPSession(page)
  try {
    await client.send('DOM.enable')
    await client.send('CSS.enable')
    const { root } = await client.send('DOM.getDocument')
    const { nodeId } = await client.send('DOM.querySelector', { nodeId: root.nodeId, selector })
    return await client.send('CSS.getPlatformFontsForNode', { nodeId })
  } finally { await client.detach() }
}

test('start form keeps name, language and button together; four scripts render', async ({ page }) => {
  await mkdir(evidence, { recursive: true })
  await page.setViewportSize({ width: 960, height: 720 })
  await page.goto('/')
  const languages = page.getByRole('combobox')
  await expect(languages).toHaveCount(1)
  await expect(page.locator('.start-card form select')).toBeVisible()
  const name = await page.locator('input[name="displayName"]').boundingBox()
  const language = await languages.boundingBox()
  const button = await page.locator('.start-card .primary').boundingBox()
  expect(name.y + name.height).toBeLessThan(language.y)
  expect(language.y + language.height).toBeLessThan(button.y)
  const fonts = {}
  for (const code of ['vi', 'en', 'fr', 'ru']) {
    await languages.selectOption(code)
    await expect(page.locator('.start-card h1')).toHaveText(messages[code].title)
    await expect(page.locator('.start-card .primary')).toContainText(messages[code].start)
    expect(await page.locator('.start-card').innerText()).not.toContain('\uFFFD')
    fonts[code] = await fontReport(page, '.start-card .primary')
    expect(fonts[code].fonts.some(font => font.familyName === 'Segoe UI')).toBe(true)
    await page.screenshot({ path: join(evidence, `start-${code}.png`), fullPage: true })
  }
  await writeFile(join(evidence, 'platform-fonts.json'), JSON.stringify(fonts, null, 2))
})

for (const viewport of [{ width: 1280, height: 800 }, { width: 960, height: 600 },
  { width: 390, height: 844 }, { width: 320, height: 568 }]) {
  test(`visit keeps map, controls and text in view at ${viewport.width}x${viewport.height}`, async ({ page }) => {
    await mkdir(evidence, { recursive: true })
    const config = JSON.parse(await readFile(join(runtime, 'browser-runtime.json'), 'utf8'))
    await page.setViewportSize(viewport)
    const errors = [], openings = []
    page.on('pageerror', error => errors.push(error.message))
    page.on('request', request => {
      if (request.method() === 'POST' && /\/openings(?:\/[^/]+\/view-ack)?$/.test(new URL(request.url()).pathname)) openings.push(request.url())
    })
    await page.goto('/')
    await page.locator('input[name="displayName"]').fill('Khách giao diện ' + randomUUID().slice(0, 8))
    await page.getByRole('button', { name: 'Bắt đầu tham quan', exact: true }).click()
    await expect(page.locator('.map-canvas')).toBeVisible()
    const end = page.getByRole('button', { name: 'Kết thúc tham quan', exact: true })
    expect(await end.evaluate(element => element.closest('header') !== null)).toBe(true)
    await page.locator('.map-canvas').click()
    const dialog = page.getByRole('dialog')
    await expect(dialog).toBeVisible()
    const cancel = dialog.getByRole('button', { name: 'Hủy', exact: true })
    const dialogBox = await dialog.boundingBox(), cancelBox = await cancel.boundingBox()
    expect(cancelBox.y + cancelBox.height).toBeLessThanOrEqual(dialogBox.y + dialogBox.height)
    expect(cancelBox.y + cancelBox.height).toBeLessThan(viewport.height)
    expect(await page.locator('.overlap-list').evaluate(element => element.scrollHeight > element.clientHeight)).toBe(true)
    await page.screenshot({ path: join(evidence, `overlap-${viewport.width}x${viewport.height}.png`), fullPage: true })
    await dialog.getByRole('button', { name: config.placeName, exact: true }).click()
    await expect(page.locator('.text-body')).toContainText('Đây là cổng chính')
    await expect(page.locator('audio')).toHaveAttribute('src', /^blob:/)
    const map = await page.locator('.map-wrapper').boundingBox()
    const title = await page.locator('.narration-section h1').boundingBox()
    const controls = await page.locator('.player-controls').boundingBox()
    const article = await page.locator('.narration').boundingBox()
    expect(title.y).toBeGreaterThanOrEqual(map.y + map.height)
    expect(controls.y).toBeGreaterThanOrEqual(title.y + title.height)
    expect(article.y).toBeGreaterThanOrEqual(controls.y + controls.height)
    expect(article.height).toBeGreaterThan(32)
    expect(article.y + article.height).toBeLessThanOrEqual(viewport.height)
    expect(await page.evaluate(() => document.documentElement.scrollHeight <= innerHeight + 1)).toBe(true)
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
    await page.screenshot({ path: join(evidence, `visit-${viewport.width}x${viewport.height}.png`), fullPage: true })

    // Nội dung dài chỉ để ép cuộn khi kiểm bố cục, không ghi lại vào API hoặc coi là bài đã duyệt
    await page.locator('.text-body').evaluate(element => { element.textContent = (element.textContent + '\n').repeat(12) })
    await page.locator('audio').evaluate(audio => audio.pause())
    const requestsBefore = openings.length
    await page.locator('.narration').hover()
    await page.mouse.wheel(0, 900)
    await expect.poll(() => page.locator('.narration').evaluate(element => element.scrollTop)).toBeGreaterThan(0)
    expect((await page.locator('.map-wrapper').boundingBox()).y).toBe(map.y)
    expect(await page.evaluate(() => scrollY)).toBe(0)
    expect(openings.length).toBe(requestsBefore)
    const position = await page.locator('audio').evaluate(audio => audio.currentTime)
    await page.locator('.map-canvas').click()
    await expect(dialog).toBeVisible()
    await cancel.click()
    await expect(dialog).not.toBeVisible()
    expect(await page.locator('audio').evaluate(audio => audio.paused)).toBe(true)
    expect(await page.locator('audio').evaluate(audio => audio.currentTime)).toBe(position)
    expect(openings.length).toBe(requestsBefore)
    expect(errors).toEqual([])
    await end.click()
    await expect(page.getByRole('heading', { name: 'Đã kết thúc tham quan', exact: true })).toBeVisible()
  })
}

test('audio failure keeps text readable and controls on a small screen', async ({ page }) => {
  const config = JSON.parse(await readFile(join(runtime, 'browser-runtime.json'), 'utf8'))
  await page.setViewportSize({ width: 320, height: 568 })
  await page.route('**/content-grants/*/audio', route => route.abort())
  await page.goto('/')
  await page.locator('input[name="displayName"]').fill('Khách lỗi audio ' + randomUUID().slice(0, 6))
  await page.getByRole('button', { name: 'Bắt đầu tham quan', exact: true }).click()
  await page.locator('.map-canvas').click()
  await page.getByRole('dialog').getByRole('button', { name: config.placeName, exact: true }).click()
  await expect(page.getByText('Chưa phát được audio.', { exact: false })).toBeVisible()
  await expect(page.locator('.text-body')).toContainText('Đây là cổng chính')
  const article = await page.locator('.narration').boundingBox()
  expect(article.height).toBeGreaterThan(32)
  expect(article.y + article.height).toBeLessThanOrEqual(568)
  expect(await page.evaluate(() => document.documentElement.scrollHeight <= innerHeight + 1)).toBe(true)
  await page.screenshot({ path: join(evidence, 'audio-error-320x568.png'), fullPage: true })
  await page.unroute('**/content-grants/*/audio')
  await page.getByRole('button', { name: 'Thử lại', exact: true }).click()
  await expect(page.locator('audio')).toHaveAttribute('src', /^blob:/)
  expect(await page.locator('audio').evaluate(audio => audio.paused)).toBe(true)
  await page.getByRole('button', { name: 'Kết thúc tham quan', exact: true }).click()
  await expect(page.getByRole('heading', { name: 'Đã kết thúc tham quan', exact: true })).toBeVisible()
})
