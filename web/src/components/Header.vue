<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { NLayoutHeader, NSpace, NButton, NAvatar, NBadge, NDropdown, useMessage } from 'naive-ui'
import { PersonOutline, NotificationsOutline, CreateOutline, HomeOutline, AppsOutline, ShareSocialOutline, BookOutline, ExtensionPuzzleOutline, GameControllerOutline, MusicalNotesOutline } from '@vicons/ionicons5'

const showMobileMenu = ref(false)

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const message = useMessage()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const currentUser = computed(() => authStore.user)
const unreadNotifications = computed(() => 3) // 模拟未读通知数量

const userDropdownOptions = [
  {
    label: '个人主页',
    key: 'profile'
  },
  {
    label: '写文章',
    key: 'write'
  },
  {
    label: '设置',
    key: 'settings'
  },
  {
    type: 'divider',
    key: 'divider'
  },
  {
    label: '退出登录',
    key: 'logout'
  }
]

function handleUserAction(key: string) {
  switch (key) {
    case 'profile':
      if (currentUser.value) {
        router.push(`/profile/${currentUser.value.username}`)
      }
      break
    case 'write':
      if (isAuthenticated.value) {
        router.push('/write')
      } else {
        message.error('请先登录')
        router.push('/login')
      }
      break
    case 'settings':
      message.info('设置功能开发中...')
      break
    case 'logout':
      authStore.logout()
      message.success('已退出登录')
      router.push('/')
      break
  }
}

function goToLogin() {
  router.push('/login')
}

function goToRegister() {
  router.push('/register')
}

function goToNotifications() {
  if (isAuthenticated.value) {
    router.push('/notifications')
  } else {
    message.error('请先登录')
    router.push('/login')
  }
}

function goToHome() {
  router.push('/')
}

function goToCategories() {
  router.push('/categories')
}

function goToResources() {
  router.push('/resources')
}

function goToBooks() {
  router.push('/books')
}

function goToPlugins() {
  router.push('/plugins')
}

function goToGames() {
  router.push('/games')
}

function goToVoices() {
  router.push('/voices')
}

// 判断当前路由是否激活
const isActiveRoute = (path: string) => {
  return route.path === path
}
</script>

