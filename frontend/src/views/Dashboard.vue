<template>
  <div>
    <h2 class="text-xl font-bold text-gray-800 mb-6">可视化看板</h2>

    <div v-if="loading" class="text-center py-12 text-gray-500">加载中...</div>

    <template v-else>
      <div class="grid grid-cols-2 gap-6">
        <!-- Risk Distribution -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="font-semibold text-gray-800 mb-4">班级风险分布</h3>
          <div ref="riskChartRef" class="h-72"></div>
        </div>

        <!-- GPA Trend -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="font-semibold text-gray-800 mb-4">各学期平均绩点趋势</h3>
          <div ref="gpaChartRef" class="h-72"></div>
        </div>

        <!-- Borrow Category -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="font-semibold text-gray-800 mb-4">借阅类别分布</h3>
          <div ref="borrowChartRef" class="h-72"></div>
        </div>

        <!-- Consumption Hourly -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="font-semibold text-gray-800 mb-4">消费时段分布</h3>
          <div ref="consumeChartRef" class="h-72"></div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import api from '../api'

const loading = ref(true)
const riskChartRef = ref(null)
const gpaChartRef = ref(null)
const borrowChartRef = ref(null)
const consumeChartRef = ref(null)

onMounted(async () => {
  try {
    const res = await api.get('/dashboard')
    const data = res.data
    await nextTick()
    renderCharts(data)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

function renderCharts(data) {
  // Risk distribution
  if (riskChartRef.value) {
    const chart = echarts.init(riskChartRef.value)
    const riskMap = { HIGH: '高风险', MEDIUM: '中风险', LOW: '低风险' }
    const riskColors = { HIGH: '#ef4444', MEDIUM: '#eab308', LOW: '#22c55e' }
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: '60%',
        data: (data.riskDistribution || []).map(d => ({
          name: riskMap[d.level] || d.level,
          value: d.count,
          itemStyle: { color: riskColors[d.level] }
        })),
        label: { formatter: '{b}: {c}人 ({d}%)' }
      }]
    })
  }

  // GPA trend
  if (gpaChartRef.value) {
    const chart = echarts.init(gpaChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: (data.gpaTrend || []).map(d => d.semester) },
      yAxis: { type: 'value', min: 0, max: 4 },
      series: [{ data: (data.gpaTrend || []).map(d => d.avg_gpa || d.avgGpa), type: 'bar', itemStyle: { color: '#3b82f6' } }],
      grid: { left: 40, right: 20, top: 10, bottom: 30 }
    })
  }

  // Borrow category
  if (borrowChartRef.value) {
    const chart = echarts.init(borrowChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: '60%',
        data: (data.borrowCategoryDistribution || []).map(d => ({ name: d.category, value: d.count })),
        label: { formatter: '{b}: {d}%' }
      }]
    })
  }

  // Consumption hourly
  if (consumeChartRef.value) {
    const chart = echarts.init(consumeChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: (data.consumptionHourly || []).map(d => d.hour + ':00') },
      yAxis: { type: 'value' },
      series: [{ data: (data.consumptionHourly || []).map(d => d.total), type: 'line', smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#8b5cf6' } }],
      grid: { left: 50, right: 20, top: 10, bottom: 30 }
    })
  }
}
</script>
