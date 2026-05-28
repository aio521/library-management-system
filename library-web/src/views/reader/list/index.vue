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
        <el-form-item label="部门">
          <el-input v-model="query.dept" placeholder="请输入部门" clearable />
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
          <el-button type="success" @click="showAddDialog">新增</el-button>
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
        <el-table-column prop="cardNo" label="借阅证号" width="140" />
        <el-table-column prop="currentBorrowCount" label="当前借阅" width="90" />
        <el-table-column prop="maxBorrow" label="最大借阅" width="90" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '挂失' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '挂失' : '恢复' }}
            </el-button>
            <el-button size="small" @click="handleIssueCard(row.id)">发证</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
        <el-form-item label="读者编号" prop="readerNo">
          <el-input v-model="form.readerNo" placeholder="请输入读者编号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别" style="width:100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="部门" prop="dept">
          <el-input v-model="form.dept" placeholder="请输入部门" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="最大借阅数">
          <el-input-number v-model="form.maxBorrow" :min="1" :max="50" style="width:100%" />
        </el-form-item>
        <el-form-item label="借阅天数">
          <el-input-number v-model="form.borrowDays" :min="1" :max="365" style="width:100%" />
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
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const isEdit = ref(false)

const query = reactive<any>({ page: 1, pageSize: 20, readerNo: '', name: '', dept: '', status: '' })
const form = reactive<any>({
  readerNo: '', name: '', gender: '', idCard: '', dept: '', phone: '',
  maxBorrow: 10, borrowDays: 30
})
const rules = {
  readerNo: [{ required: true, message: '请输入读者编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  dept: [{ required: true, message: '请输入部门', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: query.page, pageSize: query.pageSize }
    if (query.readerNo) params.readerNo = query.readerNo
    if (query.name) params.name = query.name
    if (query.dept) params.dept = query.dept
    if (query.status) params.status = query.status
    const res = await request.get('/readers', { params })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.readerNo = ''; query.name = ''; query.dept = ''; query.status = ''
  query.page = 1
  fetchData()
}

function showAddDialog() {
  isEdit.value = false
  dialogTitle.value = '新增读者'
  dialogVisible.value = true
}

function showEditDialog(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑读者'
  Object.assign(form, row)
  dialogVisible.value = true
}

function resetForm() {
  Object.keys(form).forEach(k => (form[k] = undefined))
  form.maxBorrow = 10
  form.borrowDays = 30
  formRef.value?.resetFields()
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await request.put('/readers/' + form.id, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/readers', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch { /* handled by interceptor */ }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确认删除该读者？', '提示', { type: 'warning' })
  await request.delete('/readers/' + id)
  ElMessage.success('删除成功')
  fetchData()
}

async function handleToggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '挂失' : '恢复'
  await ElMessageBox.confirm('确认' + actionText + '该读者？', '提示', { type: 'warning' })
  await request.put('/readers/' + row.id + '/status', { status: newStatus })
  ElMessage.success(actionText + '成功')
  fetchData()
}

async function handleIssueCard(id: number) {
  await request.post('/readers/' + id + '/card')
  ElMessage.success('发证成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
