<template>
  <div class="gallery-container">
    <!-- 顶部导航 -->
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>植物图鉴</span>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search
          v-model="keyword"
          placeholder="搜索植物名称"
          shape="round"
          background="#f5f7fa"
          @search="onSearch"
          @clear="onSearch"
      />
    </div>

    <!-- 分类筛选 -->
    <div class="category-tabs">
      <div
          v-for="cat in categories"
          :key="cat.value"
          :class="['category-item', { active: activeType === cat.value }]"
          @click="switchCategory(cat.value)"
      >
        <span class="cat-icon">{{ cat.icon }}</span>
        <span class="cat-name">{{ cat.name }}</span>
      </div>
    </div>

    <!-- 植物列表 -->
    <div class="plant-list" v-if="plantList.length > 0">
      <div
          v-for="plant in plantList"
          :key="plant.id"
          class="plant-card"
          @click="goToDetail(plant.id)"
      >
        <div class="plant-image">
          <img :src="plant.imageUrl || getPlaceholderImage(plant.id)" :alt="plant.name" />
          <div class="plant-type" :class="getTypeClass(plant.type)">
            {{ getTypeName(plant.type) }}
          </div>
        </div>
        <div class="plant-info">
          <h3 class="plant-name">{{ plant.name }}</h3>
          <p class="plant-family" v-if="plant.family">{{ plant.family }} / {{ plant.genus }}</p>
          <p class="plant-desc">{{ plant.characteristic ? plant.characteristic.substring(0, 30) + '...' : '暂无描述' }}</p>
          <div class="plant-stats">
            <span><van-icon name="eye-o" /> {{ plant.viewCount || 0 }} 浏览</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="loading-wrapper" v-if="loading">
      <van-loading type="spinner" size="40" />
    </div>

    <!-- 空状态 -->
    <div class="empty-wrapper" v-else-if="plantList.length === 0">
      <van-empty description="暂无植物数据" />
      <van-button type="primary" round @click="loadPlants">刷新</van-button>
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
const loading = ref(false)
const keyword = ref('')
const activeType = ref('')

const categories = [
  { name: '全部', value: '', icon: '🌿' },
  { name: '鲜花', value: 'flower', icon: '🌸' },
  { name: '绿植', value: 'green', icon: '🍃' },
  { name: '多肉', value: 'succulent', icon: '🌵' }
]

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const getTypeName = (type) => {
  const map = { flower: '鲜花', green: '绿植', succulent: '多肉' }
  return map[type] || '植物'
}

const getTypeClass = (type) => {
  const map = { flower: 'type-flower', green: 'type-green', succulent: 'type-succulent' }
  return map[type] || ''
}

const getPlaceholderImage = (id) => {
  return `https://picsum.photos/id/${100 + id}/300/200`
}

// 加载植物列表
const loadPlants = async () => {
  loading.value = true
  try {
    let url = '/api/plant/gallery'
    const params = []
    if (activeType.value) {
      params.push(`type=${activeType.value}`)
    }
    if (keyword.value) {
      params.push(`keyword=${encodeURIComponent(keyword.value)}`)
    }
    if (params.length > 0) {
      url += '?' + params.join('&')
    }

    const res = await axios.get(url, getHeaders())
    if (res.data.code === 200) {
      plantList.value = res.data.data
    } else {
      showToast(res.data.message || '加载失败')
    }
  } catch (error) {
    console.error('加载失败:', error)
    showToast('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 切换分类
const switchCategory = (type) => {
  activeType.value = type
  loadPlants()
}

// 搜索
const onSearch = () => {
  loadPlants()
}

// 跳转植物详情
const goToDetail = (id) => {
  router.push(`/plant/${id}`)
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadPlants()
})
</script>

<style scoped>
.gallery-container {
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
  position: sticky;
  top: 0;
  z-index: 10;
}

.header span {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

.search-bar {
  background: #f5f7fa;
  padding: 8px 0;
}

.category-tabs {
  background: white;
  padding: 8px 16px;
  display: flex;
  gap: 16px;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 60px;
  z-index: 9;
}

.category-item {
  flex: 1;
  text-align: center;
  padding: 8px 0;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 2px 8px rgba(118, 75, 162, 0.3);
}

.cat-icon {
  font-size: 20px;
  display: block;
  margin-bottom: 4px;
}

.cat-name {
  font-size: 12px;
}

.plant-list {
  padding: 12px;
}

.plant-card {
  background: white;
  border-radius: 16px;
  margin-bottom: 16px;
  overflow: hidden;
  display: flex;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.2s;
}

.plant-card:active {
  transform: scale(0.98);
}

.plant-image {
  width: 120px;
  height: 120px;
  position: relative;
  flex-shrink: 0;
}

.plant-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.plant-type {
  position: absolute;
  bottom: 8px;
  left: 8px;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 10px;
  color: white;
}

.type-flower { background: #e74c3c; }
.type-green { background: #27ae60; }
.type-succulent { background: #f39c12; }

.plant-info {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.plant-name {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
}

.plant-family {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
}

.plant-desc {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.plant-stats {
  font-size: 11px;
  color: #aaa;
  display: flex;
  gap: 12px;
}

.loading-wrapper,
.empty-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  gap: 20px;
}
</style>