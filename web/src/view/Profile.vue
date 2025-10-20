<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { NLayout, NLayoutHeader, NLayoutContent, NCard, NAvatar, NButton, NSpace, NTabs, NTabPane, NGrid, NGi, NStatistic, useMessage } from 'naive-ui'
import { PersonOutline, HeartOutline, EyeOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import type { Article, User } from '../types'

const route = useRoute()
const authStore = useAuthStore()
const message = useMessage()
const username = route.params.username as string

const user = ref<User | null>(null)
const articles = ref<Article[]>([])
const activeTab = ref('articles')
const isFollowing = ref(false)

const isOwnProfile = computed(() => {
  return authStore.user && authStore.user.username === username
})

onMounted(() => {
  // 模拟用户数据
  user.value = {
    id: '1',
    username: username,
    email: `${username}@example.com`,
    avatar: '',
    bio: '热爱编程的技术爱好者，专注于前端开发和用户体验设计。',
    followers: 123,
    following: 45,
    createdAt: '2024-01-01'
  }

  // 模拟文章数据
  articles.value = [
    {
      id: '1',
      title: 'Vue 3 组合式 API 最佳实践',
      content: '...',
      excerpt: 'Vue 3 的组合式 API 提供了更好的代码组织和复用性...',
      author: user.value,
      tags: ['Vue', 'JavaScript'],
      likes: 42,
      comments: 8,
      views: 156,
      isLiked: false,
      isBookmarked: false,
      createdAt: '2024-10-20T10:00:00Z',
      updatedAt: '2024-10-20T10:00:00Z'
    },
    {
      id: '2',
      title: 'TypeScript 类型系统深入解析',
      content: '...',
      excerpt: 'TypeScript 的类型系统非常强大...',
      author: user.value,
      tags: ['TypeScript'],
      likes: 31,
      comments: 5,
      views: 98,
      isLiked: true,
      isBookmarked: false,
      createdAt: '2024-10-19T15:30:00Z',
      updatedAt: '2024-10-19T15:30:00Z'
    }
  ]
})

function handleFollow() {
  if (!authStore.isAuthenticated) {
    message.error('请先登录')
    return
  }

  isFollowing.value = !isFollowing.value
  if (user.value) {
    user.value.followers += isFollowing.value ? 1 : -1
  }
  message.success(isFollowing.value ? '关注成功' : '已取消关注')
}

function handleEditProfile() {
  message.info('编辑个人资料功能开发中...')
}
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-header style="padding: 20px; background: white; border-bottom: 1px solid #e8e8e8;">
      <n-space justify="space-between" align="center">
        <h1 style="margin: 0; color: #18a058;">个人主页</h1>
      </n-space>
    </n-layout-header>

    <n-layout-content style="padding: 20px;" class="container">
      <n-card>
        <n-space vertical size="large">
          <!-- 用户信息卡片 -->
          <n-card>
            <n-space align="center" justify="space-between">
              <n-space align="center" size="large">
                <n-avatar round size="large" :src="user?.avatar">
                  <template #fallback>
                    <PersonOutline />
                  </template>
                </n-avatar>
                <n-space vertical>
                  <h2 style="margin: 0;">@{{ user?.username }}</h2>
                  <p style="margin: 0; color: #666; max-width: 400px;">{{ user?.bio }}</p>
                  <n-space>
                    <n-statistic label="粉丝" :value="user?.followers || 0" />
                    <n-statistic label="关注" :value="user?.following || 0" />
                    <n-statistic label="文章" :value="articles.length" />
                  </n-space>
                </n-space>
              </n-space>
              
              <n-space v-if="!isOwnProfile">
                <n-button type="primary" @click="handleFollow">
                  {{ isFollowing ? '已关注' : '关注' }}
                </n-button>
              </n-space>
              <n-space v-else>
                <n-button @click="handleEditProfile">编辑资料</n-button>
              </n-space>
            </n-space>
          </n-card>

          <!-- 内容区域 -->
          <n-tabs v-model:value="activeTab" type="line">
            <n-tab-pane name="articles" tab="文章">
              <n-grid :cols="1" :x-gap="16" :y-gap="16">
                <n-gi v-for="article in articles" :key="article.id">
                  <n-card>
                    <template #header>
                      <n-space justify="space-between" align="center">
                        <h3 style="margin: 0; cursor: pointer;" @click="$router.push(`/article/${article.id}`)">
                          {{ article.title }}
                        </h3>
                        <n-space>
                          <n-button text>
                            <n-icon :component="HeartOutline" />
                            {{ article.likes }}
                          </n-button>
                          <n-button text>
                            <n-icon :component="EyeOutline" />
                            {{ article.views }}
                          </n-button>
                        </n-space>
                      </n-space>
                    </template>

                    <p style="color: #666; margin-bottom: 16px;">{{ article.excerpt }}</p>

                    <n-space justify="space-between" align="center">
                      <n-space>
                        <n-tag v-for="tag in article.tags" :key="tag" size="small" type="primary">
                          {{ tag }}
                        </n-tag>
                      </n-space>
                      <span style="color: #999; font-size: 12px;">
                        {{ new Date(article.createdAt).toLocaleDateString() }}
                      </span>
                    </n-space>
                  </n-card>
                </n-gi>
              </n-grid>
            </n-tab-pane>

            <n-tab-pane name="likes" tab="点赞">
              <div style="text-align: center; padding: 40px; color: #999;">
                点赞文章功能开发中...
              </div>
            </n-tab-pane>

            <n-tab-pane name="bookmarks" tab="收藏">
              <div style="text-align: center; padding: 40px; color: #999;">
                收藏文章功能开发中...
              </div>
            </n-tab-pane>
          </n-tabs>
        </n-space>
      </n-card>
    </n-layout-content>
  </n-layout>
</template>