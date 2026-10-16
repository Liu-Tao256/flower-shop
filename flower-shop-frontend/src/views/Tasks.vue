<template>
  <div class="tasks-container">
    <div class="header">
      <van-icon name="arrow-left" size="22" @click="goBack" />
      <span>成长任务</span>
    </div>

    <div class="stats-card">
      <div class="stat-item">
        <div class="stat-value">{{ completedCount }}</div>
        <div class="stat-label">已完成任务</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-value">{{ totalPoints }}</div>
        <div class="stat-label">获得积分</div>
      </div>
    </div>

    <div class="task-tabs">
      <span :class="['tab', { active: activeTab === 'active' }]" @click="activeTab = 'active'">进行中</span>
      <span :class="['tab', { active: activeTab === 'completed' }]" @click="activeTab = 'completed'">已完成</span>
    </div>

    <div class="task-list">
      <div v-for="task in filteredTasks" :key="task.id" class="task-card">
        <div class="task-icon">🎯</div>
        <div class="task-info">
          <div class="task-name">{{ task.name }}</div>
          <div class="task-desc">{{ task.description }}</div>
          <div class="task-progress">
            <van-progress :percentage="task.progressPercentage" stroke-width="6" />
            <span class="progress-text">{{ task.progress }}/{{ task.targetValue }}</span>
          </div>
        </div>
        <div class="task-reward">
          <span class="reward-points">+{{ task.rewardPoints }}积分</span>
          <van-button
              v-if="task.completed === 1 && task.rewardReceived !== 1"
              size="small"
              type="primary"
              round
              @click="claimReward(task.id)"
          >
            领取
          </van-button>
          <van-tag v-else-if="task.completed === 1 && task.rewardReceived === 1" type="success">已领取</van-tag>
          <van-tag v-else type="warning">进行中</van-tag>
        </div>
      </div>
    </div>

    <div class="empty-tasks" v-if="filteredTasks.length === 0">
      <van-empty description="暂无任务" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const tasks = ref([])
const activeTab = ref('active')

const getHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    headers: { Authorization: `Bearer ${token}` }
  }
}

const completedCount = computed(() => {
  return tasks.value.filter(t => t.completed === 1).length
})

const totalPoints = computed(() => {
  return tasks.value.filter(t => t.rewardReceived === 1).reduce((sum, t) => sum + (t.rewardPoints || 0), 0)
})

const filteredTasks = computed(() => {
  if (activeTab.value === 'completed') {
    return tasks.value.filter(t => t.completed === 1)
  }
  return tasks.value.filter(t => t.completed !== 1)
})

const loadTasks = async () => {
  try {
    const res = await axios.get('/api/tasks', getHeaders())
    if (res.data.code === 200) {
      tasks.value = res.data.data.map(t => ({
        ...t,
        progressPercentage: t.targetValue > 0 ? (t.progress || 0) / t.targetValue * 100 : 0
      }))
    }
  } catch (error) {
    showToast('加载失败')
  }
}

const claimReward = async (taskId) => {
  try {
    const res = await axios.post(`/api/tasks/${taskId}/claim`, {}, getHeaders())
    if (res.data.code === 200) {
      showSuccessToast('领取成功')
      loadTasks()
    } else {
      showToast(res.data.message)
    }
  } catch (error) {
    showToast('领取失败')
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.tasks-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 20px;
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

.stats-card {
  background: white;
  margin: 16px;
  padding: 20px;
  border-radius: 16px;
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #764ba2;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.stat-divider {
  width: 1px;
  background: #e0e0e0;
}

.task-tabs {
  display: flex;
  background: white;
  margin: 0 16px;
  border-radius: 16px;
  overflow: hidden;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 12px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
}

.tab.active {
  background: #764ba2;
  color: white;
}

.task-list {
  padding: 12px;
}

.task-card {
  background: white;
  border-radius: 16px;
  margin-bottom: 12px;
  padding: 16px;
  display: flex;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.task-icon {
  font-size: 32px;
}

.task-info {
  flex: 1;
}

.task-name {
  font-size: 15px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
}

.task-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.task-progress {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-text {
  font-size: 11px;
  color: #666;
}

.task-reward {
  text-align: right;
  min-width: 80px;
}

.reward-points {
  display: block;
  font-size: 14px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 8px;
}

.empty-tasks {
  display: flex;
  justify-content: center;
  padding: 40px;
}
</style>