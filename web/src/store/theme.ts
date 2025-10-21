import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(false)

  // 初始化主题
  function initTheme() {
    // 检查本地存储的主题设置
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      isDark.value = savedTheme === 'dark'
    } else {
      // 如果没有保存的设置，使用系统偏好
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    
    // 应用主题
    applyTheme()
  }

  // 切换主题
  function toggleTheme() {
    isDark.value = !isDark.value
    applyTheme()
    saveTheme()
  }

  // 应用主题到DOM
  function applyTheme() {
    if (isDark.value) {
      document.documentElement.setAttribute('data-theme', 'dark')
    } else {
      document.documentElement.setAttribute('data-theme', 'light')
    }
  }

  // 保存主题设置到本地存储
  function saveTheme() {
    localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
  }

  // 监听系统主题变化
  if (typeof window !== 'undefined') {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    mediaQuery.addEventListener('change', (e) => {
      // 只有当用户没有手动设置主题时才跟随系统
      if (!localStorage.getItem('theme')) {
        isDark.value = e.matches
        applyTheme()
      }
    })
  }

  return {
    isDark,
    initTheme,
    toggleTheme,
    applyTheme,
    saveTheme
  }
})