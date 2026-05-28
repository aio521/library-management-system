<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card>
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top:20px">
      <h3 style="margin-bottom:16px">借阅趋势</h3>
      <el-table :data="trendData" border stripe v-loading="loading">
        <el-table-column prop="date" label="日期" width="140" />
        <el-table-column prop="borrowCount" label="借阅数量" width="120" />
        <el-table-column prop="returnCount" label="归还数量" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const stats = ref([
  { label: '今日借阅', value: 0 },
  { label: '本月借阅', value: 0 },
  { label: '本年借阅', value: 0 },
  { label: '逾期未还', value: 0 }
])

const trendData = ref([])
const loading = ref(false)

onMounted(async () => {
  try {
    const overviewRes = await request.get('/statistics/borrow/overview')
    if (overviewRes.data) {
      stats.value[0].value = overviewRes.data.todayBorrow || 0
      stats.value[1].value = overviewRes.data.monthBorrow || 0
      stats.value[2].value = overviewRes.data.yearBorrow || 0
      stats.value[3].value = overviewRes.data.overdueCount || 0
    }
  } catch { /* ignore */ }

  loading.value = true
  try {
    const trendRes = await request.get('/statistics/borrow/trend')
    trendData.value = trendRes.data || []
  } catch { /* ignore */ } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.stat-value { font-size: 32px; font-weight: bold; color: #409EFF; text-align: center; }
.stat-label { text-align: center; color: #909399; margin-top: 8px; }
</style>
