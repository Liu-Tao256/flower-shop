<template>
  <div class="orders-admin">
    <div class="page-header">
      <h3>订单管理</h3>
    </div>

    <!-- 搜索和筛选栏 -->
    <div class="search-bar">
      <van-field
          v-model="searchKeyword"
          placeholder="搜索订单号"
          clearable
          @clear="loadOrders"
      >
        <template #button>
          <van-button size="small" type="primary" @click="loadOrders">搜索</van-button>
        </template>
      </van-field>

      <div class="status-tabs">
        <span
            v-for="tab in statusTabs"
            :key="tab.value"
            :class="['status-tab', { active: activeStatus === tab.value }]"
            @click="filterByStatus(tab.value)"
        >
          {{ tab.name }}
        </span>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-if="orderList.length > 0">
      <div v-for="order in orderList" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ formatTime(order.createdAt) }}</span>
          </div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </div>
        </div>

        <div class="order-goods">
          <div class="goods-preview" v-if="order.items && order.items.length">
            <img :src="order.items[0].mainImage || order.items[0].productImage" />
            <div class="goods-count">共{{ order.items.length }}件商品</div>
          </div>
          <div class="order-amount">
            实付：<span class="amount">¥{{ order.finalAmount }}</span>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-user">
            <van-icon name="user-o" /> 用户ID：{{ order.userId }}
          </div>
          <div class="order-actions">
            <van-button size="small" @click="viewDetail(order.id)">详情</van-button>
            <van-button
                v-if="order.status === 0"
                size="small"
                type="danger"
                @click="updateStatus(order.id, 4)"
            >
              取消订单
            </van-button>
            <van-button
                v-if="order.status === 0"
                size="small"
                type="primary"
                @click="updateStatus(order.id, 1)"
            >
              确认付款
            </van-button>
            <van-button
                v-if="order.status === 1"
                size="small"
                type="primary"
                @click="updateStatus(order.id, 2)"
            >
              确认发货
            </van-button>
            <van-button
                v-if="order.status === 2"
                size="small"
                type="success"
                @click="updateStatus(order.id, 3)"
            >
              确认收货
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-list" v-else-if="!loading">
      <van-empty description="暂无订单" />
      <van-button type="primary" round @click="loadOrders">刷新</van-button>
    </div>

    <!-- 订单详情弹窗 -->
    <van-dialog v-model:show="showDetailDialog" title="订单详情" width="90%">
      <div class="order-detail" v-if="currentOrder">
        <div class="detail-item">
          <span class="label">订单号：</span>
          <span>{{ currentOrder.orderNo }}</span>
        </div>
        <div class="detail-item">
          <span class="label">订单状态：</span>
          <span :class="getStatusClass(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">下单时间：</span>
          <span>{{ formatTime(currentOrder.createdAt) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">商品总额：</span>
          <span>¥{{ currentOrder.totalAmount }}</span>
        </div>
        <div class="detail-item">
          <span class="label">运费：</span>
          <span>¥{{ currentOrder.deliveryFee || 0 }}</span>
        </div>
        <div class="detail-item">
          <span class="label">实付金额：</span>
          <span class="amount">¥{{ currentOrder.finalAmount }}</span>
        </div>
        <div class="detail-item">
          <span class="label">支付方式：</span>
          <span>{{ currentOrder.paymentMethod || '未支付' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">备注：</span>
          <span>{{ currentOrder.remark || '无' }}</span>
        </div>

        <div class="detail-goods" v-if="currentOrder.items && currentOrder.items.length">
          <div class="section-title">商品清单</div>
          <div v-for="item in currentOrder.items" :key="item.id" class="goods-item">
            <img :src="item.mainImage || item.productImage" />
            <div class="goods-info">
              <div class="goods-name">{{ item.productName }}</div>
              <div class="goods-price">¥{{ item.price }} × {{ item.quantity }}</div>
            </div>
          </div>
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import axios from 'axios'

const orderList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const activeStatus = ref('all')
const showDetailDialog = ref(false)
const currentOrder = ref(null)

const statusTabs = [
  { name: '全部', value: 'all' },
  { name: '待付款', value: '0' },
  { name: '已付款', value: '1' },
  { name: '已发货', value: '2' },
  { name: '已完成', value: '3' },
  { name: '已取消', value: '4' }
]

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const getStatusText = (status) => {
  const map = { 0: '待付款', 1: '已付款', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = { 0: 'status-pending', 1: 'status-paid', 2: 'status-shipping', 3: 'status-completed', 4: 'status-cancelled' }
  return map[status] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')} ${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`
}

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    let url = '/api/admin/orders'
    const params = []
    if (searchKeyword.value) {
      params.push(`keyword=${encodeURIComponent(searchKeyword.value)}`)
    }
    if (activeStatus.value !== 'all') {
      params.push(`status=${activeStatus.value}`)
    }
    if (params.length) {
      url += '?' + params.join('&')
    }
    const res = await axios.get(url, getHeaders())
    if (res.data.code === 200) {
      orderList.value = res.data.data
    }
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 按状态筛选
const filterByStatus = (status) => {
  activeStatus.value = status
  loadOrders()
}

// 更新订单状态
const updateStatus = async (orderId, status) => {
  const statusText = getStatusText(status)
  try {
    await showConfirmDialog({ message: `确认将订单状态改为"${statusText}"？` })
    const res = await axios.put(`/api/admin/orders/${orderId}/status?status=${status}`, {}, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast(`状态已更新为${statusText}`)
      loadOrders()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('操作失败')
    }
  }
}

// 查看订单详情
const viewDetail = async (orderId) => {
  try {
    const res = await axios.get(`/api/admin/orders/${orderId}`, getHeaders())
    if (res.data.code === 200) {
      currentOrder.value = res.data.data
      // 如果没有订单项数据，可以从其他接口获取
      if (!currentOrder.value.items) {
        currentOrder.value.items = []
      }
      showDetailDialog.value = true
    }
  } catch (error) {
    showToast('加载详情失败')
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-admin {
  background: white;
  border-radius: 16px;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.page-header h3 {
  font-size: 16px;
  color: #2c3e50;
}

.search-bar {
  margin-bottom: 20px;
}

.status-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
  padding: 8px 0;
}

.status-tab {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  background: #f0f0f0;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.status-tab.active {
  background: #764ba2;
  color: white;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 16px;
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 14px;
  font-weight: bold;
  color: #2c3e50;
}

.order-time {
  font-size: 12px;
  color: #999;
  margin-left: 12px;
}

.order-status {
  font-size: 13px;
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 12px;
}

.status-pending { background: #fff3e0; color: #f39c12; }
.status-paid { background: #e3f2fd; color: #3498db; }
.status-shipping { background: #e8f5e9; color: #4caf50; }
.status-completed { background: #e8f5e9; color: #27ae60; }
.status-cancelled { background: #ffebee; color: #e74c3c; }

.order-goods {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.goods-preview {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goods-preview img {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
  background: #f0f0f0;
}

.goods-count {
  font-size: 12px;
  color: #666;
}

.order-amount .amount {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.order-user {
  font-size: 12px;
  color: #999;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  gap: 16px;
}

/* 订单详情弹窗 */
.order-detail {
  max-height: 500px;
  overflow-y: auto;
  padding: 16px;
}

.detail-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-item .label {
  width: 80px;
  color: #999;
}

.detail-item .amount {
  font-weight: bold;
  color: #e74c3c;
}

.detail-goods {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 12px;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.goods-item img {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 14px;
  font-weight: 500;
}

.goods-price {
  font-size: 12px;
  color: #999;
}
</style>