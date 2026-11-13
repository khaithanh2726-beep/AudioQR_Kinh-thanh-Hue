<script setup>
import { computed } from 'vue'
const props = defineProps({ work: Object })
const editor = computed(() => props.work.state.editor)
const job = computed(() => editor.value.ttsJob)
const labels = {
  QUEUED: 'Đã nhận, đang chờ xử lý', DISPATCHING: 'Đang tạo WAV',
  UNCERTAIN: 'Chưa rõ kết quả tạo WAV', RESULT_STAGED: 'Có WAV, chờ chuyển MP3',
  CONVERTING: 'Đang chuyển MP3', SUCCEEDED: 'Đã đăng ký audio',
  FAILED: 'Xử lý lỗi', DISCARDED: 'Đích đã bị loại bỏ',
}
const locked = computed(() => props.work.state.busy || !!props.work.state.pending || editor.value.reviewRequired || editor.value.conflict)
</script>

<template>
  <section aria-labelledby="tts-heading">
    <h4 id="tts-heading">Tạo và theo dõi audio</h4>
    <p>Audio được tạo từ bản văn đã lưu. Hoàn tất audio cần được nghe và ghi hồ sơ trước khi áp dụng.</p>
    <button :disabled="locked || !editor.content?.draftText || editor.draft.body !== editor.content.draftText.body" @click="work.createTts">Tạo audio từ bản đã lưu</button>
    <button :disabled="locked" @click="work.refreshEditor(); work.activity('SELECT')">Đọc lại trạng thái</button>
    <template v-if="job">
      <p>{{ labels[job.phase] || job.state }} · mã tác vụ {{ job.jobId }}</p>
      <p>Bản văn {{ job.textRevisionId }} · ngôn ngữ {{ job.language }}</p>
      <p v-if="job.failureCode" role="alert">Mã lỗi {{ job.failureCode }}</p>
      <p v-if="job.outcomeUncertain">Kiểm tra kết quả trước khi quyết định gửi lại.</p>
      <button :disabled="locked || job.phase === 'DISCARDED'" @click="work.checkTts">Kiểm tra kết quả tác vụ</button>
      <button :disabled="locked || !['REPROCESS_LOCAL','CREATE_PROVIDER_ATTEMPT'].includes(job.retryAction)" @click="work.retryTts">
        {{ job.retryAction === 'REPROCESS_LOCAL' ? 'Chuyển lại MP3 từ WAV' : 'Thử lại tạo WAV' }}
      </button>
      <p v-if="job.result">Audio {{ job.result.audioRevisionId }} · {{ job.result.durationSeconds }} giây</p>
      <button v-if="job.result?.audioRevisionId" :disabled="locked" @click="work.chooseAudio(job.result.audioRevisionId)">Chọn kết quả để nghe và đối chiếu</button>
    </template>
    <p v-else>Chưa có tác vụ cho bản nháp này.</p>
  </section>
</template>
