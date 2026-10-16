<template>
  <div class="history-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>浏览记录</span>
      <span class="clear-btn" v-if="historyList.length > 0" @click="clearHistory">清空</span>
    </div>

    <div class="history-list" v-if="historyList.length > 0">
      <div v-for="item in historyList" :key="item.id" class="history-card" @click="goToDetail(item)">
        <div class="history-image">
          <img :src="item.imageUrl || getPlaceholderImage()" />
        </div>
        <div class="history-info">
          <div class="history-title">{{ item.title }}</div>
          <div class="history-type">
            <van-tag :type="item.type === 'product' ? 'primary' : 'success'">
              {{ item.type === 'product' ? '商品' : '植物' }}
            </van-tag>
          </div>
          <div class="history-time">{{ formatTime(item.createdAt) }}</div>
        </div>
        <div class="history-delete" @click.stop="deleteHistory(item.id)">
          <van-icon name="delete-o" size="20" color="#e74c3c" />
        </div>
      </div>
    </div>

    <div class="empty-history" v-else>
      <van-empty description="暂无浏览记录" />
      <van-button type="primary" round @click="goToShop">去逛逛</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import axios from 'axios'

const router = useRouter()
const historyList = ref([])

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    return `今天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return `${date.getMonth() + 1}/${date.getDate()}`
  }
}

const getPlaceholderImage = () => {
  return 'https://picsum.photos/80/80'
}

const loadHistory = async () => {
  try {
    const res = await axios.get('/api/browse-history', getHeaders())
    if (res.data.code === 200) {
      historyList.value = res.data.data
    }
  } catch (error) {
    console.error('加载失败', error)
    showToast('加载失败')
  }
}

const deleteHistory = async (id) => {
  try {
    await axios.delete(`/api/browse-history/${id}`, getHeaders())
    showSuccessToast('删除成功')
    loadHistory()
  } catch (error) {
    showToast('删除失败')
  }
}

const clearHistory = async () => {
  try {
    await showConfirmDialog({ message: '确认清空所有浏览记录？' })
    await axios.delete('/api/browse-history/clear', getHeaders())
    showSuccessToast('清空成功')
    loadHistory()
  } catch (error) {
    if (error !== 'cancel') {
      showToast('清空失败')
    }
  }
}

const goToDetail = (item) => {
  if (item.type === 'product' && item.productId) {
    router.push(`/product/${item.productId}`)
  } else if (item.type === 'plant' && item.plantId) {
    router.push(`/plant/${item.plantId}`)
  }
}

const goToShop = () => {
  router.push('/shop')
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.history-container {
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
}

.header span {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

.clear-btn {
  color: #e74c3c !important;
  font-size: 14px !important;
  cursor: pointer;
}

.history-list {
  padding: 12px;
}

.history-card {
  background: white;
  border-radius: 16px;
  margin-bottom: 12px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  cursor: pointer;
}

.history-image {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.history-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.history-info {
  flex: 1;
}

.history-title {
  font-size: 15px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 6px;
}

.history-type {
  margin-bottom: 4px;
}

.history-time {
  font-size: 11px;
  color: #999;
}

.history-delete {
  padding: 8px;
  cursor: pointer;
}

.empty-history {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}
</style>