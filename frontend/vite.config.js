import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: { input: { guest: 'index.html', admin: 'admin.html', mapPreview: 'map-preview.html' } },
  },
  server: {
    proxy: { '/api': { target: process.env.HUEQR_BACKEND_URL || 'http://127.0.0.1:8080', changeOrigin: false } },
  },
})
