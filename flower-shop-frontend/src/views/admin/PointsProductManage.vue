<template>
  <div class="points-product-manage">
    <div class="page-header">
      <h3>积分商品管理</h3>
      <van-button type="primary" size="small" @click="openAddDialog">
        <van-icon name="plus" /> 新增商品
      </van-button>
    </div>

    <!-- 商品列表 -->
    <div class="product-list" v-if="productList.length > 0">
      <div class="list-header">
        <div class="col-img">图片</div>
        <div class="col-name">商品名称</div>
        <div class="col-points">所需积分</div>
        <div class="col-stock">库存</div>
        <div class="col-status">状态</div>
        <div class="col-action">操作</div>
      </div>
      <div v-for="product in productList" :key="product.id" class="list-item">
        <div class="col-img">
          <img :src="product.imageUrl || '/default-product.png'" />
        </div>
        <div class="col-name">{{ product.name }}</div>
        <div class="col-points">{{ product.points }}</div>
        <div class="col-stock">{{ product.stock }}</div>
        <div class="col-status">
          <van-tag :type="product.status === 1 ? 'success' : 'default'">
            {{ product.status === 1 ? '上架' : '下架' }}
          </van-tag>
        </div>
        <div class="col-action">
          <van-button size="small" @click="editProduct(product)">编辑</van-button>
          <van-button size="small" type="danger" plain @click="deleteProduct(product.id)">删除</van-button>
        </div>
      </div>
    </div>

    <div class="empty-list" v-else-if="!loading">
      <van-empty description="暂无积分商品" />
      <van-button type="primary" round @click="loadProducts">刷新</van-button>
    </div>

    <!-- 新增/编辑弹窗 -->
    <van-dialog v-model:show="showDialog" :title="dialogTitle" width="90%" @confirm="saveProduct">
      <van-form class="product-form">
        <van-field v-model="form.name" label="商品名称" placeholder="请输入商品名称" />
        <van-field v-model="form.points" label="所需积分" placeholder="请输入积分" type="number" />
        <van-field v-model="form.stock" label="库存" placeholder="请输入库存" type="number" />
        <van-field v-model="form.imageUrl" label="图片URL" placeholder="请输入图片地址" />
        <div class="form-status">
          <span>状态：</span>
          <van-radio-group v-model="form.status" direction="horizontal">
            <van-radio :name="1">上架</van-radio>
            <van-radio :name="0">下架</van-radio>
          </van-radio-group>
        </div>
      </van-form>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import axios from 'axios'

const productList = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)

const form = ref({
  id: null,
  name: '',
  points: '',
  stock: '',
  imageUrl: '',
  status: 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑商品' : '新增商品')

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/points-products', getHeaders())
    if (res.data.code === 200) {
      productList.value = res.data.data
    }
  } catch (error) {
    console.error('加载失败', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    points: '',
    stock: '',
    imageUrl: '',
    status: 1
  }
  showDialog.value = true
}

const editProduct = (product) => {
  isEdit.value = true
  form.value = { ...product }
  showDialog.value = true
}

const deleteProduct = async (id) => {
  try {
    await showConfirmDialog({ message: '确认删除该商品？' })
    const res = await axios.delete(`/api/admin/points-products/${id}`, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('删除成功')
      loadProducts()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('删除失败')
    }
  }
}

const saveProduct = async () => {
  try {
    let res
    if (isEdit.value) {
      res = await axios.put(`/api/admin/points-products/${form.value.id}`, form.value, getHeaders())
    } else {
      res = await axios.post('/api/admin/points-products', form.value, getHeaders())
    }
    if (res.data.code === 200) {
      showSuccessToast(isEdit.value ? '修改成功' : '添加成功')
      showDialog.value = false
      loadProducts()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    console.error('保存失败', error)
    showToast('保存失败')
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.points-product-manage {
  background: white;
  border-radius: 16px;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.page-header h3 {
  font-size: 16px;
  color: #2c3e50;
}

.product-list {
  overflow-x: auto;
}

.list-header,
.list-item {
  display: flex;
  align-items: center;
  padding: 12px 8px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
}

.list-header {
  background: #f8f9fa;
  font-weight: bold;
  color: #666;
}

.col-img { width: 70px; }
.col-name { width: 200px; }
.col-points { width: 80px; }
.col-stock { width: 80px; }
.col-status { width: 70px; }
.col-action { width: 100px; }

.list-item .col-img img {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
  background: #f0f0f0;
}

.list-item .col-action {
  display: flex;
  gap: 8px;
}

.product-form {
  padding: 16px;
}

.form-status {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  gap: 16px;
}
</style>