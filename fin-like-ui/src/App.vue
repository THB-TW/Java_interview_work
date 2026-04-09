<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { authState, isLoggedIn, logout } from './stores/auth'
import LoginModal from './components/LoginModal.vue'

const router = useRouter()
const route = useRoute()
const showLoginModal = ref(false)
const redirectPath = ref('')

// 監聽路由參數，若帶有 showLogin=1 則跳出登入視窗
watch(
  () => route.query,
  (newQuery) => {
    if (newQuery.showLogin === '1') {
      showLoginModal.value = true
      redirectPath.value = newQuery.redirect || ''
      
      // 清除 URL 參數避免重整一直彈出（使用 history API 靜默清除，不觸發 Vue Router 的重新渲染與失焦）
      const url = new URL(window.location.href)
      url.searchParams.delete('showLogin')
      url.searchParams.delete('redirect')
      window.history.replaceState({}, '', url)
    }
  },
  { immediate: true }
)

const handleLogout = () => {
  logout()
  // 檢查目前頁面是否需要登入，需要的話導回首頁
  if (route.meta.requiresAuth) {
    router.push('/')
  }
}

const onLoginSuccess = () => {
  showLoginModal.value = false
  if (redirectPath.value) {
    router.push(redirectPath.value)
    redirectPath.value = ''
  }
}
</script>

<template>
  <div class="app-container">
    <!-- 頂部導航欄 -->
    <header class="navbar">
      <div class="nav-brand">
        <h1>金融商品交易系統</h1>
      </div>
      <nav class="nav-links">
        <router-link to="/" class="nav-item">商品瀏覽</router-link>
        <router-link to="/likes" class="nav-item">喜好清單</router-link>
      </nav>
      <div class="user-block">
        <template v-if="isLoggedIn()">
          <span class="user-name">歡迎, {{ authState.userName }} </span>
          <button class="btn-logout" @click="handleLogout">登出</button>
        </template>
        <template v-else>
          <button class="btn-login" @click="showLoginModal = true; redirectPath = ''">登入 / 註冊</button>
        </template>
      </div>
    </header>

    <!-- 主要內容區 -->
    <main class="main-content">
      <router-view></router-view>
    </main>

    <!-- 共用登入彈跳視窗 -->
    <LoginModal 
      :show="showLoginModal" 
      :redirect="redirectPath"
      @close="showLoginModal = false"
      @success="onLoginSuccess"
    />
  </div>
</template>

<style scoped>
.app-container {
  font-family: Arial, sans-serif;
  color: #333;
}
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background-color: #2c3e50;
  color: white;
  height: 60px;
}
.nav-brand h1 {
  margin: 0;
  font-size: 20px;
  color: #ecf0f1;
}
.nav-links {
  display: flex;
  gap: 20px;
}
.nav-item {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 16px;
  font-weight: bold;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.2s;
}
.nav-item:hover, .nav-item.router-link-active {
  color: white;
  background-color: #34495e;
}
.user-block {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-name {
  font-size: 14px;
}
.btn-login {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}
.btn-login:hover {
  background-color: #2980b9;
}
.btn-logout {
  background-color: transparent;
  color: #ecf0f1;
  border: 1px solid #ecf0f1;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}
.btn-logout:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
.main-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>