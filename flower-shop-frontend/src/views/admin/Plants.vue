<template>
  <div class="plants-admin">
    <div class="page-header">
      <h3>植物管理</h3>
      <van-button type="primary" size="small" @click="showAddDialog = true">
        <van-icon name="plus" /> 新增植物
      </van-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-field
          v-model="searchKeyword"
          placeholder="搜索植物名称"
          clearable
          @clear="loadPlants"
      >
        <template #button>
          <van-button size="small" type="primary" @click="loadPlants">搜索</van-button>
        </template>
      </van-field>
    </div>

    <!-- 植物列表 -->
    <div class="plant-list" v-if="plantList.length > 0">
      <div class="list-header">
        <div class="col-img">图片</div>
        <div class="col-name">植物名称</div>
        <div class="col-type">类型</div>
        <div class="col-family">科属</div>
        <div class="col-view">浏览量</div>
        <div class="col-action">操作</div>
      </div>
      <div v-for="plant in plantList" :key="plant.id" class="list-item">
        <div class="col-img">
          <img :src="plant.imageUrl || getPlaceholderImage(plant.id)" />
        </div>
        <div class="col-name">
          <div class="plant-name">{{ plant.name }}</div>
          <div class="plant-name-en">{{ plant.nameEn || '—' }}</div>
        </div>
        <div class="col-type">
          <van-tag :type="getTypeColor(plant.type)">
            {{ getTypeName(plant.type) }}
          </van-tag>
        </div>
        <div class="col-family">{{ plant.family || '—' }} / {{ plant.genus || '—' }}</div>
        <div class="col-view">{{ plant.viewCount || 0 }}</div>
        <div class="col-action">
          <van-button size="small" @click="editPlant(plant)">编辑</van-button>
          <van-button size="small" type="danger" plain @click="deletePlant(plant.id)">删除</van-button>
        </div>
      </div>
    </div>

    <div class="empty-list" v-else-if="!loading">
      <van-empty description="暂无植物数据" />
      <van-button type="primary" round @click="loadPlants">刷新</van-button>
    </div>

    <!-- 新增弹窗 -->
    <van-dialog v-model:show="showAddDialog" :title="isEdit ? '编辑植物' : '新增植物'" width="90%" @confirm="savePlant">
      <van-form class="plant-form">
        <van-field v-model="form.name" label="植物名称" placeholder="请输入植物名称" />
        <van-field v-model="form.nameEn" label="英文名称" placeholder="用于API匹配" />
        <van-field v-model="form.alias" label="别名" placeholder="请输入别名" />
        <van-field v-model="form.family" label="科" placeholder="科" />
        <van-field v-model="form.genus" label="属" placeholder="属" />
        <van-field v-model="form.type" label="类型" placeholder="flower/green/succulent" />
        <van-field v-model="form.characteristic" label="特性" placeholder="植物特性" type="textarea" rows="2" />
        <van-field v-model="form.careKnowledge" label="养护知识" placeholder="养护方法" type="textarea" rows="3" />
        <van-field v-model="form.imageUrl" label="图片URL" placeholder="植物图片地址" />
      </van-form>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import axios from 'axios'

const plantList = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const isEdit = ref(false)
const searchKeyword = ref('')

const form = ref({
  id: null,
  name: '',
  nameEn: '',
  alias: '',
  family: '',
  genus: '',
  type: '',
  characteristic: '',
  careKnowledge: '',
  imageUrl: ''
})

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const getTypeName = (type) => {
  const map = { flower: '鲜花', green: '绿植', succulent: '多肉' }
  return map[type] || type || '未知'
}

const getTypeColor = (type) => {
  const map = { flower: 'danger', green: 'success', succulent: 'warning' }
  return map[type] || 'default'
}

const getPlaceholderImage = (id) => {
  return `https://picsum.photos/id/${100 + id}/60/60`
}

const loadPlants = async () => {
  loading.value = true
  try {
    let url = '/api/admin/plants'
    if (searchKeyword.value) {
      url += `?keyword=${encodeURIComponent(searchKeyword.value)}`
    }
    const res = await axios.get(url, getHeaders())
    if (res.data.code === 200) {
      plantList.value = res.data.data
    }
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const editPlant = (plant) => {
  isEdit.value = true
  form.value = { ...plant }
  showAddDialog.value = true
}

const deletePlant = async (id) => {
  try {
    await showConfirmDialog({ message: '确认删除该植物？' })
    const res = await axios.delete(`/api/admin/plants/${id}`, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('删除成功')
      loadPlants()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('删除失败')
    }
  }
}

const savePlant = async () => {
  try {
    let res
    if (isEdit.value) {
      res = await axios.put(`/api/admin/plants/${form.value.id}`, form.value, getHeaders())
    } else {
      res = await axios.post('/api/admin/plants', form.value, getHeaders())
    }
    if (res.data.code === 200) {
      showSuccessToast(isEdit.value ? '修改成功' : '添加成功')
      showAddDialog.value = false
      resetForm()
      loadPlants()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    console.error('保存失败', error)
    showToast('保存失败')
  }
}

const resetForm = () => {
  form.value = {
    id: null,
    name: '',
    nameEn: '',
    alias: '',
    family: '',
    genus: '',
    type: '',
    characteristic: '',
    careKnowledge: '',
    imageUrl: ''
  }
  isEdit.value = false
}

onMounted(() => {
  loadPlants()
})
</script>

<style scoped>
.plants-admin {
  background: white;
  border-radius: 16px;
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.page-header h3 {
  font-size: 16px;
  color: #2c3e50;
}
.search-bar {
  margin-bottom: 20px;
}
.plant-list {
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
.col-name { width: 180px; }
.col-type { width: 80px; }
.col-family { width: 150px; }
.col-view { width: 70px; }
.col-action { width: 120px; }
.list-item .col-img img {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}
.plant-name {
  font-weight: bold;
  margin-bottom: 4px;
}
.plant-name-en {
  font-size: 11px;
  color: #999;
}
.list-item .col-action {
  display: flex;
  gap: 8px;
}
.plant-form {
  padding: 16px;
}
.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  gap: 16px;
}
</style>