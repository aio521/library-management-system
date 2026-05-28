<template>
  <div class="page-container">
    <el-card>
      <h3 style="margin-bottom:16px">热门图书排行</h3>
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
        <el-table-column prop="bookId" label="图书ID" width="80" />
        <el-table-column prop="title" label="书名" min-width="180" />
        <el-table-column prop="author" label="作者" width="120" />
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
    const res = await request.get('/statistics/books/popular', { params: { limit: 20 } })
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
})
</script>
