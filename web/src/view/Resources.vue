<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { NLayout, NLayoutContent, NCard, NButton, NAvatar, NInput, NIcon, NTag, NDivider, NStatistic, useMessage } from 'naive-ui'
import { SearchOutline, PersonOutline, DownloadOutline, PlayOutline, HeartOutline, BookmarkOutline, EyeOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import MusicPlayer from '../components/MusicPlayer.vue'
import type { Resource, ResourceCategory, ResourceType } from '../types/resource'
import { ResourceTypes } from '../types/resource'
// import { resourceService } from '../services/resource'

const resources = ref<Resource[]>([])
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedType = ref<ResourceType | ''>('')
const playerVisible = ref(false)
const currentPlayingResource = ref<any>(null)
const router = useRouter()
const authStore = useAuthStore()
const message = useMessage()

// 资源分类
const categories: ResourceCategory[] = [
  { id: 'tools', name: '工具软件', description: '实用工具和软件', icon: '🔧', color: '#1890ff', resourceCount: 23 },
  { id: 'games', name: '游戏娱乐', description: '游戏和娱乐资源', icon: '🎮', color: '#52c41a', resourceCount: 45 },
  { id: 'music', name: '音乐音频', description: '音乐和音频文件', icon: '🎵', color: '#fa541c', resourceCount: 67 },
  { id: 'videos', name: '视频教程', description: '学习视频和教程', icon: '🎬', color: '#722ed1', resourceCount: 34 },
  { id: 'documents', name: '文档资料', description: '文档和学习资料', icon: '📚', color: '#faad14', resourceCount: 28 },
  { id: 'others', name: '其他资源', description: '其他各类资源', icon: '📦', color: '#13c2c2', resourceCount: 15 }
]

// 资源类型
const resourceTypes = [
  { value: ResourceTypes.MUSIC, label: '音乐', icon: '🎵' },
  { value: ResourceTypes.VIDEO, label: '视频', icon: '🎬' },
  { value: ResourceTypes.DOCUMENT, label: '文档', icon: '📄' },
  { value: ResourceTypes.SOFTWARE, label: '软件', icon: '💻' },
  { value: ResourceTypes.GAME, label: '游戏', icon: '🎮' },
  { value: ResourceTypes.TOOL, label: '工具', icon: '🔧' },
  { value: ResourceTypes.OTHER, label: '其他', icon: '📦' }
]

// 模拟用户数据
const currentUser = {
  id: '1',
  username: 'resourcefan',
  email: 'resourcefan@example.com',
  followers: 89,
  following: 34,
  createdAt: '2024-01-01',
  bio: '资源分享爱好者，收集各种实用资源',
  avatar: ''
}

// 模拟资源数据
const mockResources: Resource[] = [
  {
    id: '1',
    title: 'VS Code 最新版本',
    description: 'Visual Studio Code 最新稳定版，包含常用插件',
    type: ResourceTypes.SOFTWARE,
    category: 'tools',
    tags: ['编辑器', '编程', '开发工具'],
    fileUrl: '/downloads/vscode.zip',
    downloadCount: 1234,
    size: '85.2 MB',
    format: 'ZIP',
    author: currentUser,
    isLiked: false,
    likes: 42,
    isBookmarked: false,
    createdAt: '2024-10-20T10:00:00Z',
    updatedAt: '2024-10-20T10:00:00Z'
  },
  {
    id: '2',
    title: '经典钢琴曲合集',
    description: '包含贝多芬、莫扎特等经典钢琴曲目',
    type: ResourceTypes.MUSIC,
    category: 'music',
    tags: ['钢琴', '古典音乐', '放松'],
    fileUrl: '/downloads/piano-collection.zip',
    downloadCount: 567,
    size: '256.7 MB',
    format: 'MP3',
    duration: '2:45:30',
    coverImage: '/images/piano.jpg',
    audioUrl: '/audio/piano-collection.mp3',
    tracks: [
      {
        id: '2-1',
        title: '月光奏鸣曲',
        artist: '贝多芬',
        album: 'Classical Piano Collection',
        duration: '5:30',
        audioUrl: '/audio/moonlight-sonata.mp3',
        coverImage: '/images/piano.jpg'
      },
      {
        id: '2-2',
        title: '土耳其进行曲',
        artist: '莫扎特',
        album: 'Classical Piano Collection',
        duration: '3:45',
        audioUrl: '/audio/turkish-march.mp3',
        coverImage: '/images/piano.jpg'
      },
      {
        id: '2-3',
        title: '致爱丽丝',
        artist: '贝多芬',
        album: 'Classical Piano Collection',
        duration: '4:20',
        audioUrl: '/audio/for-elise.mp3',
        coverImage: '/images/piano.jpg'
      }
    ],
    author: {
      id: '2',
      username: 'musiclover',
      avatar: ''
    },
    isLiked: true,
    likes: 89,
    isBookmarked: false,
    createdAt: '2024-10-19T15:30:00Z',
    updatedAt: '2024-10-19T15:30:00Z'
  },
  {
    id: '3',
    title: '独立游戏《星露谷物语》',
    description: '经典的农场模拟游戏，支持中文',
    type: ResourceTypes.GAME,
    category: 'games',
    tags: ['模拟', '农场', '休闲'],
    fileUrl: '/downloads/stardew-valley.zip',
    downloadCount: 2345,
    size: '1.2 GB',
    format: 'EXE',
    author: {
      id: '3',
      username: 'gamedev',
      avatar: ''
    },
    isLiked: false,
    likes: 156,
    isBookmarked: true,
    createdAt: '2024-10-18T14:20:00Z',
    updatedAt: '2024-10-18T14:20:00Z'
  },
  {
    id: '4',
    title: 'Vue 3 入门教程视频',
    description: '完整的Vue 3学习视频教程，从基础到进阶',
    type: ResourceTypes.VIDEO,
    category: 'videos',
    tags: ['Vue', '教程', '前端'],
    fileUrl: '/downloads/vue3-tutorial.zip',
    downloadCount: 789,
    size: '2.1 GB',
    format: 'MP4',
    duration: '5:20:15',
    coverImage: '/images/vue-tutorial.jpg',
    author: {
      id: '4',
      username: 'vueexpert',
      avatar: ''
    },
    isLiked: false,
    likes: 67,
    isBookmarked: false,
    createdAt: '2024-10-17T09:15:00Z',
    updatedAt: '2024-10-17T09:15:00Z'
  }
]

// 计算属性
const filteredResources = computed(() => {
  let filtered = resources.value
  
  // 根据搜索关键词过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(resource => 
      resource.title.toLowerCase().includes(query) ||
      resource.description.toLowerCase().includes(query) ||
      resource.tags.some(tag => tag.toLowerCase().includes(query))
    )
  }
  
  // 根据分类过滤
  if (selectedCategory.value) {
    filtered = filtered.filter(resource => 
      resource.category === selectedCategory.value
    )
  }
  
  // 根据类型过滤
  if (selectedType.value) {
    filtered = filtered.filter(resource => 
      resource.type === selectedType.value
    )
  }
  
  return filtered
})

