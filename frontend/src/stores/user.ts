import { defineStore } from 'pinia'
import { ref } from 'vue'

interface User {
  id: number
  username: string
  realName: string
  role: number
}

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref('')

  function setUserInfo(userInfo: User) {
    user.value = userInfo
    localStorage.setItem('user', JSON.stringify(userInfo))
  }

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function logout() {
    user.value = null
    token.value = ''
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  function initFromStorage() {
    const userStr = localStorage.getItem('user')
    const tokenStr = localStorage.getItem('token')
    if (userStr) {
      user.value = JSON.parse(userStr)
    }
    if (tokenStr) {
      token.value = tokenStr
    }
  }

  return {
    user,
    token,
    setUserInfo,
    setToken,
    logout,
    initFromStorage
  }
})
