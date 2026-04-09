<script setup>
import { ref, onMounted } from 'vue'
import * as likeApi from './services/likeApi'

const userId = ref('A1236456789')
const likeList = ref([])
const productList = ref([])

const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

//新增喜好表單
const form = ref({
  userId: 'A1236456789',
  productNo: '',
  purchaseQuantity: 1,
  account: '1111999666'
})

//更新彈跳視窗狀態
const showEditModal = ref(false)
const editLoading = ref(false)
const editForm = ref({
  sn: null,
  userId: '',
  productNo: '',
  purchaseQuantity: 1,
  account: ''
})

//查詢喜好清單
const loadLikes = async () => {
  try {
    loading.value = true
    errorMessage.value = ''
    const res = await likeApi.getLikes(userId.value)
    likeList.value = Array.isArray(res) ? res : (res?.data ?? [])
  } catch (err) {
    errorMessage.value = err.message || '查詢失敗'
  } finally {
    loading.value = false
  }
}

//載入商品清單
const loadProducts = async () => {
  try {
    const res = await likeApi.getAllProducts()
    productList.value = Array.isArray(res) ? res : (res?.data ?? [])
  } catch (err) {
    errorMessage.value = err.message || '產品清單載入失敗'
  }
}

//根據productNo取得productName
const getProductName = (productNo) => {
  const found = productList.value.find(p => p.productNo === productNo)
  return found ? found.productName : '－'
}

//刪除
const removeLike = async (sn) => {
  try {
    errorMessage.value = ''
    successMessage.value = ''
    const confirmed = window.confirm('確定要刪除這筆喜好商品嗎？')
    if (!confirmed) return
    await likeApi.deleteLike(sn, userId.value)
    successMessage.value = '刪除成功'
    await loadLikes()
  } catch (err) {
    errorMessage.value = err.message || '刪除失敗'
  }
}

//開啟更新
const openEditModal = (item) => {
  errorMessage.value = ''
  successMessage.value = ''
  editForm.value = {
    sn: item.sn,
    userId: userId.value,
    productNo: item.productNo,
    purchaseQuantity: item.purchaseQuantity,
    account: item.account
  }
  showEditModal.value = true
}

//提交更新
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
    await loadLikes()
  } catch (err) {
    errorMessage.value = err.message || '更新失敗'
  } finally {
    editLoading.value = false
  }
}

//新增
const resetForm = () => {
  form.value = {
    userId: userId.value || 'A1236456789',
    productNo: '',
    purchaseQuantity: 1,
    account: '1111999666'
  }
}

const submitAdd = async () => {
  try {
    errorMessage.value = ''
    successMessage.value = ''

    if (!form.value.userId) return (errorMessage.value = '請輸入 User ID')
    if (!form.value.productNo) return (errorMessage.value = '請選擇商品')
    if (!form.value.purchaseQuantity || form.value.purchaseQuantity <= 0) {
      return (errorMessage.value = '購買數量需大於 0')
    }
    if (!form.value.account) return (errorMessage.value = '請輸入扣款帳號')

    await likeApi.addLike({
      userId: form.value.userId,
      productNo: Number(form.value.productNo),
      purchaseQuantity: Number(form.value.purchaseQuantity),
      account: form.value.account
    })

    successMessage.value = '新增成功'
    userId.value = form.value.userId
    await loadLikes()
    resetForm()
  } catch (err) {
    errorMessage.value = err.message || '新增失敗'
  }
}

onMounted(async () => {
  await loadProducts()
  await loadLikes()
})
</script>

<template>
  <div style="padding: 24px; font-family: Arial, sans-serif;">
    <h1>金融商品喜好紀錄系統</h1>

    <!-- 查詢 -->
    <section style="margin-bottom: 24px; padding: 16px; border: 1px solid #ccc;">
      <h2>查詢喜好清單</h2>
      <div style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap;">
        <input v-model="userId" placeholder="請輸入 User ID" />
        <button @click="loadLikes">查詢</button>
      </div>
    </section>

    <!-- 新增 -->
    <section style="margin-bottom: 24px; padding: 16px; border: 1px solid #ccc;">
      <h2>新增喜好商品</h2>
      <div style="display: grid; gap: 12px; max-width: 500px;">
        <div>
          <label for="userId">使用者 ID</label><br />
          <input id="userId" v-model="form.userId" placeholder="請輸入使用者 ID" />
        </div>
        <div>
          <label for="productNo">商品名稱</label><br />
          <select id="productNo" v-model="form.productNo">
            <option disabled value="">請選擇商品</option>
            <option v-for="p in productList" :key="p.productNo" :value="p.productNo">
              {{ p.productName }}
            </option>
          </select>
        </div>
        <div>
          <label for="purchaseQuantity">購買數量</label><br />
          <input id="purchaseQuantity" v-model="form.purchaseQuantity" type="number" min="1" placeholder="請輸入購買數量" />
        </div>
        <div>
          <label for="account">扣款帳號</label><br />
          <input id="account" v-model="form.account" placeholder="請輸入扣款帳號" />
        </div>
        <button @click="submitAdd">新增</button>
      </div>
    </section>

    <p v-if="loading">載入中...</p>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
    <p v-if="successMessage" style="color: green;">{{ successMessage }}</p>

    <!-- 喜好清單 -->
    <section>
      <h2>喜好清單</h2>
      <table border="1" cellspacing="0" cellpadding="8" width="100%">
        <thead>
          <tr>
            <th>SN</th>
            <th>商品名稱</th>
            <th>購買數量</th>
            <th>扣款帳號</th>
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
            <td>{{ item.totalFee }}</td>
            <td>{{ item.totalAmount }}</td>
            <td style="white-space: nowrap;">
              <button @click="openEditModal(item)" style="margin-right: 6px;">更新</button>
              <button @click="removeLike(item.sn)">刪除</button>
            </td>
          </tr>
          <tr v-if="!likeList.length && !loading">
            <td colspan="7" style="text-align: center;">查無資料</td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 更新彈跳視窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
      <div class="modal">
        <h3 style="margin-top: 0;">更新喜好商品</h3>
        <div class="modal-body">
          <div class="field">
            <label for="edit-productNo">商品名稱</label>
            <select id="edit-productNo" v-model="editForm.productNo">
              <option v-for="p in productList" :key="p.productNo" :value="p.productNo">
                {{ p.productName }}
              </option>
            </select>
          </div>
          <div class="field">
            <label for="edit-purchaseQuantity">購買數量</label>
            <input
              id="edit-purchaseQuantity"
              v-model="editForm.purchaseQuantity"
              type="number"
              min="1"
            />
          </div>
          <div class="field">
            <label for="edit-account">扣款帳號</label>
            <input id="edit-account" v-model="editForm.account" />
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