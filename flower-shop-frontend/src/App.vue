<template>
  <div id="app">
    <router-view />

    <!-- 底部导航栏（仅在需要显示的页面显示） -->
    <van-tabbar v-if="showTabbar" v-model="activeTab" @change="onTabChange">
      <van-tabbar-item icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item icon="search">图鉴</van-tabbar-item>
      <van-tabbar-item icon="scan">识别</van-tabbar-item>
      <van-tabbar-item icon="shopping-cart-o">购物车</van-tabbar-item>
      <van-tabbar-item icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const activeTab = ref(0)

// 不需要显示底部导航栏的页面
const hideTabbarPages = ['/login', '/register', '/identify']

const showTabbar = computed(() => {
  return !hideTabbarPages.includes(route.path)
})

const onTabChange = (index) => {
  const routes = ['/home', '/gallery', '/identify', '/cart', '/profile']
  router.push(routes[index])
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
}

#app {
  padding-bottom: 50px;
}

.van-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}
</style>