<template>
  <div class="products-admin">
    <div class="page-header">
      <h3>商品管理</h3>
      <van-button type="primary" size="small" @click="showAddDialog = true">
        <van-icon name="plus" /> 新增商品
      </van-button>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <div class="list-header">
        <div class="col-img">图片</div>
        <div class="col-name">商品名称</div>
        <div class="col-price">价格</div>
        <div class="col-stock">库存</div>
        <div class="col-sales">销量</div>
        <div class="col-status">状态</div>
        <div class="col-action">操作</div>
      </div>
      <div v-for="product in productList" :key="product.id" class="list-item">
        <div class="col-img">
          <img :src="product.mainImage || '/default-product.png'" />
        </div>
        <div class="col-name">{{ product.name }}</div>
        <div class="col-price">¥{{ product.price }}</div>
        <div class="col-stock">{{ product.stock }}</div>
        <div class="col-sales">{{ product.salesCount || 0 }}</div>
        <div class="col-status">
          <van-tag :type="product.status === 'on' ? 'success' : 'default'">
            {{ product.status === 'on' ? '上架' : '下架' }}
          </van-tag>
        </div>
        <div class="col-action">
          <van-button size="small" @click="editProduct(product)">编辑</van-button>
          <van-button size="small" type="danger" plain @click="deleteProduct(product.id)">删除</van-button>
        </div>
      </div>
    </div>

    <div class="empty-list" v-if="productList.length === 0 && !loading">
      <van-empty description="暂无商品" />
      <van-button type="primary" round @click="loadProducts">刷新</van-button>
    </div>

    <!-- 新增/编辑弹窗 -->
    <van-dialog v-model:show="showAddDialog" :title="isEdit ? '编辑商品' : '新增商品'" width="90%" @confirm="saveProduct">
      <van-form class="product-form">
        <van-field v-model="form.name" label="商品名称" placeholder="请输入商品名称" />
        <van-field v-model="form.categoryId" label="分类ID" placeholder="1-鲜花 2-绿植 3-多肉" />
        <van-field v-model="form.price" label="价格" placeholder="请输入价格" />
        <van-field v-model="form.originalPrice" label="原价" placeholder="请输入原价" />
        <van-field v-model="form.stock" label="库存" placeholder="请输入库存" />
        <van-field v-model="form.mainImage" label="图片URL" placeholder="请输入图片地址" />
        <van-field v-model="form.description" label="描述" placeholder="请输入描述" />
        <div class="form-status">
          <span>状态：</span>
          <van-radio-group v-model="form.status" direction="horizontal">
            <van-radio name="on">上架</van-radio>
            <van-radio name="off">下架</van-radio>
          </van-radio-group>
        </div>
      </van-form>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import axios from 'axios'

const productList = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  name: '',
  categoryId: '',
  price: '',
  originalPrice: '',
  stock: '',
  mainImage: '',
  description: '',
  status: 'on'
})

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/products', getHeaders())
    if (res.data.code === 200) {
      productList.value = res.data.data
    }
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const editProduct = (product) => {
  isEdit.value = true
  form.value = { ...product }
  showAddDialog.value = true
}

const deleteProduct = async (id) => {
  try {
    await showConfirmDialog({ message: '确认删除该商品？' })
    const res = await axios.delete(`/api/admin/products/${id}`, getHeaders())
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
      res = await axios.put(`/api/admin/products/${form.value.id}`, form.value, getHeaders())
    } else {
      res = await axios.post('/api/admin/products', form.value, getHeaders())
    }
    if (res.data.code === 200) {
      showSuccessToast(isEdit.value ? '修改成功' : '添加成功')
      showAddDialog.value = false
      resetForm()
      loadProducts()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    showToast('保存失败')
  }
}

const resetForm = () => {
  form.value = {
    id: null,
    name: '',
    categoryId: '',
    price: '',
    originalPrice: '',
    stock: '',
    mainImage: '',
    description: '',
    status: 'on'
  }
  isEdit.value = false
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.products-admin {
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

.col-img { width: 60px; }
.col-name { width: 150px; }
.col-price { width: 80px; }
.col-stock { width: 60px; }
.col-sales { width: 60px; }
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