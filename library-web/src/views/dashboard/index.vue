<template>
  <div class="dashboard">
    <div class="welcome-section">
      <h3>欢迎回来，{{ authStore.userInfo?.realName }}</h3>
      <p>{{ greeting }}，以下是今日图书馆运营概况</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-icon" :style="{ background: stat.bg }">
          <el-icon :size="22"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stat.value.toLocaleString() }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
        <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'" v-if="stat.trend !== 0">
          {{ stat.trend > 0 ? '↑' : '↓' }} {{ Math.abs(stat.trend) }}%
        </div>
      </div>
    </div>

    <div class="panels-grid">
      <div class="panel">
        <div class="panel-header"><h4>快捷操作</h4></div>
        <div class="shortcuts">
          <router-link to="/borrow/borrow" class="shortcut-item">
            <el-icon :size="18"><Notebook /></el-icon><span>借书操作</span>
          </router-link>
          <router-link to="/borrow/return" class="shortcut-item">
            <el-icon :size="18"><Finished /></el-icon><span>还书操作</span>
          </router-link>
          <router-link to="/book/catalog" class="shortcut-item">
            <el-icon :size="18"><Reading /></el-icon><span>图书编目</span>
          </router-link>
          <router-link to="/reader/register" class="shortcut-item">
            <el-icon :size="18"><UserFilled /></el-icon><span>读者注册</span>
          </router-link>
        </div>
      </div>
      <div class="panel">
        <div class="panel-header"><h4>系统信息</h4></div>
        <div class="info-list">
          <div class="info-item"><span>系统版本</span><span>v1.0.0</span></div>
          <div class="info-item"><span>前端框架</span><span>Vue 3 + Element Plus</span></div>
          <div class="info-item"><span>后端框架</span><span>SpringBoot 3.2</span></div>
          <div class="info-item"><span>数据库</span><span>MySQL 8.0</span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import request from '@/api/request'
import { TrendCharts, Document, Notebook, Warning, Finished, Reading, UserFilled } from '@element-plus/icons-vue'

const authStore = useAuthStore()

const greeting = (() => {
  const h = new Date().getHours()
  if (h < 9) return '早上好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})()

const stats = ref([
  { label: '今日借阅', value: 0, icon: TrendCharts, bg: '#FDF2E3', trend: 12 },
  { label: '本月借阅', value: 0, icon: Document, bg: '#EBF6EF', trend: 8 },
  { label: '本年借阅', value: 0, icon: Notebook, bg: '#EBF0F8', trend: 23 },
  { label: '逾期未还', value: 0, icon: Warning, bg: '#FDECEC', trend: -5 },
])

onMounted(async () => {
  try {
    const res = await request.get('/statistics/borrow/overview')
    if (res.data) {
      stats.value[0].value = res.data.todayBorrow || 0
      stats.value[1].value = res.data.monthBorrow || 0
      stats.value[2].value = res.data.yearBorrow || 0
      stats.value[3].value = res.data.overdueCount || 0
    }
  } catch { /* non-critical */ }
})
</script>

<style scoped>
.dashboard { max-width: 1100px; }

.welcome-section { margin-bottom: 28px; }
.welcome-section h3 { font-size: 22px; font-weight: 700; color: #2C2416; margin: 0 0 6px; }
.welcome-section p { font-size: 14px; color: #9B8E78; margin: 0; }

.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 28px; }

.stat-card {
  background: #FFFFFF; border: 1px solid #EBE5D8; border-radius: 12px; padding: 20px;
  display: flex; align-items: center; gap: 16px; position: relative; overflow: hidden;
  transition: all 0.25s;
}
.stat-card:hover { box-shadow: 0 6px 24px rgba(44,36,22,0.08); transform: translateY(-2px); }

.stat-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; color: #5A4A2A; }
.stat-info { flex: 1; min-width: 0; }
.stat-value { font-size: 28px; font-weight: 700; color: #2C2416; line-height: 1.2; font-variant-numeric: tabular-nums; }
.stat-label { font-size: 13px; color: #9B8E78; margin-top: 2px; }

.stat-trend { position: absolute; top: 16px; right: 16px; font-size: 12px; font-weight: 600; padding: 2px 8px; border-radius: 100px; }
.stat-trend.up { color: #4A7C59; background: #EBF6EF; }
.stat-trend.down { color: #B85450; background: #FDECEC; }

.panels-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }

.panel { background: #FFFFFF; border: 1px solid #EBE5D8; border-radius: 12px; overflow: hidden; }
.panel-header { padding: 18px 24px; border-bottom: 1px solid #EBE5D8; }
.panel-header h4 { margin: 0; font-size: 15px; font-weight: 600; color: #2C2416; }

.shortcuts { display: grid; grid-template-columns: 1fr 1fr; gap: 1px; background: #EBE5D8; }
.shortcut-item { display: flex; align-items: center; gap: 10px; padding: 18px 24px; background: #FFFFFF; color: #5A4A2A; text-decoration: none; font-size: 14px; font-weight: 500; transition: all 0.2s; }
.shortcut-item:hover { background: #FDF9F2; color: #8B6914; }

.info-list { padding: 8px 0; }
.info-item { display: flex; justify-content: space-between; padding: 14px 24px; font-size: 14px; }
.info-item + .info-item { border-top: 1px solid #F3EFE6; }
.info-item span:first-child { color: #6B5E48; }
.info-item span:last-child { color: #9B8E78; font-weight: 500; }

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: 1fr 1fr; }
  .panels-grid { grid-template-columns: 1fr; }
}
</style>
