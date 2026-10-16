<template>
  <div class="login-container">
    <div class="bg-decoration">
      <div class="flower-1">🌸</div>
      <div class="flower-2">🌻</div>
      <div class="flower-3">🌺</div>
      <div class="flower-4">🌿</div>
    </div>

    <div class="login-card">
      <div class="logo">
        <div class="logo-icon">🌸</div>
        <h1>花店</h1>
        <p>鲜花 | 绿植 | 多肉</p>
      </div>

      <van-form @submit="handleLogin">
        <van-field
          v-model="phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[{ required: true, message: '请填写手机号' }]"
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

        <div class="forgot-pwd">
          <span>忘记密码？</span>
        </div>

        <div class="btn-wrapper">
          <van-button round block type="primary" native-type="submit" class="login-btn">
            登 录
          </van-button>
        </div>

        <div class="register-link">
          还没有账号？
          <router-link to="/register">立即注册</router-link>
        </div>
      </van-form>
    </div>

    <van-dialog v-model:show="showError" title="提示" :message="errorMessage" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import axios from 'axios'

const router = useRouter()
const phone = ref('13800138000')
const password = ref('123456')
const showError = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  try {
    const res = await axios.post('/api/user/login', {
      phone: phone.value,
      password: password.value
    })
    if (res.data.code === 200) {
      const data = res.data.data
      localStorage.setItem('token', data.token)
      localStorage.setItem('userInfo', JSON.stringify(data))

      showToast({ message: '登录成功', type: 'success', duration: 1500 })

      // 根据角色跳转不同页面
      setTimeout(() => {
        if (data.role === 'admin') {
          router.push('/admin/dashboard')
        } else {
          router.push('/profile')
        }
      }, 1500)
    } else {
      errorMessage.value = res.data.message
      showError.value = true
    }
  } catch (error) {
    errorMessage.value = '登录失败，请检查网络'
    showError.value = true
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
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
  font-size: 60px;
  animation: float 3s ease-in-out infinite;
}

.flower-2 {
  position: absolute;
  top: 60%;
  right: 8%;
  font-size: 80px;
  animation: float 4s ease-in-out infinite;
}

.flower-3 {
  position: absolute;
  bottom: 15%;
  left: 10%;
  font-size: 50px;
  animation: float 3.5s ease-in-out infinite;
}

.flower-4 {
  position: absolute;
  top: 30%;
  right: 15%;
  font-size: 45px;
  animation: float 2.5s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* 登录卡片 */
.login-card {
  width: 85%;
  max-width: 400px;
  background: white;
  border-radius: 30px;
  padding: 40px 30px;
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
  margin-bottom: 40px;
}

.logo-icon {
  font-size: 60px;
  margin-bottom: 10px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.logo h1 {
  font-size: 32px;
  color: #2c3e50;
  margin: 10px 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo p {
  color: #999;
  font-size: 14px;
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

:deep(.van-field__label) {
  color: #333;
  font-weight: 500;
}

/* 忘记密码 */
.forgot-pwd {
  text-align: right;
  margin: 10px 0 20px;
}

.forgot-pwd span {
  font-size: 13px;
  color: #999;
  cursor: pointer;
}

.forgot-pwd span:hover {
  color: #764ba2;
}

/* 登录按钮 */
.btn-wrapper {
  margin: 30px 0 20px;
}

.login-btn {
  height: 48px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  font-size: 18px;
  font-weight: bold;
  transition: transform 0.2s;
}

.login-btn:active {
  transform: scale(0.98);
}

/* 注册链接 */
.register-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #764ba2;
  text-decoration: none;
  font-weight: bold;
}

.register-link a:hover {
  text-decoration: underline;
}



.divider::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 100%;
  height: 1px;
  background: #e0e0e0;
}

.divider span {
  background: white;
  padding: 0 15px;
  position: relative;
  font-size: 13px;
  color: #999;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.social-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.social-icon:hover {
  transform: translateY(-5px);
}

.social-icon span:first-child {
  font-size: 30px;
}

.social-icon span:last-child {
  font-size: 12px;
  color: #666;
}
</style>