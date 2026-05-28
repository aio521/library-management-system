<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="角色编码">
          <el-input v-model="query.roleCode" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="角色名称">
          <el-input v-model="query.roleName" placeholder="请输入角色名称" clearable />
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
        <el-table-column prop="roleCode" label="角色编码" width="140" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="description" label="描述" min-width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
            <el-button size="small" type="warning" @click="showMenuDialog(row)">分配菜单</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuDialogVisible" title="分配菜单" width="450px" @close="resetMenuForm">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :props="{ label: 'name', children: 'children' }"
        :default-checked-keys="checkedMenuIds"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMenus">确定</el-button>
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

const menuDialogVisible = ref(false)
const menuTreeRef = ref()
const menuTree = ref<any[]>([])
const checkedMenuIds = ref<number[]>([])
const currentRoleId = ref(0)

const query = reactive<any>({ page: 1, pageSize: 20, roleCode: '', roleName: '' })
const form = reactive<any>({ roleCode: '', roleName: '', description: '' })

const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: query.page, pageSize: query.pageSize }
    if (query.roleCode) params.roleCode = query.roleCode
    if (query.roleName) params.roleName = query.roleName
    const res = await request.get('/system/roles', { params })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function fetchMenuTree() {
  try {
    const res = await request.get('/system/menus')
    menuTree.value = res.data || []
  } catch { /* ignore */ }
}

function resetQuery() {
  query.roleCode = ''; query.roleName = ''
  query.page = 1
  fetchData()
}

function showAddDialog() {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
}

function showEditDialog(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
}

function resetForm() {
  Object.keys(form).forEach(k => (form[k] = ''))
  formRef.value?.resetFields()
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await request.put('/system/roles/' + form.id, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/system/roles', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch { /* handled by interceptor */ }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确认删除该角色？', '提示', { type: 'warning' })
  await request.delete('/system/roles/' + id)
  ElMessage.success('删除成功')
  fetchData()
}

function showMenuDialog(row: any) {
  currentRoleId.value = row.id
  checkedMenuIds.value = row.menuIds || []
  menuDialogVisible.value = true
}

function resetMenuForm() {
  currentRoleId.value = 0
  checkedMenuIds.value = []
}

async function submitMenus() {
  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
  const allKeys = [...checkedKeys, ...halfCheckedKeys]
  try {
    await request.post('/system/roles/' + currentRoleId.value + '/menus', { menuIds: allKeys })
    ElMessage.success('菜单分配成功')
    menuDialogVisible.value = false
  } catch { /* handled by interceptor */ }
}

onMounted(() => {
  fetchData()
  fetchMenuTree()
})
</script>
