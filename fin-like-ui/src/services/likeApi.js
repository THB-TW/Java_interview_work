import api from '../utils/api'

export const getLikes = (userId) =>
  api.get('/likes', {
    params: { userId }
  })

export const addLike = (payload) =>
  api.post('/likes', payload)

export const updateLike = (sn, payload) =>
  api.put(`/likes/${sn}`, payload)

export const deleteLike = (id, userId) =>
  api.delete(`/likes/${id}`, {
    params: { userId }
  })

export const getAllProducts = () => api.get('/products')