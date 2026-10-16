<template>
  <div class="passport-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>植物护照</span>
    </div>

    <div class="stats-card">
      <div class="stat-item">
        <div class="stat-value">{{ totalCheckinCount }}</div>
        <div class="stat-label">总打卡次数</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-value">{{ plantList.length }}</div>
        <div class="stat-label">打卡植物种类</div>
      </div>
    </div>

    <div class="plant-list" v-if="plantList.length > 0">
      <div v-for="plant in plantList" :key="plant.plant_id" class="plant-card" @click="goToPlantDetail(plant.plant_id)">
        <div class="plant-icon">🌿</div>
        <div class="plant-info">
          <div class="plant-name">{{ plant.plant_name }}</div>
          <div class="plant-checkin-count">打卡 {{ plant.checkin_count }} 次</div>
          <div class="plant-time">最近打卡：{{ formatTime(plant.last_checkin) }}</div>
        </div>
        <van-icon name="arrow" />
      </div>
    </div>

    <div class="empty-passport" v-else>
      <van-empty description="暂无打卡记录" />
      <van-button type="primary" round @click="goToIdentify">去识别植物</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const plantList = ref([])
const totalCheckinCount = ref(0)

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
}

const loadPassport = async () => {
  try {
    const res = await axios.get('/api/checkin/plants', getHeaders())
    console.log('API返回数据:', res.data)
    if (res.data.code === 200) {
      plantList.value = res.data.data || []
      totalCheckinCount.value = plantList.value.reduce((sum, p) => sum + (p.checkin_count || 0), 0)
    }
  } catch (error) {
    console.error('加载失败', error)
    showToast('加载失败')
  }
}

const goToPlantDetail = (plantId) => {
  if (plantId) {
    router.push(`/plant/${plantId}`)
  }
}

const goToIdentify = () => {
  router.push('/identify')
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadPassport()
})
</script>

<style scoped>
.passport-container {
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

.stats-card {
  background: white;
  margin: 16px;
  padding: 20px;
  border-radius: 16px;
  display: flex;
  justify-content: space-around;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #764ba2;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  background: #e0e0e0;
}

.plant-list {
  padding: 12px;
}

.plant-card {
  background: white;
  border-radius: 16px;
  margin-bottom: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.plant-icon {
  font-size: 40px;
}

.plant-info {
  flex: 1;
}

.plant-name {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
}

.plant-checkin-count {
  font-size: 12px;
  color: #27ae60;
  margin-bottom: 2px;
}

.plant-time {
  font-size: 11px;
  color: #999;
}

.empty-passport {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}
</style>