const categoryCounts = computed(() => {
  const counts: Record<string, number> = {}
  resources.value.forEach(resource => {
    counts[resource.category] = (counts[resource.category] || 0) + 1
  })
  return counts
})

onMounted(() => {
  resources.value = mockResources
})

function handleLike(resource: Resource) {
  resource.isLiked = !resource.isLiked
  resource.likes += resource.isLiked ? 1 : -1
}

function handleBookmark(resource: Resource) {
  resource.isBookmarked = !resource.isBookmarked
}

async function handleDownload(resource: Resource) {
  try {
    // 模拟下载
    message.success(`开始下载: ${resource.title}`)
    resource.downloadCount++
    
    // 实际下载逻辑
    // const blob = await resourceService.downloadResource(resource.id)
    // const url = window.URL.createObjectURL(blob)
    // const link = document.createElement('a')
    // link.href = url
    // link.download = `${resource.title}.${resource.format?.toLowerCase()}`
    // link.click()
    // window.URL.revokeObjectURL(url)
  } catch (error) {
    message.error('下载失败')
  }
}

function handlePlay(resource: Resource) {
  if (resource.type === ResourceTypes.MUSIC) {
    // 如果当前播放的是不同的资源，重置播放列表
    if (currentPlayingResource.value?.id !== resource.id) {
      currentPlayingResource.value = resource
      playerVisible.value = true
    } else {
      // 同一个资源，只是切换显示状态
      playerVisible.value = true
    }
  }
}

function selectCategory(categoryId: string) {
  selectedCategory.value = selectedCategory.value === categoryId ? '' : categoryId
}

