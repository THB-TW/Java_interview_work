import { reactive } from 'vue'

// 從 localStorage 初始化（重整後保持登入狀態）
export const authState = reactive({
  userId: localStorage.getItem('userId') || null,
  userName: localStorage.getItem('userName') || null,
  email: localStorage.getItem('email') || null
})

export function isLoggedIn() {
  return !!authState.userId
}

export function login(user) {
  authState.userId = user.userId
  authState.userName = user.userName
  authState.email = user.email
  localStorage.setItem('userId', user.userId)
  localStorage.setItem('userName', user.userName)
  localStorage.setItem('email', user.email)
}

export function logout() {
  authState.userId = null
  authState.userName = null
  authState.email = null
  localStorage.removeItem('userId')
  localStorage.removeItem('userName')
  localStorage.removeItem('email')
}
