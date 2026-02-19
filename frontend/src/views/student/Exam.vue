<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, CheckCircle } from 'lucide-vue-next'
import request from '@/api'

interface Question {
  id: number
  content: string
  type: number
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  answer: string
  score: number
}

interface ExamRecord {
  id: number
  paperId: number
  status: number
  startTime: string
}

interface Paper {
  id: number
  title: string
  duration: number
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const recordId = Number(route.params.id)
const questions = ref<Question[]>([])
const record = ref<ExamRecord | null>(null)
const paper = ref<Paper | null>(null)
const loading = ref(false)
const submitting = ref(false)

const answers = ref<Record<string, string>>({})
const remainingTime = ref(0)
let timer: number | null = null

const formatTime = computed(() => {
  const minutes = Math.floor(remainingTime.value / 60)
  const seconds = remainingTime.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

const loadExam = async () => {
  loading.value = true
  try {
    const res: any = await request.get(`/exam-records/${recordId}`)
    record.value = res
    
    const paperRes: any = await request.get(`/papers/${res.paperId}`)
    paper.value = paperRes
    
    const questionsRes: any = await request.get(`/papers/${res.paperId}/questions`)
    questions.value = questionsRes || []
    
    remainingTime.value = (paperRes.duration || 60) * 60
    
    if (res.answers) {
      answers.value = JSON.parse(res.answers)
    }
    
    startTimer()
  } catch (error) {
    console.error(error)
    ElMessage.error('加载考试信息失败')
  } finally {
    loading.value = false
  }
}

const startTimer = () => {
  timer = window.setInterval(() => {
    remainingTime.value--
    if (remainingTime.value <= 0) {
      handleSubmit()
    }
  }, 1000)
}

const handleAnswer = (questionId: number, answer: string) => {
  answers.value[questionId.toString()] = answer
}

const handleSubmit = async () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  
  try {
    await ElMessageBox.confirm('确定要提交试卷吗？', '提示', {
      type: 'warning'
    })
  } catch {
    startTimer()
    return
  }
  
  submitting.value = true
  try {
    const res: any = await request.post(`/exam-records/${recordId}/submit`, {
      answers: JSON.stringify(answers.value)
    })
    ElMessage.success(`考试完成！得分：${res.score}分`)
    router.push('/student/records')
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  userStore.initFromStorage()
  loadExam()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<template>
  <div class="max-w-4xl mx-auto">
    <div v-if="loading" class="text-center py-12">
      <div class="animate-spin w-8 h-8 border-4 border-green-500 border-t-transparent rounded-full mx-auto"></div>
      <p class="text-gray-500 mt-4">加载中...</p>
    </div>

    <div v-else-if="record?.status === 1" class="bg-white rounded-2xl p-12 shadow-sm text-center">
      <CheckCircle class="w-20 h-20 text-green-500 mx-auto mb-4" />
      <h2 class="text-2xl font-bold text-gray-800 mb-2">考试已完成</h2>
      <p class="text-gray-500 mb-6">您已完成本次考试</p>
      <button
        @click="router.push('/student/records')"
        class="px-8 py-3 bg-green-500 text-white rounded-xl hover:bg-green-600 transition-colors"
      >
        查看成绩
      </button>
    </div>

    <div v-else class="space-y-6">
      <div class="bg-white rounded-2xl p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-gray-800">{{ paper?.title }}</h1>
            <p class="text-gray-500 text-sm mt-1">共 {{ questions.length }} 道题</p>
          </div>
          <div class="flex items-center gap-3">
            <Clock class="w-5 h-5 text-gray-400" />
            <span :class="remainingTime < 300 ? 'text-red-500' : 'text-gray-800'" class="text-2xl font-bold font-mono">
              {{ formatTime }}
            </span>
          </div>
        </div>
      </div>

      <div class="space-y-4">
        <div
          v-for="(question, index) in questions"
          :key="question.id"
          class="bg-white rounded-2xl p-6 shadow-sm"
        >
          <div class="flex items-start gap-3 mb-4">
            <span class="w-8 h-8 bg-gradient-to-br from-green-500 to-teal-600 rounded-lg flex items-center justify-center text-white font-bold text-sm">
              {{ index + 1 }}
            </span>
            <div class="flex-1">
              <p class="text-gray-800 font-medium">{{ question.content }}</p>
              <p class="text-gray-400 text-xs mt-1">{{ question.type === 1 ? '选择题' : '判断题' }} · {{ question.score }}分</p>
            </div>
          </div>

          <div v-if="question.type === 1" class="space-y-2 ml-11">
            <label
              v-for="option in [
                { label: 'A', value: question.optionA },
                { label: 'B', value: question.optionB },
                { label: 'C', value: question.optionC },
                { label: 'D', value: question.optionD }
              ]"
              :key="option.label"
              class="flex items-center gap-3 p-3 rounded-xl cursor-pointer transition-all"
              :class="answers[question.id] === option.label ? 'bg-green-50 border border-green-500' : 'bg-gray-50 hover:bg-gray-100'"
            >
              <input
                type="radio"
                :name="`question-${question.id}`"
                :value="option.label"
                :checked="answers[question.id] === option.label"
                @change="handleAnswer(question.id, option.label)"
                class="sr-only"
              />
              <span
                class="w-8 h-8 rounded-lg flex items-center justify-center font-bold text-sm"
                :class="answers[question.id] === option.label ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-600'"
              >
                {{ option.label }}
              </span>
              <span class="text-gray-700">{{ option.value }}</span>
            </label>
          </div>

          <div v-else class="flex gap-4 ml-11">
            <label
              class="flex-1 flex items-center gap-3 p-4 rounded-xl cursor-pointer transition-all"
              :class="answers[question.id] === 'A' ? 'bg-green-50 border border-green-500' : 'bg-gray-50 hover:bg-gray-100'"
            >
              <input
                type="radio"
                :name="`question-${question.id}`"
                value="A"
                :checked="answers[question.id] === 'A'"
                @change="handleAnswer(question.id, 'A')"
                class="sr-only"
              />
              <span
                class="w-10 h-10 rounded-lg flex items-center justify-center font-bold text-sm"
                :class="answers[question.id] === 'A' ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-600'"
              >
                正确
              </span>
            </label>
            <label
              class="flex-1 flex items-center gap-3 p-4 rounded-xl cursor-pointer transition-all"
              :class="answers[question.id] === 'B' ? 'bg-green-50 border border-green-500' : 'bg-gray-50 hover:bg-gray-100'"
            >
              <input
                type="radio"
                :name="`question-${question.id}`"
                value="B"
                :checked="answers[question.id] === 'B'"
                @change="handleAnswer(question.id, 'B')"
                class="sr-only"
              />
              <span
                class="w-10 h-10 rounded-lg flex items-center justify-center font-bold text-sm"
                :class="answers[question.id] === 'B' ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-600'"
              >
                错误
              </span>
            </label>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-2xl p-6 shadow-sm sticky bottom-4">
        <div class="flex items-center justify-between">
          <p class="text-gray-500">已作答：{{ Object.keys(answers).length }} / {{ questions.length }} 题</p>
          <button
            @click="handleSubmit"
            :disabled="submitting"
            class="px-8 py-3 bg-gradient-to-r from-green-500 to-teal-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all disabled:opacity-50"
          >
            {{ submitting ? '提交中...' : '提交试卷' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
