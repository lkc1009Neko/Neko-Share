<script setup lang="ts">
import { useAuthStore } from './store/auth'
import { onMounted } from 'vue'
import { NNotificationProvider, NMessageProvider, NConfigProvider } from 'naive-ui'
import Live2DWidget from './components/Live2DWidget.vue'

const authStore = useAuthStore()

onMounted(() => {
  // 检查本地存储的token
  if (authStore.token) {
    // 这里可以添加token验证逻辑
    console.log('用户已登录')
  }
})
</script>

<template>
  <n-config-provider>
    <n-notification-provider>
      <n-message-provider>
        <router-view />
        <Live2DWidget />
      </n-message-provider>
    </n-notification-provider>
  </n-config-provider>
</template>

