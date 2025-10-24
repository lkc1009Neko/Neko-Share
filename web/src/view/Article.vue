<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { NLayout, NLayoutHeader, NLayoutContent, NCard, NAvatar, NSpace, NButton, NIcon, NInput, NText, useMessage } from 'naive-ui'
import { ArrowBackOutline, HeartOutline, BookmarkOutline, ShareSocialOutline, ChatbubbleOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import type { Article } from '../types'
import Directory from '../components/Directory.vue'

const route = useRoute()
const message = useMessage()
const articleId = route.params.id as string

const article = ref<Article | null>(null)
const articleRef = ref<HTMLElement | null>(null)
const comments = ref<any[]>([])
const newComment = ref('')

const isMobile = ref(false)
let isMobileTimer: number = 0

function detectMobile() {
  window.screen.width <= 768 ? isMobile.value = true : isMobile.value = false
}

function onResize() {
  if (isMobileTimer) {
    clearTimeout(isMobileTimer)
  }
  isMobileTimer = setTimeout(() => {
    detectMobile()
  }, 200)
}

onMounted(() => {

  detectMobile()

  window.addEventListener('resize', () => {
    onResize()
  })

  // 模拟文章数据
  article.value = {
    id: articleId,
    title: 'Vue 3 组合式 API 最佳实践',
    // content: `
    //   <h1>Vue 3 组合式 API 最佳实践</h1>
    //   <p>Vue 3 的组合式 API 提供了更好的代码组织和复用性，本文将介绍一些最佳实践。</p>

    //   <h2>1. 使用 ref 和 reactive</h2>
    //   <p>在组合式 API 中，推荐使用 ref 和 reactive 来管理响应式状态。</p>

    //   <h2>2. 组合式函数的封装</h2>
    //   <p>将相关的逻辑封装成组合式函数，提高代码的可复用性。</p>

    //   <h2>3. 生命周期钩子的使用</h2>
    //   <p>合理使用 onMounted、onUnmounted 等生命周期钩子。</p>
    // `,
    content: `
      <h1>Vue 3 组合式 API 最佳实践</h1>
      <p>Vue 3 的组合式 API 提供了更好的代码组织和复用性，本文将介绍一些最佳实践。</p>

      <h2>1. 使用 ref 和 reactive</h2>
      <p>在组合式 API 中，推荐使用 ref 和 reactive 来管理响应式状态。</p>

      <h3>1.1 ref 的使用场景</h3>
      <p>当需要对基础类型数据进行响应式管理时，使用 ref 最为合适。</p>

      <h3>1.2 reactive 的使用场景</h3>
      <p>当需要对对象或数组进行响应式管理时，使用 reactive 更加方便。</p>

      <h2>2. 组合式函数的封装</h2>
      <p>将相关的逻辑封装成组合式函数，提高代码的可复用性。</p>

      <h3>2.1 封装通用逻辑</h3>
      <p>可以将重复使用的状态管理或方法封装为组合函数，方便在多个组件中调用。</p>

      <h3>2.2 封装业务逻辑</h3>
      <p>针对特定业务需求，将逻辑拆分为组合函数，提高可读性和维护性。</p>

      <h4>2.2.1 API 请求封装</h4>
      <p>将请求接口的逻辑封装到组合函数中，并返回响应数据和状态。</p>

      <h4>2.2.2 表单处理封装</h4>
      <p>封装表单校验和提交逻辑，使组件更简洁。</p>

      <h2>3. 生命周期钩子的使用</h2>
      <p>合理使用 onMounted、onUnmounted 等生命周期钩子。</p>

      <h3>3.1 onMounted 的应用</h3>
      <p>在组件挂载完成后执行初始化逻辑，例如数据请求或 DOM 操作。</p>

      <h3>3.2 onUnmounted 的应用</h3>
      <p>在组件销毁前清理定时器、事件监听或外部资源。</p>

      <h2>4. 计算属性与侦听器</h2>
      <p>计算属性和 watch 可以帮助管理复杂状态和副作用。</p>

      <h3>4.1 computed 的使用</h3>
      <p>当需要基于响应式数据计算派生状态时，使用 computed 可以保证高效缓存。</p>

      <h3>4.2 watch 的使用</h3>
      <p>当需要响应特定状态变化执行副作用时，使用 watch 更加灵活。</p>

      <h2>5. 模板语法与渲染逻辑</h2>
      <p>模板语法和组合式 API 搭配，可以保持代码清晰简洁。</p>

      <h3>5.1 条件渲染</h3>
      <p>使用 v-if、v-else 或 v-show 控制模板元素的显示。</p>

      <h3>5.2 列表渲染</h3>
      <p>使用 v-for 渲染列表，并结合 key 提升性能和稳定性。</p>

      <h2>6. 响应式工具函数</h2>
      <p>Vue 3 提供了多个工具函数，如 toRefs、toRaw、shallowRef 等。</p>

      <h3>6.1 toRefs 的使用</h3>
      <p>将 reactive 对象的属性转换为 ref，方便解构和传递。</p>

      <h3>6.2 toRaw 的使用</h3>
      <p>获取 reactive 对象的原始数据，用于非响应式处理或调试。</p>

      <h3>6.3 shallowRef 的使用</h3>
      <p>只对对象的第一层做响应式，提高性能。</p>

      <h2>7. 组合式 API 性能优化</h2>
      <p>在大型项目中，需要注意组合式 API 的性能优化策略。</p>

      <h3>7.1 避免不必要的响应式</h3>
      <p>使用非响应式变量或 shallowRef，减少性能开销。</p>

      <h3>7.2 组件拆分</h3>
      <p>将复杂组件拆分为小组件，提高渲染效率和可维护性。</p>
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

    <n-layout-header style="padding: 16px 20px; border-bottom: 1px solid #e8e8e8;">
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
              <n-text style="cursor: pointer;" @click="$router.push(`/profile/${article.author.username}`)">
                @{{ article.author.username }}
              </n-text>
              <n-text style="font-size: 14px;">
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

        <n-grid cols="12">
          <template v-if="!isMobile">
            <n-gi :span="9">
              <div ref="articleRef" v-html="article.content" class="article-content"></div>
            </n-gi>
            <n-gi :span="3">
              <!-- 目录组件占位符1 -->
              <Directory :target="articleRef" />
            </n-gi>
          </template>
          <template v-else>
            <n-gi :span="12">
              <Directory :target="articleRef" />
              <div ref="articleRef" v-html="article.content" class="article-content" style="margin-top: 20px;"></div>
            </n-gi>
          </template>
        </n-grid>

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

        <n-input v-model:value="newComment" type="textarea" placeholder="写下你的评论..." :rows="3"
          style="margin-bottom: 16px;" />
        <n-button type="primary" @click="submitComment">发表评论</n-button>

        <div style="margin-top: 20px;">
          <div v-for="comment in comments" :key="comment.id" style="padding: 12px 0; border-bottom: 1px solid #f0f0f0;">
            <n-space align="center" style="margin-bottom: 8px;">
              <n-avatar round size="small" />
              <n-text strong>@{{ comment.author.username }}</n-text>
              <n-text style="font-size: 12px;">
                {{ new Date(comment.createdAt).toLocaleString() }}
              </n-text>
            </n-space>
            <p style="margin: 0;">{{ comment.content }}</p>
          </div>
        </div>
      </n-card>
    </n-layout-content>
  </n-layout>
</template>