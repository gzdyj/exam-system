<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { FileText, Clock, BookOpen, GraduationCap, PenTool, Play, ChevronLeft, ChevronRight, CheckCircle, XCircle } from 'lucide-vue-next'
import request from '@/api'

interface Module {
  name: string
  count: number
}

interface Question {
  id: number
  content: string
  type: number
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  answer: string
}

const router = useRouter()
const userStore = useUserStore()

// 模式：exam-考试模式, practice-练习模式
const mode = ref<'exam' | 'practice' | null>(null)
const modules = ref<Module[]>([])
const selectedModule = ref<string>('')
const loading = ref(false)

// 考试模式
const examQuestions = ref<Question[]>([])
const examStarted = ref(false)
const examSubmitted = ref(false)
const examAnswers = ref<Record<number, string>>({})
const questionCount = ref(100)
const countOptions = [50, 100, 150, 200]

// 练习模式
const practiceQuestions = ref<Question[]>([])
const practicePage = ref(1)
const practiceTotal = ref(0)
const practiceLimit = ref(10)
const practiceLimitOptions = [10, 20, 50, 100]
const practiceAnswers = ref<Record<number, string>>({})
const practiceShowAnswer = ref<Record<number, boolean>>({})

const loadModules = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/questions/modules')
    modules.value = res.list || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const selectMode = (m: 'exam' | 'practice') => {
  mode.value = m
  selectedModule.value = ''
  examStarted.value = false
  examSubmitted.value = false
  examQuestions.value = []
  examAnswers.value = {}
  practiceQuestions.value = []
  practiceAnswers.value = {}
  practiceShowAnswer.value = {}
}

