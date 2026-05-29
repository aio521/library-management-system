<template>
  <div class="login-page">
    <div class="login-bg"><div class="bg-pattern"></div><div class="bg-overlay"></div></div>

    <div class="login-wrapper">
      <div class="login-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="6" y="8" width="36" height="32" rx="3" stroke="currentColor" stroke-width="2.5" fill="none"/>
            <line x1="16" y1="8" x2="16" y2="40" stroke="currentColor" stroke-width="2"/>
            <rect x="20" y="14" width="10" height="3" rx="1.5" fill="currentColor" opacity="0.6"/>
            <rect x="20" y="20" width="18" height="3" rx="1.5" fill="currentColor" opacity="0.6"/>
            <rect x="20" y="26" width="14" height="3" rx="1.5" fill="currentColor" opacity="0.6"/>
          </svg>
        </div>
        <h1>图书管理系统</h1>
        <p>新疆财经大学</p>
      </div>

      <div class="login-card">
        <div class="card-header">
          <h2>读者注册</h2>
          <p>创建您的图书馆账号</p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" class="login-form" label-position="top">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" size="large">
              <template #append>
                <el-button :disabled="countdown > 0" @click="sendCode" :loading="sending" class="code-btn">
                  {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="验证码" prop="code">
            <el-input v-model="form.code" placeholder="请输入6位验证码" maxlength="6" size="large" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="6-20位密码" size="large" show-password />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" size="large" show-password />
          </el-form-item>

          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入真实姓名" size="large" />
          </el-form-item>

          <el-form-item label="院系" prop="dept">
            <el-input v-model="form.dept" placeholder="如：金融学院" size="large" />
          </el-form-item>

          <el-form-item label="学号（可选）">
            <el-input v-model="form.readerNo" placeholder="不填则自动生成" size="large" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleRegister" class="login-btn" size="large">
              注 册
            </el-button>
          </el-form-item>
        </el-form>
        <div class="card-footer">
          已有账号？<router-link to="/login">返回登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const sending = ref(false)
const countdown = ref(0)
let timer: ReturnType<typeof setInterval> | null = null

const form = reactive({
  phone: '', code: '', password: '', confirmPassword: '', name: '', dept: '', readerNo: ''
})

const validateConfirm = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  dept: [{ required: true, message: '请输入院系', trigger: 'blur' }]
}

async function sendCode() {
  const valid = await formRef.value.validateField('phone').catch(() => false)
  if (!valid) return
  sending.value = true
  try {
    await request.post('/auth/send-code', { phone: form.phone })
    ElMessage.success('验证码已发送，请查看控制台日志')
    countdown.value = 60
    timer = setInterval(() => { countdown.value--; if (countdown.value <= 0) { clearInterval(timer!); timer = null } }, 1000)
  } catch { /* handled in interceptor */ }
  finally { sending.value = false }
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const { confirmPassword, ...registerData } = form
    await request.post('/auth/register', registerData)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch { /* handled in interceptor */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; position: relative; overflow: hidden; }
.login-bg { position: absolute; inset: 0; background: linear-gradient(160deg, #1A2F2A 0%, #243D35 30%, #3D5A4F 60%, #5A7D6A 100%); }
.bg-pattern { position: absolute; inset: 0; background-image: radial-gradient(circle at 20% 80%, rgba(212,168,83,0.08) 0%, transparent 50%), radial-gradient(circle at 80% 20%, rgba(212,168,83,0.05) 0%, transparent 50%); }
.bg-overlay { position: absolute; inset: 0; backdrop-filter: blur(0.5px); }

.login-wrapper { position: relative; z-index: 1; display: flex; align-items: center; gap: 64px; }
.login-brand { color: #F7F4EE; text-align: right; }
.brand-icon { width: 64px; height: 64px; margin-left: auto; margin-bottom: 20px; color: #D4A853; }
.brand-icon svg { width: 100%; height: 100%; }
.login-brand h1 { font-size: 32px; font-weight: 700; letter-spacing: 0.04em; margin: 0 0 8px; color: #F7F4EE; }
.login-brand p { font-size: 16px; color: rgba(247,244,238,0.6); letter-spacing: 0.08em; }

.login-card { width: 440px; padding: 36px 40px 32px; background: #FFFFFF; border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); max-height: 90vh; overflow-y: auto; }
.card-header { margin-bottom: 24px; }
.card-header h2 { font-size: 22px; font-weight: 700; color: #1A2F2A; margin: 0 0 4px; }
.card-header p { font-size: 14px; color: #9B8E78; margin: 0; }

.login-form :deep(.el-form-item) { margin-bottom: 16px; }
.login-form :deep(.el-form-item__label) { color: #6B5E48; font-weight: 500; font-size: 13px; padding-bottom: 4px; }
.login-form :deep(.el-input__wrapper) { padding: 4px 12px; background: #F9F7F2; border-radius: 8px; }
.login-form :deep(.el-input__wrapper:hover) { background: #F3EFE6; }
.login-form :deep(.el-input-group__append) { background: transparent; border: none; padding: 0; }
.code-btn { border-radius: 0 8px 8px 0 !important; height: 100% !important; white-space: nowrap; font-size: 13px; }

.login-btn { width: 100%; height: 46px; font-size: 16px; letter-spacing: 0.08em; border-radius: 8px; }
.card-footer { text-align: center; font-size: 14px; color: #9B8E78; }
.card-footer a { color: #8B6914; font-weight: 600; text-decoration: none; }
.card-footer a:hover { color: #A67C1E; }

@media (max-width: 768px) {
  .login-wrapper { flex-direction: column; gap: 32px; padding: 24px; }
  .login-brand { text-align: center; }
  .brand-icon { margin: 0 auto 16px; }
  .login-card { width: 100%; max-width: 440px; padding: 24px; }
}
</style>
