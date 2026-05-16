<template>
  <div>
    <router-link to="/students" class="text-blue-600 hover:text-blue-800 text-sm mb-4 inline-block">← 返回列表</router-link>

    <div v-if="loading" class="text-center py-12 text-gray-500">加载中...</div>

    <template v-else-if="student">
      <!-- Basic Info -->
      <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-xl font-bold text-gray-800">{{ student.name }}</h2>
            <p class="text-sm text-gray-500 mt-1">{{ student.studentId }} | {{ student.major }} | {{ student.className }}</p>
            <p class="text-sm text-gray-500">入学年份：{{ student.enrollmentYear }}</p>
          </div>
          <div class="text-right">
            <RiskBadge :level="student.riskLevel" class="!text-sm !px-3 !py-1" />
            <p class="text-xs text-gray-500 mt-1">{{ student.riskReason }}</p>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-6 mb-6">
        <!-- Academic Stats -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="font-semibold text-gray-800 mb-4">学业概况</h3>
          <div class="grid grid-cols-2 gap-4">
            <div class="text-center p-3 bg-blue-50 rounded-lg">
              <p class="text-2xl font-bold text-blue-700">{{ student.avgGpa }}</p>
              <p class="text-xs text-gray-500">平均绩点</p>
            </div>
            <div class="text-center p-3 bg-red-50 rounded-lg">
              <p class="text-2xl font-bold text-red-700">{{ student.failCount }}</p>
              <p class="text-xs text-gray-500">挂科数</p>
            </div>
          </div>
        </div>

        <!-- Borrow & Consume Stats -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="font-semibold text-gray-800 mb-4">生活概况</h3>
          <div class="grid grid-cols-2 gap-4">
            <div class="text-center p-3 bg-green-50 rounded-lg">
              <p class="text-2xl font-bold text-green-700">{{ student.totalBorrowCount }}</p>
              <p class="text-xs text-gray-500">累计借阅（近30天: {{ student.recentBorrowCount }}）</p>
            </div>
            <div class="text-center p-3 bg-purple-50 rounded-lg">
              <p class="text-2xl font-bold text-purple-700">¥{{ student.avgDailyConsume }}</p>
              <p class="text-xs text-gray-500">日均消费</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="grid grid-cols-2 gap-6 mb-6">
        <!-- GPA Trend -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="font-semibold text-gray-800 mb-4">绩点趋势</h3>
          <div ref="gpaChartRef" class="h-64"></div>
        </div>
        <!-- Consume Type Distribution -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="font-semibold text-gray-800 mb-4">消费类型分布</h3>
          <div ref="consumeChartRef" class="h-64"></div>
        </div>
      </div>

      <!-- AI Suggestion -->
      <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="font-semibold text-gray-800">AI 学习建议</h3>
          <button @click="generateAI" :disabled="aiLoading"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-blue-400 text-sm font-medium transition">
            {{ aiLoading ? '生成中...' : '生成AI建议' }}
          </button>
        </div>
        <div v-if="aiSuggestion" class="p-4 bg-blue-50 rounded-lg text-sm text-gray-700 leading-relaxed">
          {{ aiSuggestion }}
        </div>
        <div v-else class="p-4 bg-gray-50 rounded-lg text-sm text-gray-400 text-center">
          点击"生成AI建议"获取个性化学习建议
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'
import api from '../api'
import RiskBadge from '../components/RiskBadge.vue'

const route = useRoute()
const student = ref(null)
const loading = ref(true)
const aiSuggestion = ref('')
const aiLoading = ref(false)
const gpaChartRef = ref(null)
const consumeChartRef = ref(null)
let gpaChartInstance = null
let consumeChartInstance = null

onMounted(async () => {
  try {
    const res = await api.get(`/students/${route.params.studentId}`)
    student.value = res.data
    await nextTick()
    renderCharts()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

function renderCharts() {
  if (!student.value) return

  // GPA trend
  if (gpaChartRef.value) {
    gpaChartInstance = echarts.init(gpaChartRef.value)
    const semesters = student.value.gpaTrend?.map(t => t.semester) || []
    const gpas = student.value.gpaTrend?.map(t => t.avgGpa) || []
    gpaChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: semesters },
      yAxis: { type: 'value', min: 0, max: 4 },
      series: [{ data: gpas, type: 'line', smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#3b82f6' } }],
      grid: { left: 40, right: 20, top: 10, bottom: 30 }
    })
  }

  // Consume type
  if (consumeChartRef.value) {
    consumeChartInstance = echarts.init(consumeChartRef.value)
    const data = (student.value.consumeTypeDistribution || []).map(d => ({ name: d.type, value: Number(d.amount) || 0 }))
    consumeChartInstance.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: ['40%', '70%'], data,
        label: { formatter: '{b}\n{d}%' }
      }],
      grid: { left: 0, right: 0, top: 0, bottom: 0 }
    })
  }
}

async function generateAI() {
  aiLoading.value = true
  aiSuggestion.value = ''
  try {
    const res = await api.post('/ai/suggestion', { studentId: route.params.studentId })
    aiSuggestion.value = res.data.suggestion
  } catch (e) {
    aiSuggestion.value = '当前无法获取AI建议，请稍后再试。'
  } finally {
    aiLoading.value = false
  }
}
</script>
