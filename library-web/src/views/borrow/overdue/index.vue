<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="读者编号">
          <el-input v-model="query.readerNo" placeholder="请输入读者编号" clearable />
        </el-form-item>
        <el-form-item label="书名">
          <el-input v-model="query.bookTitle" placeholder="请输入书名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="readerName" label="读者" width="100" />
        <el-table-column prop="readerNo" label="读者编号" width="120" />
        <el-table-column prop="bookTitle" label="书名" min-width="140" />
        <el-table-column prop="barcode" label="条码" width="140" />
        <el-table-column prop="borrowDate" label="借阅日期" width="110" />
        <el-table-column prop="dueDate" label="应还日期" width="110" />
        <el-table-column prop="overdueDays" label="逾期天数" width="90">
          <template #default="{ row }">
            <span style="color:#f56c6c;font-weight:bold">{{ row.overdueDays }}天</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default>
            <el-tag type="danger" size="small">逾期</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 16px;">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next, sizes"
          @change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive<any>({ page: 1, pageSize: 20, readerNo: '', bookTitle: '' })

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: query.page, pageSize: query.pageSize }
    if (query.readerNo) params.readerNo = query.readerNo
    if (query.bookTitle) params.bookTitle = query.bookTitle
    const res = await request.get('/borrows/overdue', { params })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.readerNo = ''; query.bookTitle = ''
  query.page = 1
  fetchData()
}

onMounted(() => fetchData())
</script>
