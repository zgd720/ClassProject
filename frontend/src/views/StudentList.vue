<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-xl font-bold text-gray-800">学生画像</h2>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-lg shadow-sm p-4 mb-4 flex flex-wrap gap-4">
      <input v-model="keyword" @input="search" placeholder="搜索姓名/学号..."
        class="px-3 py-2 border border-gray-300 rounded-lg text-sm w-48 focus:ring-2 focus:ring-blue-500 outline-none" />
      <select v-model="riskFilter" @change="search"
        class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 outline-none">
        <option value="">全部风险等级</option>
        <option value="HIGH">高风险</option>
        <option value="MEDIUM">中风险</option>
        <option value="LOW">低风险</option>
      </select>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-lg shadow-sm overflow-hidden">
      <div v-if="loading" class="p-8 text-center text-gray-500">加载中...</div>
      <table v-else class="w-full">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">学号</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">姓名</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">班级</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">平均绩点</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">挂科数</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">风险等级</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr v-for="s in students" :key="s.studentId" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-sm">{{ s.studentId }}</td>
            <td class="px-4 py-3 text-sm font-medium">{{ s.name }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ s.className }}</td>
            <td class="px-4 py-3 text-sm">{{ s.avgGpa }}</td>
            <td class="px-4 py-3 text-sm">{{ s.failCount }}</td>
            <td class="px-4 py-3"><RiskBadge :level="s.riskLevel" /></td>
            <td class="px-4 py-3">
              <router-link :to="'/students/' + s.studentId"
                class="text-blue-600 hover:text-blue-800 text-sm font-medium">详情</router-link>
            </td>
          </tr>
          <tr v-if="students.length === 0">
            <td colspan="7" class="px-4 py-8 text-center text-gray-500">暂无数据，请先导入学生数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="flex items-center justify-between mt-4">
      <span class="text-sm text-gray-600">共 {{ total }} 条</span>
      <div class="flex gap-2">
        <button @click="changePage(current - 1)" :disabled="current <= 1"
          class="px-3 py-1 text-sm border rounded hover:bg-gray-100 disabled:opacity-50">上一页</button>
        <span class="px-3 py-1 text-sm">{{ current }} / {{ Math.ceil(total / size) || 1 }}</span>
        <button @click="changePage(current + 1)" :disabled="current >= Math.ceil(total / size)"
          class="px-3 py-1 text-sm border rounded hover:bg-gray-100 disabled:opacity-50">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'
import RiskBadge from '../components/RiskBadge.vue'

const students = ref([])
const loading = ref(true)
const keyword = ref('')
const riskFilter = ref('')
const current = ref(1)
const size = ref(10)
const total = ref(0)

let debounceTimer = null

onMounted(() => fetchStudents())

function search() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    current.value = 1
    fetchStudents()
  }, 300)
}

function changePage(page) {
  current.value = page
  fetchStudents()
}

async function fetchStudents() {
  loading.value = true
  try {
    const res = await api.get('/students', {
      params: { page: current.value, size: size.value, keyword: keyword.value, riskLevel: riskFilter.value }
    })
    students.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>
