<template>
  <div class="address-edit-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>{{ isEdit ? '编辑地址' : '新增地址' }}</span>
    </div>

    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="form.receiverName"
            label="收货人"
            placeholder="请输入收货人姓名"
            :rules="[{ required: true, message: '请填写收货人' }]"
        />
        <van-field
            v-model="form.receiverPhone"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
        />
        <van-field
            v-model="form.province"
            label="省份"
            placeholder="请输入省份"
            :rules="[{ required: true, message: '请填写省份' }]"
        />
        <van-field
            v-model="form.city"
            label="城市"
            placeholder="请输入城市"
            :rules="[{ required: true, message: '请填写城市' }]"
        />
        <van-field
            v-model="form.district"
            label="区/县"
            placeholder="请输入区/县"
            :rules="[{ required: true, message: '请填写区/县' }]"
        />
        <van-field
            v-model="form.detail"
            label="详细地址"
            placeholder="请输入街道、门牌号"
            :rules="[{ required: true, message: '请填写详细地址' }]"
        />
      </van-cell-group>

      <div class="default-checkbox">
        <van-checkbox v-model="form.isDefault">设为默认地址</van-checkbox>
      </div>

      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          保存
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const isEdit = ref(false)
const addressId = ref(null)

const form = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  }
}

// 加载地址数据（编辑时）
const loadAddress = async (id) => {
  try {
    const res = await axios.get(`/api/address/${id}`, getHeaders())
    if (res.data.code === 200) {
      const addr = res.data.data
      form.value = {
        receiverName: addr.receiverName,
        receiverPhone: addr.receiverPhone,
        province: addr.province,
        city: addr.city,
        district: addr.district,
        detail: addr.detail,
        isDefault: addr.isDefault === 1
      }
    }
  } catch (error) {
    console.error('加载地址失败:', error)
    showToast('加载地址失败')
  }
}

// 提交表单
const onSubmit = async () => {
  // 验证必填字段
  if (!form.value.receiverName) {
    showToast('请填写收货人')
    return
  }
  if (!form.value.receiverPhone || !/^1[3-9]\d{9}$/.test(form.value.receiverPhone)) {
    showToast('请填写正确的手机号')
    return
  }
  if (!form.value.province || !form.value.city || !form.value.district || !form.value.detail) {
    showToast('请完整填写地址信息')
    return
  }

  const data = {
    receiverName: form.value.receiverName,
    receiverPhone: form.value.receiverPhone,
    province: form.value.province,
    city: form.value.city,
    district: form.value.district,
    detail: form.value.detail,
    isDefault: form.value.isDefault ? 1 : 0
  }

  console.log('提交数据:', data)

  try {
    let res
    if (isEdit.value) {
      res = await axios.put(`/api/address/${addressId.value}`, data, getHeaders())
    } else {
      res = await axios.post('/api/address', data, getHeaders())
    }

    console.log('响应:', res.data)

    if (res.data.code === 200) {
      showSuccessToast(isEdit.value ? '修改成功' : '添加成功')
      setTimeout(() => {
        router.back()
      }, 1000)
    } else {
      showToast(res.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    if (error.response) {
      console.log('错误响应:', error.response.data)
      showToast(error.response.data?.message || '保存失败')
    } else {
      showToast('网络错误，请检查后端服务')
    }
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  const id = route.params.id
  if (id && id !== 'add') {
    isEdit.value = true
    addressId.value = parseInt(id)
    loadAddress(addressId.value)
  }
})
</script>

<style scoped>
.address-edit-container {
  min-height: 100vh;
  background: #f5f7fa;
}
.header {
  background: white;
  padding: 15px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.header span {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}
.default-checkbox {
  margin: 16px;
  padding-left: 8px;
}
</style>