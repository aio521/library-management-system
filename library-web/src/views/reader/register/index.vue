<template>
  <div class="page-container">
    <el-card>
      <h3 style="margin-bottom:20px">读者注册</h3>
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px" style="max-width:500px">
        <el-form-item label="读者编号" prop="readerNo">
          <el-input v-model="form.readerNo" placeholder="留空自动生成" />
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
        <el-form-item label="身份证号" prop="idCard">
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
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">注册</el-button>
          <el-button @click="resetForm">清空</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:16px">
      <h3 style="margin-bottom:16px">读者列表</h3>
      <el-table :data="tableData" border stripe v-loading="tableLoading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="readerNo" label="读者编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="dept" label="部门" width="140" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="cardNo" label="借阅证号" width="140" />
        <el-table-column prop="createTime" label="注册时间" width="160" />
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 16px;">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next, sizes"
          @change="fetchList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const formRef = ref()

const form = reactive<any>({
  readerNo: '', name: '', gender: '', idCard: '', dept: '', phone: '',
  maxBorrow: 10, borrowDays: 30
})
const query = reactive({ page: 1, pageSize: 20 })

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  dept: [{ required: true, message: '请输入部门', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const data: any = { ...form }
    if (!data.readerNo) delete data.readerNo
    await request.post('/readers', data)
    ElMessage.success('注册成功')
    resetForm()
    fetchList()
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

function resetForm() {
  Object.keys(form).forEach(k => (form[k] = undefined))
  form.maxBorrow = 10
  form.borrowDays = 30
  formRef.value?.resetFields()
}

async function fetchList() {
  tableLoading.value = true
  try {
    const res = await request.get('/readers', { params: { page: query.page, pageSize: query.pageSize } })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    tableLoading.value = false
  }
}

onMounted(() => fetchList())
</script>
