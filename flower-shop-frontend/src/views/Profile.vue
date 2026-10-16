<template>
  <div class="profile-container">
    <!-- 顶部背景 -->
    <div class="header-bg">
      <div class="wave"></div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card">

      <div class="avatar-wrapper" @click="changeAvatar">
        <van-image
            round
            :src="userInfo.avatar || 'https://img.icons8.com/color/96/000000/user.png'"
            width="80"
            height="80"
            class="avatar"
        />
        <div class="avatar-edit">
          <van-icon name="photograph" size="16" color="white" />
        </div>
      </div>

      <div class="user-name">{{ userInfo.nickname || '花友' }}</div>
      <div class="user-phone">{{ userInfo.phone || '未绑定手机' }}</div>
      <div class="user-level">
        <span class="level-tag">🌿 初级花友</span>
      </div>
      <div class="refresh-icon" @click="refreshData">
        <van-icon name="replay" size="20" color="#764ba2" />
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-card">
      <div class="stat-item" @click="goToPoints">
        <div class="stat-value">{{ userInfo.points || 0 }}</div>
        <div class="stat-label">积分</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item" @click="goToCheckin">
        <div class="stat-value">{{ checkinCount }}</div>
        <div class="stat-label">打卡次数</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item" @click="goToPlants">
        <div class="stat-value">{{ plantCount }}</div>
        <div class="stat-label">识别植物</div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-list">
      <div class="menu-item" @click="goToShop">
        <div class="menu-icon">🛍️</div>
        <div class="menu-info">
          <div class="menu-title">花店商城</div>
          <div class="menu-desc">浏览购买鲜花绿植</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <div class="menu-item" @click="goToOrders">
        <div class="menu-icon">📦</div>
        <div class="menu-info">
          <div class="menu-title">我的订单</div>
          <div class="menu-desc">查看全部订单</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <div class="menu-item" @click="goToAddress">
        <div class="menu-icon">📍</div>
        <div class="menu-info">
          <div class="menu-title">收货地址</div>
          <div class="menu-desc">管理收货地址</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <!-- 浏览记录 -->
      <div class="menu-item" @click="goToBrowseHistory">
        <div class="menu-icon">🕐</div>
        <div class="menu-info">
          <div class="menu-title">浏览记录</div>
          <div class="menu-desc">查看浏览过的商品和植物</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <div class="menu-item" @click="goToTasks">
        <div class="menu-icon">🎯</div>
        <div class="menu-info">
          <div class="menu-title">成长任务</div>
          <div class="menu-desc">完成任务获得积分奖励</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <div class="menu-item" @click="showPasswordDialog = true">
        <div class="menu-icon">🔐</div>
        <div class="menu-info">
          <div class="menu-title">修改密码</div>
          <div class="menu-desc">定期更换密码更安全</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <div class="menu-item" @click="goToHistory">
        <div class="menu-icon">📜</div>
        <div class="menu-info">
          <div class="menu-title">识别历史</div>
          <div class="menu-desc">查看历史识别记录</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <div class="menu-item" @click="goToPassport">
        <div class="menu-icon">📘</div>
        <div class="menu-info">
          <div class="menu-title">植物护照</div>
          <div class="menu-desc">我的植物图鉴</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <div class="menu-item" @click="goToPointsMall">
        <div class="menu-icon">🏪</div>
        <div class="menu-info">
          <div class="menu-title">积分商城</div>
          <div class="menu-desc">积分兑换好礼</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <div class="menu-item" @click="goToCoupon">
        <div class="menu-icon">🎫</div>
        <div class="menu-info">
          <div class="menu-title">优惠券</div>
          <div class="menu-desc">领取更多优惠</div>
        </div>
        <van-icon name="arrow" />
      </div>

      <!-- 管理后台入口（仅管理员可见） -->
      <div class="menu-item" v-if="userInfo.role === 'admin'" @click="goToAdmin">
        <div class="menu-icon">⚙️</div>
        <div class="menu-info">
          <div class="menu-title">管理后台</div>
          <div class="menu-desc">商品、订单、植物管理</div>
        </div>
        <van-icon name="arrow" />
      </div>
    </div>

    <!-- 退出登录按钮 -->
    <div class="logout-wrapper">
      <van-button round block class="logout-btn" @click="handleLogout">
        退出登录
      </van-button>
    </div>

    <!-- 修改密码弹窗 -->
    <van-dialog v-model:show="showPasswordDialog" title="修改密码" show-cancel-button @confirm="changePassword">
      <van-field
          v-model="oldPassword"
          type="password"
          label="原密码"
          placeholder="请输入原密码"
      />
      <van-field
          v-model="newPassword"
          type="password"
          label="新密码"
          placeholder="请输入新密码"
      />
      <van-field
          v-model="confirmPassword"
          type="password"
          label="确认密码"
          placeholder="请再次输入新密码"
      />
    </van-dialog>

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
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const activeTab = ref(4)

