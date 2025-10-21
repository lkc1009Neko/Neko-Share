<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { NLayout, NLayoutContent, NCard, NButton, NAvatar, NTabs, NTabPane, NInput, NIcon, NStatistic, NDivider } from 'naive-ui'
import { SearchOutline, HeartOutline, BookmarkOutline, EyeOutline, PersonOutline, BookOutline, HeartSharp, PeopleOutline, DownloadOutline, PlayOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import MusicPlayer from '../components/MusicPlayer.vue'
import type { Article, User } from '../types'
import type { Resource } from '../types/resource'
import { ResourceTypes } from '../types/resource'

const articles = ref<Article[]>([])
const resources = ref<Resource[]>([])
const searchQuery = ref('')
const activeTab = ref('latest')
const playerVisible = ref(false)
const currentPlayingResource = ref<any>(null)
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

// 模拟文章数据
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
  }
]

// 模拟资源数据
const mockResources: Resource[] = [
  {
    id: '1',
    title: 'VS Code 插件合集',
    description: '前端开发必备的VS Code插件包',
    type: ResourceTypes.SOFTWARE,
    category: 'tools',
    tags: ['编辑器', '插件', '前端'],
    fileUrl: '/downloads/vscode-plugins.zip',
    downloadCount: 234,
    size: '45.2 MB',
    format: 'ZIP',
    author: currentUser,
    isLiked: false,
    likes: 23,
    isBookmarked: false,
    createdAt: '2024-10-20T09:00:00Z',
    updatedAt: '2024-10-20T09:00:00Z'
  },
  {
    id: '2',
    title: '放松钢琴曲',
    description: '适合工作和学习时听的轻音乐',
    type: ResourceTypes.MUSIC,
    category: 'music',
    tags: ['钢琴', '轻音乐', '放松'],
    fileUrl: '/downloads/relax-piano.zip',
    downloadCount: 89,
    size: '128.5 MB',
    format: 'MP3',
    duration: '1:30:25',
    coverImage: '/images/piano.jpg',
    author: {
      id: '3',
      username: 'musiclover',
      avatar: ''
    },
    isLiked: true,
    likes: 45,
    isBookmarked: false,
    createdAt: '2024-10-19T14:20:00Z',
    updatedAt: '2024-10-19T14:20:00Z'
  },
  {
    id: '3',
    title: '前端学习资料包',
    description: '包含HTML、CSS、JavaScript学习资料',
    type: ResourceTypes.DOCUMENT,
    category: 'documents',
    tags: ['前端', '学习', '资料'],
    fileUrl: '/downloads/frontend-learning.zip',
    downloadCount: 156,
    size: '78.3 MB',
    format: 'PDF',
    author: {
      id: '4',
      username: 'learner',
      avatar: ''
    },
    isLiked: false,
    likes: 34,
    isBookmarked: true,
    createdAt: '2024-10-18T11:15:00Z',
    updatedAt: '2024-10-18T11:15:00Z'
  }
]

