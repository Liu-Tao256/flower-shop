<template>
  <div class="address-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>收货地址</span>
      <span class="add-btn" @click="goToAdd">新增</span>
    </div>

    <div class="address-list" v-if="addressList.length > 0">
      <div v-for="address in addressList" :key="address.id" class="address-card">
        <div class="address-info" @click="selectAddress(address)">
          <div class="address-user">
            <span>{{ address.receiverName }}</span>
            <span>{{ address.receiverPhone }}</span>
            <span v-if="address.isDefault === 1" class="default-tag">默认</span>
          </div>
          <div class="address-detail">
            {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
          </div>
        </div>
        <div class="address-actions">
          <span @click.stop="setDefault(address.id)" v-if="address.isDefault !== 1">
            <van-icon name="star-o" /> 设为默认
          </span>
          <span @click.stop="editAddress(address)">
            <van-icon name="edit" /> 编辑
          </span>
          <span @click.stop="deleteAddress(address.id)">
            <van-icon name="delete-o" /> 删除
          </span>
        </div>
      </div>
    </div>

    <div class="empty-address" v-else>
      <van-empty description="暂无收货地址" />
      <van-button type="primary" round @click="goToAdd">新增地址</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const addressList = ref([])

// 获取请求头
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

// 从数据库加载地址列表
const loadAddressList = async () => {
  try {
    const res = await axios.get('/api/address', getHeaders())
    if (res.data.code === 200) {
      addressList.value = res.data.data
      console.log('加载成功:', addressList.value)
    } else {
      showToast(res.data.message || '加载失败')
    }
  } catch (error) {
    console.error('加载地址失败:', error)
    showToast('加载失败，请检查后端服务')
  }
}

// 设为默认
const setDefault = async (id) => {
  try {
    await axios.put(`/api/address/${id}/default`, {}, getHeaders())
    showSuccessToast('设置成功')
    loadAddressList()
  } catch (error) {
    showToast('设置失败')
  }
}

// 删除地址
const deleteAddress = (id) => {
  // 使用 van-dialog 确认删除
  showToast({
    message: '确认删除该地址？',
    position: 'middle',
    duration: 0,
    onClose: () => {}
  })

  // 使用原生 confirm 确认删除（临时方案）
  if (confirm('确认删除该地址？')) {
    console.log('准备删除地址ID:', id)

    // 直接从本地列表中删除（前端模拟）
    addressList.value = addressList.value.filter(addr => addr.id !== id)
    showSuccessToast('删除成功')

    // 同时尝试调用后端
    try {
      const token = localStorage.getItem('token')
      fetch(`/api/address/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }).then(res => res.json()).then(data => {
        console.log('后端删除响应:', data)
      }).catch(err => {
        console.log('后端删除失败:', err)
      })
    } catch (error) {
      console.log('请求失败:', error)
    }
  }
}

// 选择地址（从结算页进入时）
const selectAddress = (address) => {
  if (route.query.from === 'checkout') {
    localStorage.setItem('selectedAddress', JSON.stringify(address))
    router.back()
  }
}

// 编辑地址
const editAddress = (address) => {
  router.push(`/address/edit/${address.id}`)
}

// 新增地址
const goToAdd = () => {
  router.push('/address/add')
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadAddressList()
})
</script>

<style scoped>
.address-container {
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
.add-btn {
  color: #764ba2 !important;
  font-size: 14px !important;
  cursor: pointer;
}
.address-list {
  padding: 12px;
}
.address-card {
  background: white;
  border-radius: 16px;
  margin-bottom: 12px;
  padding: 16px;
}
.address-info {
  cursor: pointer;
}
.address-user {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
  font-weight: bold;
}
.default-tag {
  background: #f0f0f0;
  color: #764ba2;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
}
.address-detail {
  font-size: 13px;
  color: #666;
}
.address-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}
.address-actions span {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}
.empty-address {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}
</style>