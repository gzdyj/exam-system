<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { History, Trophy } from 'lucide-vue-next'
import request from '@/api'

interface Record {
  id: number
  paperId: number
  score: number
  status: number
  startTime: string
  submitTime: string
}

const userStore = useUserStore()
const records = ref<Record[]>([])
const loading = ref(false)

const loadRecords = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/exam-records', {
      params: { 
        page: 1, 
        limit: 100, 
        userId: userStore.user?.id 
      }
    })
    records.value = res.list || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getScoreClass = (score: number) => {
  if (score >= 90) return 'text-yellow-500'
  if (score >= 70) return 'text-green-500'
  if (score >= 60) return 'text-orange-500'
  return 'text-red-500'
}

onMounted(() => {
  loadRecords()
})
</script>

<template>
  <div class="space-y-6">
    <h2 class="text-xl font-bold text-gray-800">考试记录</h2>

    <div v-if="loading" class="text-center py-12">
      <div class="animate-spin w-8 h-8 border-4 border-green-500 border-t-transparent rounded-full mx-auto"></div>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="record in records"
        :key="record.id"
        class="bg-white rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow"
      >
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 bg-gradient-to-br from-green-500 to-teal-600 rounded-2xl flex items-center justify-center shadow-lg">
              <Trophy class="w-7 h-7 text-white" />
            </div>
            <div>
              <h3 class="font-bold text-gray-800">考试 #{{ record.id }}</h3>
              <p class="text-sm text-gray-500 mt-1">
                开始时间：{{ record.startTime }}
              </p>
              <p v-if="record.submitTime" class="text-sm text-gray-500">
                提交时间：{{ record.submitTime }}
              </p>
            </div>
          </div>
          
          <div class="text-right">
            <p :class="getScoreClass(record.score)" class="text-3xl font-bold">
              {{ record.score }}分
            </p>
            <span
              :class="record.status === 1 ? 'bg-green-100 text-green-600' : 'bg-yellow-100 text-yellow-600'"
              class="px-3 py-1 rounded-full text-xs"
            >
              {{ record.status === 1 ? '已完成' : '进行中' }}
            </span>
          </div>
        </div>
      </div>

      <div v-if="records.length === 0" class="text-center py-12 bg-white rounded-2xl">
        <History class="w-16 h-16 text-gray-300 mx-auto mb-4" />
        <p class="text-gray-500">暂无考试记录</p>
      </div>
    </div>
  </div>
</template>
