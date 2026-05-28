<template>
  <div class="page-container">
    <el-card>
      <h3 style="margin-bottom:20px">图书借阅</h3>
      <el-form :model="form" ref="formRef" :rules="rules" label-width="120px" style="max-width:600px">
        <el-form-item label="读者ID/编号" prop="readerId">
          <el-input v-model="form.readerId" placeholder="请输入读者ID或读者编号" clearable />
        </el-form-item>
        <el-form-item label="图书条码" prop="barcode">
          <el-input
            ref="barcodeInputRef"
            v-model="form.barcode"
            placeholder="请扫描或输入图书条码"
            clearable
            @keyup.enter="handleBorrow"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleBorrow">
            确认借阅
          </el-button>
          <el-button size="large" @click="resetForm">清空</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="resultVisible" title="借阅成功" width="500px">
      <el-descriptions v-if="borrowResult" :column="2" border>
        <el-descriptions-item label="读者">{{ borrowResult.readerName }}</el-descriptions-item>
        <el-descriptions-item label="图书">{{ borrowResult.bookTitle }}</el-descriptions-item>
        <el-descriptions-item label="条码">{{ borrowResult.barcode }}</el-descriptions-item>
        <el-descriptions-item label="借阅日期">{{ borrowResult.borrowDate }}</el-descriptions-item>
        <el-descriptions-item label="应还日期">{{ borrowResult.dueDate }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="resultVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
const loading = ref(false)
const formRef = ref()
const barcodeInputRef = ref()
const resultVisible = ref(false)
const borrowResult = ref<any>(null)

const form = reactive({ readerId: '', barcode: '' })

const rules = {
  readerId: [{ required: true, message: '请输入读者ID/编号', trigger: 'blur' }],
  barcode: [{ required: true, message: '请输入图书条码', trigger: 'blur' }]
}

async function handleBorrow() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request.post('/borrows', {
      readerId: form.readerId,
      barcode: form.barcode
    })
    borrowResult.value = res.data
    resultVisible.value = true
    form.barcode = ''
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

function resetForm() {
  form.readerId = ''
  form.barcode = ''
  formRef.value?.resetFields()
}

onMounted(() => {
  setTimeout(() => {
    barcodeInputRef.value?.focus()
  }, 300)
})
</script>
