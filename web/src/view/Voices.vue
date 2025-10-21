<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

import { 
  NLayout, 
  NLayoutContent, 
  NCard, 
  NButton, 
  NAvatar, 
  NInput, 
  NEmpty,
  NIcon,
  NSelect,
  NTag,
  useMessage,
  NSpin,
  NDivider
} from 'naive-ui'
import { 
  SearchOutline, 
  HeartOutline, 
  DownloadOutline, 
  PlayOutline, 
  EllipsisHorizontalOutline,
  StarOutline,
  ArrowDownOutline
} from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import type { Voice } from '../types/voice'

const message = useMessage()

// 搜索和筛选状态
const searchKeyword = ref('')
const selectedCategory = ref('all')
const sortBy = ref<'hot' | 'favorites' | 'plays' | 'downloads' | 'latest'>('hot')

// 加载状态
const loading = ref(false)

// 分类选项
const categoryOptions = [
  { label: '全部类型', value: 'all' },
  { label: '恋爱', value: 'romance' },
  { label: '日常', value: 'daily' },
  { label: '奇幻', value: 'fantasy' },
  { label: '悬疑', value: 'mystery' },
  { label: '喜剧', value: 'comedy' },
  { label: '恐怖', value: 'horror' },
  { label: '科幻', value: 'sci-fi' },
  { label: '历史', value: 'history' }
]

// 排序选项
const sortOptions = [
  { label: '最热门', value: 'hot' },
  { label: '收藏最多', value: 'favorites' },
  { label: '播放最多', value: 'plays' },
  { label: '下载最多', value: 'downloads' },
  { label: '最新发布', value: 'latest' }
]

// 模拟用户数据
const currentUser = ref({
  id: '1',
  username: '音声爱好者',
  avatar: '/images/avatars/voice-lover.jpg',
  favoriteVoices: 23,
  listenedVoices: 156,
  playTime: 2450 // 分钟
})

// 模拟收藏的音声（前5个）
const favoriteVoices = ref<Voice[]>([
  {
    id: '1',
    title: '甜蜜的恋爱物语',
    author: '声优小樱',
    description: '一段温馨的恋爱故事，带你感受甜蜜的恋爱氛围',
    coverImage: '/images/voices/romance1.jpg',
    audioUrl: '/audio/romance1.mp3',
    duration: 1860,
    rating: 4.8,
    downloads: 2345,
    favorites: 1890,
    plays: 5678,
    tags: ['恋爱', '温馨', '日常'],
    category: 'romance',
    createdAt: '2024-10-15',
    updatedAt: '2024-10-15'
  },
  {
    id: '2',
    title: '奇幻冒险之旅',
    author: '幻想之声',
    description: '穿越异世界的奇幻冒险，体验不一样的刺激旅程',
    coverImage: '/images/voices/fantasy1.jpg',
    audioUrl: '/audio/fantasy1.mp3',
    duration: 2540,
    rating: 4.6,
    downloads: 1876,
    favorites: 1567,
    plays: 4234,
    tags: ['奇幻', '冒险', '刺激'],
    category: 'fantasy',
    createdAt: '2024-10-12',
    updatedAt: '2024-10-12'
  }
])

// 模拟听过的音声（前5个）
const listenedVoices = ref<Voice[]>([
  {
    id: '3',
    title: '悬疑推理之夜',
    author: '侦探之声',
    description: '一起离奇的案件，考验你的推理能力',
    coverImage: '/images/voices/mystery1.jpg',
    audioUrl: '/audio/mystery1.mp3',
    duration: 1980,
    rating: 4.7,
    downloads: 1654,
    favorites: 1345,
    plays: 2987,
    tags: ['悬疑', '推理', '紧张'],
    category: 'mystery',
    createdAt: '2024-10-10',
    updatedAt: '2024-10-10'
  },
  {
    id: '4',
    title: '喜剧欢乐时光',
    author: '欢乐之声',
    description: '爆笑不断的喜剧故事，让你开怀大笑',
    coverImage: '/images/voices/comedy1.jpg',
    audioUrl: '/audio/comedy1.mp3',
    duration: 1240,
    rating: 4.5,
    downloads: 1432,
    favorites: 1234,
    plays: 2654,
    tags: ['喜剧', '欢乐', '轻松'],
    category: 'comedy',
    createdAt: '2024-10-08',
    updatedAt: '2024-10-08'
  }
])

