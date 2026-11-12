<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import QRCode from 'qrcode'
const props = defineProps({ work: Object })
const url = import.meta.env.VITE_PUBLIC_START_URL || ''
const image = ref(''), error = ref(''), printing = ref(false)
let mounted = true
async function generate() {
  error.value = ''
  try {
    const parsed = new URL(url)
    if (parsed.protocol !== 'https:' || parsed.username || parsed.password || parsed.hash) throw new Error()
    const result = await QRCode.toDataURL(url, { type: 'image/png', width: 512, margin: 4, errorCorrectionLevel: 'M' })
    if (mounted && !props.work.state.locked) image.value = result
  } catch {
    if (mounted) error.value = 'Chưa tạo được QR. Kiểm tra URL HTTPS và thử lại.'
  }
}
function print() {
  if (!image.value || props.work.state.locked) return
  printing.value = true
  void props.work.activity('SELECT')
  window.print()
}
function afterPrint() { printing.value = false }
onMounted(() => {
  if (url) void generate()
  window.addEventListener('afterprint', afterPrint)
})
onBeforeUnmount(() => {
  mounted = false
  window.removeEventListener('afterprint', afterPrint)
})
</script>

<template>
  <section class="qr-sheet" :class="{ 'qr-printing': printing }">
    <h2>QR chung — Kinh thành Huế</h2>
    <p v-if="!url">Chưa có URL triển khai. QR sẽ sẵn sàng khi cấu hình đường dẫn trang bắt đầu.</p>
    <template v-else>
      <p class="qr-url">{{ url }}</p>
      <img v-if="image" :src="image" width="256" height="256" alt="QR mở trang bắt đầu tham quan">
      <p v-if="error" role="alert">{{ error }} <button @click="generate">Thử lại</button></p>
      <div v-if="image" class="admin-toolbar qr-actions">
        <a :href="image" download="audioqr-kinh-thanh-hue.png" @click="work.activity('SELECT')">Tải PNG</a>
        <button @click="print">In QR và đường dẫn</button>
      </div>
    </template>
  </section>
</template>
