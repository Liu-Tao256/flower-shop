<template>
  <div class="product-detail">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>商品详情</span>
      <van-icon name="cart-o" size="22" @click="goToCart" />
    </div>

    <van-swipe class="product-swipe" :autoplay="3000" indicator-color="white">
      <van-swipe-item v-for="(img, index) in product.images || [product.mainImage]" :key="index">
        <img :src="img" :alt="product.name" />
      </van-swipe-item>
    </van-swipe>

    <div class="product-info">
      <div class="price-row">
        <span class="current-price">¥{{ product.price }}</span>
        <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
      </div>
      <h1 class="product-name">{{ product.name }}</h1>
      <p class="product-desc">{{ product.description }}</p>
      <div class="sales-row">
        <span>销量 {{ product.salesCount || 0 }}</span>
        <span>库存 {{ product.stock || 0 }}件</span>
      </div>
    </div>

    <div class="product-detail-section">
      <div class="section-title">商品详情</div>
      <div class="section-content">
        <p>{{ product.detail || product.description }}</p>
      </div>
    </div>

    <div class="bottom-bar">
      <div class="cart-icon" @click="goToCart">
        <van-icon name="cart-o" size="24" />
        <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
      </div>
      <div class="action-buttons">
        <van-button type="primary" round @click="addToCart">加入购物车</van-button>
        <van-button type="danger" round @click="buyNow">立即购买</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const productId = route.params.id
const product = ref({})
const loading = ref(false)

const cartCount = computed(() => {
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  return cart.reduce((sum, item) => sum + item.quantity, 0)
})

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

// 保存浏览记录
const saveBrowseHistory = async () => {
  const token = localStorage.getItem('token')
  if (!token) return

  try {
    await axios.post('/api/browse-history', {
      productId: product.value.id,
      type: 'product',
      title: product.value.name,
      imageUrl: product.value.mainImage
    }, getHeaders())
  } catch (error) {
    console.error('保存浏览记录失败', error)
  }
}

const loadProduct = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/products/${productId}`)
    if (res.data.code === 200) {
      product.value = res.data.data
      if (product.value.mainImage && !product.value.images) {
        product.value.images = [product.value.mainImage]
      }
      await saveBrowseHistory()
    } else {
      showToast('商品不存在')
      router.back()
    }
  } catch (error) {
    showToast('加载失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const addToCart = () => {
  let cart = JSON.parse(localStorage.getItem('cart') || '[]')
  const existItem = cart.find(item => item.id === product.value.id)

  if (existItem) {
    existItem.quantity += 1
  } else {
    cart.push({
      id: product.value.id,
      name: product.value.name,
      price: product.value.price,
      mainImage: product.value.mainImage,
      quantity: 1,
      selected: true
    })
  }

  localStorage.setItem('cart', JSON.stringify(cart))
  showToast({ message: '已加入购物车', type: 'success', duration: 1000 })
}

const buyNow = () => {
  addToCart()
  router.push('/checkout')
}

const goBack = () => {
  router.back()
}

const goToCart = () => {
  router.push('/cart')
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 70px;
}

.header {
  position: sticky;
  top: 0;
  background: white;
  padding: 15px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  z-index: 10;
}

.header span {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

.product-swipe {
  height: 350px;
  background: #f0f0f0;
}

.product-swipe img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  background: white;
  padding: 20px;
  margin: 12px;
  border-radius: 16px;
}

.price-row {
  margin-bottom: 12px;
}

.current-price {
  font-size: 28px;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 16px;
  color: #bbb;
  text-decoration: line-through;
  margin-left: 10px;
}

.product-name {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
}

.product-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
  line-height: 1.5;
}

.sales-row {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #aaa;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.product-detail-section {
  background: white;
  margin: 12px;
  padding: 20px;
  border-radius: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #764ba2;
  display: inline-block;
}

.section-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.cart-icon {
  position: relative;
  padding: 8px;
  cursor: pointer;
}

.cart-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: #e74c3c;
  color: white;
  font-size: 10px;
  border-radius: 10px;
  padding: 2px 6px;
  min-width: 18px;
  text-align: center;
}

.action-buttons {
  flex: 1;
  display: flex;
  gap: 12px;
}

.action-buttons .van-button {
  flex: 1;
  height: 44px;
}
</style>