// 模拟音声数据
const voices = ref<Voice[]>([
  ...favoriteVoices.value,
  ...listenedVoices.value,
  {
    id: '5',
    title: '恐怖惊魂夜',
    author: '惊悚之声',
    description: '令人毛骨悚然的恐怖故事，挑战你的胆量',
    coverImage: '/images/voices/horror1.jpg',
    audioUrl: '/audio/horror1.mp3',
    duration: 1760,
    rating: 4.9,
    downloads: 1987,
    favorites: 1765,
    plays: 3456,
    tags: ['恐怖', '惊悚', '刺激'],
    category: 'horror',
    createdAt: '2024-10-05',
    updatedAt: '2024-10-05'
  },
  {
    id: '6',
    title: '科幻未来世界',
    author: '未来之声',
    description: '探索未来的科幻世界，体验科技的魅力',
    coverImage: '/images/voices/scifi1.jpg',
    audioUrl: '/audio/scifi1.mp3',
    duration: 2130,
    rating: 4.7,
    downloads: 1765,
    favorites: 1543,
    plays: 3123,
    tags: ['科幻', '未来', '科技'],
    category: 'sci-fi',
    createdAt: '2024-10-03',
    updatedAt: '2024-10-03'
  },
  {
    id: '7',
    title: '历史传奇故事',
    author: '历史之声',
    description: '重现历史传奇，感受历史的厚重',
    coverImage: '/images/voices/history1.jpg',
    audioUrl: '/audio/history1.mp3',
    duration: 2450,
    rating: 4.6,
    downloads: 1543,
    favorites: 1432,
    plays: 2876,
    tags: ['历史', '传奇', '文化'],
    category: 'history',
    createdAt: '2024-10-01',
    updatedAt: '2024-10-01'
  },
  {
    id: '8',
    title: '日常温馨时光',
    author: '日常之声',
    description: '平凡的日常故事，感受生活的温暖',
    coverImage: '/images/voices/daily1.jpg',
    audioUrl: '/audio/daily1.mp3',
    duration: 980,
    rating: 4.4,
    downloads: 1321,
    favorites: 1210,
    plays: 2543,
    tags: ['日常', '温馨', '生活'],
    category: 'daily',
    createdAt: '2024-09-28',
    updatedAt: '2024-09-28'
  }
])



// 过滤后的音声列表
const filteredVoices = computed(() => {
  let result = voices.value

  // 分类筛选
  if (selectedCategory.value !== 'all') {
    result = result.filter(voice => voice.category === selectedCategory.value)
  }

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(voice => 
      voice.title.toLowerCase().includes(keyword) ||
      voice.author.toLowerCase().includes(keyword) ||
      voice.description.toLowerCase().includes(keyword) ||
      voice.tags.some(tag => tag.toLowerCase().includes(keyword))
    )
  }

  // 排序
  switch (sortBy.value) {
    case 'hot':
      result.sort((a, b) => (b.plays + b.favorites + b.downloads) - (a.plays + a.favorites + a.downloads))
      break
    case 'favorites':
      result.sort((a, b) => b.favorites - a.favorites)
      break
    case 'plays':
      result.sort((a, b) => b.plays - a.plays)
      break
    case 'downloads':
      result.sort((a, b) => b.downloads - a.downloads)
      break
    case 'latest':
      result.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      break
  }

  return result
})

// 获取时长显示
function getDurationDisplay(duration: number) {
  const hours = Math.floor(duration / 3600)
  const minutes = Math.floor((duration % 3600) / 60)
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
}



// 查看详情
function viewDetail(voice: Voice) {
  message.info(`查看音声详情: ${voice.title}`)
  // 实际应该跳转到详情页面
}

// 下载音声
function downloadVoice(voice: Voice) {
  message.success(`开始下载: ${voice.title}`)
  // 实际应该触发下载逻辑
}

// 查看收藏记录页面
function goToCollection() {
  message.info('跳转到收藏和记录页面')
  // 实际应该跳转到收藏记录页面
}

