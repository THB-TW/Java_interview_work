import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn } from '../stores/auth'
import ProductsPage from '../pages/ProductsPage.vue'
import LikesPage from '../pages/LikesPage.vue'

const routes = [
  {
    path: '/',
    name: 'Products',
    component: ProductsPage
  },
  {
    path: '/likes',
    name: 'Likes',
    component: LikesPage,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守衛：未登入時存取 /likes 會留在原頁並觸發登入提示
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !isLoggedIn()) {
    // 改以 query 帶 redirect，讓 App.vue 中的 LoginModal 接收
    next({ path: '/', query: { showLogin: '1', redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
