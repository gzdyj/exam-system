<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from 'lucide-vue-next'
import request from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  realName: '',
  role: 2
})

const isLogin = ref(true)
const loading = ref(false)

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res: any = await request.post('/auth/login', loginForm)
    userStore.setToken(res.token)
    userStore.setUserInfo(res.user)
    ElMessage.success('登录成功')
    
    if (res.user.role === 1) {
      router.push('/admin')
    } else {
      router.push('/student')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password || !registerForm.realName) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    await request.post('/auth/register', registerForm)
    ElMessage.success('注册成功，请登录')
    isLogin.value = true
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500">
    <div class="absolute inset-0 bg-black/20"></div>
    
    <div class="relative z-10 w-full max-w-md px-4 lg:px-6">
      <div class="bg-white/95 backdrop-blur-sm rounded-2xl lg:rounded-3xl shadow-2xl p-6 lg:p-8">
        <div class="text-center mb-6 lg:mb-8">
          <div class="inline-flex items-center justify-center w-14 h-14 lg:w-16 lg:h-16 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl lg:rounded-2xl mb-3 lg:mb-4 shadow-lg">
            <School class="w-7 h-7 lg:w-8 lg:h-8 text-white" />
          </div>
          <h1 class="text-xl lg:text-2xl font-bold text-gray-800">计算机题目考试系统</h1>
          <p class="text-gray-500 mt-1 lg:mt-2 text-sm lg:text-base">在线考试，智能评分</p>
        </div>

        <div v-if="isLogin" class="space-y-4 lg:space-y-6">
          <div class="space-y-1 lg:space-y-2">
            <label class="text-xs lg:text-sm font-medium text-gray-700">用户名</label>
            <div class="relative">
              <User class="absolute left-3 lg:left-4 top-1/2 -translate-y-1/2 w-4 h-4 lg:w-5 lg:h-5 text-gray-400" />
              <input
                v-model="loginForm.username"
                type="text"
                placeholder="请输入用户名"
                class="w-full pl-10 lg:pl-12 pr-4 py-2.5 lg:py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm lg:text-base"
              />
            </div>
          </div>
          
          <div class="space-y-1 lg:space-y-2">
            <label class="text-xs lg:text-sm font-medium text-gray-700">密码</label>
            <div class="relative">
              <Lock class="absolute left-3 lg:left-4 top-1/2 -translate-y-1/2 w-4 h-4 lg:w-5 lg:h-5 text-gray-400" />
              <input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                class="w-full pl-10 lg:pl-12 pr-4 py-2.5 lg:py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm lg:text-base"
                @keyup.enter="handleLogin"
              />
            </div>
          </div>

          <button
            @click="handleLogin"
            :disabled="loading"
            class="w-full py-2.5 lg:py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed text-sm lg:text-base"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </button>

          <div class="text-center text-sm lg:text-base">
            <span class="text-gray-500">还没有账号？</span>
            <button @click="isLogin = false" class="text-indigo-600 font-medium hover:underline ml-1">
              立即注册
            </button>
          </div>
        </div>

        <div v-else class="space-y-4 lg:space-y-6">
          <div class="space-y-1 lg:space-y-2">
            <label class="text-xs lg:text-sm font-medium text-gray-700">用户名</label>
            <div class="relative">
              <User class="absolute left-3 lg:left-4 top-1/2 -translate-y-1/2 w-4 h-4 lg:w-5 lg:h-5 text-gray-400" />
              <input
                v-model="registerForm.username"
                type="text"
                placeholder="请输入用户名"
                class="w-full pl-10 lg:pl-12 pr-4 py-2.5 lg:py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm lg:text-base"
              />
            </div>
          </div>

          <div class="space-y-1 lg:space-y-2">
            <label class="text-xs lg:text-sm font-medium text-gray-700">姓名</label>
            <div class="relative">
              <User class="absolute left-3 lg:left-4 top-1/2 -translate-y-1/2 w-4 h-4 lg:w-5 lg:h-5 text-gray-400" />
              <input
                v-model="registerForm.realName"
                type="text"
                placeholder="请输入姓名"
                class="w-full pl-10 lg:pl-12 pr-4 py-2.5 lg:py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm lg:text-base"
              />
            </div>
          </div>
          
          <div class="space-y-1 lg:space-y-2">
            <label class="text-xs lg:text-sm font-medium text-gray-700">密码</label>
            <div class="relative">
              <Lock class="absolute left-3 lg:left-4 top-1/2 -translate-y-1/2 w-4 h-4 lg:w-5 lg:h-5 text-gray-400" />
              <input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                class="w-full pl-10 lg:pl-12 pr-4 py-2.5 lg:py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all text-sm lg:text-base"
              />
            </div>
          </div>

          <button
            @click="handleRegister"
            :disabled="loading"
            class="w-full py-2.5 lg:py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed text-sm lg:text-base"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </button>

          <div class="text-center text-sm lg:text-base">
            <span class="text-gray-500">已有账号？</span>
            <button @click="isLogin = true" class="text-indigo-600 font-medium hover:underline ml-1">
              立即登录
            </button>
          </div>
        </div>

        <div class="mt-4 lg:mt-6 pt-4 lg:pt-6 border-t border-gray-200">
          <p class="text-xs text-center text-gray-400">默认账号: admin/admin123 (教师) | student/student123 (学生)</p>
        </div>
      </div>
    </div>
  </div>
</template>
