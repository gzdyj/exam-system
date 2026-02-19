<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { BookOpen, FileText, History, LogOut, Menu, X } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const sidebarOpen = ref(false)

userStore.initFromStorage()

const menuItems = [
  { path: '/student/exams', name: 'Exams', icon: FileText, label: '在线考试' },
  { path: '/student/records', name: 'StudentRecords', icon: History, label: '考试记录' }
]

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

const closeSidebar = () => {
  sidebarOpen.value = false
}
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 移动端遮罩 -->
    <div 
      v-if="sidebarOpen" 
      @click="closeSidebar"
      class="fixed inset-0 bg-black/50 z-40 lg:hidden"
    ></div>

    <!-- 侧边栏 -->
    <aside 
      :class="[
        'fixed left-0 top-0 h-full w-64 bg-gradient-to-b from-green-500 to-teal-600 shadow-xl z-50 transition-transform duration-300',
        sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
      ]"
    >
      <div class="p-4 lg:p-6">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-white/20 rounded-xl flex items-center justify-center">
              <BookOpen class="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 class="text-white font-bold">考试系统</h1>
              <p class="text-green-100 text-xs">学生端</p>
            </div>
          </div>
          <button @click="closeSidebar" class="lg:hidden p-2 text-white/80 hover:text-white">
            <X class="w-5 h-5" />
          </button>
        </div>
      </div>

      <nav class="px-3 mt-4">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          @click="closeSidebar"
          class="flex items-center gap-3 px-4 py-3 rounded-xl mb-1 transition-all"
          :class="route.path === item.path ? 'bg-white/20 text-white' : 'text-green-100 hover:bg-white/10'"
        >
          <component :is="item.icon" class="w-5 h-5" />
          <span class="font-medium">{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="absolute bottom-0 left-0 right-0 p-4">
        <button
          @click="handleLogout"
          class="flex items-center gap-3 w-full px-4 py-3 rounded-xl text-green-100 hover:bg-white/10 transition-all"
        >
          <LogOut class="w-5 h-5" />
          <span class="font-medium">退出登录</span>
        </button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="lg:ml-64 min-h-screen">
      <header class="bg-white shadow-sm border-b border-gray-100 px-4 lg:px-8 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <button @click="toggleSidebar" class="lg:hidden p-2 hover:bg-gray-100 rounded-lg">
              <Menu class="w-5 h-5 text-gray-600" />
            </button>
            <h2 class="text-lg lg:text-xl font-bold text-gray-800">{{ userStore.user?.realName || '学生' }}</h2>
          </div>
          <div class="flex items-center gap-3">
            <div class="w-9 h-9 lg:w-10 lg:h-10 bg-gradient-to-br from-green-500 to-teal-600 rounded-full flex items-center justify-center text-white font-bold text-sm lg:text-base">
              {{ userStore.user?.realName?.charAt(0) || 'S' }}
            </div>
          </div>
        </div>
      </header>

      <div class="p-4 lg:p-8">
        <router-view />
      </div>
    </main>
  </div>
</template>
