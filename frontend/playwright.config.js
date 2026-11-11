import { defineConfig } from '@playwright/test'
import { tmpdir } from 'node:os'
import { join } from 'node:path'

export default defineConfig({
  testDir: './tests/e2e',
  timeout: 120000,
  expect: { timeout: 15000 },
  workers: 1,
  reporter: 'list',
  outputDir: process.env.HUEQR_E2E_OUTPUT || join(tmpdir(), 'hueqr-playwright', 'audioqr-kinh-thanh-hue'),
  use: {
    baseURL: process.env.HUEQR_E2E_URL || 'http://localhost:15173',
    headless: true,
    launchOptions: {
      executablePath: process.env.HUEQR_BROWSER_PATH || 'C:/Program Files/Google/Chrome/Application/chrome.exe',
    },
    screenshot: 'only-on-failure',
  },
})