onMounted(() => {
  articles.value = mockArticles
  resources.value = mockResources
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

function handleResourceLike(resource: Resource) {
  resource.isLiked = !resource.isLiked
  resource.likes += resource.isLiked ? 1 : -1
}

function handleResourceBookmark(resource: Resource) {
  resource.isBookmarked = !resource.isBookmarked
}

function handleResourceDownload(resource: Resource) {
  // 模拟下载逻辑
  resource.downloadCount++
}

function handleResourcePlay(resource: Resource) {
  if (resource.type === ResourceTypes.MUSIC) {
    currentPlayingResource.value = resource
    playerVisible.value = true
  }
}

function goToResources() {
  router.push('/resources')
}
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="home-content">
      <div class="home-container">
        <!-- 左侧个人信息卡片 -->
        <div class="sidebar">
          <n-card class="user-card" title="个人信息" :bordered="false">
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

        <!-- 右侧文章列表 -->
        <div class="main-content">
          <div class="content-header">
            <h2 class="section-title">最新文章</h2>
            <n-input
              v-model:value="searchQuery"
              placeholder="搜索文章..."
              class="search-input"
              size="large"
            >
              <template #prefix>
                <n-icon :component="SearchOutline" />
              </template>
            </n-input>
          </div>

          <n-tabs v-model:value="activeTab" type="line" class="articles-tabs">
            <n-tab-pane name="latest" tab="最新文章">
              <div class="articles-grid">
                <n-card v-for="article in articles" :key="article.id" class="article-card">
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
                        <n-tag v-for="tag in article.tags" :key="tag" size="small" type="primary">
                          {{ tag }}
                        </n-tag>
                      </div>
                    </div>
                  </div>
                </n-card>
              </div>
            </n-tab-pane>
            <n-tab-pane name="popular" tab="热门文章">
              <div style="text-align: center; padding: 40px;">
                热门文章功能开发中...
              </div>
            </n-tab-pane>
          </n-tabs>

          <!-- 资源分享区 -->
          <div class="resources-section">
            <div class="section-header">
              <h3 class="section-title">热门资源分享</h3>
              <n-button text @click="goToResources" type="primary" class="view-all-button">
                查看全部
              </n-button>
            </div>
            
            <div class="resources-grid">
              <n-card v-for="resource in resources" :key="resource.id" class="resource-card">
                <template #cover v-if="resource.coverImage">
                  <img :src="resource.coverImage" :alt="resource.title" class="resource-cover" />
                </template>

                <template #header>
                  <div class="resource-header">
                    <h4 class="resource-title">{{ resource.title }}</h4>
                    <div class="resource-actions">
                      <n-button text @click="handleResourceLike(resource)" class="action-button">
                        <n-icon :component="HeartOutline" :color="resource.isLiked ? 'red' : undefined" />
                        <span class="action-count">{{ resource.likes }}</span>
                      </n-button>
                      <n-button text @click="handleResourceBookmark(resource)" class="action-button">
                        <n-icon :component="BookmarkOutline" :color="resource.isBookmarked ? 'gold' : undefined" />
                      </n-button>
                    </div>
                  </div>
                </template>

                <p class="resource-description">{{ resource.description }}</p>

                <div class="resource-meta">
                  <div class="resource-tags">
                    <n-tag v-for="tag in resource.tags.slice(0, 2)" :key="tag" size="small" type="primary">
                      {{ tag }}
                    </n-tag>
                    <span v-if="resource.tags.length > 2" class="more-tags">+{{ resource.tags.length - 2 }}</span>
                  </div>
                  
                  <div class="resource-info">
                    <span class="resource-size" v-if="resource.size">{{ resource.size }}</span>
                    <span class="resource-format" v-if="resource.format">{{ resource.format }}</span>
                  </div>
                </div>

                <div class="resource-footer">
                  <div class="resource-author">
                    <n-avatar round size="small" />
                    <span class="author-name">@{{ resource.author.username }}</span>
                  </div>
                  
                  <div class="resource-stats">
                    <div class="stat-item">
                      <n-icon :component="DownloadOutline" size="12" />
                      <span class="stat-count">{{ resource.downloadCount }}</span>
                    </div>
                  </div>
                </div>

                <template #action>
                  <div class="resource-actions-footer">
                    <n-button 
                      type="primary" 
                      @click="handleResourceDownload(resource)"
                      class="download-button"
                      size="small"
                    >
                      <template #icon>
                        <n-icon :component="DownloadOutline" />
                      </template>
                      下载
                    </n-button>
                    
                    <n-button 
                      v-if="resource.type === ResourceTypes.MUSIC"
                      @click="handleResourcePlay(resource)"
                      class="play-button"
                      size="small"
                    >
                      <template #icon>
                        <n-icon :component="PlayOutline" />
                      </template>
                      播放
                    </n-button>
                  </div>
                </template>
              </n-card>
            </div>
          </div>
        </div>
      </div>
    </n-layout-content>

    <!-- 音乐播放器 -->
    <MusicPlayer 
      v-if="currentPlayingResource"
      :resource="currentPlayingResource"
      :visible="playerVisible"
      @close="playerVisible = false"
      @minimize="playerVisible = false"
    />
  </n-layout>
</template>

<style scoped>
.home-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
}

.home-container {
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

/* 右侧文章区域 */
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

.search-input {
  width: 300px;
}

.articles-tabs {
  margin-top: 16px;
}

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
  font-size: 0.9rem;
}

.article-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .home-container {
    grid-template-columns: 280px 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .home-content {
    padding: 16px;
  }

  .home-container {
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
  .home-content {
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

/* 资源分享区样式 */
.resources-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header .section-title {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.view-all-button {
  font-weight: 500;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.resource-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  height: fit-content;
}

.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.resource-cover {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.resource-title {
  margin: 0;
  font-size: 1rem;
  line-height: 1.3;
  flex: 1;
  font-weight: 600;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.resource-actions {
  display: flex;
  gap: 2px;
  flex-shrink: 0;
}

.resource-description {
  margin-bottom: 12px;
  line-height: 1.4;
  font-size: 0.9rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.resource-meta {
  margin-bottom: 12px;
}

.resource-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 6px;
  align-items: center;
}

.more-tags {
  font-size: 0.8rem;
}

.resource-info {
  display: flex;
  gap: 8px;
  font-size: 0.8rem;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.resource-author {
  display: flex;
  align-items: center;
  gap: 6px;
}

.resource-author .author-name {
  font-size: 0.8rem;
  font-weight: 400;
}

.resource-stats {
  display: flex;
  gap: 8px;
}

.resource-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 0.8rem;
}

.resource-actions-footer {
  display: flex;
  gap: 8px;
}

.resource-actions-footer .download-button,
.resource-actions-footer .play-button {
  flex: 1;
  font-size: 0.8rem;
  padding: 4px 8px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .resources-grid {
    grid-template-columns: 1fr;
  }
  
  .resource-title {
    font-size: 0.95rem;
  }
  
  .resource-description {
    font-size: 0.85rem;
  }
  
  .resource-actions-footer {
    flex-direction: column;
  }
}
</style>