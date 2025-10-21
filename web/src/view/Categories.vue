<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { NLayout, NLayoutContent, NCard, NButton, NAvatar, NInput, NIcon, NTag, NDivider, NStatistic } from 'naive-ui'
import { SearchOutline, PersonOutline, BookOutline, HeartSharp, PeopleOutline, HeartOutline, BookmarkOutline, EyeOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import type { Article, User } from '../types'

const articles = ref<Article[]>([])
const searchQuery = ref('')
const selectedTag = ref('')
const router = useRouter()
const authStore = useAuthStore()

// 模拟用户数据
const currentUser: User = {
  id: '1',
  username: 'vuefan',
  email: 'vuefan@example.com',
  followers: 123,
  following: 45,
  createdAt: '2024-01-01',
  bio: '前端开发工程师，热爱Vue和TypeScript',
  avatar: ''
}

// 模拟数据
const mockArticles: Article[] = [
  {
    id: '1',
    title: 'Vue 3 组合式 API 最佳实践',
    content: 'Vue 3 的组合式 API 提供了更好的代码组织和复用性...',
    excerpt: 'Vue 3 的组合式 API 提供了更好的代码组织和复用性，本文将介绍一些最佳实践。',
    author: currentUser,
    tags: ['Vue', 'JavaScript', '前端'],
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
    content: 'TypeScript 的类型系统非常强大，本文将深入解析...',
    excerpt: 'TypeScript 的类型系统非常强大，本文将深入解析其高级特性。',
    author: {
      id: '2',
      username: 'tsmaster',
      email: 'tsmaster@example.com',
      followers: 89,
      following: 23,
      createdAt: '2024-01-02',
      bio: 'TypeScript爱好者，专注于类型系统研究',
      avatar: ''
    },
    tags: ['TypeScript', '编程语言'],
    likes: 31,
    comments: 5,
    views: 98,
    isLiked: true,
    isBookmarked: false,
    createdAt: '2024-10-19T15:30:00Z',
    updatedAt: '2024-10-19T15:30:00Z'
  },
  {
    id: '3',
    title: 'React Hooks 使用技巧',
    content: 'React Hooks 是 React 16.8 引入的新特性...',
    excerpt: 'React Hooks 是 React 16.8 引入的新特性，本文将分享一些使用技巧。',
    author: {
      id: '3',
      username: 'reactdev',
      email: 'reactdev@example.com',
      followers: 67,
      following: 34,
      createdAt: '2024-01-03',
      bio: 'React开发者，专注于组件开发',
      avatar: ''
    },
    tags: ['React', 'JavaScript', '前端'],
    likes: 28,
    comments: 3,
    views: 76,
    isLiked: false,
    isBookmarked: false,
    createdAt: '2024-10-18T14:20:00Z',
    updatedAt: '2024-10-18T14:20:00Z'
  },
  {
    id: '4',
    title: 'Python 数据分析入门',
    content: 'Python 在数据分析领域有着广泛的应用...',
    excerpt: 'Python 在数据分析领域有着广泛的应用，本文将介绍入门知识。',
    author: {
      id: '4',
      username: 'pythondata',
      email: 'pythondata@example.com',
      followers: 145,
      following: 56,
      createdAt: '2024-01-04',
      bio: '数据科学家，Python爱好者',
      avatar: ''
    },
    tags: ['Python', '数据分析', '编程'],
    likes: 56,
    comments: 12,
    views: 234,
    isLiked: false,
    isBookmarked: false,
    createdAt: '2024-10-17T09:15:00Z',
    updatedAt: '2024-10-17T09:15:00Z'
  }
]

// 计算属性
const filteredArticles = computed(() => {
  let filtered = articles.value
  
  // 根据搜索关键词过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(article => 
      article.title.toLowerCase().includes(query) ||
      article.excerpt.toLowerCase().includes(query) ||
      article.tags.some(tag => tag.toLowerCase().includes(query))
    )
  }
  
  // 根据标签过滤
  if (selectedTag.value) {
    filtered = filtered.filter(article => 
      article.tags.includes(selectedTag.value)
    )
  }
  
  return filtered
})

const allTags = computed(() => {
  const tags = new Set<string>()
  articles.value.forEach(article => {
    article.tags.forEach(tag => tags.add(tag))
  })
  return Array.from(tags)
})

const tagCounts = computed(() => {
  const counts: Record<string, number> = {}
  articles.value.forEach(article => {
    article.tags.forEach(tag => {
      counts[tag] = (counts[tag] || 0) + 1
    })
  })
  return counts
})

