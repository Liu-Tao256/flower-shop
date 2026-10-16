<template>
  <div class="checkout-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>确认订单</span>
    </div>

    <!-- 收货地址 - 可点击选择 -->
    <div class="address-card" @click="goToAddress">
      <div class="address-info" v-if="selectedAddress">
        <div class="address-user">
          <span>{{ selectedAddress.receiverName }}</span>
          <span>{{ selectedAddress.receiverPhone }}</span>
        </div>
        <div class="address-detail">
          {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detail }}
        </div>
      </div>
      <div class="address-empty" v-else>
        <van-icon name="add-o" size="20" />
        <span>请选择收货地址</span>
      </div>
      <van-icon name="arrow" class="address-arrow" />
    </div>

    <!-- 商品列表 -->
    <div class="goods-list">
      <div class="section-title">商品清单</div>
      <div v-for="item in checkoutItems" :key="item.id" class="goods-item">
        <div class="goods-image">
          <img :src="item.mainImage" :alt="item.name" />
        </div>
        <div class="goods-info">
          <div class="goods-name">{{ item.name }}</div>
          <div class="goods-price">¥{{ item.price }}</div>
        </div>
        <div class="goods-quantity">x{{ item.quantity }}</div>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="order-info">
      <div class="section-title">订单信息</div>
      <div class="info-row">
        <span>商品总价</span>
        <span>¥{{ totalAmount }}</span>
      </div>
      <div class="info-row">
        <span>运费</span>
        <span>¥{{ deliveryFee }}</span>
      </div>
      <div class="info-row total-row">
        <span>实付金额</span>
        <span class="total-price">¥{{ finalAmount }}</span>
      </div>
    </div>

    <!-- 支付方式 -->
    <div class="payment-methods">
      <div class="section-title">支付方式</div>
      <van-radio-group v-model="paymentMethod" direction="horizontal">
        <van-radio name="wechat">微信支付</van-radio>
        <van-radio name="alipay">支付宝支付</van-radio>
        <van-radio name="balance">余额支付</van-radio>
      </van-radio-group>
    </div>

    <!-- 备注 -->
    <div class="remark-section">
      <van-field
          v-model="remark"
          label="备注"
          placeholder="选填，给商家的留言"
          type="textarea"
          rows="1"
          autosize
      />
    </div>

    <!-- 底部提交按钮 -->
    <div class="bottom-bar">
      <div class="total">
        <span>实付：</span>
        <span class="total-price">¥{{ finalAmount }}</span>
      </div>
      <div class="submit-btn" @click="submitOrder">
        提交订单
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()

const checkoutItems = ref([])
const selectedAddress = ref(null)
const paymentMethod = ref('wechat')
const remark = ref('')
const deliveryFee = ref(0)

// 计算商品总价
const totalAmount = computed(() => {
  return checkoutItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

// 计算实付金额
const finalAmount = computed(() => {
  return (parseFloat(totalAmount.value) + deliveryFee.value).toFixed(2)
})

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

// 加载结算商品
const loadCheckoutItems = () => {
  const items = localStorage.getItem('checkoutItems')
  if (items) {
    checkoutItems.value = JSON.parse(items)
  } else {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]')
    checkoutItems.value = cart.filter(item => item.selected)
  }

  if (checkoutItems.value.length === 0) {
    showToast('请选择要结算的商品')
    router.back()
  }
}

// 加载选中的地址
const loadSelectedAddress = () => {
  const savedAddress = localStorage.getItem('selectedAddress')
  if (savedAddress) {
    selectedAddress.value = JSON.parse(savedAddress)
    localStorage.removeItem('selectedAddress')
  } else {
    loadDefaultAddress()
  }
}

// 加载默认地址
const loadDefaultAddress = async () => {
  try {
    const res = await axios.get('/api/address/default', getHeaders())
    if (res.data.code === 200 && res.data.data) {
      selectedAddress.value = res.data.data
    }
  } catch (error) {
    console.log('加载默认地址失败')
  }
}

// 提交订单
const submitOrder = async () => {
  if (!selectedAddress.value) {
    showToast('请选择收货地址')
    return
  }

  if (checkoutItems.value.length === 0) {
    showToast('购物车是空的')
    return
  }

  // 构建订单数据
  const orderData = {
    addressId: selectedAddress.value.id,
    items: checkoutItems.value.map(item => ({
      productId: item.id,
      productName: item.name,
      price: item.price,
      quantity: item.quantity,
      totalPrice: item.price * item.quantity
    })),
    totalAmount: totalAmount.value,
    deliveryFee: deliveryFee.value,
    finalAmount: finalAmount.value,
    paymentMethod: paymentMethod.value,
    remark: remark.value
  }

  console.log('提交订单:', orderData)

  try {
    const res = await axios.post('/api/orders', orderData, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('订单提交成功！')

      // 清空购物车中已结算的商品
      const cart = JSON.parse(localStorage.getItem('cart') || '[]')
      const newCart = cart.filter(item => !item.selected)
      localStorage.setItem('cart', JSON.stringify(newCart))
      localStorage.removeItem('checkoutItems')
      localStorage.removeItem('selectedAddress')

      setTimeout(() => {
        router.push('/orders')
      }, 1500)
    } else {
      showToast(res.data.message || '提交失败')
    }
  } catch (error) {
    console.error('提交订单失败:', error)
    showToast('提交失败，请稍后重试')
  }
}

const goBack = () => {
  router.back()
}

const goToAddress = () => {
  router.push('/address?from=checkout')
}

onMounted(() => {
  loadCheckoutItems()
  loadSelectedAddress()
})
</script>

<style scoped>
.checkout-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 70px;
}

.header {
  position: sticky;
  top: 0;
  background: white;
  padding: 15px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #f0f0f0;
  z-index: 10;
}

.header span {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

/* 地址卡片 */
.address-card {
  background: white;
  margin: 12px;
  padding: 16px;
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.address-info {
  flex: 1;
}

.address-user {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
  font-weight: bold;
  color: #2c3e50;
}

.address-detail {
  font-size: 13px;
  color: #666;
}

.address-empty {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
}

.address-arrow {
  color: #ccc;
}

/* 商品清单 */
.goods-list {
  background: white;
  margin: 12px;
  padding: 16px;
  border-radius: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #764ba2;
  display: inline-block;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f0f0;
  flex-shrink: 0;
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 14px;
  color: #2c3e50;
  margin-bottom: 4px;
}

.goods-price {
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
}

.goods-quantity {
  font-size: 14px;
  color: #999;
}

/* 订单信息 */
.order-info {
  background: white;
  margin: 12px;
  padding: 16px;
  border-radius: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
}

.total-row {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: 4px;
  font-weight: bold;
  font-size: 16px;
}

.total-price {
  color: #e74c3c;
  font-size: 20px;
}

/* 支付方式 */
.payment-methods {
  background: white;
  margin: 12px;
  padding: 16px;
  border-radius: 16px;
}

:deep(.van-radio-group) {
  display: flex;
  gap: 24px;
}

/* 备注 */
.remark-section {
  background: white;
  margin: 12px;
  padding: 8px 16px;
  border-radius: 16px;
}

/* 底部栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.total {
  font-size: 14px;
  color: #666;
}

.total .total-price {
  font-size: 22px;
  font-weight: bold;
  color: #e74c3c;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 12px 32px;
  border-radius: 30px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}
</style>