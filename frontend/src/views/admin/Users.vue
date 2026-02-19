<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search } from 'lucide-vue-next'
import request from '@/api'

interface User {
  id?: number
  username: string
  realName: string
  role: number
  status: number
  password?: string
}

const users = ref([])
const total = ref(0)
const loading = ref(false)
const searchForm = reactive({
  role: null as number | null,
  username: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive<User>({
  username: '',
  realName: '',
  role: 2,
  status: 1,
  password: ''
})

const currentPage = ref(1)
const pageSize = ref(10)

const loadUsers = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/users', {
      params: {
        page: currentPage.value,
        limit: pageSize.value,
        role: searchForm.role,
        username: searchForm.username
      }
    })
    users.value = res.list
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

const handleAdd = () => {
  Object.assign(form, {
    id: undefined,
    username: '',
    realName: '',
    role: 2,
    status: 1,
    password: ''
  })
  dialogTitle.value = '添加用户'
  dialogVisible.value = true
}

const handleEdit = (row: User) => {
  Object.assign(form, { ...row, password: '' })
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
}

const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/users/${row.id}`)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  if (!form.username || !form.realName) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!form.id && !form.password) {
    ElMessage.warning('请填写密码')
    return
  }
  
  try {
    if (form.id) {
      await request.put(`/users/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/users', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadUsers()
  } catch (error) {
    console.error(error)
  }
}

const roleOptions = [
  { label: '教师', value: 1 },
  { label: '学生', value: 2 }
]

onMounted(() => {
  loadUsers()
})
</script>

<template>
  <div class="space-y-6">
    <div class="bg-white rounded-2xl p-6 shadow-sm">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-lg font-bold text-gray-800">用户管理</h2>
        <button
          @click="handleAdd"
          class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-xl hover:shadow-lg transition-all"
        >
          <Plus class="w-4 h-4" />
          添加用户
        </button>
      </div>

      <div class="flex items-center gap-4 mb-6">
        <el-select v-model="searchForm.role" placeholder="用户角色" clearable class="w-40">
          <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <div class="flex-1 relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="searchForm.username"
            type="text"
            placeholder="搜索用户名..."
            class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
            @keyup.enter="handleSearch"
          />
        </div>
        <button
          @click="handleSearch"
          class="px-6 py-2 bg-indigo-500 text-white rounded-xl hover:bg-indigo-600 transition-colors"
        >
          搜索
        </button>
      </div>

      <el-table :data="users" v-loading="loading" class="w-full">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <span :class="row.role === 1 ? 'bg-purple-100 text-purple-600' : 'bg-blue-100 text-blue-600'" class="px-2 py-1 rounded-full text-xs">
              {{ row.role === 1 ? '教师' : '学生' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span :class="row.status === 1 ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'" class="px-2 py-1 rounded-full text-xs">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <button @click="handleEdit(row)" class="p-1 text-indigo-500 hover:bg-indigo-50 rounded">
                <Edit class="w-4 h-4" />
              </button>
              <button @click="handleDelete(row)" class="p-1 text-red-500 hover:bg-red-50 rounded">
                <Delete class="w-4 h-4" />
              </button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-end mt-4">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadUsers"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio :value="1">教师</el-radio>
            <el-radio :value="2">学生</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
