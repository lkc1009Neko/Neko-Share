<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, NCard, useMessage } from 'naive-ui'
import { useAuthStore } from '../store/auth'
import type { LoginForm } from '../types'

const router = useRouter()
const authStore = useAuthStore()
const message = useMessage()

const form = ref<LoginForm>({
  username: '',
  password: ''
})
const loading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    message.error('请填写用户名和密码')
    return
  }

  loading.value = true

  try {
    // 模拟登录成功
    const mockUser = {
      id: '1',
      username: form.value.username,
      email: `${form.value.username}@example.com`,
      followers: 0,
      following: 0,
      createdAt: new Date().toISOString()
    }

    authStore.setUser(mockUser)
    authStore.setToken('mock-token-' + Date.now())

    message.success('登录成功')
    router.push('/')
  } catch (error) {
    message.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

function goToRegister() {
  router.push('/register')
}
</script>

<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <n-card style="width: 400px; max-width: 90vw;">
      <template #header>
        <h2 style="text-align: center; margin: 0; color: #18a058;">登录博客社区</h2>
      </template>

      <n-form :model="form">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" />
        </n-form-item>

        <n-form-item label="密码" path="password">
          <n-input v-model:value="form.password" type="password" placeholder="请输入密码" />
        </n-form-item>

        <n-button
          type="primary"
          :loading="loading"
          @click="handleLogin"
          style="width: 100%; margin-top: 20px;"
        >
          登录
        </n-button>

        <div style="text-align: center; margin-top: 20px;">
          <span style="color: #666;">还没有账号？</span>
          <n-button text type="primary" @click="goToRegister">
            立即注册
          </n-button>
        </div>
      </n-form>
    </n-card>
  </div>
</template>