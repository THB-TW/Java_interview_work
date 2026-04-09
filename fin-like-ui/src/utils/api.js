import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const res = error.response?.data
    return Promise.reject({
      code: res?.code || '9999',
      message: res?.message || '系統異常，請稍後再試'
    })
  }
)

export default api