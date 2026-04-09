<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import * as authApi from '../services/authApi'
import { login as storeLogin } from '../stores/auth'

const props = defineProps({
  show: Boolean,
  redirect: String
})

const emit = defineEmits(['close', 'success'])
const router = useRouter()

const isRegister = ref(false)
const loading = ref(false)
const errorMessage = ref('')

// 登入表單
const loginForm = ref({
  username: '',
  password: ''
})

// 註冊表單
const registerForm = ref({
  userId: '',
  userName: '',
  email: '',
  account: '',
  username: '',
  password: '',
  confirmPassword: ''
})

const close = () => {
  emit('close')
  errorMessage.value = ''
  isRegister.value = false
}

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    errorMessage.value = '請輸入帳號密碼'
    return
  }

  try {
    loading.value = true
    errorMessage.value = ''
    const res = await authApi.login(loginForm.value)
    const user = Array.isArray(res) ? res : (res?.data ?? res)
    if (user && user.userId) {
      storeLogin(user)
      emit('success')
      if (props.redirect) {
        router.push(props.redirect)
      }
      close()
    } else {
      errorMessage.value = '登入失敗，回傳資料格式錯誤'
    }
  } catch (err) {
    errorMessage.value = err.message || '登入失敗（帳號或密碼錯誤）'
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.value.userId || !registerForm.value.userName || 
      !registerForm.value.email || !registerForm.value.account || 
      !registerForm.value.username || !registerForm.value.password) {
    errorMessage.value = '所有欄位皆為必填'
    return
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    errorMessage.value = '兩次輸入的密碼不一致'
    return
  }

  try {
    loading.value = true
    errorMessage.value = ''
    const res = await authApi.register(registerForm.value)
    const user = Array.isArray(res) ? res : (res?.data ?? res)
    if (user && user.userId) {
      // 註冊成功直接登入
      storeLogin(user)
      emit('success')
      if (props.redirect) {
        router.push(props.redirect)
      }
      close()
    } else {
      errorMessage.value = '申請失敗，回傳資料格式錯誤'
    }
  } catch (err) {
    errorMessage.value = err.message || '申請失敗'
  } finally {
    loading.value = false
  }
}

const toggleMode = () => {
  isRegister.value = !isRegister.value
  errorMessage.value = ''
}
</script>

<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal">
      <h3 style="margin-top: 0;">{{ isRegister ? '申請帳號' : '會員登入' }}</h3>
      
      <div v-if="!isRegister" class="modal-body">
        <div class="field">
          <label>登入帳號</label>
          <input v-model="loginForm.username" placeholder="請輸入帳號" />
        </div>
        <div class="field">
          <label>密碼</label>
          <input type="password" v-model="loginForm.password" placeholder="請輸入密碼" @keyup.enter="handleLogin" />
        </div>
        <p v-if="errorMessage" style="color: red; margin: 8px 0 0;">{{ errorMessage }}</p>
      </div>

      <div v-else class="modal-body">
        <div class="field">
          <label>身分證字號 / User ID</label>
          <input v-model="registerForm.userId" placeholder="例如：A123456789" />
        </div>
        <div class="field">
          <label>姓名</label>
          <input v-model="registerForm.userName" placeholder="請輸入姓名" />
        </div>
        <div class="field">
          <label>Email</label>
          <input type="email" v-model="registerForm.email" placeholder="請輸入 Email" />
        </div>
        <div class="field">
          <label>扣款銀行帳號</label>
          <input v-model="registerForm.account" placeholder="請輸入銀行帳號" />
        </div>
        <hr style="margin: 16px 0; border: 0; border-top: 1px solid #eee;" />
        <div class="field">
          <label>自訂登入帳號</label>
          <input v-model="registerForm.username" placeholder="請輸入登入帳號" />
        </div>
        <div class="field">
          <label>登入密碼</label>
          <input type="password" v-model="registerForm.password" placeholder="至少 6 碼" />
        </div>
        <div class="field">
          <label>確認密碼</label>
          <input type="password" v-model="registerForm.confirmPassword" placeholder="請再次輸入密碼" />
        </div>
        <p v-if="errorMessage" style="color: red; margin: 8px 0 0;">{{ errorMessage }}</p>
      </div>

      <div class="modal-footer" style="flex-wrap: wrap;">
        <div style="width: 100%; display: flex; justify-content: flex-end; gap: 8px; margin-bottom: 8px;">
          <button class="btn-primary" @click="isRegister ? handleRegister() : handleLogin()" :disabled="loading">
            {{ loading ? '處理中...' : (isRegister ? '送出申請' : '登入') }}
          </button>
          <button class="btn-secondary" @click="close">取消</button>
        </div>
        <div style="width: 100%; text-align: center; font-size: 14px;">
          <a href="#" @click.prevent="toggleMode" style="color: #0066cc;">
            {{ isRegister ? '已有帳號？點此登入' : '還沒有帳號？立即申請' }}
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.field {
  margin-bottom: 12px;
}
.field label {
  display: block;
  margin-bottom: 4px;
  font-weight: bold;
}
.field input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}
.btn-primary {
  background: #3498db;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}
.btn-primary:disabled {
  background: #95a5a6;
}
.btn-secondary {
  background: #ecf0f1;
  color: #333;
  border: 1px solid #bdc3c7;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}
.modal-footer {
  margin-top: 20px;
}
</style>
