<template>
  <div class="tasks-manage">
    <div class="page-header">
      <h3>任务管理</h3>
      <van-button type="primary" size="small" @click="openAddDialog">
        <van-icon name="plus" /> 新增任务
      </van-button>
    </div>

    <!-- 任务列表 -->
    <div class="task-list">
      <div class="list-header">
        <div class="col-name">任务名称</div>
        <div class="col-type">类型</div>
        <div class="col-target">目标值</div>
        <div class="col-reward">奖励积分</div>
        <div class="col-status">状态</div>
        <div class="col-action">操作</div>
      </div>
      <div v-for="task in taskList" :key="task.id" class="list-item">
        <div class="col-name">
          <div class="task-name">{{ task.name }}</div>
          <div class="task-desc">{{ task.description }}</div>
        </div>
        <div class="col-type">
          <van-tag :type="task.type === 'checkin_count' ? 'primary' : 'success'">
            {{ task.type === 'checkin_count' ? '打卡次数' : '识别植物' }}
          </van-tag>
        </div>
        <div class="col-target">{{ task.targetValue }}</div>
        <div class="col-reward">{{ task.rewardPoints }}</div>
        <div class="col-status">
          <van-switch v-model="task.status" :active-value="1" :inactive-value="0" @change="updateStatus(task)" />
        </div>
        <div class="col-action">
          <van-button size="small" @click="editTask(task)">编辑</van-button>
          <van-button size="small" type="danger" plain @click="deleteTask(task.id)">删除</van-button>
        </div>
      </div>
    </div>

    <div class="empty-list" v-if="taskList.length === 0 && !loading">
      <van-empty description="暂无任务" />
      <van-button type="primary" round @click="loadTasks">刷新</van-button>
    </div>

    <!-- 新增/编辑弹窗 -->
    <van-dialog v-model:show="showDialog" :title="dialogTitle" width="90%" @confirm="saveTask">
      <van-form class="task-form">
        <van-field v-model="form.name" label="任务名称" placeholder="请输入任务名称" />
        <van-field v-model="form.description" label="任务描述" placeholder="请输入任务描述" type="textarea" rows="2" />

        <div class="form-row">
          <van-field v-model="form.type" label="任务类型" placeholder="checkin_count / plant_count" />
          <span class="form-hint">checkin_count-打卡次数 / plant_count-识别植物数量</span>
        </div>

        <van-field v-model="form.targetValue" label="目标值" placeholder="完成目标所需数量" type="number" />
        <van-field v-model="form.rewardPoints" label="奖励积分" placeholder="完成任务获得积分" type="number" />

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
import { ref, onMounted, computed } from 'vue'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import axios from 'axios'

const taskList = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)

const form = ref({
  id: null,
  name: '',
  description: '',
  type: '',
  targetValue: '',
  rewardPoints: '',
  status: 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑任务' : '新增任务')

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

// 加载任务列表
const loadTasks = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/tasks/admin', getHeaders())
    if (res.data.code === 200) {
      taskList.value = res.data.data
    }
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 更新状态
const updateStatus = async (task) => {
  try {
    await axios.put(`/api/tasks/admin/${task.id}`, task, getHeaders())
    showSuccessToast('状态更新成功')
  } catch (error) {
    showToast('更新失败')
    loadTasks()
  }
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    description: '',
    type: '',
    targetValue: '',
    rewardPoints: '',
    status: 1
  }
  showDialog.value = true
}

// 编辑任务
const editTask = (task) => {
  isEdit.value = true
  form.value = { ...task }
  showDialog.value = true
}

// 删除任务
const deleteTask = async (id) => {
  try {
    await showConfirmDialog({ message: '确认删除该任务？' })
    const res = await axios.delete(`/api/tasks/admin/${id}`, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('删除成功')
      loadTasks()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('删除失败')
    }
  }
}

// 保存任务
const saveTask = async () => {
  try {
    let res
    if (isEdit.value) {
      res = await axios.put(`/api/tasks/admin/${form.value.id}`, form.value, getHeaders())
    } else {
      res = await axios.post('/api/tasks/admin', form.value, getHeaders())
    }
    if (res.data.code === 200) {
      showSuccessToast(isEdit.value ? '修改成功' : '添加成功')
      showDialog.value = false
      loadTasks()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    showToast('保存失败')
  }
}

onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.tasks-manage {
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

.task-list {
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

.col-name { width: 200px; }
.col-type { width: 100px; }
.col-target { width: 80px; }
.col-reward { width: 80px; }
.col-status { width: 80px; }
.col-action { width: 100px; }

.task-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.task-desc {
  font-size: 11px;
  color: #999;
}

.list-item .col-action {
  display: flex;
  gap: 8px;
}

.task-form {
  padding: 16px;
}

.form-row {
  margin-bottom: 8px;
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