<template>
  <n-layout-header class="header">
    <div class="header-content">
      <!-- 左侧Logo和导航 -->
      <div class="nav-left">
        <n-button text @click="goToHome" class="logo">
          博客社区
        </n-button>
        
        <!-- 桌面端导航菜单 -->
        <div class="desktop-nav">
          <n-button 
            text 
            @click="goToHome" 
            class="nav-button"
            :type="isActiveRoute('/') ? 'primary' : 'default'"
          >
            <template #icon>
              <n-icon :component="HomeOutline" />
            </template>
            主页
          </n-button>
          <n-button 
            text 
            @click="goToCategories" 
            class="nav-button"
            :type="isActiveRoute('/categories') ? 'primary' : 'default'"
          >
            <template #icon>
              <n-icon :component="AppsOutline" />
            </template>
            文章分类
          </n-button>
          <n-button 
            text 
            @click="goToResources" 
            class="nav-button"
            :type="isActiveRoute('/resources') ? 'primary' : 'default'"
          >
            <template #icon>
              <n-icon :component="ShareSocialOutline" />
            </template>
            资源分享
          </n-button>
          <n-button 
            text 
            @click="goToBooks" 
            class="nav-button"
            :type="isActiveRoute('/books') ? 'primary' : 'default'"
          >
            <template #icon>
              <n-icon :component="BookOutline" />
            </template>
            书籍
          </n-button>
          <n-button 
            text 
            @click="goToPlugins" 
            class="nav-button"
            :type="isActiveRoute('/plugins') ? 'primary' : 'default'"
          >
            <template #icon>
              <n-icon :component="ExtensionPuzzleOutline" />
            </template>
            插件
          </n-button>
          <n-button 
            text 
            @click="goToGames" 
            class="nav-button"
            :type="isActiveRoute('/games') ? 'primary' : 'default'"
          >
            <template #icon>
              <n-icon :component="GameControllerOutline" />
            </template>
            游戏
          </n-button>
          <n-button 
            text 
            @click="goToVoices" 
            class="nav-button"
            :type="isActiveRoute('/voices') ? 'primary' : 'default'"
          >
            <template #icon>
              <n-icon :component="MusicalNotesOutline" />
            </template>
            音声
          </n-button>
        </div>
      </div>

      <!-- 桌面端操作区 -->
      <n-space align="center" class="desktop-actions">
        <!-- 通知按钮 -->
        <n-badge :value="unreadNotifications" :max="99" v-if="isAuthenticated">
          <n-button text @click="goToNotifications" class="action-button">
            <template #icon>
              <n-icon :component="NotificationsOutline" />
            </template>
            <span class="action-text">通知</span>
          </n-button>
        </n-badge>

        <!-- 写文章按钮 -->
        <n-button 
          type="primary" 
          @click="handleUserAction('write')"
          v-if="isAuthenticated"
          class="write-button"
        >
          <template #icon>
            <n-icon :component="CreateOutline" />
          </template>
          <span class="action-text">写文章</span>
        </n-button>

        <!-- 用户登录状态 -->
        <template v-if="isAuthenticated">
          <n-dropdown
            :options="userDropdownOptions"
            @select="handleUserAction"
            placement="bottom-end"
          >
            <n-button text class="user-button">
              <n-space align="center">
                <n-avatar round size="small">
                  <template #fallback>
                    <n-icon :component="PersonOutline" />
                  </template>
                </n-avatar>
                <span class="username">{{ currentUser?.username }}</span>
              </n-space>
            </n-button>
          </n-dropdown>
        </template>

        <!-- 未登录状态 -->
        <template v-else>
          <n-button text @click="goToLogin" class="login-button">登录</n-button>
          <n-button type="primary" @click="goToRegister" class="register-button">注册</n-button>
        </template>
      </n-space>

      <!-- 移动端菜单按钮 -->
      <n-button 
        text 
        class="mobile-menu-button"
        @click="showMobileMenu = !showMobileMenu"
      >
        <n-icon>
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
            <path fill="currentColor" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
          </svg>
        </n-icon>
      </n-button>
    </div>

    <!-- 移动端菜单 -->
    <div v-if="showMobileMenu" class="mobile-menu">
      <div class="mobile-menu-content">
        <!-- 移动端导航菜单 -->
        <div class="mobile-nav">
          <n-button 
            text 
            @click="goToHome" 
            class="mobile-nav-button"
            :type="isActiveRoute('/') ? 'primary' : 'default'"
            block
          >
            <template #icon>
              <n-icon :component="HomeOutline" />
            </template>
            主页
          </n-button>
          <n-button 
            text 
            @click="goToCategories" 
            class="mobile-nav-button"
            :type="isActiveRoute('/categories') ? 'primary' : 'default'"
            block
          >
            <template #icon>
              <n-icon :component="AppsOutline" />
            </template>
            文章分类
          </n-button>
          <n-button 
            text 
            @click="goToResources" 
            class="mobile-nav-button"
            :type="isActiveRoute('/resources') ? 'primary' : 'default'"
            block
          >
            <template #icon>
              <n-icon :component="ShareSocialOutline" />
            </template>
            资源分享
          </n-button>
          <n-button 
            text 
            @click="goToBooks" 
            class="mobile-nav-button"
            :type="isActiveRoute('/books') ? 'primary' : 'default'"
            block
          >
            <template #icon>
              <n-icon :component="BookOutline" />
            </template>
            书籍
          </n-button>
          <n-button 
            text 
            @click="goToPlugins" 
            class="mobile-nav-button"
            :type="isActiveRoute('/plugins') ? 'primary' : 'default'"
            block
          >
            <template #icon>
              <n-icon :component="ExtensionPuzzleOutline" />
            </template>
            插件
          </n-button>
          <n-button 
            text 
            @click="goToGames" 
            class="mobile-nav-button"
            :type="isActiveRoute('/games') ? 'primary' : 'default'"
            block
          >
            <template #icon>
              <n-icon :component="GameControllerOutline" />
            </template>
            游戏
          </n-button>
          <n-button 
            text 
            @click="goToVoices" 
            class="mobile-nav-button"
            :type="isActiveRoute('/voices') ? 'primary' : 'default'"
            block
          >
            <template #icon>
              <n-icon :component="MusicalNotesOutline" />
            </template>
            音声
          </n-button>
        </div>

        <!-- 通知按钮 -->
        <n-badge :value="unreadNotifications" :max="99" v-if="isAuthenticated">
          <n-button text @click="goToNotifications" class="mobile-action-button" block>
            <template #icon>
              <n-icon :component="NotificationsOutline" />
            </template>
            通知
          </n-button>
        </n-badge>

        <!-- 写文章按钮 -->
        <n-button 
          type="primary" 
          @click="handleUserAction('write')"
          v-if="isAuthenticated"
          class="mobile-write-button"
          block
        >
          <template #icon>
            <n-icon :component="CreateOutline" />
          </template>
          写文章
        </n-button>

        <!-- 用户菜单 -->
        <template v-if="isAuthenticated">
          <n-button 
            text 
            @click="handleUserAction('profile')" 
            class="mobile-action-button"
            block
          >
            <template #icon>
              <n-icon :component="PersonOutline" />
            </template>
            个人主页
          </n-button>
          <n-button 
            text 
            @click="handleUserAction('settings')" 
            class="mobile-action-button"
            block
          >
            <template #icon>
              <n-icon>
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                  <path fill="currentColor" d="M19.14 12.94c.04-.3.06-.61.06-.94c0-.32-.02-.64-.07-.94l2.03-1.58a.49.49 0 0 0 .12-.61l-1.92-3.32a.488.488 0 0 0-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54a.484.484 0 0 0-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58a.49.49 0 0 0-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6s3.6 1.62 3.6 3.6s-1.62 3.6-3.6 3.6z"/>
                </svg>
              </n-icon>
            </template>
            设置
          </n-button>
          <n-button 
            text 
            @click="handleUserAction('logout')" 
            class="mobile-action-button"
            block
          >
            <template #icon>
              <n-icon>
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                  <path fill="currentColor" d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
                </svg>
              </n-icon>
            </template>
            退出登录
          </n-button>
        </template>

        <!-- 未登录状态 -->
        <template v-else>
          <n-button text @click="goToLogin" class="mobile-action-button" block>登录</n-button>
          <n-button type="primary" @click="goToRegister" class="mobile-register-button" block>注册</n-button>
        </template>
      </div>
    </div>
  </n-layout-header>
