<template>
  <div class="cart-container">
    <!-- 顶部导航 -->
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>购物车</span>
      <span class="edit-btn" @click="toggleEdit">{{ isEdit ? '完成' : '编辑' }}</span>
    </div>

    <!-- 购物车列表 -->
    <div class="cart-list" v-if="cartItems.length > 0">
      <van-swipe-cell v-for="item in cartItems" :key="item.id">
        <div class="cart-item">
          <div class="checkbox" @click="toggleSelect(item.id)">
            <van-icon :name="item.selected ? 'success' : 'circle'" :color="item.selected ? '#764ba2' : '#ccc'" size="20" />
          </div>
          <div class="item-image">
            <img :src="item.mainImage" :alt="item.name" />
          </div>
          <div class="item-info">
            <h4 class="item-name">{{ item.name }}</h4>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-controls">
              <div class="quantity-control">
                <van-icon name="minus" size="18" @click="decreaseQuantity(item.id)" />
                <span class="quantity">{{ item.quantity }}</span>
                <van-icon name="plus" size="18" @click="increaseQuantity(item.id)" />
              </div>
              <div v-if="isEdit" class="delete-btn" @click="deleteItem(item.id)">
                <van-icon name="delete-o" size="20" color="#e74c3c" />
              </div>
            </div>
          </div>
        </div>
        <template #right>
          <div class="swipe-delete" @click="deleteItem(item.id)">
            <span>删除</span>
          </div>
        </template>
      </van-swipe-cell>
    </div>

    <!-- 空购物车 -->
    <div class="empty-cart" v-else>
      <van-empty description="购物车空空如也" />
      <van-button type="primary" round @click="goToShop">去逛逛</van-button>
    </div>

    <!-- 底部结算栏 -->
    <div class="bottom-bar" v-if="cartItems.length > 0">
      <div class="all-checkbox" @click="toggleSelectAll">
        <van-icon :name="isAllSelected ? 'success' : 'circle'" :color="isAllSelected ? '#764ba2' : '#ccc'" size="20" />
        <span>全选</span>
      </div>
      <div class="total">
        <span class="total-label">合计：</span>
        <span class="total-price">¥{{ totalPrice }}</span>
      </div>
      <div class="checkout-btn" @click="checkout">
        结算({{ selectedCount }})
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const cartItems = ref([])
const isEdit = ref(false)

// 加载购物车数据
const loadCart = () => {
  const cart = localStorage.getItem('cart')
  if (cart) {
    cartItems.value = JSON.parse(cart)
    // 确保每个商品都有 selected 属性，默认为 true
    cartItems.value.forEach(item => {
      if (item.selected === undefined) {
        item.selected = true
      }
    })
    saveCart()
  }
}

// 保存购物车到本地
const saveCart = () => {
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
}

// 计算选中商品总数
const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).reduce((sum, item) => sum + item.quantity, 0)
})

// 计算总价
const totalPrice = computed(() => {
  return cartItems.value
      .filter(item => item.selected)
      .reduce((sum, item) => sum + item.price * item.quantity, 0)
      .toFixed(2)
})

// 是否全选
const isAllSelected = computed(() => {
  return cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
})

// 切换单个商品选中状态
const toggleSelect = (id) => {
  const item = cartItems.value.find(i => i.id === id)
  if (item) {
    item.selected = !item.selected
    saveCart()
  }
}

// 全选/取消全选
const toggleSelectAll = () => {
  const newStatus = !isAllSelected.value
  cartItems.value.forEach(item => {
    item.selected = newStatus
  })
  saveCart()
}

// 增加数量
const increaseQuantity = (id) => {
  const item = cartItems.value.find(i => i.id === id)
  if (item) {
    item.quantity++
    saveCart()
  }
}

// 减少数量
const decreaseQuantity = (id) => {
  const item = cartItems.value.find(i => i.id === id)
  if (item && item.quantity > 1) {
    item.quantity--
    saveCart()
  } else if (item && item.quantity === 1) {
    deleteItem(id)
  }
}

// 删除商品
const deleteItem = (id) => {
  cartItems.value = cartItems.value.filter(item => item.id !== id)
  saveCart()
  showToast({ message: '已删除', type: 'success', duration: 1000 })
}

// 编辑模式切换
const toggleEdit = () => {
  isEdit.value = !isEdit.value
}

//结算
const checkout = () => {
  if (selectedCount.value === 0) {
    showToast('请选择要结算的商品')
    return
  }
  // 保存选中的商品到结算页
  const selectedItems = cartItems.value.filter(item => item.selected)
  localStorage.setItem('checkoutItems', JSON.stringify(selectedItems))
  router.push('/checkout')
}

const goBack = () => {
  router.back()
}

const goToShop = () => {
  router.push('/shop')
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-container {
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
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  z-index: 10;
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

.cart-list {
  padding: 12px;
}

.cart-item {
  background: white;
  border-radius: 16px;
  padding: 12px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.checkbox {
  flex-shrink: 0;
  cursor: pointer;
}

.item-image {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  background: #f0f0f0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-name {
  font-size: 15px;
  font-weight: 500;
  color: #2c3e50;
  margin: 0;
}

.item-price {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.item-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #f5f7fa;
  padding: 4px 12px;
  border-radius: 20px;
}

.quantity {
  font-size: 14px;
  font-weight: 500;
  min-width: 30px;
  text-align: center;
}

.delete-btn {
  padding: 8px;
  cursor: pointer;
}

.swipe-delete {
  background: #e74c3c;
  color: white;
  width: 70px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
}

:deep(.van-swipe-cell__right) {
  height: auto;
  border-radius: 16px;
  overflow: hidden;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.all-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
}

.total {
  flex: 1;
  text-align: right;
}

.total-label {
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
}

.checkout-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 10px 24px;
  border-radius: 30px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s;
}

.checkout-btn:active {
  transform: scale(0.96);
}
</style>