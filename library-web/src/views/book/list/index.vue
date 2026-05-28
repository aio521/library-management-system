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
      <el-table :data="tableData" border stripe v-loading="loading" @expand-change="handleExpand">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div style="padding: 12px 40px">
              <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
                <strong>复本列表（{{ row.title }}）</strong>
                <el-button size="small" type="primary" @click="showAddStock(row)">添加复本</el-button>
              </div>
              <el-table :data="stockCache[row.id]" border v-loading="stockLoading[row.id]" size="small">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="barcode" label="条码" width="160" />
                <el-table-column prop="location" label="馆藏位置" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row: srow }">
                    <el-tag :type="srow.status === 1 ? 'success' : 'danger'" size="small">
                      {{ srow.status === 1 ? '可借' : '已借出' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row: srow }">
                    <el-button size="small" type="danger" @click="handleDeleteStock(srow.id, row.id)">报损</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="isbn" label="ISBN" width="140" />
        <el-table-column prop="title" label="书名" min-width="160" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="140" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="totalStock" label="总库存" width="80" />
        <el-table-column prop="availableStock" label="可借" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
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

    <el-dialog v-model="stockDialogVisible" title="添加复本" width="450px" @close="stockForm.barcode = ''">
      <el-form :model="stockForm" label-width="80px">
        <el-form-item label="条码">
          <el-input v-model="stockForm.barcode" placeholder="请输入条码" />
        </el-form-item>
        <el-form-item label="馆藏位置">
          <el-input v-model="stockForm.location" placeholder="请输入馆藏位置" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddStock">确定</el-button>
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
const categories = ref<any[]>([])
const stockCache = ref<Record<number, any[]>>({})
const stockLoading = ref<Record<number, boolean>>({})
const stockDialogVisible = ref(false)
const currentBookId = ref<number>(0)
const stockForm = reactive({ barcode: '', location: '' })

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

async function handleExpand(row: any, expandedRows: any[]) {
  const isExpanded = expandedRows.some((r: any) => r.id === row.id)
  if (!isExpanded) return
  if (stockCache.value[row.id]) return
  stockLoading.value[row.id] = true
  try {
    const res = await request.get('/books/' + row.id + '/stocks')
    stockCache.value[row.id] = res.data || []
  } finally {
    stockLoading.value[row.id] = false
  }
}

function showAddStock(row: any) {
  currentBookId.value = row.id
  stockDialogVisible.value = true
}

async function submitAddStock() {
  if (!stockForm.barcode) {
    ElMessage.warning('请输入条码')
    return
  }
  try {
    await request.post('/books/' + currentBookId.value + '/stocks', {
      barcode: stockForm.barcode,
      location: stockForm.location
    })
    ElMessage.success('添加复本成功')
    stockDialogVisible.value = false
    stockForm.barcode = ''
    stockForm.location = ''
    // Refresh stock cache
    const res = await request.get('/books/' + currentBookId.value + '/stocks')
    stockCache.value[currentBookId.value] = res.data || []
    fetchData()
  } catch { /* handled by interceptor */ }
}

async function handleDeleteStock(stockId: number, bookId: number) {
  await ElMessageBox.confirm('确认报损该复本？', '提示', { type: 'warning' })
  await request.delete('/stocks/' + stockId)
  ElMessage.success('报损成功')
  const res = await request.get('/books/' + bookId + '/stocks')
  stockCache.value[bookId] = res.data || []
  fetchData()
}

onMounted(() => {
  fetchCategories()
  fetchData()
})
</script>
