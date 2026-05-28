import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/api/request'

interface UserInfo {
  userId: number
  username: string
  realName: string
  roles: string[]
  menus: MenuItem[]
}

interface MenuItem {
  id: number
  name: string
  path: string
  component: string
  icon: string
  parentId: number
  children: MenuItem[]
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  async function login(username: string, password: string) {
    const res = await request.post('/auth/login', { username, password })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    userInfo.value = res.data
    return res.data
  }

  async function getUserInfo() {
    const res = await request.get('/auth/info')
    userInfo.value = res.data
    return res.data
  }

  async function logout() {
    await request.post('/auth/logout')
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, login, getUserInfo, logout }
})
