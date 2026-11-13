<script setup>
import { onBeforeUnmount, ref, watch } from 'vue'
import TtsPanel from './TtsPanel.vue'
const props = defineProps({ work: Object })
const e = () => props.work.state.editor
const audioId = ref('')
const previewUrl = ref('')
const previewError = ref(false)

function releasePreview() {
  if (previewUrl.value) URL.revokeObjectURL(previewUrl.value)
  previewUrl.value = ''
  previewError.value = false
}

async function loadPreview() {
  releasePreview()
  void props.work.activity('AUDIO_PREVIEW')
  await props.work.previewAudio(bytes => {
    previewUrl.value = URL.createObjectURL(new Blob([bytes], { type: 'audio/mpeg' }))
  })
}

function previewControl(event) {
  if (!event.isTrusted) return
  if (event.type === 'pointerdown' || [' ', 'Enter', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End'].includes(event.key)) {
    void props.work.activity('AUDIO_PREVIEW')
  }
}

watch(() => e()?.audioReview?.audioRevisionId, releasePreview, { flush: 'sync' })
watch(() => e()?.content?.draftText?.textRevisionId, () => {
  audioId.value = ''
  releasePreview()
}, { flush: 'sync' })
onBeforeUnmount(releasePreview)
function edit(field, event) { props.work.editDraft({ [field]: event.target.value }); void props.work.activity('EDIT') }
</script>
<template>
  <section>
    <h3>Nội dung — {{ e().language }}</h3>
    <p>Nháp đã lưu: {{ e().content?.draftText?.textRevisionId || 'Chưa có' }}</p>
    <fieldset :disabled="work.state.busy || !!work.state.pending || e().reviewRequired">
      <label for="text-body">Văn bản / bản dịch đang soạn</label>
      <textarea id="text-body" rows="12" :value="e().draft.body" @input="edit('body',$event)"></textarea>
      <p>Giữ tạm trong tab khác với lưu nháp. Sửa văn bản không gắn audio cũ vào bản mới.</p>
      <button :disabled="e().conflict" @click="work.saveText">Lưu nháp văn bản</button>
      <TtsPanel :work="work" />
      <details><summary>Bản nháp hiện đã lưu để đối chiếu</summary><pre class="admin-body">{{ e().content?.draftText?.body || 'Chưa có' }}</pre></details>
      <h4>Cặp đang phục vụ</h4>
      <p v-if="!e().content?.appliedPair">Chưa có cặp áp dụng</p>
      <template v-else><p>Văn bản {{ e().content.appliedPair.textRevisionId }} / audio {{ e().content.appliedPair.audioRevisionId }}</p><pre class="admin-body">{{ e().content.appliedPair.body }}</pre></template>
      <h4>Hồ sơ duyệt văn bản đã lưu</h4>
      <p>Ghi tham chiếu không tự thẩm định hoặc áp dụng. Phần đang soạn không được âm thầm lưu cùng hồ sơ.</p>
      <label for="text-locator">Tham chiếu hồ sơ văn bản</label><input id="text-locator" :value="e().draft.textLocator" @input="edit('textLocator',$event)">
      <label for="text-limit">Giới hạn văn bản</label><textarea id="text-limit" :value="e().draft.textLimitations" @input="edit('textLimitations',$event)"></textarea>
      <button :disabled="!e().content?.draftText || e().conflict" @click="work.saveReview('text')">Ghi tham chiếu cho văn bản đã lưu</button>
      <p>Hồ sơ hiện đã lưu: {{ e().textReview?.locator || 'Chưa ghi' }} · phiên bản {{ e().textReview?.version ?? 'Chưa có' }}</p>
      <h4>Chọn audio đã lưu để đối chiếu</h4>
      <p>Danh sách lấy từ mức đầy đủ nội dung, chỉ giữ audio của đúng nháp hiện đã lưu. Chọn audio không tự ghi hồ sơ hoặc áp dụng.</p>
      <label for="audio-id">Audio của bản văn hiện đã lưu</label><select id="audio-id" v-model="audioId"><option value="">Chọn audio</option><option v-for="candidate in e().candidates" :key="candidate.audioRevisionId" :value="candidate.audioRevisionId">{{ candidate.audioRevisionId }}</option></select>
      <p v-if="!e().candidates?.length">Chưa có audio cho bản nháp này. Tạo audio từ bản đã lưu và đọc lại trạng thái để theo dõi.</p>
      <ul><li v-for="condition in e().missingConditions" :key="condition">{{ condition }}</li></ul>
      <button :disabled="!audioId || !e().content?.draftText" @click="work.chooseAudio(audioId)">Đọc và đối chiếu audio</button>
      <button v-if="e().content?.appliedPair?.textRevisionId && e().content.appliedPair.textRevisionId === e().content.draftText?.textRevisionId" @click="work.chooseAudio(e().content.appliedPair.audioRevisionId)">Đối chiếu audio của cặp đang dùng</button>
      <template v-if="e().audioReview">
        <p>Audio {{ e().audioReview.audioRevisionId }} · văn bản {{ e().audioReview.textRevisionId }} — {{ e().audioReview.language }}</p>
        <button :disabled="e().audioReview.textRevisionId !== e().content.draftText?.textRevisionId" @click="loadPreview">Tải để nghe thử</button>
        <audio v-if="previewUrl" :src="previewUrl" controls preload="metadata" @pointerdown="previewControl" @keydown="previewControl" @error="previewError = true"></audio>
        <p v-if="previewError" role="alert">Không phát được bản nghe thử.</p>
        <label for="audio-locator">Tham chiếu hồ sơ audio</label><input id="audio-locator" :value="e().draft.audioLocator" @input="edit('audioLocator',$event)">
        <label for="audio-limit">Giới hạn audio</label><textarea id="audio-limit" :value="e().draft.audioLimitations" @input="edit('audioLimitations',$event)"></textarea>
        <button :disabled="e().conflict" @click="work.saveReview('audio')">Ghi tham chiếu cho audio đã lưu</button>
        <p>Hồ sơ hiện đã lưu: {{ e().audioReview.locator || 'Chưa ghi' }} · phiên bản {{ e().audioReview.version ?? 'Chưa có' }}</p>
      </template>
      <button v-if="work.reviewDraftDirty()" @click="work.discardReviewDraft">Bỏ thay đổi ở các tham chiếu</button>
      <p>Chỉ áp dụng cả cặp khớp đúng bản/ngôn ngữ và hai hồ sơ đã lưu. Không tự bật hiện địa điểm.</p>
      <button :disabled="work.dirty() || !e().audioReview || e().textReview?.version == null || e().audioReview?.version == null || e().conflict" @click="work.applyPair">Áp dụng văn bản và audio</button>
    </fieldset>
  </section>
</template>
