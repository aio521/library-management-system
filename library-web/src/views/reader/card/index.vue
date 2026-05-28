<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="读者编号">
          <el-input v-model="query.readerNo" placeholder="请输入读者编号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="挂失" :value="0" />
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
        <el-table-column prop="readerNo" label="读者编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="dept" label="部门" width="140" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="cardNo" label="借阅证号" width="160" />
        <el-table-column prop="status" label="证状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '挂失' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleReissueCard(row.id)">补办借阅证</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '挂失' : '恢复' }}
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

const query = reactive<any>({ page: 1, pageSize: 20, readerNo: '', name: '', status: '' })

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: query.page, pageSize: query.pageSize }
    if (query.readerNo) params.readerNo = query.readerNo
    if (query.name) params.name = query.name
    if (query.status) params.status = query.status
    const res = await request.get('/readers', { params })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.readerNo = ''; query.name = ''; query.status = ''
  query.page = 1
  fetchData()
}

async function handleReissueCard(id: number) {
  await ElMessageBox.confirm('确认补办借阅证？', '提示', { type: 'info' })
  await request.post('/readers/' + id + '/card')
  ElMessage.success('补办成功')
  fetchData()
}

async function handleToggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '挂失' : '恢复'
  await ElMessageBox.confirm('确认' + actionText + '该借阅证？', '提示', { type: 'warning' })
  await request.put('/readers/' + row.id + '/status', { status: newStatus })
  ElMessage.success(actionText + '成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
