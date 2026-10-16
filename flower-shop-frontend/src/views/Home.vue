<template>
  <div class="home-container">
    <div class="hero">
      <h1>🌸 花店</h1>
      <p>鲜花 | 绿植 | 多肉</p>
    </div>

    <div class="button-group">
      <router-link to="/login" class="btn btn-primary">
        登录
      </router-link>
      <router-link to="/register" class="btn btn-secondary">
        注册
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.hero {
  text-align: center;
  color: white;
  margin-bottom: 60px;
}

.hero h1 {
  font-size: 48px;
  margin-bottom: 10px;
}

.hero p {
  font-size: 16px;
  opacity: 0.9;
}

.button-group {
  display: flex;
  gap: 20px;
  flex-direction: column;
  width: 80%;
  max-width: 250px;
}

.btn {
  display: block;
  text-align: center;
  padding: 14px 20px;
  border-radius: 30px;
  text-decoration: none;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s;
}

.btn-primary {
  background: white;
  color: #764ba2;
}

.btn-primary:active {
  transform: scale(0.98);
}

.btn-secondary {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.btn-secondary:active {
  transform: scale(0.98);
}
</style>