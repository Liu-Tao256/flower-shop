<template>
  <div class="plant-detail-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>植物百科</span>
      <van-icon name="share-o" size="22" @click="share" />
    </div>

    <div class="plant-image" :style="{ backgroundImage: 'url(' + plant.imageUrl + ')' }" v-if="plant.imageUrl">
      <div class="image-overlay"></div>
    </div>
    <div class="plant-image plant-image-placeholder" v-else>
      <div class="image-overlay"></div>
      <span class="placeholder-icon">🌿</span>
    </div>

    <div class="plant-info">
      <h1 class="plant-name">{{ plant.name }}</h1>
      <p class="plant-alias" v-if="plant.alias">别名：{{ plant.alias }}</p>
      <div class="plant-tags">
        <span class="tag" v-if="plant.family">🌱 {{ plant.family }}</span>
        <span class="tag" v-if="plant.genus">📚 {{ plant.genus }}</span>
        <span class="tag" v-if="plant.type">{{ getTypeText(plant.type) }}</span>
      </div>
    </div>

    <div class="detail-section">
      <div class="section-title">
        <span class="title-icon">🌿</span>
        <span>植物特性</span>
      </div>
      <div class="section-content">
        <p>{{ plant.characteristic || '暂无描述' }}</p>
      </div>
    </div>

    <div class="detail-section">
      <div class="section-title">
        <span class="title-icon">💧</span>
        <span>养护知识</span>
      </div>
      <div class="section-content">
        <p>{{ plant.careKnowledge || '暂无养护信息' }}</p>
      </div>
    </div>

    <div class="detail-section" v-if="plant.relatedProducts && plant.relatedProducts.length > 0">
      <div class="section-title">
        <span class="title-icon">🛒</span>
        <span>花店同款商品</span>
      </div>
      <div class="product-list">
        <div v-for="product in plant.relatedProducts" :key="product.id" class="product-card" @click="goToProduct(product.id)">
          <img :src="product.mainImage" :alt="product.name" />
          <div class="product-info">
            <div class="product-name">{{ product.name }}</div>
            <div class="product-price">¥{{ product.price }}</div>
            <van-button size="small" type="primary" round @click.stop="addToCart(product)">购买</van-button>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-bar">
      <div class="action-btn checkin-btn" @click="handleCheckin">
        <van-icon name="star-o" size="22" />
        <span>打卡</span>
      </div>
      <div class="action-btn collect-btn" @click="handleCollect">
        <van-icon name="like-o" size="22" />
        <span>收藏</span>
      </div>
      <div class="action-btn share-btn" @click="share">
        <van-icon name="share-o" size="22" />
        <span>分享</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const plantId = route.params.id
const plant = ref({})

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const getTypeText = (type) => {
  const map = {
    flower: '🌸 鲜花',
    green: '🌿 绿植',
    succulent: '🌵 多肉'
  }
  return map[type] || '🌱 植物'
}

// 保存浏览记录
const saveBrowseHistory = async () => {
  const token = localStorage.getItem('token')
  if (!token) return

  try {
    await axios.post('/api/browse-history', {
      plantId: plant.value.id,
      type: 'plant',
      title: plant.value.name,
      imageUrl: plant.value.imageUrl
    }, getHeaders())
  } catch (error) {
    console.error('保存浏览记录失败', error)
  }
}

const loadPlantDetail = async () => {
  try {
    const res = await axios.get(`/api/plant/${plantId}`, getHeaders())
    if (res.data.code === 200) {
      plant.value = res.data.data
      await saveBrowseHistory()
    } else {
      showToast(res.data.message || '加载失败')
    }
  } catch (error) {
    console.error('加载失败:', error)
    showToast('加载失败')
  }
}

const handleCheckin = () => {
  showSuccessToast('打卡成功！+10积分')
}

const handleCollect = () => {
  showSuccessToast('已收藏')
}

const share = () => {
  showToast('分享功能开发中')
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

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
  showSuccessToast('已加入购物车')
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadPlantDetail()
})
</script>

<style scoped>
.plant-detail-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 80px;
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

.plant-image {
  height: 280px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.plant-image-placeholder {
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 80px;
  opacity: 0.5;
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: linear-gradient(transparent, rgba(0,0,0,0.3));
}

.plant-info {
  background: white;
  margin: 16px;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.plant-name {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
}

.plant-alias {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
}

.plant-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: #f0f0f0;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  color: #666;
}

.detail-section {
  background: white;
  margin: 0 16px 16px;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #764ba2;
  display: inline-block;
}

.title-icon {
  font-size: 18px;
}

.section-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-top: 12px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 12px;
}

.product-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.product-card:active {
  transform: scale(0.98);
}

.product-card img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.product-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-name {
  font-size: 15px;
  font-weight: bold;
  color: #2c3e50;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  justify-content: space-around;
  padding: 12px 16px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 10;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: transform 0.2s;
}

.action-btn:active {
  transform: scale(0.95);
}

.action-btn span {
  font-size: 12px;
  color: #666;
}

.checkin-btn .van-icon {
  color: #27ae60;
}

.collect-btn .van-icon {
  color: #e74c3c;
}

.share-btn .van-icon {
  color: #3498db;
}
</style>