import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), 'VITE_')
  const input = { guest: 'index.html', admin: 'admin.html' }
  if (env.VITE_MAP_PREVIEW === 'true') input.mapPreview = 'map-preview.html'
  return {
    plugins: [vue()],
    build: {
      rollupOptions: { input },
    },
    server: {
      proxy: { '/api': { target: process.env.HUEQR_BACKEND_URL || 'http://127.0.0.1:8080', changeOrigin: false } },
    },
  }
})
