<template>
  <span :class="badgeClass">{{ label }}</span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ level: String })

const label = computed(() => {
  switch (props.level) {
    case 'HIGH': return '高风险'
    case 'MEDIUM': return '中风险'
    case 'LOW': return '低风险'
    default: return props.level
  }
})

const badgeClass = computed(() => {
  switch (props.level) {
    case 'HIGH': return 'px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-700'
    case 'MEDIUM': return 'px-2 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-700'
    case 'LOW': return 'px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-700'
    default: return 'px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-600'
  }
})
</script>
