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
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择状态" clearable>
            <el-option label="借阅中" :value="1" />
            <el-option label="已归还" :value="2" />
            <el-option label="逾期" :value="3" />
          </el-select>
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
        <el-table-column prop="renewCount" label="续借次数" width="90" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'primary' : row.status === 2 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '借阅中' : row.status === 2 ? '已归还' : '逾期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              size="small"
              type="warning"
              @click="handleRenew(row.id)"
            >
              续借
            </el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive<any>({ page: 1, pageSize: 20, readerNo: '', bookTitle: '', status: '' })

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: query.page, pageSize: query.pageSize }
    if (query.readerNo) params.readerNo = query.readerNo
    if (query.bookTitle) params.bookTitle = query.bookTitle
    if (query.status) params.status = query.status
    const res = await request.get('/borrows', { params })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.readerNo = ''; query.bookTitle = ''; query.status = ''
  query.page = 1
  fetchData()
}

async function handleRenew(id: number) {
  await ElMessageBox.confirm('确认续借该图书？', '提示', { type: 'info' })
  await request.post('/borrows/' + id + '/renew')
  ElMessage.success('续借成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
