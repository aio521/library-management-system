<template>
  <div class="dashboard">
    <h3>欢迎使用新疆财经大学图书馆管理系统</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card>
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </el-card>
      </el-col>
    </el-row>
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

onMounted(async () => {
  try {
    const res = await request.get('/statistics/borrow/overview')
    if (res.data) {
      stats.value[0].value = res.data.todayBorrow
      stats.value[1].value = res.data.monthBorrow
      stats.value[2].value = res.data.yearBorrow
      stats.value[3].value = res.data.overdueCount
    }
  } catch { /* stats not critical */ }
})
</script>

<style scoped>
.dashboard h3 { margin-bottom: 20px; }
.stat-value { font-size: 32px; font-weight: bold; color: #409EFF; text-align: center; }
.stat-label { text-align: center; color: #909399; margin-top: 8px; }
</style>
