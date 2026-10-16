<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <span v-if="!isCollapsed">🌸 花店后台</span>
        <span v-else>🌸</span>
      </div>

      <div class="menu">
        <div
            v-for="item in menuItems"
            :key="item.path"
            class="menu-item"
            :class="{ active: currentPath === item.path }"
            @click="goToPage(item.path)"
        >
          <span class="menu-icon">{{ item.icon }}</span>
          <span class="menu-title" v-if="!isCollapsed">{{ item.title }}</span>
        </div>
      </div>

      <div class="sidebar-footer">
        <div class="menu-item" @click="toggleCollapse">
          <span class="menu-icon">📐</span>
          <span class="menu-title" v-if="!isCollapsed">{{ isCollapsed ? '展开' : '收起' }}</span>
        </div>
        <div class="menu-item" @click="logout">
          <span class="menu-icon">🚪</span>
          <span class="menu-title" v-if="!isCollapsed">退出登录</span>
        </div>
      </div>
    </div>

    <!-- 右侧内容区 -->
    <div class="main-content">
      <div class="header">
        <div class="header-title">{{ currentTitle }}</div>
        <div class="header-user">
          <span>{{ adminName }}</span>
          <van-icon name="manager-o" size="20" />
        </div>
      </div>
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isCollapsed = ref(false)

const menuItems = [
  { path: '/admin/dashboard', title: '数据看板', icon: '📊' },
  { path: '/admin/products', title: '商品管理', icon: '📦' },
  { path: '/admin/plants', title: '植物管理', icon: '🌿' },
  { path: '/admin/orders', title: '订单管理', icon: '📋' },
  { path: '/admin/tasks', title: '任务管理', icon: '🎯' },
  { path: '/admin/points-products', title: '积分商品管理', icon: '🎁' },
  { path: '/admin/coupons', title: '优惠券管理', icon: '🎟️' },
]

const currentPath = computed(() => route.path)
const currentTitle = computed(() => {
  const item = menuItems.find(i => i.path === route.path)
  return item ? item.title : '管理后台'
})

const adminName = ref('管理员')

const goToPage = (path) => {
  router.push(path)
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  adminName.value = userInfo.nickname || '管理员'
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #2c3e50 0%, #1a252f 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
}

.sidebar.collapsed {
  width: 70px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  background: rgba(0,0,0,0.2);
}

.menu {
  flex: 1;
  padding: 20px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
  color: rgba(255,255,255,0.7);
}

.menu-item:hover {
  background: rgba(255,255,255,0.1);
  color: white;
}

.menu-item.active {
  background: #764ba2;
  color: white;
  border-left: 3px solid #ffd700;
}

.menu-icon {
  font-size: 20px;
  width: 30px;
  text-align: center;
}

.menu-title {
  margin-left: 12px;
  font-size: 14px;
}

.sidebar.collapsed .menu-title {
  display: none;
}

.sidebar-footer {
  border-top: 1px solid rgba(255,255,255,0.1);
  padding: 10px 0;
}

/* 右侧内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background: white;
  padding: 15px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.content {
  padding: 20px;
  flex: 1;
}
</style>