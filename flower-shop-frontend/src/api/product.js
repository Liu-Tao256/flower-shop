import request from '@/utils/request'

export const getProductList = () => request.get('/products')
export const getProductById = (id) => request.get(`/products/${id}`)
export const getProductsByCategory = (categoryId) => request.get(`/products/category/${categoryId}`)