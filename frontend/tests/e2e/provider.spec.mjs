import { test, expect } from '@playwright/test'
import { open, readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { createHash } from 'node:crypto'
import { interceptTiles } from './helpers/tile-fixture.mjs'

const runtimePath = process.env.HUEQR_PROVIDER_RUNTIME
test.skip(!runtimePath || process.env.HUEQR_ALLOW_PROVIDER_TEST !== 'one-vi', 'Cần runtime riêng và quyền gọi đúng một bài vi.')

test('Admin creates one real provider job and previews its MP3 without approving the content', async ({ page }) => {
  test.setTimeout(240000)
  const runtime = JSON.parse(await readFile(join(runtimePath, 'provider-runtime.json'), 'utf8'))
  expect(runtime.project).toMatch(/^hueqr-compose-check-[a-f0-9]{8}$/)
  expect(runtime.providerEnabled).toBe(true)
  expect(new URL(runtime.url).hostname).toBe('localhost')
  expect(new URL(process.env.HUEQR_E2E_URL).origin).toBe(runtime.url)
  // Giữ khóa sau cả lượt lỗi để chạy lại không vô tình gọi thêm provider có tính quota.
  const lock = await open(join(runtimePath, 'provider-attempt.lock'), 'wx')
  await lock.close()
  const report = { startedAt: new Date().toISOString(), scope: 'real-provider-one-vi-no-approval',
    providerCreateRequests: 0, status: 'running', stages: [] }
  await interceptTiles(page)
  page.on('request', request => {
    if (request.method() === 'POST' && /\/text-revisions\/[^/]+\/tts-jobs$/.test(new URL(request.url()).pathname)) report.providerCreateRequests++
  })
  try {
    await page.goto('/admin.html')
    await page.locator('#login-name').fill(runtime.adminLogin)
    await page.locator('#login-password').fill(runtime.adminPassword)
    await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
    const place = page.locator('.admin-place').filter({ hasText: 'Cổng Ngọ Môn' })
    await place.getByRole('button', { name: 'Nội dung và cặp', exact: true }).click()
    const source = await readFile('../docs/05-noi-dung/thuyet-minh-vi-v01.md', 'utf8')
    const text = source.split('## 1. Cổng Ngọ Môn')[1].split('<a id="thai-hoa">')[0].trim()
    expect(text.length).toBeGreaterThan(100)
    report.textHash = createHash('sha256').update(text).digest('hex')
    await page.locator('#text-body').fill(text)
    const saved = page.waitForResponse(r => r.url().endsWith('/text-revisions') && r.request().method() === 'POST')
    await page.getByRole('button', { name: 'Lưu nháp văn bản', exact: true }).click()
    const savedResponse = await saved
    expect(savedResponse.ok()).toBe(true)
    report.textId = (await savedResponse.json()).result.textRevisionId
    await expect(page.getByText('Không có phần chưa lưu', { exact: true })).toBeVisible()
    const accepted = page.waitForResponse(r => /\/text-revisions\/[^/]+\/tts-jobs$/.test(new URL(r.url()).pathname) && r.request().method() === 'POST')
    await page.getByRole('button', { name: 'Tạo audio từ bản đã lưu', exact: true }).click()
    const acceptedResponse = await accepted
    expect(acceptedResponse.status()).toBe(202)
    report.jobId = (await acceptedResponse.json()).result.jobId
    expect(report.jobId).toMatch(/^[0-9a-f-]{36}$/)
    let job
    await expect.poll(async () => {
      const response = await page.request.get('/api/v1/admin/tts-jobs/' + report.jobId)
      expect(response.ok()).toBe(true)
      job = await response.json()
      if (report.stages.at(-1) !== job.phase) report.stages.push(job.phase)
      return ['SUCCEEDED', 'FAILED', 'UNCERTAIN', 'DISCARDED'].includes(job.phase)
    }, { timeout: 180000, intervals: [1000, 2000, 3000] }).toBe(true)
    Object.assign(report, { phase: job.phase, failureCode: job.failureCode,
      retryAction: job.retryAction, model: job.model, voice: job.voice })
    expect(job.textRevisionId).toBe(report.textId)
    expect(job.language).toBe('vi')
    expect(job.phase, 'Provider outcome is recorded; this test never retries it automatically.').toBe('SUCCEEDED')
    report.audioId = job.result.audioRevisionId
    report.mp3Checksum = job.result.checksum
    report.durationSeconds = job.result.durationSeconds
    await page.getByRole('button', { name: 'Đọc lại trạng thái', exact: true }).click()
    await page.getByRole('button', { name: 'Chọn kết quả để nghe và đối chiếu', exact: true }).click()
    await page.getByRole('button', { name: 'Tải để nghe thử', exact: true }).click()
    const audio = page.locator('audio')
    await expect(audio).toHaveAttribute('src', /^blob:/)
    const checksum = await audio.evaluate(async element => {
      const bytes = await (await fetch(element.src)).arrayBuffer()
      return [...new Uint8Array(await crypto.subtle.digest('SHA-256', bytes))].map(v => v.toString(16).padStart(2, '0')).join('')
    })
    expect(checksum).toBe(job.result.checksum)
    await audio.evaluate(element => element.play())
    await expect.poll(() => audio.evaluate(element => !element.paused && element.currentTime > 0 && element.duration > 0)).toBe(true)
    await audio.evaluate(element => element.pause())
    expect(report.providerCreateRequests).toBe(1)
    report.status = 'passed'
    await page.screenshot({ path: join(runtimePath, 'provider-preview.png'), fullPage: true })
  } finally {
    if (report.status === 'running') report.status = 'failed'
    report.finishedAt = new Date().toISOString()
    await writeFile(join(runtimePath, 'provider-result.json'), JSON.stringify(report, null, 2))
  }
})
