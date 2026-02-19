<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { BookOpen, FileText, Users, CheckCircle } from 'lucide-vue-next'
import request from '@/api'

const stats = ref({
  questionCount: 0,
  paperCount: 0,
  studentCount: 0,
  recordCount: 0
})

const recentRecords = ref([])

onMounted(async () => {
  try {
    const [questions, papers, users, records] = await Promise.all([
      request.get('/questions', { params: { page: 1, limit: 1 } }),
      request.get('/papers', { params: { page: 1, limit: 1 } }),
      request.get('/users', { params: { page: 1, limit: 1, role: 2 } }),
      request.get('/exam-records', { params: { page: 1, limit: 5, status: 1 } })
    ])
    
    stats.value = {
      questionCount: questions.total || 0,
      paperCount: papers.total || 0,
      studentCount: users.total || 0,
      recordCount: records.total || 0
    }
    recentRecords.value = records.list || []
  } catch (error) {
    console.error(error)
  }
})

const statCards = [
  { label: '题目总数', icon: BookOpen, color: 'from-blue-500 to-blue-600', value: () => stats.value.questionCount },
  { label: '试卷总数', icon: FileText, color: 'from-purple-500 to-purple-600', value: () => stats.value.paperCount },
  { label: '学生总数', icon: Users, color: 'from-green-500 to-green-600', value: () => stats.value.studentCount },
  { label: '考试记录', icon: CheckCircle, color: 'from-orange-500 to-orange-600', value: () => stats.value.recordCount }
]
</script>

<template>
  <div class="space-y-6">
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div
        v-for="(stat, index) in statCards"
        :key="index"
        class="bg-white rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow"
      >
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">{{ stat.label }}</p>
            <p class="text-3xl font-bold text-gray-800 mt-2">{{ stat.value() }}</p>
          </div>
          <div :class="`w-14 h-14 rounded-2xl bg-gradient-to-br ${stat.color} flex items-center justify-center shadow-lg`">
            <component :is="stat.icon" class="w-7 h-7 text-white" />
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-2xl p-6 shadow-sm">
      <h3 class="text-lg font-bold text-gray-800 mb-4">最近考试记录</h3>
      <div class="space-y-3">
        <div
          v-for="record in recentRecords"
          :key="record.id"
          class="flex items-center justify-between p-4 bg-gray-50 rounded-xl"
        >
          <div>
            <p class="font-medium text-gray-800">考试记录 #{{ record.id }}</p>
            <p class="text-sm text-gray-500">提交时间：{{ record.submitTime }}</p>
          </div>
          <div class="text-right">
            <p class="text-2xl font-bold text-green-600">{{ record.score }}分</p>
          </div>
        </div>
        <div v-if="recentRecords.length === 0" class="text-center py-8 text-gray-400">
          暂无考试记录
        </div>
      </div>
    </div>
  </div>
</template>
