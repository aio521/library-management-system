<template>
  <div class="page-container">
    <el-card style="max-width:600px">
      <h3 style="margin-bottom:20px">个人信息</h3>
      <el-descriptions v-if="userInfo" :column="1" border>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ userInfo.realName }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ (userInfo as any).phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <template v-if="userInfo.roles && userInfo.roles.length">
            <el-tag v-for="r in userInfo.roles" :key="r" size="small" style="margin-right:4px">{{ r }}</el-tag>
          </template>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
      <div style="margin-top:20px">
        <el-button type="primary" @click="handleRefresh">刷新信息</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const userInfo = computed(() => authStore.userInfo)

async function handleRefresh() {
  try {
    await authStore.getUserInfo()
    ElMessage.success('信息已刷新')
  } catch { /* ignore */ }
}
</script>
