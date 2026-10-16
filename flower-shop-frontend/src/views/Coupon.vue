<template>
  <div class="coupon-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>我的优惠券</span>
    </div>

    <van-tabs v-model:active="activeTab">
      <van-tab title="未使用" name="unused"></van-tab>
      <van-tab title="已使用/过期" name="used"></van-tab>
    </van-tabs>

    <div class="coupon-list" v-if="filteredCoupons.length > 0">
      <div v-for="uc in filteredCoupons" :key="uc.id" class="coupon-card" :class="{ used: uc.status !== 0 }">
        <div class="coupon-left">
          <div class="coupon-value">
            <span v-if="uc.coupon.type === 'fixed'">¥{{ uc.coupon.value }}</span>
            <span v-else>{{ uc.coupon.value }}折</span>
          </div>
          <div class="coupon-condition" v-if="uc.coupon.minAmount > 0">
            满¥{{ uc.coupon.minAmount }}可用
          </div>
          <div class="coupon-condition" v-else>无门槛</div>
        </div>
        <div class="coupon-right">
          <div class="coupon-name">{{ uc.coupon.name }}</div>
          <div class="coupon-expire">有效期至：{{ formatDate(uc.expireAt) }}</div>
        </div>
      </div>
    </div>

    <div class="empty-coupon" v-else>
      <van-empty description="暂无优惠券" />
      <van-button type="primary" round @click="goToPointsMall">积分商城兑换</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const activeTab = ref('unused')
const userCoupons = ref([])

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const filteredCoupons = computed(() => {
  if (activeTab.value === 'unused') {
    return userCoupons.value.filter(uc => uc.status === 0)
  }
  return userCoupons.value.filter(uc => uc.status !== 0)
})

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

const loadCoupons = async () => {
  try {
    const res = await axios.get('/api/user-coupons', getHeaders())
    console.log('优惠券数据:', res.data)
    if (res.data.code === 200) {
      userCoupons.value = res.data.data
    }
  } catch (error) {
    console.error('加载失败', error)
    showToast('加载失败')
  }
}

const goToPointsMall = () => {
  router.push('/points-mall')
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadCoupons()
})
</script>

<style scoped>
.coupon-container {
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

.coupon-list {
  padding: 12px;
}

.coupon-card {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  margin-bottom: 12px;
  padding: 16px;
  display: flex;
  color: white;
}

.coupon-card.used {
  opacity: 0.6;
}

.coupon-left {
  width: 100px;
  text-align: center;
  border-right: 1px dashed rgba(255,255,255,0.3);
}

.coupon-value {
  font-size: 28px;
  font-weight: bold;
}

.coupon-condition {
  font-size: 11px;
  opacity: 0.8;
}

.coupon-right {
  flex: 1;
  padding-left: 16px;
}

.coupon-name {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 8px;
}

.coupon-expire {
  font-size: 11px;
  opacity: 0.8;
}

.empty-coupon {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}
</style>