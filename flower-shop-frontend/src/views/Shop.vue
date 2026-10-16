<template>
  <div class="shop-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <h1>花店商城</h1>
      <van-icon name="cart-o" size="22" @click="goToCart" />
    </div>

    <div class="category-tabs">
      <div
          v-for="cat in categories"
          :key="cat.id"
          :class="['category-item', { active: activeCategory === cat.id }]"
          @click="switchCategory(cat.id)"
      >
        <span class="cat-icon">{{ cat.icon }}</span>
        <span class="cat-name">{{ cat.name }}</span>
      </div>
    </div>

    <div class="product-list" v-if="productList.length > 0">
      <div
          v-for="product in productList"
          :key="product.id"
          class="product-card"
          @click="goToDetail(product.id)"
      >
        <div class="product-image">
          <img :src="product.mainImage" :alt="product.name" />
          <div class="sale-tag" v-if="product.salesCount > 100">热卖</div>
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-desc">{{ product.description }}</p>
          <div class="price-wrapper">
            <span class="current-price">¥{{ product.price }}</span>
            <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
            <span class="sales-count">已售{{ product.salesCount || 0 }}</span>
          </div>
          <div class="action-wrapper">
            <van-button size="small" type="primary" round @click.stop="addToCart(product)">
              <van-icon name="cart-o" /> 加入购物车
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <div class="loading-wrapper" v-else-if="loading">
      <van-loading type="spinner" size="40" />
    </div>

    <div class="empty-wrapper" v-else>
      <van-empty description="暂无商品" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const productList = ref([])
const loading = ref(false)
const activeCategory = ref(0)

const categories = ref([
  { id: 0, name: '全部', icon: '🏠' },
  { id: 1, name: '鲜花', icon: '🌸' },
  { id: 2, name: '绿植', icon: '🌿' },
  { id: 3, name: '多肉', icon: '🌵' },
  { id: 4, name: '花束', icon: '💐' }
])

// 获取请求头
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

// 加载商品
const loadProducts = async () => {
  loading.value = true
  try {
    let url = '/api/products'
    if (activeCategory.value !== 0) {
      url = `/api/products/category/${activeCategory.value}`
    }
    const res = await axios.get(url, getHeaders())
    if (res.data.code === 200) {
      productList.value = res.data.data
      console.log('加载商品数量:', productList.value.length)
    }
  } catch (error) {
    console.error('加载失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 切换分类
const switchCategory = (categoryId) => {
  activeCategory.value = categoryId
  loadProducts()
}

// 跳转商品详情
const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

// 加入购物车
const addToCart = (product) => {
  let cart = JSON.parse(localStorage.getItem('cart') || '[]')
  const existItem = cart.find(item => item.id === product.id)
  if (existItem) {
    existItem.quantity += 1
  } else {
    cart.push({
      id: product.id,
      name: product.name,
      price: product.price,
      mainImage: product.mainImage,
      quantity: 1,
      selected: true
    })
  }
  localStorage.setItem('cart', JSON.stringify(cart))
  showToast({ message: '已加入购物车', type: 'success', duration: 1000 })
}

const goBack = () => {
  router.back()
}

const goToCart = () => {
  router.push('/cart')
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.shop-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 20px;
}

.header {
  background: white;
  padding: 15px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header h1 {
  font-size: 18px;
  color: #2c3e50;
}

.category-tabs {
  background: white;
  padding: 12px 16px;
  display: flex;
  justify-content: space-around;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 53px;
  z-index: 10;
}

.category-item {
  text-align: center;
  padding: 8px 12px;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.cat-icon {
  font-size: 20px;
  display: block;
  margin-bottom: 4px;
}

.cat-name {
  font-size: 12px;
}

.product-list {
  padding: 12px;
}

.product-card {
  background: white;
  border-radius: 16px;
  margin-bottom: 16px;
  overflow: hidden;
  display: flex;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.2s;
}

.product-card:active {
  transform: scale(0.98);
}

.product-image {
  width: 120px;
  height: 120px;
  position: relative;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.sale-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 10px;
}

.product-info {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 6px;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-wrapper {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 12px;
  color: #bbb;
  text-decoration: line-through;
}

.sales-count {
  font-size: 11px;
  color: #aaa;
}

.action-wrapper {
  display: flex;
  justify-content: flex-end;
}

:deep(.van-button) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  font-size: 12px;
  padding: 6px 12px;
}

.loading-wrapper,
.empty-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}
</style>