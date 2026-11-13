<script setup>
defineProps({ state: String, labels: Object, canRetry: Boolean, disabled: Boolean })
defineEmits(['retry'])
</script>

<template>
  <div v-if="state === 'loading' || state === 'error'" class="basemap-status" :class="{ 'basemap-error': state === 'error' }" :role="state === 'error' ? 'alert' : 'status'">
    <span>{{ state === 'loading' ? labels.mapLoading : labels.mapError }}</span>
    <button v-if="state === 'error' && canRetry" type="button" :disabled="disabled" @click="$emit('retry')">{{ labels.mapRetry }}</button>
  </div>
</template>

<style scoped>
.basemap-status { flex-shrink: 0; display: flex; align-items: center; gap: .5rem; padding: .3rem 0; font-size: .75rem; line-height: 1.4; overflow-wrap: anywhere; }
.basemap-error { color: #7b1818; }
.basemap-status span { min-width: 0; }
.basemap-status button { flex-shrink: 0; font: inherit; cursor: pointer; border: 1px solid #b9a7a7; border-radius: 4px; padding: .35rem .5rem; background: #fff; color: #7b1818; }
.basemap-status button:disabled { opacity: .5; cursor: default; }
</style>
