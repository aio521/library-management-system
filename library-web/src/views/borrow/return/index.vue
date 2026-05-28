<template>
  <div class="page-container">
    <el-card>
      <h3 style="margin-bottom:20px">图书归还</h3>
      <el-form :model="form" ref="formRef" :rules="rules" label-width="140px" style="max-width:600px">
        <el-form-item label="借阅记录ID" prop="borrowId">
          <el-input v-model="form.borrowId" placeholder="请输入借阅记录ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleReturn">
            确认归还
          </el-button>
          <el-button size="large" @click="resetForm">清空</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="resultVisible" title="归还成功" width="500px">
      <el-descriptions v-if="returnResult" :column="2" border>
        <el-descriptions-item label="读者">{{ returnResult.readerName }}</el-descriptions-item>
        <el-descriptions-item label="图书">{{ returnResult.bookTitle }}</el-descriptions-item>
        <el-descriptions-item label="条码">{{ returnResult.barcode }}</el-descriptions-item>
        <el-descriptions-item label="借阅日期">{{ returnResult.borrowDate }}</el-descriptions-item>
        <el-descriptions-item label="归还日期">{{ returnResult.returnDate }}</el-descriptions-item>
        <el-descriptions-item v-if="returnResult.isOverdue" label="逾期">
          <el-tag type="danger">逾期</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="resultVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import request from '@/api/request'
const loading = ref(false)
const formRef = ref()
const resultVisible = ref(false)
const returnResult = ref<any>(null)

const form = reactive({ borrowId: '' })

const rules = {
  borrowId: [{ required: true, message: '请输入借阅记录ID', trigger: 'blur' }]
}

async function handleReturn() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request.post('/borrows/' + form.borrowId + '/return')
    returnResult.value = res.data
    resultVisible.value = true
    form.borrowId = ''
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

function resetForm() {
  form.borrowId = ''
  formRef.value?.resetFields()
}
</script>
