<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as likeApi from '../services/likeApi'
import { authState, isLoggedIn } from '../stores/auth'

const router = useRouter()
const productList = ref([])
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const showAddModal = ref(false)
const addLoading = ref(false)
const addForm = ref({
  productNo: null,
  productName: '',
  purchaseQuantity: 1,
  account: ''
})

const loadProducts = async () => {
  try {
    loading.value = true
    const res = await likeApi.getAllProducts()
    productList.value = Array.isArray(res) ? res : (res?.data ?? [])
  } catch (err) {
    errorMessage.value = err.message || '產品清單載入失敗'
  } finally {
    loading.value = false
  }
}

const handleAddClick = (product) => {
  if (!isLoggedIn()) {
    // 導向帶參數，觸發 App.vue 顯示登入 Modal
    router.push({ query: { showLogin: '1' } })
    return
  }

  // 登入後，開啟新增喜好商品視窗
  addForm.value = {
    productNo: product.productNo,
    productName: product.productName,
    purchaseQuantity: 1,
    account: '' // 可選：自動帶入 currentUser 的某個預設帳號（若有存）
  }
  showAddModal.value = true
  errorMessage.value = ''
  successMessage.value = ''
}

const submitAdd = async () => {
  try {
    addLoading.value = true
    errorMessage.value = ''
    successMessage.value = ''

    if (!addForm.value.purchaseQuantity || addForm.value.purchaseQuantity <= 0) {
      errorMessage.value = '購買數量需大於 0'
      return
    }
    if (!addForm.value.account?.trim()) {
      errorMessage.value = '請輸入扣款帳號'
      return
    }

    await likeApi.addLike({
      userId: authState.userId,
      productNo: Number(addForm.value.productNo),
      purchaseQuantity: Number(addForm.value.purchaseQuantity),
      account: addForm.value.account
    })

    successMessage.value = '加入喜好清單成功！'
    setTimeout(() => {
      showAddModal.value = false
    }, 1000)
  } catch (err) {
    errorMessage.value = err.message || '加入失敗'
  } finally {
    addLoading.value = false
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<template>
  <div>
    <h2 style="margin-bottom: 24px;">金融商品總覽</h2>

    <p v-if="loading">載入商品中...</p>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
    
    <table border="1" cellspacing="0" cellpadding="8" width="100%">
      <thead>
        <tr>
          <th>商品編號</th>
          <th>商品名稱</th>
          <th>價格</th>
          <th>費率</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in productList" :key="p.productNo">
          <td>{{ p.productNo }}</td>
          <td>{{ p.productName }}</td>
          <td>{{ p.price }}</td>
          <td>{{ (p.feeRate * 100).toFixed(2) }}%</td>
          <td style="text-align: center;">
            <button @click="handleAddClick(p)" class="btn-primary">加入喜好清單</button>
          </td>
        </tr>
        <tr v-if="!productList.length && !loading">
          <td colspan="5" style="text-align: center;">暫無商品資料</td>
        </tr>
      </tbody>
    </table>

    <!-- 加入喜好清單彈跳視窗 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal">
        <h3 style="margin-top: 0;">加入喜好清單</h3>
        <div class="modal-body">
          <p><strong>商品：</strong> {{ addForm.productName }}</p>
          <div class="field">
            <label>購買數量</label>
            <input type="number" v-model="addForm.purchaseQuantity" min="1" />
          </div>
          <div class="field">
            <label>扣款帳號</label>
            <input v-model="addForm.account" placeholder="請輸入扣款帳號" />
          </div>
          <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
          <p v-if="successMessage" style="color: green;">{{ successMessage }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn-primary" @click="submitAdd" :disabled="addLoading">
            {{ addLoading ? '處理中...' : '確認加入' }}
          </button>
          <button class="btn-secondary" @click="showAddModal = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.btn-primary {
  background: #3498db;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}
.btn-primary:focus, .btn-primary:hover {
  background: #2980b9;
}
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
}
.field {
  margin-bottom: 12px;
}
.field label {
  display: block; margin-bottom: 4px; font-weight: bold;
}
.field input {
  width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;
}
.modal-footer {
  margin-top: 16px;
  display: flex; gap: 8px; justify-content: flex-end;
}
.btn-secondary {
  background: #ecf0f1; border: 1px solid #ccc; padding: 6px 12px; border-radius: 4px; cursor: pointer;
}
</style>