// 模拟从API获取数据
async function fetchVoiceData() {
  loading.value = true
  
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    message.success('音声数据加载成功')
  } catch (error) {
    message.error('获取音声数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchVoiceData()
})
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="voices-content">
      <div class="voices-container">
        <!-- 个人信息区域 -->
        <n-card class="profile-card" :bordered="false">
          <div class="profile-section">
            <div class="user-info">
              <n-avatar
                round
                :size="60"
                :src="currentUser.avatar"
                :fallback-src="'/images/avatars/default.jpg'"
              />
              <div class="user-details">
                <h3 class="username">{{ currentUser.username }}</h3>
                <div class="user-stats">
                  <span>收藏: {{ currentUser.favoriteVoices }}</span>
                  <span>听过: {{ currentUser.listenedVoices }}</span>
                  <span>播放: {{ currentUser.playTime }}分钟</span>
                </div>
              </div>
            </div>

            <n-divider />

            <!-- 收藏的音声 -->
            <div class="collection-section">
              <div class="section-header">
                <h4>我收藏的音声</h4>
                <n-button text @click="goToCollection" v-if="favoriteVoices.length > 0">
                  <template #icon>
                    <n-icon :component="EllipsisHorizontalOutline" />
                  </template>
                </n-button>
              </div>
              <div v-if="favoriteVoices.length > 0" class="voice-previews">
                <div v-for="voice in favoriteVoices.slice(0, 5)" :key="voice.id" class="voice-preview">
                  <span class="voice-title">{{ voice.title }}</span>
                  <n-tag size="small" type="info">{{ voice.author }}</n-tag>
                </div>
                <div v-if="favoriteVoices.length > 5" class="more-voices">
                  <n-button text @click="goToCollection">
                    ... 查看更多
                  </n-button>
                </div>
              </div>
              <n-empty v-else description="暂无收藏的音声" size="small">
                <template #extra>
                  <n-button size="small" type="primary">去收藏</n-button>
                </template>
              </n-empty>
            </div>

            <n-divider />

            <!-- 听过的音声 -->
            <div class="collection-section">
              <div class="section-header">
                <h4>我听过的音声</h4>
                <n-button text @click="goToCollection" v-if="listenedVoices.length > 0">
                  <template #icon>
                    <n-icon :component="EllipsisHorizontalOutline" />
                  </template>
                </n-button>
              </div>
              <div v-if="listenedVoices.length > 0" class="voice-previews">
                <div v-for="voice in listenedVoices.slice(0, 5)" :key="voice.id" class="voice-preview">
                  <span class="voice-title">{{ voice.title }}</span>
                  <n-tag size="small" type="info">{{ voice.author }}</n-tag>
                </div>
                <div v-if="listenedVoices.length > 5" class="more-voices">
                  <n-button text @click="goToCollection">
                    ... 查看更多
                  </n-button>
                </div>
              </div>
              <n-empty v-else description="暂无听过的音声" size="small">
                <template #extra>
                  <n-button size="small" type="primary">去听听</n-button>
                </template>
              </n-empty>
            </div>

            <n-divider />

            <!-- 搜索筛选 -->
            <div class="filter-section">
              <h4>搜索筛选</h4>
              <div class="filter-options">
                <n-button 
                  v-for="option in sortOptions" 
                  :key="option.value"
                  :type="sortBy === option.value ? 'primary' : 'default'"
                  size="small"
                  @click="sortBy = option.value as any"
                >
                  {{ option.label }}
                </n-button>
              </div>
            </div>
          </div>
        </n-card>

        <!-- 音声资源区域 -->
        <div class="voices-main">
          <!-- 搜索区域 -->
          <n-card class="search-card" :bordered="false">
            <div class="search-section">
              <n-input
                v-model:value="searchKeyword"
                placeholder="搜索音声名称、作者、描述或标签..."
                clearable
                size="large"
              >
                <template #prefix>
                  <n-icon :component="SearchOutline" />
                </template>
              </n-input>
              
              <div class="search-filters">
                <n-select
                  v-model:value="selectedCategory"
                  :options="categoryOptions"
                  placeholder="选择分类"
                  style="width: 140px"
                />
              </div>
            </div>
          </n-card>

          <!-- 音声列表 -->
          <n-spin :show="loading">
            <div v-if="filteredVoices.length > 0" class="voices-grid">
              <n-card 
                v-for="voice in filteredVoices" 
                :key="voice.id" 
                class="voice-card"
                :bordered="false"
              >
                <div class="voice-content">
                  <!-- 音声封面 -->
                  <div class="voice-image">
                    <img :src="voice.coverImage" :alt="voice.title" class="voice-cover" />
                    <div class="voice-overlay">
                      <n-button circle type="primary" @click="viewDetail(voice)">
                        <template #icon>
                          <n-icon :component="PlayOutline" />
                        </template>
                      </n-button>
                    </div>
                  </div>

                  <!-- 音声信息 -->
                  <div class="voice-info">
                    <h3 class="voice-title">{{ voice.title }}</h3>
                    <div class="voice-author">作者: {{ voice.author }}</div>
                    
                    <p class="voice-description">{{ voice.description }}</p>

                    <div class="voice-meta">
                      <div class="meta-item">
                        <n-icon :component="StarOutline" size="14" />
                        <span>{{ voice.rating }}/5</span>
                      </div>
                      <div class="meta-item">
                        <n-icon :component="DownloadOutline" size="14" />
                        <span>{{ voice.downloads.toLocaleString() }}</span>
                      </div>
                      <div class="meta-item">
                        <n-icon :component="HeartOutline" size="14" />
                        <span>{{ voice.favorites.toLocaleString() }}</span>
                      </div>
                      <div class="meta-item">
                        <n-icon :component="PlayOutline" size="14" />
                        <span>{{ voice.plays.toLocaleString() }}</span>
                      </div>
                    </div>

                    <div class="voice-tags">
                      <n-tag 
                        v-for="tag in voice.tags" 
                        :key="tag" 
                        size="small"
                        type="info"
                      >
                        {{ tag }}
                      </n-tag>
                    </div>

                    <div class="voice-duration">
                      时长: {{ getDurationDisplay(voice.duration) }}
                    </div>

                    <!-- 操作按钮 -->
                    <div class="voice-actions">
                      <n-button type="primary" @click="viewDetail(voice)" class="action-button">
                        查看详情
                      </n-button>
                      <n-button type="info" @click="downloadVoice(voice)" class="action-button">
                        <template #icon>
                          <n-icon :component="ArrowDownOutline" />
                        </template>
                        下载
                      </n-button>
                    </div>
                  </div>
                </div>
              </n-card>
            </div>

            <n-empty v-else description="没有找到符合条件的音声">
              <template #extra>
                <n-button type="primary" @click="() => {
                  searchKeyword = ''
                  selectedCategory = 'all'
                  sortBy = 'hot'
                }">
                  显示所有音声
                </n-button>
              </template>
            </n-empty>
          </n-spin>
        </div>
      </div>
    </n-layout-content>
  </n-layout>