onMounted(() => {
  articles.value = mockArticles
})

function handleLike(article: Article) {
  article.isLiked = !article.isLiked
  article.likes += article.isLiked ? 1 : -1
}

function handleBookmark(article: Article) {
  article.isBookmarked = !article.isBookmarked
}

function goToProfile() {
  if (authStore.isAuthenticated) {
    router.push(`/profile/${authStore.user?.username}`)
  } else {
    router.push('/login')
  }
}

function selectTag(tag: string) {
  selectedTag.value = selectedTag.value === tag ? '' : tag
}

function clearFilters() {
  searchQuery.value = ''
  selectedTag.value = ''
}
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="categories-content">
      <div class="categories-container">
        <!-- 左侧个人信息卡片 -->
        <div class="sidebar">
          <n-card class="user-card" :bordered="false">
            <div class="user-header">
              <n-avatar round size="large" :src="currentUser.avatar">
                <template #fallback>
                  <n-icon :component="PersonOutline" />
                </template>
              </n-avatar>
              <div class="user-info">
                <h3 class="username">{{ currentUser.username }}</h3>
                <p class="user-bio">{{ currentUser.bio }}</p>
              </div>
            </div>
            
            <n-divider />
            
            <div class="user-stats">
              <n-statistic label="文章" :value="articles.filter(a => a.author.id === currentUser.id).length">
                <template #prefix>
                  <n-icon :component="BookOutline" />
                </template>
              </n-statistic>
              <n-statistic label="粉丝" :value="currentUser.followers">
                <template #prefix>
                  <n-icon :component="HeartSharp" />
                </template>
              </n-statistic>
              <n-statistic label="关注" :value="currentUser.following">
                <template #prefix>
                  <n-icon :component="PeopleOutline" />
                </template>
              </n-statistic>
            </div>
            
            <n-button type="primary" block @click="goToProfile" class="profile-button">
              查看个人主页
            </n-button>
          </n-card>
        </div>

        <!-- 右侧分类和搜索区域 -->
        <div class="main-content">
          <div class="content-header">
            <h2 class="section-title">文章分类</h2>
            <div class="search-controls">
              <n-input
                v-model:value="searchQuery"
                placeholder="搜索文章标题、内容或标签..."
                class="search-input"
                size="large"
                clearable
              >
                <template #prefix>
                  <n-icon :component="SearchOutline" />
                </template>
              </n-input>
              <n-button @click="clearFilters" type="default" size="large">
                清除筛选
              </n-button>
            </div>
          </div>

          <!-- 标签筛选 -->
          <div class="tags-section">
            <h3 class="tags-title">热门标签</h3>
            <div class="tags-container">
              <n-tag
                v-for="tag in allTags"
                :key="tag"
                :type="selectedTag === tag ? 'primary' : 'default'"
                size="large"
                @click="selectTag(tag)"
                class="tag-item"
              >
                {{ tag }} ({{ tagCounts[tag] }})
              </n-tag>
            </div>
          </div>

          <!-- 筛选结果统计 -->
          <div class="results-info">
            <span class="results-count">找到 {{ filteredArticles.length }} 篇文章</span>
            <span v-if="selectedTag" class="selected-tag">当前标签: {{ selectedTag }}</span>
            <span v-if="searchQuery" class="search-query">搜索关键词: "{{ searchQuery }}"</span>
          </div>

          <!-- 文章列表 -->
          <div class="articles-grid">
            <n-card 
              v-for="article in filteredArticles" 
              :key="article.id" 
              class="article-card"
            >
              <template #header>
                <div class="article-header">
                  <h3 class="article-title" @click="$router.push(`/article/${article.id}`)">
                    {{ article.title }}
                  </h3>
                  <div class="article-actions">
                    <n-button text @click="handleLike(article)" class="action-button">
                      <n-icon :component="HeartOutline" :color="article.isLiked ? 'red' : undefined" />
                      <span class="action-count">{{ article.likes }}</span>
                    </n-button>
                    <n-button text @click="handleBookmark(article)" class="action-button">
                      <n-icon :component="BookmarkOutline" :color="article.isBookmarked ? 'gold' : undefined" />
                    </n-button>
                  </div>
                </div>
              </template>

              <p class="article-excerpt">{{ article.excerpt }}</p>

              <div class="article-footer">
                <div class="article-meta">
                  <n-avatar round size="small" />
                  <span class="author-name" @click="$router.push(`/profile/${article.author.username}`)">
                    @{{ article.author.username }}
                  </span>
                  <span class="article-date">{{ new Date(article.createdAt).toLocaleDateString() }}</span>
                </div>
                <div class="article-stats">
                  <div class="stat-item">
                    <n-icon :component="EyeOutline" size="14" />
                    <span class="stat-count">{{ article.views }}</span>
                  </div>
                  <div class="article-tags">
                    <n-tag 
                      v-for="tag in article.tags" 
                      :key="tag" 
                      size="small" 
                      type="primary"
                      @click="selectTag(tag)"
                    >
                      {{ tag }}
                    </n-tag>
                  </div>
                </div>
              </div>
            </n-card>
          </div>

          <!-- 无结果提示 -->
          <div v-if="filteredArticles.length === 0" class="no-results">
            <n-card>
              <div style="text-align: center; padding: 40px;">
                <h3>没有找到相关文章</h3>
                <p>尝试调整搜索关键词或选择其他标签</p>
                <n-button @click="clearFilters" type="primary">
                  清除筛选条件
                </n-button>
              </div>
            </n-card>
          </div>
        </div>
      </div>
    </n-layout-content>
  </n-layout>
