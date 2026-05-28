<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="书名">
          <el-input v-model="query.bookTitle" placeholder="请输入书名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="showAddDialog">新增预约</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="readerName" label="读者" width="100" />
        <el-table-column prop="readerNo" label="读者编号" width="120" />
        <el-table-column prop="bookTitle" label="书名" min-width="140" />
        <el-table-column prop="reserveDate" label="预约日期" width="160" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : 'info'" size="small">
              {{ row.status === 1 ? '预约中' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              size="small"
              type="danger"
              @click="handleCancel(row.id)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增预约" width="500px" @close="resetForm">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
        <el-form-item label="读者ID" prop="readerId">
          <el-input v-model="form.readerId" placeholder="请输入读者ID" />
        </el-form-item>
        <el-form-item label="图书ID" prop="bookId">
          <el-input v-model="form.bookId" placeholder="请输入图书ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ bookTitle: '' })
const form = reactive({ readerId: '', bookId: '' })

const rules = {
  readerId: [{ required: true, message: '请输入读者ID', trigger: 'blur' }],
  bookId: [{ required: true, message: '请输入图书ID', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const params: any = {}
    if (query.bookTitle) params.bookTitle = query.bookTitle
    const res = await request.get('/reserves', { params })
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

function resetQuery() { query.bookTitle = ''; fetchData() }

function showAddDialog() { dialogVisible.value = true }

function resetForm() {
  form.readerId = ''; form.bookId = ''
  formRef.value?.resetFields()
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await request.post('/reserves', { readerId: form.readerId, bookId: form.bookId })
    ElMessage.success('预约成功')
    dialogVisible.value = false
    fetchData()
  } catch { /* handled by interceptor */ }
}

async function handleCancel(id: number) {
  await ElMessageBox.confirm('确认取消该预约？', '提示', { type: 'warning' })
  await request.delete('/reserves/' + id)
  ElMessage.success('取消成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
