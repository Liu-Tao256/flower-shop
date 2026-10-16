<template>
  <div class="coupon-manage">
    <div class="page-header">
      <h3>优惠券管理</h3>
      <van-button type="primary" size="small" @click="openAddDialog">
        <van-icon name="plus" /> 新增优惠券
      </van-button>
    </div>

    <div class="coupon-list" v-if="couponList.length > 0">
      <div class="list-header">
        <div class="col-name">优惠券名称</div>
        <div class="col-type">类型</div>
        <div class="col-value">面额</div>
        <div class="col-min">门槛</div>
        <div class="col-points">所需积分</div>
        <div class="col-status">状态</div>
        <div class="col-action">操作</div>
      </div>
      <div v-for="coupon in couponList" :key="coupon.id" class="list-item">
        <div class="col-name">{{ coupon.name }}</div>
        <div class="col-type">
          <van-tag :type="coupon.type === 'fixed' ? 'primary' : 'success'">
            {{ coupon.type === 'fixed' ? '满减' : '折扣' }}
          </van-tag>
        </div>
        <div class="col-value">
          {{ coupon.type === 'fixed' ? '¥' + coupon.value : coupon.value + '折' }}
        </div>
        <div class="col-min">{{ coupon.minAmount > 0 ? '满¥' + coupon.minAmount : '无门槛' }}</div>
        <div class="col-points">{{ coupon.pointsCost }}</div>
        <div class="col-status">
          <van-switch v-model="coupon.status" :active-value="1" :inactive-value="0" @change="updateStatus(coupon)" />
        </div>
        <div class="col-action">
          <van-button size="small" @click="editCoupon(coupon)">编辑</van-button>
          <van-button size="small" type="danger" plain @click="deleteCoupon(coupon.id)">删除</van-button>
        </div>
      </div>
    </div>

    <div class="empty-list" v-else-if="!loading">
      <van-empty description="暂无优惠券" />
      <van-button type="primary" round @click="loadCoupons">刷新</van-button>
    </div>

    <!-- 新增/编辑弹窗 -->
    <van-dialog v-model:show="showDialog" :title="dialogTitle" width="90%" @confirm="saveCoupon">
      <van-form class="coupon-form">
        <van-field v-model="form.name" label="优惠券名称" placeholder="请输入优惠券名称" />

        <van-field v-model="form.type" label="类型" placeholder="fixed / percent" />
        <span class="form-hint">fixed-固定金额 / percent-折扣</span>

        <van-field v-model="form.value" label="面额" placeholder="固定金额填数字，折扣填10" type="number" />
        <van-field v-model="form.minAmount" label="使用门槛" placeholder="满多少可用，0为无门槛" type="number" />
        <van-field v-model="form.pointsCost" label="所需积分" placeholder="兑换所需积分" type="number" />
        <van-field v-model="form.expireDays" label="有效天数" placeholder="有效天数" type="number" />
        <van-field v-model="form.totalQuantity" label="发行总量" placeholder="发行数量" type="number" />

        <div class="form-status">
          <span>状态：</span>
          <van-radio-group v-model="form.status" direction="horizontal">
            <van-radio :name="1">启用</van-radio>
            <van-radio :name="0">禁用</van-radio>
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

const couponList = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)

const form = ref({
  id: null,
  name: '',
  type: 'fixed',
  value: '',
  minAmount: 0,
  pointsCost: '',
  expireDays: 30,
  totalQuantity: '',
  status: 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑优惠券' : '新增优惠券')

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/coupons', getHeaders())
    if (res.data.code === 200) {
      couponList.value = res.data.data
    }
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const updateStatus = async (coupon) => {
  try {
    await axios.put(`/api/admin/coupons/${coupon.id}`, coupon, getHeaders())
    showSuccessToast('状态更新成功')
  } catch (error) {
    showToast('更新失败')
    loadCoupons()
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    type: 'fixed',
    value: '',
    minAmount: 0,
    pointsCost: '',
    expireDays: 30,
    totalQuantity: '',
    status: 1
  }
  showDialog.value = true
}

const editCoupon = (coupon) => {
  isEdit.value = true
  form.value = { ...coupon }
  showDialog.value = true
}

const deleteCoupon = async (id) => {
  try {
    await showConfirmDialog({ message: '确认删除该优惠券？' })
    const res = await axios.delete(`/api/admin/coupons/${id}`, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('删除成功')
      loadCoupons()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('删除失败')
    }
  }
}

const saveCoupon = async () => {
  try {
    let res
    if (isEdit.value) {
      res = await axios.put(`/api/admin/coupons/${form.value.id}`, form.value, getHeaders())
    } else {
      res = await axios.post('/api/admin/coupons', form.value, getHeaders())
    }
    if (res.data.code === 200) {
      showSuccessToast(isEdit.value ? '修改成功' : '添加成功')
      showDialog.value = false
      loadCoupons()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    showToast('保存失败')
  }
}

onMounted(() => {
  loadCoupons()
})
</script>

<style scoped>
.coupon-manage {
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

.coupon-list {
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

.col-name { width: 150px; }
.col-type { width: 70px; }
.col-value { width: 80px; }
.col-min { width: 80px; }
.col-points { width: 80px; }
.col-status { width: 60px; }
.col-action { width: 100px; }

.coupon-form {
  padding: 16px;
}

.form-hint {
  font-size: 11px;
  color: #999;
  padding: 0 12px 8px;
  display: block;
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