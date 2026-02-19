<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search } from 'lucide-vue-next'
import request from '@/api'

const records = ref([])
const total = ref(0)
const loading = ref(false)
const searchForm = ref({
  status: null as number | null
})

const currentPage = ref(1)
const pageSize = ref(10)

const loadRecords = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/exam-records', {
      params: {
        page: currentPage.value,
        limit: pageSize.value,
        status: searchForm.value.status
      }
    })
    records.value = res.list
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadRecords()
}

onMounted(() => {
  loadRecords()
})
</script>

<template>
  <div class="space-y-6">
    <div class="bg-white rounded-2xl p-6 shadow-sm">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-lg font-bold text-gray-800">考试记录</h2>
        <div class="flex items-center gap-4">
          <el-select v-model="searchForm.status" placeholder="考试状态" clearable class="w-40" @change="handleSearch">
            <el-option label="进行中" :value="0" />
            <el-option label="已完成" :value="1" />
          </el-select>
        </div>
      </div>

      <el-table :data="records" v-loading="loading" class="w-full">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="paperId" label="试卷ID" width="80" />
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <span class="font-bold text-green-600">{{ row.score }}分</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span :class="row.status === 1 ? 'bg-green-100 text-green-600' : 'bg-yellow-100 text-yellow-600'" class="px-2 py-1 rounded-full text-xs">
              {{ row.status === 1 ? '已完成' : '进行中' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
      </el-table>

      <div class="flex justify-end mt-4">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadRecords"
        />
      </div>
    </div>
  </div>
</template>
