import assert from 'node:assert/strict'
import { readFile, writeFile } from 'node:fs/promises'
import { join, resolve } from 'node:path'
import { openAdmin } from './content-admin-session.mjs'

const directory = resolve(process.argv[2])
const runtime = JSON.parse(await readFile(join(directory, 'runtime.json'), 'utf8'))
const ledger = JSON.parse(await readFile(join(directory, 'content-generation-ledger.json'), 'utf8'))
const source = JSON.parse(await readFile('docs/05-noi-dung/demo-content-v02.json', 'utf8'))
const { browser, page, read } = await openAdmin(runtime)
const writes = [], results = []
page.on('request', request => {
  const path = new URL(request.url()).pathname
  if (request.method() !== 'GET' && path.startsWith('/api/v1/admin/') && !path.endsWith('/session/activity')) writes.push(path)
})
// Bản đồ không phải đối tượng kiểm của lượt nghe Admin này; tránh gọi máy chủ tile bên ngoài.
await page.route(/https:\/\/[^/]*tile[^/]*\//, route => route.abort())
try {
  const completeness = (await read('/admin/content-completeness')).data
  assert.equal(completeness.length, 20)
  const initialPairs = completeness.filter(row => row.content.appliedPair).length
  for (const entry of ledger.articles.filter(a => a.status === 'GENERATED')) {
    const article = source.articles.find(a => a.placeKey + '/' + a.language === entry.name)
    const place = page.locator('.admin-place').filter({ hasText: article.placeName })
    await place.getByRole('button', { name: 'Nội dung và cặp', exact: true }).click()
    if (article.language !== 'vi') await page.getByLabel(/^Ngôn ngữ/).selectOption(article.language)
    await page.waitForFunction(body => document.querySelector('#text-body')?.value === body, article.body)
    await page.locator('#audio-id').selectOption(entry.audioRevisionId)
    await page.getByRole('button', { name: 'Chọn kết quả để nghe và đối chiếu', exact: true }).click()
    await page.getByRole('button', { name: 'Tải để nghe thử', exact: true }).click()
    const audio = page.locator('audio')
    await page.waitForFunction(() => document.querySelector('audio')?.src.startsWith('blob:'))
    const checksum = await audio.evaluate(async element => {
      const bytes = await (await fetch(element.src)).arrayBuffer()
      return [...new Uint8Array(await crypto.subtle.digest('SHA-256', bytes))].map(n => n.toString(16).padStart(2, '0')).join('')
    })
    assert.equal(checksum, entry.mp3Checksum)
    await audio.evaluate(element => element.play())
    await page.waitForFunction(() => {
      const element = document.querySelector('audio')
      return element && element.currentTime > 0 && !element.paused
    })
    const playedSeconds = await audio.evaluate(element => { const time = element.currentTime; element.pause(); return time })
    results.push({ article: entry.name, checksum, textMatches: true, actualPlaybackStarted: true, playedSeconds })
    if (entry.name === 'thai-hoa/fr') await page.screenshot({ path: join(directory, 'admin-pronunciation-review.png'), fullPage: true })
    await page.getByRole('button', { name: 'Về danh sách', exact: true }).click()
    console.log(JSON.stringify(results.at(-1)))
  }
  assert.equal((await read('/admin/content-completeness')).data.filter(row => row.content.appliedPair).length, initialPairs)
  assert.deepEqual(writes, [])
  await writeFile(join(directory, 'admin-review-browser.json'), JSON.stringify({ checkedAt: new Date().toISOString(),
    status: 'PASSED', cases: results.length, results, administrativeBusinessWrites: writes,
    appliedPairsUnchanged: true, fullHumanListeningClaimed: false, realPhoneVerified: false,
    externalTilesBlocked: true }, null, 2))
} catch (error) {
  await writeFile(join(directory, 'admin-review-browser-failed-attempt.json'), JSON.stringify({
    status: 'FAILED', completedPreviews: results, reason: error.message, fullHumanListeningClaimed: false }, null, 2))
  throw error
} finally { await browser.close() }
