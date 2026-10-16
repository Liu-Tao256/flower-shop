<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalProducts }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalOrders }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">👤</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-info">
          <div class="stat-value">¥{{ stats.totalSales }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </div>
    </div>

    <div class="chart-container">
      <div ref="chartRef" class="chart"></div>
    </div>

    <div class="recent-orders">
      <h3>近期订单</h3>
      <div class="order-list">
        <div v-for="order in stats.recentOrders" :key="order.id" class="order-item">
          <div class="order-info">
            <span class="order-no">{{ order.orderNo }}</span>
            <span class="order-amount">¥{{ order.finalAmount }}</span>
          </div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </div>
        </div>
        <div v-if="!stats.recentOrders || stats.recentOrders.length === 0" class="empty">
          暂无订单
        </div>
      </div>
    </div>

    <div class="top-plants">
      <h3>热门植物榜</h3>
      <div class="plant-list">
        <div v-for="(plant, index) in stats.topPlants" :key="plant.id" class="plant-item">
          <div class="plant-rank">{{ index + 1 }}</div>
          <div class="plant-name">{{ plant.name }}</div>
          <div class="plant-count">{{ plant.viewCount || 0 }} 次浏览</div>
        </div>
        <div v-if="!stats.topPlants || stats.topPlants.length === 0" class="empty">
          暂无数据
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const stats = ref({
  totalProducts: 0,
  totalOrders: 0,
  totalUsers: 0,
  totalSales: 0,
  recentOrders: [],
  topPlants: []
})

const chartRef = ref(null)
let chart = null

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
  const map = { 0: 'status-pending', 1: 'status-paid', 2: 'status-shipping', 3: 'status-completed' }
  return map[status] || ''
}

// 加载数据看板
const loadDashboard = async () => {
  try {
    const res = await axios.get('/api/admin/dashboard', getHeaders())
    if (res.data.code === 200) {
      stats.value = res.data.data
      console.log('数据加载成功:', stats.value)
    }
  } catch (error) {
    console.error('加载失败:', error)
  }
}

// 加载订单趋势
const loadOrderTrend = async () => {
  try {
    const res = await axios.get('/api/admin/order-trend', getHeaders())
    if (res.data.code === 200) {
      const data = res.data.data
      console.log('订单趋势数据:', data)
      initChart(data.days, data.counts)
    }
  } catch (error) {
    console.error('加载订单趋势失败:', error)
    initChart(['6/3', '6/4', '6/5', '6/6', '6/7', '6/8', '6/9'], [5, 8, 12, 9, 15, 18, 22])
  }
}

// 初始化图表
const initChart = (days, counts) => {
  if (!chartRef.value) {
    console.log('chartRef 未就绪')
    return
  }
  if (chart) chart.dispose()

  chart = echarts.init(chartRef.value)
  chart.setOption({
    title: { text: '近7天订单趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: days, name: '日期' },
    yAxis: { type: 'value', name: '订单数' },
    series: [{
      data: counts,
      type: 'line',
      smooth: true,
      lineStyle: { color: '#764ba2', width: 3 },
      areaStyle: { opacity: 0.3, color: '#764ba2' },
      symbol: 'circle',
      symbolSize: 8,
      itemStyle: { color: '#764ba2' }
    }]
  })
  window.addEventListener('resize', () => chart && chart.resize())
}

onMounted(() => {
  loadDashboard()
  loadOrderTrend()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.stat-icon {
  font-size: 40px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #764ba2;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.chart-container {
  background: white;
  padding: 16px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.chart {
  height: 300px;
  width: 100%;
}

.recent-orders, .top-plants {
  background: white;
  padding: 16px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #764ba2;
  display: inline-block;
}

.order-item, .plant-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 13px;
  color: #666;
}

.order-amount {
  font-weight: bold;
  color: #e74c3c;
  margin-left: 16px;
}

.order-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
}

.status-pending { background: #fff3e0; color: #f39c12; }
.status-paid { background: #e3f2fd; color: #3498db; }
.status-shipping { background: #e8f5e9; color: #4caf50; }
.status-completed { background: #e8f5e9; color: #27ae60; }

.plant-rank {
  width: 30px;
  height: 30px;
  background: #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.plant-name {
  flex: 1;
  margin-left: 12px;
}

.plant-count {
  font-size: 12px;
  color: #999;
}

.empty {
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>