const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const checkinCount = ref(0)
const plantCount = ref(8)

const showPasswordDialog = ref(false)
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const getUserInfo = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return
    const res = await axios.get('/api/user/info', getHeaders())
    if (res.data.code === 200) {
      userInfo.value = res.data.data
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  } catch (error) {
    console.error('获取用户信息失败')
  }
}

const loadCheckinStatus = async () => {
  try {
    const res = await axios.get('/api/checkin/status', getHeaders())
    if (res.data.code === 200) {
      checkinCount.value = res.data.data.totalCount || 0
    }
  } catch (error) {
    console.error('加载打卡状态失败', error)
  }
}

const refreshData = async () => {
  await getUserInfo()
  await loadCheckinStatus()
  showToast('刷新成功')
}

const changePassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    showToast('两次输入的密码不一致')
    return
  }
  try {
    const token = localStorage.getItem('token')
    const res = await axios.put('/api/user/password', {
      oldPassword: oldPassword.value,
      newPassword: newPassword.value
    }, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('修改成功，请重新登录')
      setTimeout(() => {
        handleLogout()
      }, 1500)
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    showToast('修改失败')
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

const loadIdentifiedCount = async () => {
  try {
    const res = await axios.get('/api/checkin/plants', getHeaders())
    if (res.data.code === 200) {
      plantCount.value = res.data.data.length
    }
  } catch (error) {
    console.error('加载识别植物数量失败', error)
  }
}

// 跳转函数
const goToShop = () => {
  router.push('/shop')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToAddress = () => {
  router.push('/address')
}

const goToBrowseHistory = () => {
  router.push('/browse-history')
}

const goToHistory = () => {
  router.push('/history')
}

const goToPassport = () => {
  router.push('/passport')
}



const goToPoints = () => {
  router.push('/points-mall')
}

const goToCheckin = async () => {
  await loadCheckinStatus()
  await getUserInfo()
  showToast('已刷新')
}

const goToPlants = () => {
  router.push('/history')
}

const changeAvatar = () => {
  showToast('头像功能开发中')
}

const goToAdmin = () => {
  router.push('/admin/dashboard')
}

const goToCoupon = () => {
  router.push('/coupon')
}

const goToPointsMall = () => {
  router.push('/points-mall')
}

const onTabChange = (index) => {
  const routes = ['/home', '/gallery', '/identify', '/cart', '/profile']
  router.push(routes[index])
}

const goToTasks = () => {
  router.push('/tasks')
}

onMounted(() => {
  getUserInfo()
  loadCheckinStatus()
  loadIdentifiedCount()
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 70px;
}

.header-bg {
  position: relative;
  height: 180px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 0 0 30px 30px;
  overflow: hidden;
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 50px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 320'%3E%3Cpath fill='%23f5f7fa' fill-opacity='1' d='M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,122.7C672,117,768,139,864,149.3C960,160,1056,160,1152,138.7C1248,117,1344,75,1392,53.3L1440,32L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z'%3E%3C/path%3E%3C/svg%3E");
  background-size: cover;
}

.user-card {
  background: white;
  margin: -60px 16px 16px;
  padding: 20px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.avatar {
  border: 3px solid white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #764ba2;
  border-radius: 50%;
  padding: 4px;
  border: 2px solid white;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  margin: 12px 0 4px;
}

.user-phone {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.user-level {
  margin-top: 8px;
}

.level-tag {
  display: inline-block;
  background: linear-gradient(135deg, #f093fb, #667eea);
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 12px;
  color: white;
}

.refresh-icon {
  position: absolute;
  top: 16px;
  right: 16px;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.05);
  transition: background 0.2s;
}

.refresh-icon:active {
  background: rgba(0, 0, 0, 0.1);
}

.stats-card {
  background: white;
  margin: 0 16px 16px;
  padding: 20px;
  border-radius: 16px;
  display: flex;
  justify-content: space-around;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item {
  flex: 1;
  cursor: pointer;
}

.stat-value {
  font-size: 24px;
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

.menu-list {
  background: white;
  margin: 0 16px 16px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:active {
  background: #f8f9fa;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 28px;
  margin-right: 12px;
}

.menu-info {
  flex: 1;
}

.menu-title {
  font-size: 15px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 4px;
}

.menu-desc {
  font-size: 11px;
  color: #aaa;
}

.logout-wrapper {
  margin: 20px 16px 30px;
}

.logout-btn {
  height: 48px;
  background: white;
  border: 1px solid #e0e0e0;
  color: #e74c3c;
  font-size: 16px;
  transition: all 0.2s;
}

.logout-btn:active {
  background: #fff5f5;
}

:deep(.van-tabbar) {
  border-top: 1px solid #f0f0f0;
}

:deep(.van-tabbar-item--active) {
  color: #764ba2;
}

:deep(.van-dialog) {
  border-radius: 20px;
}

:deep(.van-field) {
  padding: 10px 16px;
}
</style>