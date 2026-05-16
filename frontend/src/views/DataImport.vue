<template>
  <div>
    <h2 class="text-xl font-bold text-gray-800 mb-6">数据管理</h2>

    <!-- Import Section -->
    <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
      <h3 class="font-semibold text-gray-800 mb-4">导入CSV数据</h3>
      <div class="grid grid-cols-2 gap-4">
        <div v-for="item in importTypes" :key="item.type"
          class="border-2 border-dashed border-gray-300 rounded-lg p-4 hover:border-blue-400 transition">
          <p class="font-medium text-gray-700 mb-2">{{ item.label }}</p>
          <p class="text-xs text-gray-500 mb-3">{{ item.desc }}</p>
          <input type="file" accept=".csv" @change="e => handleFile(e, item.type)"
            class="text-sm text-gray-600 file:mr-3 file:py-1 file:px-3 file:rounded file:border-0 file:text-sm file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" />
          <p v-if="importMsg[item.type]" class="text-xs mt-2" :class="importMsg[item.type].includes('成功') ? 'text-green-600' : 'text-red-500'">
            {{ importMsg[item.type] }}
          </p>
        </div>
      </div>
    </div>

    <!-- Preview Section -->
    <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="font-semibold text-gray-800">数据预览</h3>
        <select v-model="previewType" @change="fetchPreview"
          class="px-3 py-1.5 border border-gray-300 rounded-lg text-sm">
          <option value="student">学生信息</option>
          <option value="course_score">成绩记录</option>
          <option value="library_borrow">借阅记录</option>
          <option value="consumption">消费记录</option>
        </select>
      </div>
      <div v-if="previewLoading" class="text-center py-4 text-gray-500">加载中...</div>
      <div v-else-if="previewData.length === 0" class="text-center py-4 text-gray-400">暂无数据</div>
      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="bg-gray-50">
              <th v-for="col in previewColumns" :key="col" class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase">{{ col }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="(row, i) in previewData" :key="i">
              <td v-for="col in previewColumns" :key="col" class="px-3 py-1.5 text-gray-700">{{ row[col] }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Clear Button -->
    <div class="flex justify-end">
      <button @click="handleClear"
        class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 text-sm font-medium transition">
        清空所有业务数据
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../api'

const importTypes = [
  { type: 'student', label: '学生基本信息', desc: '字段：student_id, name, gender, major, class_name, enrollment_year' },
  { type: 'course_score', label: '成绩记录', desc: '字段：student_id, semester, course_name, score, gpa' },
  { type: 'library_borrow', label: '借阅记录', desc: '字段：student_id, borrow_date, book_name, category' },
  { type: 'consumption', label: '消费记录', desc: '字段：student_id, consume_time, amount, type' }
]

const importMsg = reactive({})
const previewType = ref('student')
const previewData = ref([])
const previewColumns = ref([])
const previewLoading = ref(false)

onMounted(() => fetchPreview())

async function handleFile(e, type) {
  const file = e.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', type)
  try {
    await api.post('/data/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    importMsg[type] = `导入成功 (${file.name})`
    fetchPreview()
  } catch (e) {
    importMsg[type] = `导入失败: ${e.response?.data?.message || e.message}`
  }
}

async function fetchPreview() {
  previewLoading.value = true
  try {
    const res = await api.get('/data/preview', { params: { type: previewType.value, limit: 10 } })
    previewData.value = res.data || []
    if (previewData.value.length > 0) {
      previewColumns.value = Object.keys(previewData.value[0])
    } else {
      previewColumns.value = []
    }
  } catch (e) {
    console.error(e)
  } finally {
    previewLoading.value = false
  }
}

async function handleClear() {
  if (!confirm('确定要清空所有业务数据吗？此操作不可恢复！')) return
  try {
    await api.delete('/data/clear')
    alert('数据已清空')
    fetchPreview()
  } catch (e) {
    alert('清空失败: ' + (e.response?.data?.message || e.message))
  }
}
</script>