</template>

<style scoped>
.categories-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
}

.categories-container {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 左侧个人信息卡片 */
.sidebar {
  position: sticky;
  top: 84px;
  height: fit-content;
}

.user-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.user-info {
  flex: 1;
}

.username {
  margin: 0 0 4px 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.user-bio {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.4;
}

.user-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.user-stats :deep(.n-statistic) {
  text-align: center;
}

.user-stats :deep(.n-statistic-label) {
  font-size: 0.8rem;
  color: #999;
}

.user-stats :deep(.n-statistic-value) {
  font-size: 1.1rem;
  font-weight: 600;
  color: #18a058;
}

.profile-button {
  margin-top: 8px;
}

/* 右侧分类区域 */
.main-content {
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.search-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  width: 400px;
}

/* 标签区域 */
.tags-section {
  margin-bottom: 24px;
}

.tags-title {
  margin: 0 0 16px 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag-item:hover {
  transform: translateY(-1px);
}

/* 结果信息 */
.results-info {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px;
  border-radius: 6px;
  font-size: 0.9rem;
}

.results-count {
  font-weight: 600;
  color: #18a058;
}

.selected-tag {
  padding: 4px 8px;
  border-radius: 4px;
  color: #1890ff;
}

.search-query {
  padding: 4px 8px;
  border-radius: 4px;
  color: #fa8c16;
}

/* 文章列表 */
.articles-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
}

.article-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.article-title {
  margin: 0;
  cursor: pointer;
  font-size: 1.2rem;
  line-height: 1.4;
  flex: 1;
  transition: color 0.2s ease;
  font-weight: 600;
}

.article-title:hover {
  color: #18a058;
}

.article-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.action-button {
  padding: 4px 8px;
}

.action-count {
  margin-left: 4px;
  font-size: 0.9rem;
}

.article-excerpt {
  color: #666;
  margin-bottom: 16px;
  line-height: 1.5;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  color: #18a058;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s ease;
}

.author-name:hover {
  color: #36ad6a;
}

.article-date {
  color: #999;
  font-size: 0.9rem;
}

.article-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 0.9rem;
}

.article-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.no-results {
  margin-top: 40px;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .categories-container {
    grid-template-columns: 280px 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .categories-content {
    padding: 16px;
  }

  .categories-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .sidebar {
    position: static;
    order: 2;
  }

  .main-content {
    order: 1;
    padding: 20px;
  }

  .content-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .section-title {
    text-align: center;
    font-size: 1.3rem;
  }

  .search-controls {
    flex-direction: column;
    gap: 12px;
  }

  .search-input {
    width: 100%;
  }

  .user-header {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .user-stats {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }

  .tags-container {
    justify-content: center;
  }

  .results-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .article-header {
    flex-direction: column;
    gap: 12px;
  }

  .article-actions {
    align-self: flex-end;
  }

  .article-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .article-stats {
    width: 100%;
    justify-content: space-between;
  }

  .article-tags {
    justify-content: flex-start;
  }
}

@media (max-width: 480px) {
  .categories-content {
    padding: 12px;
  }

  .main-content {
    padding: 16px;
  }

  .section-title {
    font-size: 1.2rem;
  }

  .username {
    font-size: 1.2rem;
  }

  .article-title {
    font-size: 1.1rem;
  }

  .article-excerpt {
    font-size: 0.9rem;
  }

  .user-stats {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .article-meta {
    flex-wrap: wrap;
  }
}
</style>