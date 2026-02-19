<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, View } from 'lucide-vue-next'
import request from '@/api'

interface Paper {
  id?: number
  title: string
  questionIds: string
  totalScore: number
  duration: number
  status: number
}

const papers = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive<Paper>({
  title: '',
  questionIds: '',
  totalScore: 0,
  duration: 60,
  status: 1
})

const questions = ref<any[]>([])
const selectedQuestions = ref<number[]>([])
const questionDialogVisible = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)

const loadPapers = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/papers', {
      params: {
        page: currentPage.value,
        limit: pageSize.value
      }
    })
    papers.value = res.list
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadQuestions = async () => {
  try {
    const res: any = await request.get('/questions', {
      params: { page: 1, limit: 100 }
    })
    questions.value = res.list
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = async () => {
  await loadQuestions()
  Object.assign(form, {
    id: undefined,
    title: '',
    questionIds: '',
    totalScore: 0,
    duration: 60,
    status: 1
  })
  selectedQuestions.value = []
  dialogTitle.value = '添加试卷'
  dialogVisible.value = true
}

const handleEdit = async (row: Paper) => {
  await loadQuestions()
  Object.assign(form, row)
  selectedQuestions.value = row.questionIds ? row.questionIds.split(',').map(Number) : []
  dialogTitle.value = '编辑试卷'
  dialogVisible.value = true
}

const handleDelete = async (row: Paper) => {
  try {
    await ElMessageBox.confirm('确定要删除这份试卷吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/papers/${row.id}`)
    ElMessage.success('删除成功')
    loadPapers()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  if (!form.title) {
    ElMessage.warning('请输入试卷标题')
    return
  }
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请选择题目')
    return
  }
  
  form.questionIds = selectedQuestions.value.join(',')
  form.totalScore = selectedQuestions.value.length * 5
  
  try {
    if (form.id) {
      await request.put(`/papers/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/papers', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadPapers()
  } catch (error) {
    console.error(error)
  }
}

const handleQuestionSelect = () => {
  questionDialogVisible.value = true
}

onMounted(() => {
  loadPapers()
})
</script>

<template>
  <div class="space-y-6">
    <div class="bg-white rounded-2xl p-6 shadow-sm">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-lg font-bold text-gray-800">试卷管理</h2>
        <button
          @click="handleAdd"
          class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-xl hover:shadow-lg transition-all"
        >
          <Plus class="w-4 h-4" />
          添加试卷
        </button>
      </div>

      <el-table :data="papers" v-loading="loading" class="w-full">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="试卷标题" min-width="150" />
        <el-table-column prop="totalScore" label="总分" width="100" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span :class="row.status === 1 ? 'bg-green-100 text-green-600' : 'bg-gray-100 text-gray-600'" class="px-2 py-1 rounded-full text-xs">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
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
          @change="loadPapers"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="试卷标题">
          <el-input v-model="form.title" placeholder="请输入试卷标题" />
        </el-form-item>
        <el-form-item label="考试时长">
          <el-input-number v-model="form.duration" :min="10" :max="180" />
          <span class="ml-2 text-gray-500">分钟</span>
        </el-form-item>
        <el-form-item label="选择题目">
          <div class="border border-gray-200 rounded-xl p-4 max-h-60 overflow-y-auto">
            <el-checkbox-group v-model="selectedQuestions">
              <div v-for="q in questions" :key="q.id" class="mb-2">
                <el-checkbox :value="q.id" class="w-full">
                  <span class="text-sm">{{ q.content.substring(0, 50) }}...</span>
                  <span class="text-xs text-gray-400 ml-2">({{ q.type === 1 ? '选择' : '判断' }})</span>
                </el-checkbox>
              </div>
            </el-checkbox-group>
          </div>
          <p class="mt-2 text-sm text-gray-500">已选择 {{ selectedQuestions.length }} 道题目</p>
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
