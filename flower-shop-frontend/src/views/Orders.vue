<template>
  <div class="orders-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>我的订单</span>
    </div>

    <van-tabs v-model:active="activeTab">
      <van-tab title="全部" name="all"></van-tab>
      <van-tab title="待付款" name="0"></van-tab>
      <van-tab title="已付款" name="1"></van-tab>
      <van-tab title="已完成" name="3"></van-tab>
    </van-tabs>

    <div class="order-list" v-if="orders.length > 0">
      <div v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </span>
        </div>

        <div class="order-goods">
          <div class="goods-info">
            <span class="goods-count">共 {{ order.itemCount || 1 }} 件商品</span>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            实付：<span class="total-price">¥{{ order.finalAmount }}</span>
          </div>
          <div class="order-actions">
            <van-button
                v-if="order.status === 0"
                size="small"
                type="danger"
                round
                @click="payOrder(order.id)"
            >
              去支付
            </van-button>
            <van-button
                v-if="order.status === 0"
                size="small"
                round
                @click="cancelOrder(order.id)"
            >
              取消订单
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-orders" v-else>
      <van-empty description="暂无订单" />
      <van-button type="primary" round @click="goToShop">去逛逛</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const activeTab = ref('all')
const orders = ref([])

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => String(order.status) === activeTab.value)
})

const getStatusText = (status) => {
  const map = { 0: '待付款', 1: '已付款', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = { 0: 'status-pending', 1: 'status-paid', 2: 'status-shipping', 3: 'status-completed', 4: 'status-cancelled' }
  return map[status] || ''
}

const loadOrders = async () => {
  try {
    const res = await axios.get('/api/orders', getHeaders())
    if (res.data.code === 200) {
      orders.value = res.data.data
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    showToast('加载订单失败')
  }
}

const payOrder = async (orderId) => {
  try {
    const res = await axios.put(`/api/orders/${orderId}/pay`, {}, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('支付成功')
      await loadOrders()
    } else {
      showToast(res.data.message || '支付失败')
    }
  } catch (error) {
    showToast('支付失败')
  }
}

const cancelOrder = async (orderId) => {
  try {
    const res = await axios.put(`/api/orders/${orderId}/cancel`, {}, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('订单已取消')
      await loadOrders()
    } else {
      showToast(res.data.message || '取消失败')
    }
  } catch (error) {
    showToast('取消失败')
  }
}

const goBack = () => {
  router.back()
}

const goToShop = () => {
  router.push('/shop')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-container {
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
:deep(.van-tabs) {
  background: white;
}
.order-list {
  padding: 12px;
}
.order-card {
  background: white;
  border-radius: 16px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.order-header {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}
.order-no {
  font-size: 12px;
  color: #999;
}
.order-status {
  font-size: 13px;
  font-weight: bold;
}
.status-pending { color: #e74c3c; }
.status-paid { color: #f39c12; }
.status-shipping { color: #3498db; }
.status-completed { color: #27ae60; }
.status-cancelled { color: #999; }
.order-goods {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}
.goods-count {
  font-size: 13px;
  color: #666;
}
.order-footer {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-total {
  font-size: 13px;
  color: #666;
}
.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}
.order-actions {
  display: flex;
  gap: 10px;
}
.empty-orders {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}
</style>