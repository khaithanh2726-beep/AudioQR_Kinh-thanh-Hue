import { test, expect } from '@playwright/test'
import { readFile } from 'node:fs/promises'
import { join } from 'node:path'

const runtime = process.env.HUEQR_MAP_PREVIEW_RUNTIME
test.skip(!runtime, 'Cần tài khoản và database preview riêng.')

async function login(page) {
  const config = JSON.parse(await readFile(join(runtime, 'runtime.json'), 'utf8'))
  await page.locator('#login-name').fill(config.adminLogin)
  await page.locator('#login-password').fill(config.adminPassword)
  await page.getByRole('button', { name: 'Đăng nhập', exact: true }).click()
}

async function editor(page) {
  await page.goto('/admin.html')
  await login(page)
  await expect(page.locator('.admin-place')).toHaveCount(5)
  await page.locator('.admin-place').first().getByRole('button', { name: 'Nội dung và cặp', exact: true }).click()
  await expect(page.locator('#text-body')).toBeEnabled()
  const original = await page.locator('#text-body').inputValue()
  expect(original.length).toBeGreaterThan(100)
  return original
}

function watchWrites(page) {
  const writes = []
  page.on('request', request => {
    const path = new URL(request.url()).pathname
    if (request.method() !== 'GET' && path.startsWith('/api/v1/admin/') && path !== '/api/v1/admin/session/activity') writes.push(path)
  })
  return writes
}

test('Q2 cancel and reload preserve unsaved text without writing to the database', async ({ page }) => {
  const writes = watchWrites(page)
  const original = await editor(page), draft = original + '\nBản kiểm chưa lưu.'
  await page.locator('#text-body').fill(draft)
  await page.getByRole('button', { name: 'Về danh sách', exact: true }).click()
  const dialog = page.getByRole('dialog', { name: 'Bỏ phần đang soạn?', exact: true })
  await expect(dialog).toBeVisible()
  await dialog.getByRole('button', { name: 'Quay lại soạn', exact: true }).click()
  await expect(page.locator('#text-body')).toHaveValue(draft)
  page.on('dialog', dialog => dialog.accept())
  await page.reload()
  await expect(page.locator('#text-body')).toHaveValue(draft)
  await expect(page.getByText('Có phần chưa lưu', { exact: true })).toBeVisible()
  await page.getByRole('button', { name: 'Về danh sách', exact: true }).click()
  await page.getByRole('button', { name: 'Bỏ thay đổi và tiếp tục', exact: true }).click()
  await expect(page.locator('.admin-place')).toHaveCount(5)
  await page.locator('.admin-place').first().getByRole('button', { name: 'Nội dung và cặp', exact: true }).click()
  await expect(page.locator('#text-body')).toHaveValue(original)
  expect(writes).toEqual([])
})

test('401 masks unsaved content; same account login restores it without automatic save', async ({ page }) => {
  const writes = watchWrites(page)
  const original = await editor(page), draft = original + '\nBản kiểm khóa phiên.'
  await page.locator('#text-body').fill(draft)
  // Chỉ giả phản hồi mất quyền để kiểm UI; không chứng minh chờ đủ 30 phút thật.
  await page.route('**/api/v1/admin/places/**', route => route.fulfill({ status: 401, json: { code: 'ADMIN_SESSION_EXPIRED' } }))
  await page.getByRole('button', { name: 'Đọc lại trạng thái', exact: true }).click()
  await expect(page.getByRole('heading', { name: 'Đăng nhập lại để tiếp tục', exact: true })).toBeVisible()
  await expect(page.locator('#text-body')).toHaveCount(0)
  await expect(page.locator('.admin-main')).toHaveCount(0)
  await page.unroute('**/api/v1/admin/places/**')
  await login(page)
  await expect(page.locator('#text-body')).toHaveValue(draft)
  await expect(page.getByText('Có phần chưa lưu', { exact: true })).toBeVisible()
  expect(writes).toEqual([])
})

test('statistics reads the real API and labels changed filters as old results', async ({ page }) => {
  await page.goto('/admin.html')
  await login(page)
  await expect(page.locator('.admin-place')).toHaveCount(5)
  await page.getByRole('button', { name: 'Phiên và thống kê', exact: true }).click()
  await page.getByRole('button', { name: 'Thống kê', exact: true }).click()
  await page.getByRole('button', { name: 'Xem dữ liệu', exact: true }).click()
  await expect(page.getByRole('heading', { name: /Phiên bắt đầu trong khoảng/ })).toBeVisible()
  const from = page.getByLabel('Từ ngày', { exact: true })
  const current = await from.inputValue()
  const date = new Date(current + 'T00:00:00Z')
  date.setUTCDate(date.getUTCDate() - 1)
  await from.fill(date.toISOString().slice(0, 10))
  await expect(page.getByText('Bộ lọc đã thay đổi. Bảng dưới thuộc lần tải trước.', { exact: true })).toBeVisible()
})
