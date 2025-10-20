<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, NCard, useMessage } from 'naive-ui'
import { useAuthStore } from '../store/auth'
import type { RegisterForm } from '../types'

const router = useRouter()
const authStore = useAuthStore()
const message = useMessage()

const form = ref<RegisterForm>({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})
const loading = ref(false)

async function handleRegister() {
  if (!form.value.username || !form.value.email || !form.value.password) {
    message.error('请填写所有必填字段')
    return
  }

  if (form.value.password !== form.value.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }

  if (form.value.password.length < 6) {
    message.error('密码长度至少6位')
    return
  }

  loading.value = true

  try {
    // 模拟注册成功
    const mockUser = {
      id: 'new-user-' + Date.now(),
      username: form.value.username,
      email: form.value.email,
      followers: 0,
      following: 0,
      createdAt: new Date().toISOString()
    }

    authStore.setUser(mockUser)
    authStore.setToken('mock-token-' + Date.now())

    message.success('注册成功')
    router.push('/')
  } catch (error) {
    message.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function goToLogin() {
  router.push('/login')
}
</script>

<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <n-card style="width: 400px; max-width: 90vw;">
      <template #header>
        <h2 style="text-align: center; margin: 0; color: #18a058;">注册博客社区</h2>
      </template>

      <n-form :model="form">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" />
        </n-form-item>

        <n-form-item label="邮箱" path="email">
          <n-input v-model:value="form.email" placeholder="请输入邮箱" />
        </n-form-item>

        <n-form-item label="密码" path="password">
          <n-input v-model:value="form.password" type="password" placeholder="请输入密码" />
        </n-form-item>

        <n-form-item label="确认密码" path="confirmPassword">
          <n-input v-model:value="form.confirmPassword" type="password" placeholder="请再次输入密码" />
        </n-form-item>

        <n-button
          type="primary"
          :loading="loading"
          @click="handleRegister"
          style="width: 100%; margin-top: 20px;"
        >
          注册
        </n-button>

        <div style="text-align: center; margin-top: 20px;">
          <span style="color: #666;">已有账号？</span>
          <n-button text type="primary" @click="goToLogin">
            立即登录
          </n-button>
        </div>
      </n-form>
    </n-card>
  </div>
</template>