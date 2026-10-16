<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="flower-1">🌸</div>
      <div class="flower-2">🌻</div>
      <div class="flower-3">🌺</div>
    </div>

    <!-- 注册卡片 -->
    <div class="register-card">
      <div class="logo">
        <div class="logo-icon">🌱</div>
        <h1>注册新账号</h1>
        <p>加入花店，开启美好生活</p>
      </div>

      <van-form @submit="handleRegister">
        <van-field
            v-model="phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
        >
          <template #left-icon>
            <van-icon name="phone-o" size="20" color="#27ae60" />
          </template>
        </van-field>

        <van-field
            v-model="password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        >
          <template #left-icon>
            <van-icon name="lock-o" size="20" color="#27ae60" />
          </template>
        </van-field>

        <van-field
            v-model="nickname"
            name="nickname"
            label="昵称"
            placeholder="请输入昵称（选填）"
        >
          <template #left-icon>
            <van-icon name="user-o" size="20" color="#27ae60" />
          </template>
        </van-field>

        <div class="btn-wrapper">
          <van-button round block type="primary" native-type="submit" class="register-btn">
            注 册
          </van-button>
        </div>

        <div class="login-link">
          已有账号？
          <router-link to="/login">去登录</router-link>
        </div>
      </van-form>

      <!-- 注册提示 -->
      <div class="tips">
        <p>注册即表示同意 <span>《用户协议》</span> 和 <span>《隐私政策》</span></p>
      </div>
    </div>

    <!-- 错误提示 -->
    <van-dialog v-model:show="showError" title="提示" :message="errorMessage" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const phone = ref('')
const password = ref('')
const nickname = ref('')
const showError = ref(false)
const errorMessage = ref('')

const handleRegister = async () => {
  try {
    const res = await axios.post('/api/user/register', {
      phone: phone.value,
      password: password.value,
      nickname: nickname.value || '花友'
    })
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.data))
      showToast({ message: '注册成功', type: 'success', duration: 1500 })
      setTimeout(() => {
        router.push('/profile')
      }, 1500)
    } else {
      errorMessage.value = res.data.message
      showError.value = true
    }
  } catch (error) {
    errorMessage.value = '请求失败'
    showError.value = true
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f093fb 0%, #667eea 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景装饰花朵 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.flower-1 {
  position: absolute;
  top: 10%;
  left: 5%;
  font-size: 50px;
  animation: float 3s ease-in-out infinite;
}

.flower-2 {
  position: absolute;
  bottom: 15%;
  right: 8%;
  font-size: 60px;
  animation: float 4s ease-in-out infinite;
}

.flower-3 {
  position: absolute;
  top: 40%;
  right: 5%;
  font-size: 40px;
  animation: float 3.5s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-15px);
  }
}

/* 注册卡片 */
.register-card {
  width: 85%;
  max-width: 400px;
  background: white;
  border-radius: 30px;
  padding: 35px 30px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  z-index: 1;
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Logo区域 */
.logo {
  text-align: center;
  margin-bottom: 35px;
}

.logo-icon {
  font-size: 50px;
  margin-bottom: 10px;
}

.logo h1 {
  font-size: 28px;
  color: #2c3e50;
  margin: 10px 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo p {
  color: #999;
  font-size: 13px;
}

/* 表单项样式 */
:deep(.van-field) {
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 16px;
  border: 1px solid transparent;
  transition: all 0.3s;
}

:deep(.van-field--focused) {
  border-color: #764ba2;
  background: white;
  box-shadow: 0 0 0 3px rgba(118, 75, 162, 0.1);
}

/* 注册按钮 */
.btn-wrapper {
  margin: 30px 0 20px;
}

.register-btn {
  height: 48px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  font-size: 18px;
  font-weight: bold;
  transition: transform 0.2s;
}

.register-btn:active {
  transform: scale(0.98);
}

/* 登录链接 */
.login-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #764ba2;
  text-decoration: none;
  font-weight: bold;
}

/* 提示 */
.tips {
  text-align: center;
  margin-top: 25px;
}

.tips p {
  font-size: 11px;
  color: #bbb;
}

.tips span {
  color: #764ba2;
  cursor: pointer;
}
</style>