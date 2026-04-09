<script setup>
import { ref, onMounted } from 'vue'
import * as likeApi from '../services/likeApi'
import { authState } from '../stores/auth'

const likeList = ref([])
const productList = ref([])
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

// 分頁與篩選
const params = ref({
  page: 0,
  size: 10,
  productNo: '',
  account: ''
})
const totalElements = ref(0)
const totalPages = ref(0)

// 更新表單
const showEditModal = ref(false)
const editLoading = ref(false)
const editForm = ref({
  sn: null,
  userId: '',
  productNo: '',
  purchaseQuantity: 1,
  account: ''
})

const loadProducts = async () => {
  try {
    const res = await likeApi.getAllProducts()
    productList.value = Array.isArray(res) ? res : (res?.data ?? [])
  } catch (err) {
    console.error('產品清單載入失敗', err)
  }
}

const loadLikes = async () => {
  try {
    loading.value = true
    errorMessage.value = ''
    
    // 準備 API 參數
    const queryParams = {
      page: params.value.page,
      size: params.value.size
    }
    if (params.value.productNo) queryParams.productNo = params.value.productNo
    if (params.value.account) queryParams.account = params.value.account

    const res = await likeApi.getLikesPaged(authState.userId, queryParams)
    const result = res?.data ?? res
    
    // 如果回傳的是 PagedResult
    if (result && result.data !== undefined) {
      likeList.value = result.data
      totalElements.value = result.total || 0
      totalPages.value = result.totalPages || 0
    } else if (Array.isArray(result)) {
      // 容錯機制
      likeList.value = result
      totalElements.value = result.length
      totalPages.value = 1
    } else {
      likeList.value = []
    }
  } catch (err) {
    errorMessage.value = err.message || '查詢失敗'
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  params.value.page = 0 // 重新搜尋從第一頁開始
  loadLikes()
}

const changePage = (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return
  params.value.page = newPage
  loadLikes()
}

const getProductName = (productNo) => {
  const found = productList.value.find(p => p.productNo == productNo)
  return found ? found.productName : '－'
}

const removeLike = async (sn) => {
  try {
    const confirmed = window.confirm('確定要刪除這筆喜好商品嗎？')
    if (!confirmed) return
    
    errorMessage.value = ''
    successMessage.value = ''
    await likeApi.deleteLike(sn, authState.userId)
    successMessage.value = '刪除成功'
    loadLikes()
  } catch (err) {
    errorMessage.value = err.message || '刪除失敗'
  }
}

const openEditModal = (item) => {
  errorMessage.value = ''
  successMessage.value = ''
  editForm.value = {
    sn: item.sn,
    userId: authState.userId,
    productNo: item.productNo,
    purchaseQuantity: item.purchaseQuantity,
    account: item.account
  }
  showEditModal.value = true
}

const submitUpdate = async () => {
  try {
    editLoading.value = true
    errorMessage.value = ''
    successMessage.value = ''

    if (!editForm.value.productNo) return (errorMessage.value = '請選擇商品')
    if (!editForm.value.purchaseQuantity || editForm.value.purchaseQuantity <= 0) {
      return (errorMessage.value = '購買數量需大於 0')
    }
    if (!editForm.value.account?.trim()) return (errorMessage.value = '請輸入扣款帳號')

    await likeApi.updateLike(editForm.value.sn, {
      userId: editForm.value.userId,
      productNo: Number(editForm.value.productNo),
      purchaseQuantity: Number(editForm.value.purchaseQuantity),
      account: editForm.value.account
    })

    successMessage.value = '更新成功'
    showEditModal.value = false
    loadLikes()
  } catch (err) {
    errorMessage.value = err.message || '更新失敗'
  } finally {
    editLoading.value = false
  }
}

onMounted(() => {
  loadProducts()
  loadLikes()
})
</script>

<template>
  <div>
    <h2 style="margin-bottom: 24px;">登入使用者 Email: {{ authState.email }}</h2>

    <!-- 篩選列 -->
    <section class="filter-section">
      <h3>條件篩選</h3>
      <div class="filter-bar">
        <div class="field">
          <label>商品名稱：</label>
          <select v-model="params.productNo">
            <option value="">-- 全部商品 --</option>
            <option v-for="p in productList" :key="p.productNo" :value="p.productNo">
              {{ p.productName }}
            </option>
          </select>
        </div>
        <div class="field">
          <label>扣款帳號：</label>
          <input v-model="params.account" placeholder="輸入帳號關鍵字" @keyup.enter="handleSearch" />
        </div>
        <button class="btn-primary" @click="handleSearch">搜尋</button>
      </div>
    </section>

    <p v-if="loading">載入中...</p>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
    <p v-if="successMessage" style="color: green;">{{ successMessage }}</p>

    <!-- 喜好清單表格 -->
    <section>
      <table border="1" cellspacing="0" cellpadding="8" width="100%">
        <thead>
          <tr>
            <th>SN</th>
            <th>商品名稱</th>
            <th>購買數量</th>
            <th>扣款帳號</th>
            <th>User Email</th>
            <th>總手續費</th>
            <th>總扣款金額</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in likeList" :key="item.sn">
            <td>{{ item.sn }}</td>
            <td>{{ getProductName(item.productNo) }}</td>
            <td>{{ item.purchaseQuantity }}</td>
            <td>{{ item.account }}</td>
            <td>{{ item.email }}</td>
            <td>{{ item.totalFee }}</td>
            <td>{{ item.totalAmount }}</td>
            <td style="white-space: nowrap;">
              <button @click="openEditModal(item)" class="btn-sm">更新</button>
              <button @click="removeLike(item.sn)" class="btn-sm btn-danger" style="margin-left: 6px;">刪除</button>
            </td>
          </tr>
          <tr v-if="!likeList.length && !loading">
            <td colspan="8" style="text-align: center;">查無結果</td>
          </tr>
        </tbody>
      </table>

      <!-- 分頁 -->
      <div class="pagination" v-if="totalPages > 0">
        <button :disabled="params.page === 0" @click="changePage(params.page - 1)">上一頁</button>
        <span>第 {{ params.page + 1 }} 頁 / 共 {{ totalPages }} 頁 (總計 {{ totalElements }} 筆)</span>
        <button :disabled="params.page === totalPages - 1" @click="changePage(params.page + 1)">下一頁</button>
      </div>
    </section>

    <!-- 更新彈跳視窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
      <div class="modal">
        <h3 style="margin-top: 0;">更新喜好商品</h3>
        <div class="modal-body">
          <div class="field">
            <label>商品名稱</label>
            <select v-model="editForm.productNo">
              <option v-for="p in productList" :key="p.productNo" :value="p.productNo">
                {{ p.productName }}
              </option>
            </select>
          </div>
          <div class="field">
            <label>購買數量</label>
            <input v-model="editForm.purchaseQuantity" type="number" min="1" />
          </div>
          <div class="field">
            <label>扣款帳號</label>
            <input v-model="editForm.account" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-primary" @click="submitUpdate" :disabled="editLoading">
            {{ editLoading ? '更新中...' : '更新' }}
          </button>
          <button class="btn-secondary" @click="showEditModal = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.filter-section {
  margin-bottom: 24px;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fdfdfd;
}
.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}
.field {
  display: flex;
  align-items: center;
  gap: 8px;
}
.btn-primary {
  background: #3498db; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer;
}
.btn-sm {
  background: #3498db; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 13px;
}
.btn-danger {
  background: #e74c3c;
}
.btn-secondary {
  background: #ecf0f1; border: 1px solid #ccc; padding: 6px 12px; border-radius: 4px; cursor: pointer;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000;
}
.modal {
  background: white; padding: 24px; border-radius: 8px; width: 100%; max-width: 400px;
}
.modal-body .field {
  flex-direction: column; align-items: flex-start; margin-bottom: 12px;
}
.modal-body .field select, .modal-body .field input {
  width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;
}
.modal-footer {
  margin-top: 16px; display: flex; gap: 8px; justify-content: flex-end;
}
</style>
