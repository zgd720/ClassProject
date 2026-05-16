<template>
  <header class="bg-white shadow-sm px-6 py-3 flex items-center justify-between">
    <h1 class="text-lg font-semibold text-gray-800">学业行为分析助手</h1>
    <div class="flex items-center gap-4">
      <span class="text-sm text-gray-600">{{ auth.name }} ({{ roleText }})</span>
      <button @click="handleLogout" class="text-sm text-gray-500 hover:text-red-500 transition">退出登录</button>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const roleText = computed(() => {
  switch (auth.role) {
    case 'COUNSELOR': return '辅导员'
    case 'STUDENT': return '学生'
    case 'ADMIN': return '管理员'
    default: return auth.role
  }
})

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>
