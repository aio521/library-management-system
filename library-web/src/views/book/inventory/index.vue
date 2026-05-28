<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="ISBN">
          <el-input v-model="query.isbn" placeholder="请输入ISBN" clearable />
        </el-form-item>
        <el-form-item label="书名">
          <el-input v-model="query.title" placeholder="请输入书名" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="query.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="c in categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
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
        <el-table-column prop="title" label="书名" min-width="160" />
        <el-table-column prop="isbn" label="ISBN" width="140" />
        <el-table-column prop="totalStock" label="总库存" width="100" />
        <el-table-column prop="availableStock" label="可借" width="100" />
        <el-table-column prop="location" label="馆藏位置" width="120" />
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
const categories = ref<any[]>([])

const query = reactive<any>({ page: 1, pageSize: 20, isbn: '', title: '', author: '', categoryId: '' })

async function fetchCategories() {
  try {
    const res = await request.get('/categories')
    categories.value = res.data || []
  } catch { /* ignore */ }
}

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: query.page, pageSize: query.pageSize }
    if (query.isbn) params.isbn = query.isbn
    if (query.title) params.title = query.title
    if (query.author) params.author = query.author
    if (query.categoryId) params.categoryId = query.categoryId
    const res = await request.get('/books', { params })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.isbn = ''; query.title = ''; query.author = ''; query.categoryId = ''
  query.page = 1
  fetchData()
}

onMounted(() => {
  fetchCategories()
  fetchData()
})
</script>
