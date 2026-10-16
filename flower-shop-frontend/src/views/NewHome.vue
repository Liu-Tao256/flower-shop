<template>
  <div class="home-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search
          v-model="searchKeyword"
          placeholder="搜索植物或商品"
          shape="round"
          background="#f5f7fa"
      />
    </div>

    <!-- 轮播图 -->
    <van-swipe class="banner-swipe" :autoplay="3000" indicator-color="#764ba2">
      <van-swipe-item v-for="(banner, index) in banners" :key="index">
        <div class="banner-item" :style="{ backgroundImage: 'url(' + banner.image + ')' }">
          <div class="banner-text">
            <h3>{{ banner.title }}</h3>
            <p>{{ banner.subtitle }}</p>
          </div>
        </div>
      </van-swipe-item>
    </van-swipe>

    <!-- 快捷入口 -->
    <div class="quick-entry">
      <div class="entry-item" @click="goToIdentify">
        <div class="entry-icon">📸</div>
        <span>植物识别</span>
      </div>
      <div class="entry-item" @click="goToShop">
        <div class="entry-icon">🛍️</div>
        <span>花店商城</span>
      </div>
      <div class="entry-item" @click="goToGallery">
        <div class="entry-icon">📚</div>
        <span>植物图鉴</span>
      </div>

    </div>

    <!-- 热门植物TOP榜 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">🔥 热门植物榜</span>
        <span class="section-more" @click="goToRank">查看更多 ></span>
      </div>
      <div class="rank-tabs">
        <span
            v-for="tab in rankTabs"
            :key="tab.value"
            :class="['rank-tab', { active: rankPeriod === tab.value }]"
            @click="switchRankPeriod(tab.value)"
        >
          {{ tab.name }}
        </span>
      </div>
      <div class="rank-list">
        <div v-for="(plant, index) in topPlants" :key="plant.id" class="rank-item" @click="goToPlantDetail(plant.id)">
          <div class="rank-number" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
          <div class="rank-info">
            <div class="rank-name">{{ plant.name }}</div>
            <div class="rank-count">{{ plant.viewCount || 0 }} 次浏览</div>
          </div>
          <div class="rank-tag" :class="getTypeClass(plant.type)">
            {{ getTypeName(plant.type) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 智能科普与资讯 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">📰 花店资讯</span>
        <span class="section-more" @click="goToNews">更多 ></span>
      </div>
      <div class="news-list">
        <div v-for="news in newsList" :key="news.id" class="news-card" @click="viewNews(news)">
          <div class="news-icon">{{ news.icon }}</div>
          <div class="news-info">
            <div class="news-title">{{ news.title }}</div>
            <div class="news-category">
              <van-tag :type="getNewsTypeColor(news.category)" size="small">{{ news.category }}</van-tag>
            </div>
            <div class="news-summary">{{ news.content.substring(0, 40) }}...</div>
          </div>
          <van-icon name="arrow" />
        </div>
      </div>
    </div>



    <!-- 底部导航栏 -->
    <van-tabbar v-model="activeTab" @change="onTabChange">
      <van-tabbar-item icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item icon="search">图鉴</van-tabbar-item>
      <van-tabbar-item icon="scan">识别</van-tabbar-item>
      <van-tabbar-item icon="shopping-cart-o">购物车</van-tabbar-item>
      <van-tabbar-item icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const activeTab = ref(0)
const searchKeyword = ref('')
const rankPeriod = ref('week')
const topPlants = ref([])
const newsList = ref([])

const banners = ref([
  { title: '新品上市', subtitle: '夏日限定花束', image: 'https://picsum.photos/id/16/375/150' },
  { title: '限时优惠', subtitle: '全场满100减20', image: 'https://picsum.photos/id/108/375/150' },
  { title: '会员专享', subtitle: '双倍积分活动', image: 'https://picsum.photos/id/29/375/150' }
])

const rankTabs = [
  { name: '本周', value: 'week' },
  { name: '本月', value: 'month' },
  { name: '节日', value: 'festival' }
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

const getNewsTypeColor = (category) => {
  const map = { '养护知识': 'success', '节日搭配': 'primary', '花店资讯': 'warning' }
  return map[category] || 'default'
}

// 加载热门植物榜
const loadTopPlants = async () => {
  try {
    const res = await axios.get(`/api/plant/top-rank?period=${rankPeriod.value}`)
    if (res.data.code === 200) {
      topPlants.value = res.data.data
    } else {
      showToast(res.data.message || '加载失败')
    }
  } catch (error) {
    console.error('加载热门植物失败:', error)
    showToast('加载失败')
  }
}

// 加载资讯
const loadNews = async () => {
  try {
    const res = await axios.get('/api/news')
    if (res.data.code === 200) {
      newsList.value = res.data.data
    }
  } catch (error) {
    console.error('加载资讯失败:', error)
    showToast('加载失败')
  }
}


const switchRankPeriod = (period) => {
  rankPeriod.value = period
  loadTopPlants()
}

const goToIdentify = () => {
  router.push('/identify')
}

const goToShop = () => {
  router.push('/shop')
}

const goToGallery = () => {
  router.push('/gallery')
}

const goToRank = () => {
  router.push('/rank')
}

const goToPlantDetail = (id) => {
  router.push(`/plant/${id}`)
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

const viewNews = (news) => {
  showToast(news.title)
}

const goToNews = () => {
  showToast('更多资讯')
}

const onTabChange = (index) => {
  const routes = ['/', '/gallery', '/identify', '/cart', '/profile']
  router.push(routes[index])
}

onMounted(() => {
  loadTopPlants()
  loadNews()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 60px;
}

.search-bar {
  background: #f5f7fa;
  padding: 8px 0;
}

.banner-swipe {
  height: 150px;
  margin: 0 12px;
  border-radius: 12px;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-text {
  text-align: center;
  color: white;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}

.banner-text h3 {
  font-size: 20px;
  margin-bottom: 4px;
}

.banner-text p {
  font-size: 12px;
  opacity: 0.9;
}

.quick-entry {
  display: flex;
  justify-content: space-around;
  background: white;
  margin: 12px;
  padding: 16px 0;
  border-radius: 16px;
}

.entry-item {
  text-align: center;
  cursor: pointer;
}

.entry-icon {
  font-size: 28px;
  margin-bottom: 6px;
}

.entry-item span {
  font-size: 12px;
  color: #666;
}

.section {
  background: white;
  margin: 12px;
  border-radius: 16px;
  padding: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
}

.section-more {
  font-size: 12px;
  color: #999;
  cursor: pointer;
}

.rank-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.rank-tab {
  font-size: 13px;
  color: #999;
  cursor: pointer;
  padding: 4px 0;
}

.rank-tab.active {
  color: #764ba2;
  border-bottom: 2px solid #764ba2;
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 4px 0;
}

.rank-number {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: bold;
}

.rank-number.top-three {
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  color: white;
}

.rank-info {
  flex: 1;
}

.rank-name {
  font-size: 14px;
  font-weight: 500;
}

.rank-count {
  font-size: 11px;
  color: #999;
}

.rank-tag {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
}

.type-flower { background: #ffe0e0; color: #e74c3c; }
.type-green { background: #e0ffe0; color: #27ae60; }
.type-succulent { background: #fff0e0; color: #f39c12; }

.news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.news-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
}

.news-icon {
  font-size: 32px;
}

.news-info {
  flex: 1;
}

.news-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
}

.news-category {
  margin-bottom: 4px;
}

.news-summary {
  font-size: 11px;
  color: #999;
}

.product-scroll {
  display: flex;
  overflow-x: auto;
  gap: 12px;
  padding: 4px 0;
}

.product-card {
  width: 100px;
  flex-shrink: 0;
  text-align: center;
  cursor: pointer;
}

.product-card img {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  object-fit: cover;
}

.product-name {
  font-size: 12px;
  margin-top: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 12px;
  color: #e74c3c;
  font-weight: bold;
}
</style>