<script setup>
import { ref } from 'vue'
import GuestMap from '../components/GuestMap.vue'
import { mapPreviewEnabled } from '../map/local-basemap.js'
import proposal from '../assets/hue-proposed-regions.json'

const selected = ref(null)
const select = place => { selected.value = place }
</script>

<template>
  <main class="page map-review-page">
    <header class="masthead"><h1>Xem trước bản đồ Kinh thành Huế</h1></header>
    <template v-if="mapPreviewEnabled">
      <p class="preview-intro">Chạm trong vùng để chọn điểm và đặt ghim. Kéo hoặc phóng để xem bản đồ.</p>
      <GuestMap :places="proposal.places" :selected="selected?.id" cancel-label="Hủy" overlap-label="Bạn muốn xem vùng nào?" @select="select" />
      <section class="preview-selection" aria-live="polite">
        <h2>{{ selected ? selected.name : 'Chọn một vùng để xem' }}</h2>
      </section>
      <p class="preview-disclaimer">Bản xem trước · Năm vùng đề xuất chờ duyệt, chưa phải ranh giới di tích được xác nhận.</p>
    </template>
    <p v-else>Chế độ xem trước bản đồ chưa được bật cho bản dựng này.</p>
  </main>
</template>

<style>
.map-review-page { padding-bottom: 1.5rem; }
.map-review-page h1 { font-size: clamp(1.4rem, 3vw, 2rem); margin: 0; }
.map-review-page .map-wrapper { height: clamp(300px, 58dvh, 560px); }
.preview-intro { margin: .75rem 0; }
.preview-disclaimer { margin: .75rem 0 0; color: #666; font-size: .8rem; }
.preview-selection { padding: 1rem; border: 1px solid #e1e1e1; border-radius: 10px; background: #fff; }
.preview-selection h2 { font-size: 1.2rem; color: var(--imperial-red); font-weight: 600; }
.preview-selection { margin-top: .75rem; }
.preview-selection h2 { margin-bottom: 0; }
</style>
