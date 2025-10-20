<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { NLayout, NLayoutHeader, NLayoutContent, NCard, NAvatar, NSpace, NButton, NIcon, NInput, NText, useMessage } from 'naive-ui'
import { ArrowBackOutline, HeartOutline, BookmarkOutline, ShareSocialOutline, ChatbubbleOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import type { Article } from '../types'

const route = useRoute()
const message = useMessage()
const articleId = route.params.id as string

const article = ref<Article | null>(null)
const comments = ref<any[]>([])
const newComment = ref('')

onMounted(() => {
  // 模拟文章数据
  article.value = {
    id: articleId,
    title: 'Vue 3 组合式 API 最佳实践',
    content: `
      <h1>Vue 3 组合式 API 最佳实践</h1>
      <p>Vue 3 的组合式 API 提供了更好的代码组织和复用性，本文将介绍一些最佳实践。</p>
      
      <h2>1. 使用 ref 和 reactive</h2>
      <p>在组合式 API 中，推荐使用 ref 和 reactive 来管理响应式状态。</p>
      
      <h2>2. 组合式函数的封装</h2>
      <p>将相关的逻辑封装成组合式函数，提高代码的可复用性。</p>
      
      <h2>3. 生命周期钩子的使用</h2>
      <p>合理使用 onMounted、onUnmounted 等生命周期钩子。</p>
    `,
    excerpt: 'Vue 3 的组合式 API 提供了更好的代码组织和复用性，本文将介绍一些最佳实践。',
    author: {
      id: '1',
      username: 'vuefan',
      email: 'vuefan@example.com',
      followers: 123,
      following: 45,
      createdAt: '2024-01-01'
    },
    tags: ['Vue', 'JavaScript', '前端'],
    likes: 42,
    comments: 8,
    views: 156,
    isLiked: false,
    isBookmarked: false,
    createdAt: '2024-10-20T10:00:00Z',
    updatedAt: '2024-10-20T10:00:00Z'
  }

  // 模拟评论数据
  comments.value = [
    {
      id: '1',
      author: { username: 'user1', avatar: '' },
      content: '很好的文章，学到了很多！',
      createdAt: '2024-10-20T11:00:00Z'
    },
    {
      id: '2',
      author: { username: 'user2', avatar: '' },
      content: '组合式 API 确实比选项式 API 更灵活',
      createdAt: '2024-10-20T12:00:00Z'
    }
  ]
})

function handleLike() {
  if (article.value) {
    article.value.isLiked = !article.value.isLiked
    article.value.likes += article.value.isLiked ? 1 : -1
    message.success(article.value.isLiked ? '已点赞' : '已取消点赞')
  }
}

function handleBookmark() {
  if (article.value) {
    article.value.isBookmarked = !article.value.isBookmarked
    message.success(article.value.isBookmarked ? '已收藏' : '已取消收藏')
  }
}

function handleShare() {
  navigator.clipboard.writeText(window.location.href)
  message.success('链接已复制到剪贴板')
}

function submitComment() {
  if (!newComment.value.trim()) {
    message.error('请输入评论内容')
    return
  }

  comments.value.unshift({
    id: 'new-' + Date.now(),
    author: { username: '当前用户', avatar: '' },
    content: newComment.value,
    createdAt: new Date().toISOString()
  })

  if (article.value) {
    article.value.comments += 1
  }

  newComment.value = ''
  message.success('评论发表成功')
}
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-header style="padding: 16px 20px; background: white; border-bottom: 1px solid #e8e8e8;">
      <n-space justify="space-between" align="center">
        <n-button text @click="$router.back()">
          <n-icon :component="ArrowBackOutline" />
          返回
        </n-button>
        <n-space>
          <n-button text @click="handleLike">
            <n-icon :component="HeartOutline" :color="article?.isLiked ? 'red' : undefined" />
            {{ article?.likes }}
          </n-button>
          <n-button text @click="handleBookmark">
            <n-icon :component="BookmarkOutline" :color="article?.isBookmarked ? 'gold' : undefined" />
          </n-button>
          <n-button text @click="handleShare">
            <n-icon :component="ShareSocialOutline" />
            分享
          </n-button>
        </n-space>
      </n-space>
    </n-layout-header>

    <n-layout-content style="padding: 20px;" class="container">
      <n-card v-if="article">
        <template #header>
          <h1 style="margin: 0 0 16px 0;">{{ article.title }}</h1>
          <n-space justify="space-between" align="center">
            <n-space align="center">
              <n-avatar round size="small" />
              <n-text style="color: #18a058; cursor: pointer;" @click="$router.push(`/profile/${article.author.username}`)">
                @{{ article.author.username }}
              </n-text>
              <n-text style="color: #999; font-size: 14px;">
                {{ new Date(article.createdAt).toLocaleDateString() }}
              </n-text>
            </n-space>
            <n-space>
              <n-tag v-for="tag in article.tags" :key="tag" size="small" type="primary">
                {{ tag }}
              </n-tag>
            </n-space>
          </n-space>
        </template>

        <div v-html="article.content" class="article-content"></div>

        <template #action>
          <n-space justify="space-between" align="center" style="padding-top: 20px; border-top: 1px solid #f0f0f0;">
            <n-space>
              <n-button text @click="handleLike">
                <n-icon :component="HeartOutline" :color="article.isLiked ? 'red' : undefined" />
                {{ article.likes }} 点赞
              </n-button>
              <n-button text>
                <n-icon :component="ChatbubbleOutline" />
                {{ article.comments }} 评论
              </n-button>
            </n-space>
            <n-button text @click="handleBookmark">
              <n-icon :component="BookmarkOutline" :color="article.isBookmarked ? 'gold' : undefined" />
              {{ article.isBookmarked ? '已收藏' : '收藏' }}
            </n-button>
          </n-space>
        </template>
      </n-card>

      <!-- 评论区域 -->
      <n-card style="margin-top: 20px;">
        <template #header>
          <h3 style="margin: 0;">评论 ({{ article?.comments || 0 }})</h3>
        </template>

        <n-input
          v-model:value="newComment"
          type="textarea"
          placeholder="写下你的评论..."
          :rows="3"
          style="margin-bottom: 16px;"
        />
        <n-button type="primary" @click="submitComment">发表评论</n-button>

        <div style="margin-top: 20px;">
          <div v-for="comment in comments" :key="comment.id" style="padding: 12px 0; border-bottom: 1px solid #f0f0f0;">
            <n-space align="center" style="margin-bottom: 8px;">
              <n-avatar round size="small" />
              <n-text strong>@{{ comment.author.username }}</n-text>
              <n-text style="color: #999; font-size: 12px;">
                {{ new Date(comment.createdAt).toLocaleString() }}
              </n-text>
            </n-space>
            <p style="margin: 0; color: #333;">{{ comment.content }}</p>
          </div>
        </div>
      </n-card>
    </n-layout-content>
  </n-layout>
</template>