</template>

<style scoped>
.voices-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
}

.voices-container {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

.profile-card {
  height: fit-content;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-section {
  padding: 8px 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-details {
  flex: 1;
}

.username {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.user-stats {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 8px;
  font-size: 0.9rem;
}

.collection-section {
  margin: 16px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-header h4 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
}

.voice-previews {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.voice-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-radius: 6px;
  font-size: 0.85rem;
}

.voice-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.more-voices {
  text-align: center;
  padding: 8px;
}

.filter-section h4 {
  margin: 0 0 12px 0;
  font-size: 1rem;
  font-weight: 600;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.voices-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-section :deep(.n-input) {
  flex: 1;
}

.voices-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.voice-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.voice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.voice-content {
  display: flex;
  gap: 16px;
}

.voice-image {
  position: relative;
  flex-shrink: 0;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
}

.voice-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.voice-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.voice-card:hover .voice-overlay {
  opacity: 1;
}

.voice-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.voice-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  line-height: 1.3;
}

.voice-author {
  font-size: 0.9rem;
}

.voice-description {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.voice-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.85rem;
}

.voice-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 8px 0;
}

.voice-duration {
  font-size: 0.85rem;
}

.voice-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
}

.action-button {
  flex: 1;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .voices-container {
    grid-template-columns: 1fr;
  }
  
  .profile-card {
    order: 2;
  }
  
  .voices-main {
    order: 1;
  }
}

@media (max-width: 768px) {
  .voices-content {
    padding: 16px;
  }
  
  .voices-grid {
    grid-template-columns: 1fr;
  }
  
  .voice-content {
    flex-direction: column;
  }
  
  .voice-image {
    width: 100%;
    height: 200px;
  }
  
  .search-section {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .voices-content {
    padding: 12px;
  }
  
  .voice-card {
    padding: 12px;
  }
  
  .voice-actions {
    flex-direction: column;
  }
}
</style>