function selectType(type: ResourceType) {
  selectedType.value = selectedType.value === type ? '' : type
}

function clearFilters() {
  searchQuery.value = ''
  selectedCategory.value = ''
  selectedType.value = ''
}

function goToProfile() {
  if (authStore.isAuthenticated) {
    router.push(`/profile/${authStore.user?.username}`)
  } else {
    router.push('/login')
  }
}
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="resources-content">
      <div class="resources-container">
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
              <n-statistic label="分享资源" :value="resources.filter(r => r.author.id === currentUser.id).length">
                <template #prefix>📦</template>
              </n-statistic>
              <n-statistic label="下载量" :value="resources.filter(r => r.author.id === currentUser.id).reduce((sum, r) => sum + r.downloadCount, 0)">
                <template #prefix>⬇️</template>
              </n-statistic>
              <n-statistic label="获赞" :value="resources.filter(r => r.author.id === currentUser.id).reduce((sum, r) => sum + r.likes, 0)">
                <template #prefix>❤️</template>
              </n-statistic>
            </div>
            
            <n-button type="primary" block @click="goToProfile" class="profile-button">
              查看个人主页
            </n-button>
          </n-card>

          <!-- 资源分类 -->
          <n-card class="categories-card" title="资源分类" :bordered="false">
            <div class="categories-list">
              <n-button 
                v-for="category in categories" 
                :key="category.id"
                :type="selectedCategory === category.id ? 'primary' : 'default'"
                @click="selectCategory(category.id)"
                class="category-button"
                block
              >
                <span class="category-icon">{{ category.icon }}</span>
                <span class="category-name">{{ category.name }}</span>
                <span class="category-count">({{ categoryCounts[category.id] || 0 }})</span>
              </n-button>
            </div>
          </n-card>
        </div>

        <!-- 右侧资源区域 -->
        <div class="main-content">
          <div class="content-header">
            <h2 class="section-title">资源分享</h2>
            <div class="search-controls">
              <n-input
                v-model:value="searchQuery"
                placeholder="搜索资源标题、描述或标签..."
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

          <!-- 类型筛选 -->
          <div class="types-section">
            <h3 class="types-title">资源类型</h3>
            <div class="types-container">
              <n-tag
                v-for="type in resourceTypes"
                :key="type.value"
                :type="selectedType === type.value ? 'primary' : 'default'"
                size="large"
                @click="selectType(type.value)"
                class="type-tag"
              >
                <span class="type-icon">{{ type.icon }}</span>
                {{ type.label }}
              </n-tag>
            </div>
          </div>

          <!-- 筛选结果统计 -->
          <div class="results-info">
            <span class="results-count">找到 {{ filteredResources.length }} 个资源</span>
            <span v-if="selectedCategory" class="selected-category">当前分类: {{ categories.find(c => c.id === selectedCategory)?.name }}</span>
            <span v-if="selectedType" class="selected-type">当前类型: {{ resourceTypes.find(t => t.value === selectedType)?.label }}</span>
            <span v-if="searchQuery" class="search-query">搜索关键词: "{{ searchQuery }}"</span>
          </div>

          <!-- 资源卡片网格 -->
          <div class="resources-grid">
            <n-card 
              v-for="resource in filteredResources" 
              :key="resource.id" 
              class="resource-card"
            >
              <template #cover v-if="resource.coverImage">
                <img :src="resource.coverImage" :alt="resource.title" class="resource-cover" />
              </template>

              <template #header>
                <div class="resource-header">
                  <h3 class="resource-title">{{ resource.title }}</h3>
                  <div class="resource-actions">
                    <n-button text @click="handleLike(resource)" class="action-button">
                      <n-icon :component="HeartOutline" :color="resource.isLiked ? 'red' : undefined" />
                      <span class="action-count">{{ resource.likes }}</span>
                    </n-button>
                    <n-button text @click="handleBookmark(resource)" class="action-button">
                      <n-icon :component="BookmarkOutline" :color="resource.isBookmarked ? 'gold' : undefined" />
                    </n-button>
                  </div>
                </div>
              </template>

              <p class="resource-description">{{ resource.description }}</p>

              <div class="resource-meta">
                <div class="resource-tags">
                  <n-tag 
                    v-for="tag in resource.tags" 
                    :key="tag" 
                    size="small" 
                    type="primary"
                  >
                    {{ tag }}
                  </n-tag>
                </div>
                
                <div class="resource-info">
                  <span class="resource-size" v-if="resource.size">{{ resource.size }}</span>
                  <span class="resource-format" v-if="resource.format">{{ resource.format }}</span>
                  <span class="resource-duration" v-if="resource.duration">{{ resource.duration }}</span>
                </div>
              </div>

              <div class="resource-footer">
                <div class="resource-author">
                  <n-avatar round size="small" />
                  <span class="author-name">@{{ resource.author.username }}</span>
                </div>
                
                <div class="resource-stats">
                  <div class="stat-item">
                    <n-icon :component="DownloadOutline" size="14" />
                    <span class="stat-count">{{ resource.downloadCount }}</span>
                  </div>
                  <div class="stat-item">
                    <n-icon :component="EyeOutline" size="14" />
                    <span class="stat-count">{{ resource.likes * 10 }}</span>
                  </div>
                </div>
              </div>

              <template #action>
                <div class="resource-actions-footer">
                  <n-button 
                    type="primary" 
                    @click="handleDownload(resource)"
                    class="download-button"
                  >
                    <template #icon>
                      <n-icon :component="DownloadOutline" />
                    </template>
                    下载
                  </n-button>
                  
                  <n-button 
                    v-if="resource.type === ResourceTypes.MUSIC"
                    @click="handlePlay(resource)"
                    class="play-button"
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

          <!-- 无结果提示 -->
          <div v-if="filteredResources.length === 0" class="no-results">
            <n-card>
              <div style="text-align: center; padding: 40px;">
                <h3>没有找到相关资源</h3>
                <p>尝试调整搜索关键词或选择其他分类</p>
                <n-button @click="clearFilters" type="primary">
                  清除筛选条件
                </n-button>
              </div>
            </n-card>
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
.resources-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
}