</template>

<style scoped>
.header {
  padding: 0 20px;
  border-bottom: 1px solid #e8e8e8;
  height: 64px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #18a058;
}

.desktop-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-button {
  padding: 8px 16px;
  font-weight: 500;
}

.nav-button:deep(.n-button__content) {
  gap: 6px;
}

.desktop-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-button,
.user-button,
.login-button,
.register-button {
  padding: 8px 12px;
}

.write-button {
  padding: 8px 16px;
}

.username {
  color: #333;
  font-weight: 500;
}

.action-text {
  margin-left: 4px;
}

.mobile-menu-button {
  display: none;
  padding: 8px;
}

.mobile-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.mobile-menu-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.mobile-nav-button {
  justify-content: flex-start;
  padding: 12px 16px;
}

.mobile-nav-button:deep(.n-button__content) {
  gap: 8px;
  justify-content: flex-start;
}

.mobile-action-button,
.mobile-write-button,
.mobile-register-button {
  justify-content: flex-start;
  padding: 12px 16px;
}

.mobile-write-button,
.mobile-register-button {
  margin-top: 8px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .header {
    padding: 0 16px;
    height: 56px;
  }

  .nav-left .desktop-nav {
    display: none;
  }

  .desktop-actions {
    display: none;
  }

  .mobile-menu-button {
    display: flex;
  }

  .logo {
    font-size: 18px;
  }

  .nav-left {
    gap: 0;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 12px;
  }

  .mobile-menu-content {
    padding: 12px;
  }
}
</style>