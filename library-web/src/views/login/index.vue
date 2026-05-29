<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-pattern"></div>
      <div class="bg-overlay"></div>
    </div>

    <div class="login-wrapper">
      <div class="login-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="6" y="8" width="36" height="32" rx="3" stroke="currentColor" stroke-width="2.5" fill="none"/>
            <line x1="16" y1="8" x2="16" y2="40" stroke="currentColor" stroke-width="2"/>
            <rect x="20" y="14" width="10" height="3" rx="1.5" fill="currentColor" opacity="0.6"/>
            <rect x="20" y="20" width="18" height="3" rx="1.5" fill="currentColor" opacity="0.6"/>
            <rect x="20" y="26" width="14" height="3" rx="1.5" fill="currentColor" opacity="0.6"/>
            <rect x="20" y="32" width="16" height="3" rx="1.5" fill="currentColor" opacity="0.4"/>
          </svg>
        </div>
        <h1>图书管理系统</h1>
        <p>新疆财经大学</p>
      </div>

      <div class="login-card">
        <div class="card-header">
          <h2>欢迎回来</h2>
          <p>请登录您的账号</p>
        </div>
        <el-form :model="loginForm" :rules="rules" ref="formRef" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名 / 手机号"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="login-btn"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="card-footer">
          没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const loginForm = reactive({ username: 'admin', password: 'admin123' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.login(loginForm.username, loginForm.password)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
    // handled in interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, #1A2F2A 0%, #243D35 30%, #3D5A4F 60%, #5A7D6A 100%);
}

.bg-pattern {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 20% 80%, rgba(212, 168, 83, 0.08) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(212, 168, 83, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(255,255,255,0.02) 0%, transparent 70%);
}

.bg-overlay {
  position: absolute;
  inset: 0;
  backdrop-filter: blur(0.5px);
}

.login-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 64px;
}

.login-brand {
  color: #F7F4EE;
  text-align: right;
}

.brand-icon {
  width: 64px;
  height: 64px;
  margin-left: auto;
  margin-bottom: 20px;
  color: #D4A853;
}

.brand-icon svg {
  width: 100%;
  height: 100%;
}

.login-brand h1 {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 0.04em;
  margin: 0 0 8px;
  color: #F7F4EE;
}

.login-brand p {
  font-size: 16px;
  color: rgba(247, 244, 238, 0.6);
  letter-spacing: 0.08em;
}

.login-card {
  width: 400px;
  padding: 48px 40px 40px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.card-header {
  margin-bottom: 32px;
}

.card-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1A2F2A;
  margin: 0 0 6px;
}

.card-header p {
  font-size: 14px;
  color: #9B8E78;
  margin: 0;
}

.login-form :deep(.el-input__wrapper) {
  padding: 4px 12px;
  background: #F9F7F2;
  border-radius: 8px;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  background: #F3EFE6;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 0.08em;
  border-radius: 8px;
  margin-top: 8px;
}

.card-footer {
  text-align: center;
  font-size: 14px;
  color: #9B8E78;
}

.card-footer a {
  color: #8B6914;
  font-weight: 600;
  text-decoration: none;
}

.card-footer a:hover {
  color: #A67C1E;
}

@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    gap: 32px;
    padding: 24px;
  }
  .login-brand {
    text-align: center;
  }
  .brand-icon {
    margin: 0 auto 16px;
  }
  .login-card {
    width: 100%;
    max-width: 400px;
    padding: 32px 24px;
  }
}
</style>
