<template>
  <div class="history-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>识别历史</span>
      <span class="edit-btn" @click="toggleEdit">{{ isEdit ? '完成' : '编辑' }}</span>
    </div>

    <div class="history-list" v-if="historyList.length > 0">
      <div v-for="item in historyList" :key="item.id" class="history-card">
        <div class="history-image">
          <img :src="item.imageUrl || 'https://picsum.photos/80/80?random=' + item.id" alt="植物图片" />
        </div>
        <div class="history-info" @click="goToPlantDetail(item.plantId)">
          <div class="plant-name">{{ item.plantName }}</div>
          <div class="plant-confidence">匹配度：{{ (item.confidence * 100).toFixed(1) }}%</div>
          <div class="history-time">{{ formatTime(item.createdAt) }}</div>
        </div>
        <div class="history-delete" v-if="isEdit" @click="deleteHistory(item.id)">
          <van-icon name="delete-o" size="22" color="#e74c3c" />
        </div>
      </div>
    </div>

    <div class="empty-history" v-else>
      <van-empty description="暂无识别历史" />
      <van-button type="primary" round @click="goToIdentify">去识别植物</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const historyList = ref([])
const isEdit = ref(false)

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

// 加载历史记录
const loadHistory = async () => {
  try {
    const res = await axios.get('/api/history', getHeaders())
    if (res.data.code === 200) {
      historyList.value = res.data.data
    }
  } catch (error) {
    console.error('加载历史失败:', error)
    // 模拟数据
    historyList.value = [
      { id: 1, plantId: 3, plantName: '向日葵', confidence: 0.95, createdAt: new Date().toISOString(), imageUrl: '' },
      { id: 2, plantId: 1, plantName: '玫瑰', confidence: 0.89, createdAt: new Date(Date.now() - 86400000).toISOString(), imageUrl: '' },
      { id: 3, plantId: 4, plantName: '绿萝', confidence: 0.92, createdAt: new Date(Date.now() - 172800000).toISOString(), imageUrl: '' }
    ]
  }
}

// 删除历史
const deleteHistory = async (id) => {
  try {
    await axios.delete(`/api/history/${id}`, getHeaders())
    showSuccessToast('删除成功')
    loadHistory()
  } catch (error) {
    showToast('删除失败')
  }
}

// 切换编辑模式
const toggleEdit = () => {
  isEdit.value = !isEdit.value
}

// 跳转植物详情
const goToPlantDetail = (plantId) => {
  if (plantId) {
    router.push(`/plant/${plantId}`)
  }
}

// 跳转识别页
const goToIdentify = () => {
  router.push('/identify')
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

.edit-btn {
  color: #764ba2 !important;
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.history-image {
  width: 70px;
  height: 70px;
  border-radius: 12px;
  overflow: hidden;
  background: #f0f0f0;
  flex-shrink: 0;
}

.history-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.history-info {
  flex: 1;
  cursor: pointer;
}

.plant-name {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
}

.plant-confidence {
  font-size: 12px;
  color: #27ae60;
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