const startExam = async () => {
  if (!selectedModule.value) {
    alert('请选择模块')
    return
  }
  loading.value = true
  try {
    const res: any = await request.get('/questions/exam', {
      params: { 
        module: selectedModule.value,
        count: questionCount.value
      }
    })
    examQuestions.value = res.list || []
    examStarted.value = true
    examSubmitted.value = false
    examAnswers.value = {}
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadPracticeQuestions = async () => {
  if (!selectedModule.value) return
  loading.value = true
  try {
    const res: any = await request.get('/questions/practice', {
      params: { 
        module: selectedModule.value,
        page: practicePage.value,
        limit: practiceLimit.value
      }
    })
    practiceQuestions.value = res.list || []
    practiceTotal.value = res.total || 0
    practiceAnswers.value = {}
    practiceShowAnswer.value = {}
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const selectModule = (moduleName: string) => {
  selectedModule.value = moduleName
  practicePage.value = 1
  if (mode.value === 'practice') {
    loadPracticeQuestions()
  }
}

// 考试模式：计算已答题数量
const examAnsweredCount = computed(() => {
  return Object.keys(examAnswers.value).length
})

// 考试模式：计算正确数量
const examCorrectCount = computed(() => {
  let correct = 0
  examQuestions.value.forEach(q => {
    if (examAnswers.value[q.id] === q.answer) {
      correct++
    }
  })
  return correct
})

// 提交考试
const submitExam = () => {
  const unanswered = examQuestions.value.length - examAnsweredCount.value
  if (unanswered > 0) {
    if (!confirm(`还有 ${unanswered} 道题未作答，确定要提交吗？`)) {
      return
    }
  }
  examSubmitted.value = true
}

// 练习模式：显示单题答案
const toggleAnswer = (questionId: number) => {
  practiceShowAnswer.value[questionId] = !practiceShowAnswer.value[questionId]
}

// 练习模式：检查答案是否正确
const isAnswerCorrect = (questionId: number, answer: string) => {
  return practiceAnswers.value[questionId] === answer
}

const totalPages = computed(() => Math.ceil(practiceTotal.value / practiceLimit.value))

const prevPage = () => {
  if (practicePage.value > 1) {
    practicePage.value--
    loadPracticeQuestions()
  }
}

const nextPage = () => {
  if (practicePage.value < totalPages.value) {
    practicePage.value++
    loadPracticeQuestions()
  }
}

const goBack = () => {
  mode.value = null
  selectedModule.value = ''
  examStarted.value = false
  examSubmitted.value = false
  examQuestions.value = []
  examAnswers.value = {}
  practiceQuestions.value = []
  practiceAnswers.value = {}
}

const goBackToModules = () => {
  selectedModule.value = ''
  examStarted.value = false
  examSubmitted.value = false
  examQuestions.value = []
  examAnswers.value = {}
  practiceQuestions.value = []
  practiceAnswers.value = {}
}

onMounted(() => {
  loadModules()
})
</script>

<template>
  <div class="space-y-4 lg:space-y-6">
    <!-- 模式选择 -->
    <div v-if="!mode" class="space-y-4 lg:space-y-6">
      <div class="flex items-center justify-between">
        <h2 class="text-lg lg:text-xl font-bold text-gray-800">选择模式</h2>
      </div>

      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin w-8 h-8 border-4 border-indigo-500 border-t-transparent rounded-full mx-auto"></div>
        <p class="text-gray-500 mt-4">加载中...</p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-6">
        <!-- 考试模式 -->
        <div 
          @click="selectMode('exam')"
          class="bg-white rounded-xl lg:rounded-2xl p-5 lg:p-8 shadow-sm hover:shadow-xl transition-all cursor-pointer group border-2 border-transparent hover:border-indigo-500"
        >
          <div class="w-12 h-12 lg:w-16 lg:h-16 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl lg:rounded-2xl flex items-center justify-center shadow-lg mx-auto mb-3 lg:mb-4 group-hover:scale-110 transition-transform">
            <GraduationCap class="w-6 h-6 lg:w-8 lg:h-8 text-white" />
          </div>
          <h3 class="text-lg lg:text-xl font-bold text-gray-800 text-center mb-1 lg:mb-2">考试模式</h3>
          <p class="text-gray-500 text-center text-xs lg:text-sm">选择模块后随机抽取题目进行考试</p>
          <div class="mt-3 lg:mt-4 text-center">
            <span class="inline-flex items-center gap-1 text-indigo-600 text-xs lg:text-sm font-medium">
              <Play class="w-3 h-3 lg:w-4 lg:h-4" /> 开始考试
            </span>
          </div>
        </div>

        <!-- 练习模式 -->
        <div 
          @click="selectMode('practice')"
          class="bg-white rounded-xl lg:rounded-2xl p-5 lg:p-8 shadow-sm hover:shadow-xl transition-all cursor-pointer group border-2 border-transparent hover:border-green-500"
        >
          <div class="w-12 h-12 lg:w-16 lg:h-16 bg-gradient-to-br from-green-500 to-teal-600 rounded-xl lg:rounded-2xl flex items-center justify-center shadow-lg mx-auto mb-3 lg:mb-4 group-hover:scale-110 transition-transform">
            <PenTool class="w-6 h-6 lg:w-8 lg:h-8 text-white" />
          </div>
          <h3 class="text-lg lg:text-xl font-bold text-gray-800 text-center mb-1 lg:mb-2">练习模式</h3>
          <p class="text-gray-500 text-center text-xs lg:text-sm">按模块逐题练习，即时查看答案</p>
          <div class="mt-3 lg:mt-4 text-center">
            <span class="inline-flex items-center gap-1 text-green-600 text-xs lg:text-sm font-medium">
              <Play class="w-3 h-3 lg:w-4 lg:h-4" /> 开始练习
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 模块选择 -->
    <div v-else-if="mode && !selectedModule || (mode === 'exam' && !examStarted)" class="space-y-4 lg:space-y-6">
      <div class="flex items-center gap-3 lg:gap-4">
        <button @click="goBack" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
          <ChevronLeft class="w-5 h-5 text-gray-600" />
        </button>
        <h2 class="text-base lg:text-xl font-bold text-gray-800">
          {{ mode === 'exam' ? '考试模式 - 选择模块' : '练习模式 - 选择模块' }}
        </h2>
      </div>

      <!-- 题目数量选择 -->
      <div class="bg-white rounded-xl p-3 lg:p-4 shadow-sm">
        <label class="text-xs lg:text-sm font-medium text-gray-700 mb-2 block">
          {{ mode === 'exam' ? '考试题目数量' : '每页显示题目数量' }}
        </label>
        <div class="flex flex-wrap gap-2">
          <button 
            v-for="count in (mode === 'exam' ? countOptions : practiceLimitOptions)" 
            :key="count"
            @click="mode === 'exam' ? (questionCount = count) : (practiceLimit = count)"
            :class="[
              'px-3 lg:px-4 py-2 rounded-lg text-xs lg:text-sm font-medium transition-all',
              (mode === 'exam' ? questionCount : practiceLimit) === count 
                ? (mode === 'exam' ? 'bg-indigo-500 text-white' : 'bg-green-500 text-white') 
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            ]"
          >
            {{ count }}题
          </button>
        </div>
      </div>

      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin w-8 h-8 border-4 border-indigo-500 border-t-transparent rounded-full mx-auto"></div>
        <p class="text-gray-500 mt-4">加载中...</p>
      </div>

      <div v-else class="grid grid-cols-2 lg:grid-cols-3 gap-3 lg:gap-4">
        <!-- 全部模块（仅考试模式） -->
        <div 
          v-if="mode === 'exam'"
          @click="selectedModule = '全部'; startExam()"
          class="bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl p-3 lg:p-5 shadow-sm hover:shadow-lg transition-all cursor-pointer text-white"
        >
          <div class="flex items-center gap-2 lg:gap-3">
            <BookOpen class="w-4 h-4 lg:w-6 lg:h-6" />
            <div>
              <h3 class="font-bold text-sm lg:text-base">全部模块</h3>
              <p class="text-xs opacity-80">{{ modules.reduce((sum, m) => sum + m.count, 0) }} 道题</p>
            </div>
          </div>
        </div>

        <div 
          v-for="module in modules" 
          :key="module.name"
          @click="mode === 'exam' ? (selectedModule = module.name, startExam()) : selectModule(module.name)"
          class="bg-white rounded-xl p-3 lg:p-5 shadow-sm hover:shadow-lg transition-all cursor-pointer border-2 border-transparent hover:border-indigo-500"
        >
          <div class="flex items-center gap-2 lg:gap-3">
            <div class="w-8 h-8 lg:w-10 lg:h-10 bg-gradient-to-br from-green-500 to-teal-600 rounded-lg flex items-center justify-center flex-shrink-0">
              <FileText class="w-4 h-4 lg:w-5 lg:h-5 text-white" />
            </div>
            <div class="min-w-0">
              <h3 class="font-bold text-gray-800 text-sm lg:text-base truncate">{{ module.name }}</h3>
              <p class="text-xs text-gray-500">{{ module.count }} 道</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 考试模式：答题界面 -->
    <div v-else-if="mode === 'exam' && examStarted" class="space-y-4 lg:space-y-6">
      <!-- 顶部进度条 -->
      <div class="bg-white rounded-xl p-3 lg:p-4 shadow-sm">
        <div class="flex items-center justify-between flex-wrap gap-2">
          <div class="flex items-center gap-2 lg:gap-4">
            <button @click="goBackToModules" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <ChevronLeft class="w-5 h-5 text-gray-600" />
            </button>
            <h2 class="text-base lg:text-lg font-bold text-gray-800">考试中 - {{ selectedModule }}</h2>
          </div>
          <div class="flex items-center gap-4 text-sm">
            <div class="flex items-center gap-1 text-gray-600">
              <Clock class="w-4 h-4" />
              <span>{{ examQuestions.length }} 题</span>
            </div>
            <div class="flex items-center gap-1" :class="examSubmitted ? 'text-indigo-600' : 'text-gray-600'">
              <CheckCircle class="w-4 h-4" />
              <span>已答 {{ examAnsweredCount }}/{{ examQuestions.length }}</span>
            </div>
          </div>
        </div>
        <!-- 进度条 -->
        <div class="mt-3 h-2 bg-gray-200 rounded-full overflow-hidden">
          <div 
            class="h-full bg-gradient-to-r from-indigo-500 to-purple-600 transition-all duration-300"
            :style="{ width: `${(examAnsweredCount / examQuestions.length) * 100}%` }"
          ></div>
        </div>
      </div>

      <!-- 考试结果 -->
      <div v-if="examSubmitted" class="bg-gradient-to-r from-indigo-500 to-purple-600 rounded-xl p-4 lg:p-6 text-white">
        <div class="text-center">
          <CheckCircle class="w-12 h-12 mx-auto mb-3" />
          <h3 class="text-xl lg:text-2xl font-bold mb-2">考试完成！</h3>
          <div class="text-3xl lg:text-4xl font-bold mb-2">
            {{ Math.round(examCorrectCount / examQuestions.length * 100) }} 分
          </div>
          <p class="text-sm opacity-90">
            正确 {{ examCorrectCount }} 题 / 共 {{ examQuestions.length }} 题
          </p>
        </div>
      </div>

      <!-- 题目列表 -->
      <div class="bg-white rounded-xl lg:rounded-2xl p-4 lg:p-6 shadow-sm">
        <div class="space-y-4 lg:space-y-6">
          <div v-for="(q, index) in examQuestions" :key="q.id" class="border-b border-gray-100 pb-4 lg:pb-6 last:border-0">
            <p class="font-medium text-gray-800 mb-2 lg:mb-3 text-sm lg:text-base">
              <span class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-indigo-100 text-indigo-600 text-xs font-bold mr-2">{{ index + 1 }}</span>
              {{ q.content }}
            </p>
            <div class="grid grid-cols-1 gap-2">
              <label 
                v-if="q.optionA" 
                class="flex items-center gap-2 p-2 lg:p-3 rounded-lg cursor-pointer transition-all text-xs lg:text-sm"
                :class="[
                  examSubmitted 
                    ? (q.answer === 'A' ? 'bg-green-100 border-2 border-green-500' : (examAnswers[q.id] === 'A' ? 'bg-red-100 border-2 border-red-500' : 'bg-gray-50'))
                    : (examAnswers[q.id] === 'A' ? 'bg-indigo-100 border-2 border-indigo-500' : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'),
                  examSubmitted && examAnswers[q.id] !== q.answer && examAnswers[q.id] === 'A' ? 'text-red-600' : ''
                ]"
              >
                <input 
                  type="radio" 
                  :name="'exam-q'+q.id" 
                  value="A" 
                  v-model="examAnswers[q.id]" 
                  :disabled="examSubmitted"
                  class="text-indigo-500"
                >
                <span>A. {{ q.optionA }}</span>
                <CheckCircle v-if="examSubmitted && q.answer === 'A'" class="w-4 h-4 text-green-600 ml-auto" />
                <XCircle v-if="examSubmitted && examAnswers[q.id] === 'A' && q.answer !== 'A'" class="w-4 h-4 text-red-600 ml-auto" />
              </label>
              <label 
                v-if="q.optionB" 
                class="flex items-center gap-2 p-2 lg:p-3 rounded-lg cursor-pointer transition-all text-xs lg:text-sm"
                :class="[
                  examSubmitted 
                    ? (q.answer === 'B' ? 'bg-green-100 border-2 border-green-500' : (examAnswers[q.id] === 'B' ? 'bg-red-100 border-2 border-red-500' : 'bg-gray-50'))
                    : (examAnswers[q.id] === 'B' ? 'bg-indigo-100 border-2 border-indigo-500' : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'),
                  examSubmitted && examAnswers[q.id] !== q.answer && examAnswers[q.id] === 'B' ? 'text-red-600' : ''
                ]"
              >
                <input 
                  type="radio" 
                  :name="'exam-q'+q.id" 
                  value="B" 
                  v-model="examAnswers[q.id]" 
                  :disabled="examSubmitted"
                  class="text-indigo-500"
                >
                <span>B. {{ q.optionB }}</span>
                <CheckCircle v-if="examSubmitted && q.answer === 'B'" class="w-4 h-4 text-green-600 ml-auto" />
                <XCircle v-if="examSubmitted && examAnswers[q.id] === 'B' && q.answer !== 'B'" class="w-4 h-4 text-red-600 ml-auto" />
              </label>
              <label 
                v-if="q.optionC" 
                class="flex items-center gap-2 p-2 lg:p-3 rounded-lg cursor-pointer transition-all text-xs lg:text-sm"
                :class="[
                  examSubmitted 
                    ? (q.answer === 'C' ? 'bg-green-100 border-2 border-green-500' : (examAnswers[q.id] === 'C' ? 'bg-red-100 border-2 border-red-500' : 'bg-gray-50'))
                    : (examAnswers[q.id] === 'C' ? 'bg-indigo-100 border-2 border-indigo-500' : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'),
                  examSubmitted && examAnswers[q.id] !== q.answer && examAnswers[q.id] === 'C' ? 'text-red-600' : ''
                ]"
              >
                <input 
                  type="radio" 
                  :name="'exam-q'+q.id" 
                  value="C" 
                  v-model="examAnswers[q.id]" 
                  :disabled="examSubmitted"
                  class="text-indigo-500"
                >
                <span>C. {{ q.optionC }}</span>
                <CheckCircle v-if="examSubmitted && q.answer === 'C'" class="w-4 h-4 text-green-600 ml-auto" />
                <XCircle v-if="examSubmitted && examAnswers[q.id] === 'C' && q.answer !== 'C'" class="w-4 h-4 text-red-600 ml-auto" />
              </label>
              <label 
                v-if="q.optionD" 
                class="flex items-center gap-2 p-2 lg:p-3 rounded-lg cursor-pointer transition-all text-xs lg:text-sm"
                :class="[
                  examSubmitted 
                    ? (q.answer === 'D' ? 'bg-green-100 border-2 border-green-500' : (examAnswers[q.id] === 'D' ? 'bg-red-100 border-2 border-red-500' : 'bg-gray-50'))
                    : (examAnswers[q.id] === 'D' ? 'bg-indigo-100 border-2 border-indigo-500' : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'),
                  examSubmitted && examAnswers[q.id] !== q.answer && examAnswers[q.id] === 'D' ? 'text-red-600' : ''
                ]"
              >
                <input 
                  type="radio" 
                  :name="'exam-q'+q.id" 
                  value="D" 
                  v-model="examAnswers[q.id]" 
                  :disabled="examSubmitted"
                  class="text-indigo-500"
                >
                <span>D. {{ q.optionD }}</span>
                <CheckCircle v-if="examSubmitted && q.answer === 'D'" class="w-4 h-4 text-green-600 ml-auto" />
                <XCircle v-if="examSubmitted && examAnswers[q.id] === 'D' && q.answer !== 'D'" class="w-4 h-4 text-red-600 ml-auto" />
              </label>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="mt-4 lg:mt-6 pt-4 lg:pt-6 border-t">
          <button 
            v-if="!examSubmitted"
            @click="submitExam"
            class="w-full px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white font-medium rounded-xl shadow hover:shadow-lg transition-all text-sm lg:text-base"
          >
            提交考试
          </button>
          <button 
            v-else
            @click="goBackToModules"
            class="w-full px-6 py-3 bg-gray-100 text-gray-700 font-medium rounded-xl hover:bg-gray-200 transition-all text-sm lg:text-base"
          >
            返回重新考试
          </button>
        </div>
      </div>
    </div>

    <!-- 练习模式：答题界面 -->
    <div v-else-if="mode === 'practice' && selectedModule" class="space-y-4 lg:space-y-6">
      <!-- 顶部进度条 -->
      <div class="bg-white rounded-xl p-3 lg:p-4 shadow-sm">
        <div class="flex items-center justify-between flex-wrap gap-2">
          <div class="flex items-center gap-2 lg:gap-4">
            <button @click="goBackToModules" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <ChevronLeft class="w-5 h-5 text-gray-600" />
            </button>
            <h2 class="text-base lg:text-lg font-bold text-gray-800">练习中 - {{ selectedModule }}</h2>
          </div>
          <div class="flex items-center gap-2 text-gray-500 text-sm">
            <BookOpen class="w-4 h-4" />
            <span>第 {{ practicePage }}/{{ totalPages || 1 }} 页</span>
            <span class="text-gray-400">|</span>
            <span>共 {{ practiceTotal }} 题</span>
          </div>
        </div>
        <!-- 进度条 -->
        <div class="mt-3 h-2 bg-gray-200 rounded-full overflow-hidden">
          <div 
            class="h-full bg-gradient-to-r from-green-500 to-teal-600 transition-all duration-300"
            :style="{ width: `${(practicePage / (totalPages || 1)) * 100}%` }"
          ></div>
        </div>
      </div>

      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin w-8 h-8 border-4 border-green-500 border-t-transparent rounded-full mx-auto"></div>
        <p class="text-gray-500 mt-4">加载中...</p>
      </div>

      <div v-else class="bg-white rounded-xl lg:rounded-2xl p-4 lg:p-6 shadow-sm">
        <div class="space-y-4 lg:space-y-6">
          <div v-for="(q, index) in practiceQuestions" :key="q.id" class="border-b border-gray-100 pb-4 lg:pb-6 last:border-0">
            <p class="font-medium text-gray-800 mb-2 lg:mb-3 text-sm lg:text-base">
              <span class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-green-100 text-green-600 text-xs font-bold mr-2">{{ (practicePage - 1) * practiceLimit + index + 1 }}</span>
              {{ q.content }}
            </p>
            <div class="grid grid-cols-1 gap-2">
              <label 
                v-if="q.optionA" 
                class="flex items-center gap-2 p-2 lg:p-3 rounded-lg cursor-pointer transition-all text-xs lg:text-sm"
                :class="[
                  practiceAnswers[q.id] === 'A' 
                    ? (practiceShowAnswer[q.id] ? (q.answer === 'A' ? 'bg-green-100 border-2 border-green-500' : 'bg-red-100 border-2 border-red-500') : 'bg-green-50 border-2 border-green-400')
                    : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'
                ]"
              >
                <input 
                  type="radio" 
                  :name="'practice-q'+q.id" 
                  value="A" 
                  v-model="practiceAnswers[q.id]"
                  class="text-green-500"
                  @change="practiceShowAnswer[q.id] = true"
                >
                <span>A. {{ q.optionA }}</span>
                <CheckCircle v-if="practiceShowAnswer[q.id] && q.answer === 'A'" class="w-4 h-4 text-green-600 ml-auto" />
                <XCircle v-if="practiceShowAnswer[q.id] && practiceAnswers[q.id] === 'A' && q.answer !== 'A'" class="w-4 h-4 text-red-600 ml-auto" />
              </label>
              <label 
                v-if="q.optionB" 
                class="flex items-center gap-2 p-2 lg:p-3 rounded-lg cursor-pointer transition-all text-xs lg:text-sm"
                :class="[
                  practiceAnswers[q.id] === 'B' 
                    ? (practiceShowAnswer[q.id] ? (q.answer === 'B' ? 'bg-green-100 border-2 border-green-500' : 'bg-red-100 border-2 border-red-500') : 'bg-green-50 border-2 border-green-400')
                    : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'
                ]"
              >
                <input 
                  type="radio" 
                  :name="'practice-q'+q.id" 
                  value="B" 
                  v-model="practiceAnswers[q.id]"
                  class="text-green-500"
                  @change="practiceShowAnswer[q.id] = true"
                >
                <span>B. {{ q.optionB }}</span>
                <CheckCircle v-if="practiceShowAnswer[q.id] && q.answer === 'B'" class="w-4 h-4 text-green-600 ml-auto" />
                <XCircle v-if="practiceShowAnswer[q.id] && practiceAnswers[q.id] === 'B' && q.answer !== 'B'" class="w-4 h-4 text-red-600 ml-auto" />
              </label>
              <label 
                v-if="q.optionC" 
                class="flex items-center gap-2 p-2 lg:p-3 rounded-lg cursor-pointer transition-all text-xs lg:text-sm"
                :class="[
                  practiceAnswers[q.id] === 'C' 
                    ? (practiceShowAnswer[q.id] ? (q.answer === 'C' ? 'bg-green-100 border-2 border-green-500' : 'bg-red-100 border-2 border-red-500') : 'bg-green-50 border-2 border-green-400')
                    : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'
                ]"
              >
                <input 
                  type="radio" 
                  :name="'practice-q'+q.id" 
                  value="C" 
                  v-model="practiceAnswers[q.id]"
                  class="text-green-500"
                  @change="practiceShowAnswer[q.id] = true"
                >
                <span>C. {{ q.optionC }}</span>
                <CheckCircle v-if="practiceShowAnswer[q.id] && q.answer === 'C'" class="w-4 h-4 text-green-600 ml-auto" />
                <XCircle v-if="practiceShowAnswer[q.id] && practiceAnswers[q.id] === 'C' && q.answer !== 'C'" class="w-4 h-4 text-red-600 ml-auto" />
              </label>
              <label 
                v-if="q.optionD" 
                class="flex items-center gap-2 p-2 lg:p-3 rounded-lg cursor-pointer transition-all text-xs lg:text-sm"
                :class="[
                  practiceAnswers[q.id] === 'D' 
                    ? (practiceShowAnswer[q.id] ? (q.answer === 'D' ? 'bg-green-100 border-2 border-green-500' : 'bg-red-100 border-2 border-red-500') : 'bg-green-50 border-2 border-green-400')
                    : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'
                ]"
              >
                <input 
                  type="radio" 
                  :name="'practice-q'+q.id" 
                  value="D" 
                  v-model="practiceAnswers[q.id]"
                  class="text-green-500"
                  @change="practiceShowAnswer[q.id] = true"
                >
                <span>D. {{ q.optionD }}</span>
                <CheckCircle v-if="practiceShowAnswer[q.id] && q.answer === 'D'" class="w-4 h-4 text-green-600 ml-auto" />
                <XCircle v-if="practiceShowAnswer[q.id] && practiceAnswers[q.id] === 'D' && q.answer !== 'D'" class="w-4 h-4 text-red-600 ml-auto" />
              </label>
            </div>
            <!-- 答案反馈 -->
            <div v-if="practiceShowAnswer[q.id]" class="mt-2 text-sm font-medium">
              <p v-if="practiceAnswers[q.id] === q.answer" class="text-green-600 flex items-center gap-1">
                <CheckCircle class="w-4 h-4" /> 回答正确！
              </p>
              <p v-else class="text-red-600 flex items-center gap-1">
                <XCircle class="w-4 h-4" /> 回答错误，正确答案是：{{ q.answer }}
              </p>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="flex items-center justify-center gap-3 mt-4 lg:mt-6 pt-4 lg:pt-6 border-t">
          <button 
            @click="prevPage" 
            :disabled="practicePage <= 1"
            class="px-4 py-2 bg-gray-100 rounded-lg text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200 flex items-center gap-1"
          >
            <ChevronLeft class="w-4 h-4" /> 上一页
          </button>
          <span class="text-sm text-gray-500">{{ practicePage }} / {{ totalPages || 1 }}</span>
          <button 
            @click="nextPage" 
            :disabled="practicePage >= totalPages"
            class="px-4 py-2 bg-gray-100 rounded-lg text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200 flex items-center gap-1"
          >
            下一页 <ChevronRight class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
