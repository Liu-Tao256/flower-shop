<template>
  <div class="points-mall-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>积分商城</span>
    </div>

    <div class="points-info">
      <span class="points-label">我的积分</span>
      <span class="points-value">{{ userPoints }}</span>
    </div>

    <van-tabs v-model:active="activeTab">
      <van-tab title="优惠券兑换" name="coupon"></van-tab>
      <van-tab title="实物兑换" name="product"></van-tab>
    </van-tabs>

    <!-- 优惠券列表 -->
    <div class="coupon-list" v-if="activeTab === 'coupon'">
      <div v-for="coupon in coupons" :key="coupon.id" class="exchange-card">
        <div class="card-left">
          <div class="card-value">¥{{ coupon.value }}</div>
          <div class="card-condition" v-if="coupon.minAmount > 0">
            满{{ coupon.minAmount }}元可用
          </div>
          <div class="card-condition" v-else>无门槛</div>
        </div>
        <div class="card-info">
          <div class="card-name">{{ coupon.name }}</div>
          <div class="card-points">{{ coupon.pointsCost }} 积分</div>
        </div>
        <div class="card-action">
          <van-button
              size="small"
              type="primary"
              round
              :disabled="userPoints < coupon.pointsCost"
              @click="exchangeCoupon(coupon)"
          >
            兑换
          </van-button>
        </div>
      </div>
    </div>

    <!-- 实物商品列表 -->
    <div class="product-list" v-if="activeTab === 'product'">
      <div v-for="product in pointsProducts" :key="product.id" class="product-card">
        <img :src="product.imageUrl" />
        <div class="product-info">
          <div class="product-name">{{ product.name }}</div>
          <div class="product-points">{{ product.points }} 积分</div>
          <div class="product-stock">库存：{{ product.stock }}</div>
        </div>
        <div class="product-action">
          <van-button
              size="small"
              type="primary"
              round
              :disabled="userPoints < product.points || product.stock === 0"
              @click="exchangeProduct(product)"
          >
            兑换
          </van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const activeTab = ref('coupon')
const userPoints = ref(0)
const coupons = ref([])
const pointsProducts = ref([])

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const loadUserPoints = async () => {
  try {
    const res = await axios.get('/api/user/info', getHeaders())
    if (res.data.code === 200) {
      userPoints.value = res.data.data.points || 0
    }
  } catch (error) {
    console.error('加载积分失败', error)
  }
}

const loadCoupons = async () => {
  try {
    const res = await axios.get('/api/coupons', getHeaders())
    if (res.data.code === 200) {
      coupons.value = res.data.data
    }
  } catch (error) {
    console.error('加载优惠券失败', error)
  }
}

const loadPointsProducts = async () => {
  try {
    const res = await axios.get('/api/points-products', getHeaders())
    if (res.data.code === 200) {
      pointsProducts.value = res.data.data
    }
  } catch (error) {
    console.error('加载商品失败', error)
  }
}

const exchangeCoupon = async (coupon) => {
  if (userPoints.value < coupon.pointsCost) {
    showToast(`积分不足，当前积分: ${userPoints.value}`)
    return
  }

  try {
    const res = await axios.post(`/api/exchange/coupon/${coupon.id}`, {}, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('兑换成功')
      // 刷新用户积分
      await loadUserPoints()
      // 刷新优惠券列表
      await loadCoupons()
    } else {
      showToast(res.data.message || '兑换失败')
    }
  } catch (error) {
    console.error('兑换失败', error)
    showToast('兑换失败')
  }
}

const exchangeProduct = async (product) => {
  if (userPoints.value < product.points) {
    showToast('积分不足')
    return
  }
  if (product.stock === 0) {
    showToast('库存不足')
    return
  }
  try {
    const res = await axios.post(`/api/exchange/product/${product.id}`, {}, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('兑换成功')
      loadUserPoints()
      loadPointsProducts()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    showToast('兑换失败')
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadUserPoints()
  loadCoupons()
  loadPointsProducts()
})
</script>

<style scoped>
.points-mall-container {
  min-height: 100vh;
  background: #f5f7fa;
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
}

.points-info {
  background: linear-gradient(135deg, #667eea, #764ba2);
  margin: 16px;
  padding: 20px;
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  color: white;
}

.points-label {
  font-size: 14px;
}

.points-value {
  font-size: 28px;
  font-weight: bold;
}

.coupon-list, .product-list {
  padding: 12px;
}

.exchange-card {
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-left {
  width: 80px;
  text-align: center;
}

.card-value {
  font-size: 22px;
  font-weight: bold;
  color: #e74c3c;
}

.card-condition {
  font-size: 11px;
  color: #999;
}

.card-info {
  flex: 1;
}

.card-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.card-points {
  color: #764ba2;
  font-size: 12px;
}

.product-card {
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  padding: 12px;
  display: flex;
  gap: 12px;
}

.product-card img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.product-info {
  flex: 1;
}

.product-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.product-points {
  color: #764ba2;
  font-size: 12px;
  margin-bottom: 2px;
}

.product-stock {
  font-size: 11px;
  color: #999;
}

.product-action {
  display: flex;
  align-items: center;
}
</style>