.resources-container {
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

.user-card, .categories-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
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
  gap: 12px;
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

/* 分类列表 */
.categories-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-button {
  justify-content: flex-start;
  padding: 12px 16px;
}

.category-icon {
  font-size: 1.2rem;
  margin-right: 8px;
}

.category-name {
  flex: 1;
  text-align: left;
}

.category-count {
  font-size: 0.9rem;
}

/* 右侧资源区域 */
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

/* 类型筛选 */
.types-section {
  margin-bottom: 24px;
}

.types-title {
  margin: 0 0 16px 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.types-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.type-tag {
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 8px 12px;
}

.type-tag:hover {
  transform: translateY(-1px);
}

.type-icon {
  margin-right: 4px;
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

.selected-category {
  padding: 4px 8px;
  border-radius: 4px;
  color: #1890ff;
}

.selected-type {
  padding: 4px 8px;
  border-radius: 4px;
  color: #52c41a;
}

.search-query {
  padding: 4px 8px;
  border-radius: 4px;
  color: #fa8c16;
}

/* 资源网格 */
.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.resource-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  height: fit-content;
}

.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.resource-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.resource-title {
  margin: 0;
  font-size: 1.1rem;
  line-height: 1.3;
  flex: 1;
  font-weight: 600;
}

.resource-actions {
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

.resource-description {
  color: #666;
  margin-bottom: 16px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.resource-meta {
  margin-bottom: 16px;
}

.resource-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 8px;
}

.resource-info {
  display: flex;
  gap: 12px;
  font-size: 0.9rem;
  color: #999;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.resource-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  color: #18a058;
  font-weight: 500;
}

.resource-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 0.9rem;
}

.resource-actions-footer {
  display: flex;
  gap: 8px;
}

.download-button, .play-button {
  flex: 1;
}

.no-results {
  grid-column: 1 / -1;
  margin-top: 40px;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .resources-container {
    grid-template-columns: 280px 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .resources-content {
    padding: 16px;
  }

  .resources-container {
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

  .resources-grid {
    grid-template-columns: 1fr;
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

  .types-container {
    justify-content: center;
  }

  .results-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .resource-header {
    flex-direction: column;
    gap: 12px;
  }

  .resource-actions {
    align-self: flex-end;
  }

  .resource-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .resource-stats {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .resources-content {
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

  .resource-title {
    font-size: 1rem;
  }

  .resource-description {
    font-size: 0.9rem;
  }

  .user-stats {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .resource-actions-footer {
    flex-direction: column;
  }
}
</style>