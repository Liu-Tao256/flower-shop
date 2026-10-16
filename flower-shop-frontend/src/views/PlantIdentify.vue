<template>
  <div class="identify-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>植物识别</span>
    </div>

    <div class="upload-area" @click="selectImage">
      <div class="upload-content" v-if="!imageUrl">
        <van-icon name="photograph" size="48" color="#ccc" />
        <p>点击选择图片</p>
        <p class="tip">支持相册或拍照</p>
      </div>
      <div class="preview-area" v-else>
        <img :src="imageUrl" alt="预览" />
        <div class="reupload" @click.stop="selectImage">
          <van-icon name="replay" size="20" />
        </div>
      </div>
    </div>

    <div class="action-buttons" v-if="imageUrl && !identifying && !result">
      <van-button type="primary" round block @click="identify">开始识别</van-button>
    </div>

    <van-loading v-if="identifying" size="40" class="loading" />

    <div class="result-area" v-if="result">
      <div class="result-card">
        <div class="result-header">
          <span class="plant-icon">🌿</span>
          <div class="plant-name">{{ result.name }}</div>
          <div class="confidence">匹配度：{{ (result.confidence * 100).toFixed(1) }}%</div>
        </div>

        <div class="plant-info">
          <div class="info-row" v-if="result.alias">
            <span class="label">别名：</span>
            <span>{{ result.alias }}</span>
          </div>
          <div class="info-row" v-if="result.family">
            <span class="label">科属：</span>
            <span>{{ result.family }} / {{ result.genus }}</span>
          </div>
          <div class="info-row" v-if="result.characteristic">
            <span class="label">特性：</span>
            <span>{{ result.characteristic }}</span>
          </div>
          <div class="info-row" v-if="result.careKnowledge">
            <span class="label">养护：</span>
            <span>{{ result.careKnowledge }}</span>
          </div>
        </div>

        <div class="action-buttons">
          <van-button round @click="checkin">📌 打卡记录</van-button>
          <van-button round type="primary" @click="viewDetail">📖 查看详情</van-button>
        </div>
      </div>

      <div class="related-products" v-if="result.relatedProducts && result.relatedProducts.length">
        <div class="section-title">🛒 同款商品</div>
        <div class="product-list">
          <div v-for="product in result.relatedProducts" :key="product.id" class="product-card" @click="goToProduct(product.id)">
            <img :src="product.mainImage" :alt="product.name" />
            <div class="product-info">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-price">¥{{ product.price }}</div>
              <van-button size="small" type="primary" round @click.stop="addToCart(product)">购买</van-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const imageUrl = ref('')
const imageFile = ref(null)
const identifying = ref(false)
const result = ref(null)

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const selectImage = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      imageFile.value = file
      imageUrl.value = URL.createObjectURL(file)
      result.value = null
    }
  }
  input.click()
}

const identify = async () => {
  if (!imageFile.value) return

  identifying.value = true
  const formData = new FormData()
  formData.append('image', imageFile.value)

  try {
    const res = await axios.post('/api/plant/identify', formData, {
      headers: { 'Content-Type': 'multipart/form-data', ...getHeaders().headers }
    })
    if (res.data.code === 200) {
      result.value = res.data.data
      showSuccessToast('识别成功')
    } else {
      showToast(res.data.message || '识别失败')
    }
  } catch (error) {
    console.error('识别失败:', error)
    showToast('识别失败，请重试')
  } finally {
    identifying.value = false
  }
}

const checkin = async () => {
  if (!result.value || !result.value.plantId) {
    showToast('请先识别植物')
    return
  }

  // 检查今天是否已打卡过该植物
  try {
    const checkRes = await axios.get(`/api/checkin/check-plant?plantId=${result.value.plantId}`, getHeaders())
    if (checkRes.data.code === 200 && checkRes.data.data.hasCheckinToday) {
      showToast('今天已经打卡过该植物了')
      return
    }
  } catch (error) {
    console.error('检查打卡状态失败', error)
  }

  // 执行打卡
  try {
    const res = await axios.post('/api/checkin', {
      plantId: result.value.plantId,
      plantName: result.value.name,
      imageUrl: imageUrl.value
    }, getHeaders())

    if (res.data.code === 200) {
      showSuccessToast(res.data.data.message || '打卡成功！+10积分')

      // 更新本地积分
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      userInfo.points = (userInfo.points || 0) + 10
      localStorage.setItem('userInfo', JSON.stringify(userInfo))

      // 更新任务进度（新增）
      await updateTaskProgress()
    } else {
      showToast(res.data.message || '打卡失败')
    }
  } catch (error) {
    console.error('打卡失败', error)
    showToast('打卡失败')
  }
}

// 更新任务进度
const updateTaskProgress = async () => {
  try {
    await axios.post('/api/tasks/update-progress', {}, getHeaders())
  } catch (error) {
    console.error('更新任务进度失败', error)
  }
}

const viewDetail = () => {
  if (result.value && result.value.plantId) {
    router.push(`/plant/${result.value.plantId}`)
  } else if (result.value && result.value.name) {
    showToast('该植物暂无详细信息')
  } else {
    showToast('暂无详细信息')
  }
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
</script>

<style scoped>
.identify-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 20px;
}

.header {
  background: white;
  padding: 15px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header span {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

.upload-area {
  margin: 20px;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
}

.upload-content {
  padding: 60px 20px;
  text-align: center;
  border: 2px dashed #ccc;
  border-radius: 16px;
  margin: 0;
}

.upload-content p {
  margin-top: 12px;
  color: #999;
}

.tip {
  font-size: 12px;
}

.preview-area {
  position: relative;
}

.preview-area img {
  width: 100%;
  height: auto;
  display: block;
}

.reupload {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0,0,0,0.6);
  border-radius: 50%;
  padding: 8px;
  color: white;
}

.action-buttons {
  margin: 0 20px;
}

.loading {
  display: flex;
  justify-content: center;
  margin: 40px;
}

.result-area {
  margin: 20px;
}

.result-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
}

.result-header {
  text-align: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.plant-icon {
  font-size: 48px;
}

.plant-name {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin: 8px 0;
}

.confidence {
  font-size: 13px;
  color: #27ae60;
}

.plant-info {
  padding: 16px 0;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}

.info-row .label {
  width: 60px;
  flex-shrink: 0;
  color: #999;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-card {
  display: flex;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
}

.product-card img {
  width: 80px;
  height: 80px;
  object-fit: cover;
}

.product-info {
  flex: 1;
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-name {
  font-weight: bold;
}

.product-price {
  color: #e74c3c;
}
</style>