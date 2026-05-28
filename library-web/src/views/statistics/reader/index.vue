<template>
  <div class="page-container">
    <el-card>
      <h3 style="margin-bottom:16px">活跃读者排行</h3>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column label="排名" width="70">
          <template #default="{ $index }">
            <el-tag
              :type="$index === 0 ? 'danger' : $index === 1 ? 'warning' : $index === 2 ? 'primary' : 'info'"
              size="small"
            >
              {{ $index + 1 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="readerId" label="读者ID" width="80" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="readerNo" label="读者编号" width="120" />
        <el-table-column prop="dept" label="部门" width="140" />
        <el-table-column prop="borrowCount" label="借阅次数" width="110">
          <template #default="{ row }">
            <span style="color:#409EFF;font-weight:bold">{{ row.borrowCount }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const tableData = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await request.get('/statistics/readers/active', { params: { limit: 20 } })
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
})
</script>
