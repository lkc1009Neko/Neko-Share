<script setup lang="ts">
import { ref, onMounted } from 'vue'

import { NLayout, NLayoutHeader, NLayoutContent, NCard, NList, NListItem, NThing, NBadge, NSpace, NButton, useMessage } from 'naive-ui'
import Header from '../components/Header.vue'
import type { Notification } from '../types'

// const authStore = useAuthStore()
const message = useMessage()

const notifications = ref<Notification[]>([])
const unreadCount = ref(0)

onMounted(() => {
  // 模拟通知数据
  notifications.value = [
    {
      id: '1',
      type: 'like',
      title: '有人点赞了你的文章',
      content: '用户 "techlover" 点赞了你的文章 "Vue 3 组合式 API 最佳实践"',
      isRead: false,
      createdAt: '2024-10-20T10:30:00Z',
      relatedId: '1'
    },
    {
      id: '2',
      type: 'comment',
      title: '新的评论',
      content: '用户 "coder123" 评论了你的文章： "很好的文章，学到了很多！"',
      isRead: false,
      createdAt: '2024-10-20T11:00:00Z',
      relatedId: '1'
    },
    {
      id: '3',
      type: 'follow',
      title: '新的关注者',
      content: '用户 "webdev" 开始关注你',
      isRead: true,
      createdAt: '2024-10-19T14:20:00Z'
    },
    {
      id: '4',
      type: 'system',
      title: '系统通知',
      content: '博客系统已更新到最新版本，新增了多项功能',
      isRead: true,
      createdAt: '2024-10-18T09:00:00Z'
    }
  ]

  unreadCount.value = notifications.value.filter(n => !n.isRead).length
})

function markAsRead(notification: Notification) {
  notification.isRead = true
  unreadCount.value = notifications.value.filter(n => !n.isRead).length
  message.success('标记为已读')
}

function markAllAsRead() {
  notifications.value.forEach(n => { n.isRead = true })
  unreadCount.value = 0
  message.success('全部标记为已读')
}

function clearAll() {
  notifications.value = []
  unreadCount.value = 0
  message.success('已清空所有通知')
}

function getNotificationIcon(type: string) {
  const icons = {
    like: '❤️',
    comment: '💬',
    follow: '👤',
    system: '🔔'
  }
  return icons[type as keyof typeof icons] || '📢'
}
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-header style="padding: 20px; border-bottom: 1px solid #e8e8e8;">
      <n-space justify="space-between" align="center">
        <h1 style="margin: 0; color: #18a058;">
          通知中心
          <n-badge v-if="unreadCount > 0" :value="unreadCount" type="error" />
        </h1>
        <n-space>
          <n-button v-if="unreadCount > 0" @click="markAllAsRead">全部标记已读</n-button>
          <n-button @click="clearAll">清空全部</n-button>
        </n-space>
      </n-space>
    </n-layout-header>

    <n-layout-content style="padding: 20px;" class="container">
      <n-card>
        <n-list>
          <n-list-item v-for="notification in notifications" :key="notification.id">
            <n-thing>
              <template #avatar>
                <span style="font-size: 24px;">{{ getNotificationIcon(notification.type) }}</span>
              </template>
              
              <template #header>
                <n-space align="center">
                  <span :style="{ fontWeight: notification.isRead ? 'normal' : 'bold' }">
                    {{ notification.title }}
                  </span>
                  <n-badge v-if="!notification.isRead" dot type="error" />
                </n-space>
              </template>

              <template #description>
                <p style="margin: 8px 0; color: #666;">{{ notification.content }}</p>
                <span style="color: #999; font-size: 12px;">
                  {{ new Date(notification.createdAt).toLocaleString() }}
                </span>
              </template>

              <template #action>
                <n-space>
                  <n-button 
                    v-if="!notification.isRead" 
                    size="small" 
                    @click="markAsRead(notification)"
                  >
                    标记已读
                  </n-button>
                  <n-button 
                    v-if="notification.relatedId" 
                    size="small" 
                    type="primary"
                  >
                    查看详情
                  </n-button>
                </n-space>
              </template>
            </n-thing>
          </n-list-item>

          <n-list-item v-if="notifications.length === 0">
            <div style="text-align: center; padding: 40px; color: #999;">
              暂无通知
            </div>
          </n-list-item>
        </n-list>
      </n-card>
    </n-layout-content>
  </n-layout>
</template>