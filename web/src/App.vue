<script setup lang="ts">
import { useAuthStore } from './store/auth'
import { useThemeStore } from './store/theme'
import { onMounted, computed } from 'vue'
import { NNotificationProvider, NMessageProvider, NConfigProvider, darkTheme } from 'naive-ui'
import Live2DWidget from './components/Live2DWidget.vue'

const authStore = useAuthStore()
const themeStore = useThemeStore()

// 计算当前主题
const currentTheme = computed(() => {
  return themeStore.isDark ? darkTheme : null
})

onMounted(() => {
  // 检查本地存储的token
  if (authStore.token) {
    // 这里可以添加token验证逻辑
    console.log('用户已登录')
  }
  
  // 初始化主题
  themeStore.initTheme()
})
</script>

<template>
  <n-config-provider :theme="currentTheme">
    <n-notification-provider>
      <n-message-provider>
        <router-view />
        <Live2DWidget />
      </n-message-provider>
    </n-notification-provider>
  </n-config-provider>
</template>

