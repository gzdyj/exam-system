<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete } from 'lucide-vue-next'
import request from '@/api'

interface Question {
  id?: number
  content: string
  type: number
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  answer: string
  score: number
  analysis: string
}

const questions = ref([])
const total = ref(0)
const loading = ref(false)
const searchForm = reactive({
  type: null as number | null,
  keyword: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive<Question>({
  content: '',
  type: 1,
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  answer: '',
  score: 5,
  analysis: ''
})

const currentPage = ref(1)
const pageSize = ref(10)

const loadQuestions = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/questions', {
      params: {
        page: currentPage.value,
        limit: pageSize.value,
        type: searchForm.type,
        keyword: searchForm.keyword
      }
    })
    questions.value = res.list
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadQuestions()
}

const handleAdd = () => {
  Object.assign(form, {
    id: undefined,
    content: '',
    type: 1,
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    answer: '',
    score: 5,
    analysis: ''
  })
  dialogTitle.value = '添加题目'
  dialogVisible.value = true
}

const handleEdit = (row: Question) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑题目'
  dialogVisible.value = true
}

const handleDelete = async (row: Question) => {
  try {
    await ElMessageBox.confirm('确定要删除这道题目吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/questions/${row.id}`)
    ElMessage.success('删除成功')
    loadQuestions()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  try {
    if (form.id) {
      await request.put(`/questions/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/questions', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadQuestions()
  } catch (error) {
    console.error(error)
  }
}

const typeOptions = [
  { label: '选择题', value: 1 },
  { label: '判断题', value: 2 }
]

const answerOptions = [
  { label: 'A', value: 'A' },
  { label: 'B', value: 'B' },
  { label: 'C', value: 'C' },
  { label: 'D', value: 'D' }
]

onMounted(() => {
  loadQuestions()
})
</script>

<template>
  <div class="space-y-6">
    <div class="bg-white rounded-2xl p-6 shadow-sm">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-lg font-bold text-gray-800">题库管理</h2>
        <button
          @click="handleAdd"
          class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-xl hover:shadow-lg transition-all"
        >
          <Plus class="w-4 h-4" />
          添加题目
        </button>
      </div>

      <div class="flex items-center gap-4 mb-6">
        <el-select v-model="searchForm.type" placeholder="题目类型" clearable class="w-40">
          <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <div class="flex-1 relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="searchForm.keyword"
            type="text"
            placeholder="搜索题目内容..."
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

      <el-table :data="questions" v-loading="loading" class="w-full">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="content" label="题目内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <span :class="row.type === 1 ? 'bg-blue-100 text-blue-600' : 'bg-green-100 text-green-600'" class="px-2 py-1 rounded-full text-xs">
              {{ row.type === 1 ? '选择题' : '判断题' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="answer" label="答案" width="80" />
        <el-table-column prop="score" label="分值" width="80" />
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
          @change="loadQuestions"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="题目类型">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">选择题</el-radio>
            <el-radio :value="2">判断题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目内容">
          <el-input v-model="form.content" type="textarea" rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <template v-if="form.type === 1">
          <el-form-item label="选项A">
            <el-input v-model="form.optionA" placeholder="请输入选项A内容" />
          </el-form-item>
          <el-form-item label="选项B">
            <el-input v-model="form.optionB" placeholder="请输入选项B内容" />
          </el-form-item>
          <el-form-item label="选项C">
            <el-input v-model="form.optionC" placeholder="请输入选项C内容" />
          </el-form-item>
          <el-form-item label="选项D">
            <el-input v-model="form.optionD" placeholder="请输入选项D内容" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="选项A">
            <el-input v-model="form.optionA" placeholder="正确" />
          </el-form-item>
          <el-form-item label="选项B">
            <el-input v-model="form.optionB" placeholder="错误" />
          </el-form-item>
        </template>
        <el-form-item label="正确答案">
          <el-select v-model="form.answer" placeholder="请选择正确答案">
            <el-option v-for="item in answerOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="分值">
          <el-input-number v-model="form.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" rows="2" placeholder="请输入题目解析" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
