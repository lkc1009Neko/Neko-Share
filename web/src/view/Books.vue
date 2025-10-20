<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { NLayout, NLayoutContent, NCard, NButton, NAvatar, NInput, NIcon, NTag, NDivider, NStatistic, useMessage } from 'naive-ui'
import { SearchOutline, PersonOutline, BookOutline, HeartOutline, EyeOutline, EllipsisHorizontalOutline, ChevronForwardOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import type { BookCollection, BookType, BookStyle, BookStatusType, UserBookshelf, ReadingRecord } from '../types/book'
import { BookTypes, BookStyles, BookStatus } from '../types/book'

const router = useRouter()
const authStore = useAuthStore()
const message = useMessage()

// 搜索和筛选状态
const searchQuery = ref('')
const selectedType = ref<BookType | ''>('')
const selectedStyle = ref<BookStyle | ''>('')
const selectedStatus = ref<BookStatusType | ''>('')

// 用户书架数据
const userBookshelf = ref<UserBookshelf>({
  totalBooks: 0,
  reading: [],
  completed: [],
  favorites: [],
  wantToRead: []
})

// 阅读记录
const readingRecords = ref<ReadingRecord[]>([])

// 模拟书籍合集数据
const bookCollections = ref<BookCollection[]>([
  {
    id: '1',
    title: '魔法学院物语',
    description: '讲述魔法学院学生们的奇幻冒险故事',
    coverImage: '/images/books/magic-academy.jpg',
    author: '魔法作家',
    books: [
      {
        id: '1-1',
        title: '魔法学院物语 第一卷',
        coverImage: '/images/books/magic-academy-vol1.jpg',
        author: '魔法作家',
        description: '第一卷：入学篇',
        tags: ['魔法', '学院', '冒险'],
        type: BookTypes.LIGHT_NOVEL,
        styles: [BookStyles.MAGIC, BookStyles.FANTASY],
        status: BookStatus.COMPLETED,
        chapters: 12,
        words: 120000,
        popularity: 4567,
        favorites: 234,
        rating: 4.5,
        readCount: 1234,
        createdAt: '2024-01-01',
        updatedAt: '2024-06-01'
      },
      {
        id: '1-2',
        title: '魔法学院物语 第二卷',
        coverImage: '/images/books/magic-academy-vol2.jpg',
        author: '魔法作家',
        description: '第二卷：试炼篇',
        tags: ['魔法', '学院', '成长'],
        type: BookTypes.LIGHT_NOVEL,
        styles: [BookStyles.MAGIC, BookStyles.FANTASY],
        status: BookStatus.COMPLETED,
        chapters: 15,
        words: 150000,
        popularity: 3890,
        favorites: 198,
        rating: 4.3,
        readCount: 987,
        createdAt: '2024-03-01',
        updatedAt: '2024-08-01'
      }
    ],
    totalBooks: 2,
    totalChapters: 27,
    totalWords: 270000,
    status: BookStatus.SERIALIZING,
    popularity: 8457,
    favorites: 432,
    tags: ['魔法', '学院', '冒险', '成长'],
    styles: [BookStyles.MAGIC, BookStyles.FANTASY],
    type: BookTypes.LIGHT_NOVEL,
    createdAt: '2024-01-01',
    updatedAt: '2024-08-01'
  },
  {
    id: '2',
    title: '奇幻冒险记',
    description: '异世界奇幻冒险故事合集',
    coverImage: '/images/books/fantasy-adventure.jpg',
    author: '冒险作家',
    books: [
      {
        id: '2-1',
        title: '奇幻冒险记 第一卷',
        coverImage: '/images/books/fantasy-adventure-vol1.jpg',
        author: '冒险作家',
        description: '异世界穿越冒险',
        tags: ['异世界', '冒险', '战斗'],
        type: BookTypes.LIGHT_NOVEL,
        styles: [BookStyles.FANTASY, BookStyles.ADVENTURE],
        status: BookStatus.COMPLETED,
        chapters: 10,
        words: 100000,
        popularity: 3210,
        favorites: 156,
        rating: 4.2,
        readCount: 765,
        createdAt: '2024-02-01',
        updatedAt: '2024-07-01'
      }
    ],
    totalBooks: 1,
    totalChapters: 10,
    totalWords: 100000,
    status: BookStatus.SERIALIZING,
    popularity: 3210,
    favorites: 156,
    tags: ['异世界', '冒险', '战斗'],
    styles: [BookStyles.FANTASY, BookStyles.ADVENTURE],
    type: BookTypes.LIGHT_NOVEL,
    createdAt: '2024-02-01',
    updatedAt: '2024-07-01'
  },
  {
    id: '3',
    title: '校园恋爱物语',
    description: '温馨校园恋爱故事',
    coverImage: '/images/books/school-romance.jpg',
    author: '恋爱作家',
    books: [
      {
        id: '3-1',
        title: '校园恋爱物语 第一卷',
        coverImage: '/images/books/school-romance-vol1.jpg',
        author: '恋爱作家',
        description: '青涩的校园恋情',
        tags: ['校园', '恋爱', '青春'],
        type: BookTypes.LIGHT_NOVEL,
        styles: [BookStyles.ROMANCE, BookStyles.SLICE_OF_LIFE],
        status: BookStatus.COMPLETED,
        chapters: 8,
        words: 80000,
        popularity: 2789,
        favorites: 134,
        rating: 4.1,
        readCount: 543,
        createdAt: '2024-03-01',
        updatedAt: '2024-06-01'
      }
    ],
    totalBooks: 1,
    totalChapters: 8,
    totalWords: 80000,
    status: BookStatus.COMPLETED,
    popularity: 2789,
    favorites: 134,
    tags: ['校园', '恋爱', '青春'],
    styles: [BookStyles.ROMANCE, BookStyles.SLICE_OF_LIFE],
    type: BookTypes.LIGHT_NOVEL,
    createdAt: '2024-03-01',
    updatedAt: '2024-06-01'
  }
])

// 书籍类型选项
const bookTypeOptions = [
  { value: BookTypes.LIGHT_NOVEL, label: '轻小说', icon: '📚' },
  { value: BookTypes.MANGA, label: '漫画', icon: '🎨' },
  { value: BookTypes.MAGAZINE, label: '杂志', icon: '📰' },
  { value: BookTypes.NOVEL, label: '小说', icon: '📖' },
  { value: BookTypes.COMIC, label: '漫画', icon: '🖼️' }
]

// 书籍风格选项
const bookStyleOptions = Object.values(BookStyles).map(style => ({
  value: style,
  label: style
}))

// 书籍状态选项
const bookStatusOptions = [
  { value: BookStatus.COMPLETED, label: '已完结', color: 'success' as const },
  { value: BookStatus.SERIALIZING, label: '连载中', color: 'warning' as const },
  { value: BookStatus.SUSPENDED, label: '停更', color: 'error' as const },
  { value: BookStatus.HIATUS, label: '暂停', color: 'default' as const }
]

// 计算属性：筛选后的书籍合集
const filteredCollections = computed(() => {
  let filtered = bookCollections.value
  
  // 搜索关键词筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(collection => 
      collection.title.toLowerCase().includes(query) ||
      collection.description.toLowerCase().includes(query) ||
      collection.tags.some(tag => tag.toLowerCase().includes(query)) ||
      collection.author.toLowerCase().includes(query)
    )
  }
  
  // 类型筛选
  if (selectedType.value) {
    filtered = filtered.filter(collection => collection.type === selectedType.value)
  }
  
  // 风格筛选
  if (selectedStyle.value) {
    filtered = filtered.filter(collection => 
      collection.styles.includes(selectedStyle.value as BookStyle)
    )
  }
  
  // 状态筛选
  if (selectedStatus.value) {
    filtered = filtered.filter(collection => collection.status === selectedStatus.value)
  }
  
  return filtered
})

// 跳转到书籍详情
function goToBookDetail(collectionId: string, bookId?: string) {
  if (bookId) {
    router.push(`/books/${collectionId}/book/${bookId}`)
  } else {
    router.push(`/books/${collectionId}`)
  }
}

// 跳转到书架详情页面
function goToBookshelfDetail(tab?: string) {
  if (tab) {
    router.push(`/bookshelf?tab=${tab}`)
  } else {
    router.push('/bookshelf')
  }
}

// 添加到书架
function addToBookshelf(collection: BookCollection) {
  if (!authStore.isAuthenticated) {
    message.error('请先登录')
    router.push('/login')
    return
  }
  
  // 模拟添加到书架
  message.success(`已添加到书架: ${collection.title}`)
}

// 收藏书籍
function toggleFavorite(collection: BookCollection) {
  if (!authStore.isAuthenticated) {
    message.error('请先登录')
    router.push('/login')
    return
  }
  
  // 模拟收藏操作
  message.success(collection.favorites % 2 === 0 ? '已收藏' : '已取消收藏')
}

// 清除筛选条件
function clearFilters() {
  searchQuery.value = ''
  selectedType.value = ''
  selectedStyle.value = ''
  selectedStatus.value = ''
}

onMounted(() => {
  // 模拟加载用户书架数据
  if (authStore.isAuthenticated) {
    userBookshelf.value = {
      totalBooks: 12,
      reading: [
        {
          id: '1',
          bookId: '1-1',
          bookTitle: '魔法学院物语 第一卷',
          bookCover: '/images/books/magic-academy-vol1.jpg',
          author: '魔法作家',
          addedAt: '2024-10-15',
          lastReadAt: '2024-10-20',
          isFavorite: true,
          tags: ['魔法', '学院', '冒险']
        },
        {
          id: '2',
          bookId: '2-1',
          bookTitle: '奇幻冒险记 第一卷',
          bookCover: '/images/books/fantasy-adventure-vol1.jpg',
          author: '冒险作家',
          addedAt: '2024-10-10',
          lastReadAt: '2024-10-18',
          isFavorite: false,
          tags: ['异世界', '冒险', '战斗']
        },
        {
          id: '3',
          bookId: '3-1',
          bookTitle: '校园恋爱物语 第一卷',
          bookCover: '/images/books/school-romance-vol1.jpg',
          author: '恋爱作家',
          addedAt: '2024-10-05',
          lastReadAt: '2024-10-15',
          isFavorite: true,
          tags: ['校园', '恋爱', '青春']
        },
        {
          id: '4',
          bookId: '4-1',
          bookTitle: '科幻未来世界',
          bookCover: '/images/books/sci-fi.jpg',
          author: '科幻作家',
          addedAt: '2024-09-28',
          lastReadAt: '2024-10-12',
          isFavorite: false,
          tags: ['科幻', '未来', '科技']
        }
      ],
      completed: [
        {
          id: '5',
          bookId: '5-1',
          bookTitle: '历史传记',
          bookCover: '/images/books/history.jpg',
          author: '历史作家',
          addedAt: '2024-08-15',
          lastReadAt: '2024-09-30',
          isFavorite: true,
          tags: ['历史', '传记', '文化']
        },
        {
          id: '6',
          bookId: '6-1',
          bookTitle: '哲学思考',
          bookCover: '/images/books/philosophy.jpg',
          author: '哲学作家',
          addedAt: '2024-08-10',
          lastReadAt: '2024-09-25',
          isFavorite: false,
          tags: ['哲学', '思考', '人生']
        }
      ],
      favorites: [
        {
          id: '1',
          bookId: '1-1',
          bookTitle: '魔法学院物语 第一卷',
          bookCover: '/images/books/magic-academy-vol1.jpg',
          author: '魔法作家',
          addedAt: '2024-10-15',
          lastReadAt: '2024-10-20',
          isFavorite: true,
          tags: ['魔法', '学院', '冒险']
        },
        {
          id: '3',
          bookId: '3-1',
          bookTitle: '校园恋爱物语 第一卷',
          bookCover: '/images/books/school-romance-vol1.jpg',
          author: '恋爱作家',
          addedAt: '2024-10-05',
          lastReadAt: '2024-10-15',
          isFavorite: true,
          tags: ['校园', '恋爱', '青春']
        }
      ],
      wantToRead: []
    }
    
    readingRecords.value = [
      {
        id: '1',
        bookId: '1-1',
        bookTitle: '魔法学院物语 第一卷',
        currentChapter: 5,
        totalChapters: 12,
        progress: 42,
        lastReadAt: '2024-10-20',
        readTime: 120
      },
      {
        id: '2',
        bookId: '2-1',
        bookTitle: '奇幻冒险记 第一卷',
        currentChapter: 3,
        totalChapters: 10,
        progress: 30,
        lastReadAt: '2024-10-18',
        readTime: 90
      },
      {
        id: '3',
        bookId: '3-1',
        bookTitle: '校园恋爱物语 第一卷',
        currentChapter: 8,
        totalChapters: 8,
        progress: 100,
        lastReadAt: '2024-10-15',
        readTime: 180
      },
      {
        id: '4',
        bookId: '4-1',
        bookTitle: '科幻未来世界',
        currentChapter: 2,
        totalChapters: 15,
        progress: 13,
        lastReadAt: '2024-10-12',
        readTime: 60
      },
      {
        id: '5',
        bookId: '5-1',
        bookTitle: '悬疑推理小说',
        currentChapter: 6,
        totalChapters: 12,
        progress: 50,
        lastReadAt: '2024-10-08',
        readTime: 150
      },
      {
        id: '6',
        bookId: '6-1',
        bookTitle: '历史传记',
        currentChapter: 10,
        totalChapters: 10,
        progress: 100,
        lastReadAt: '2024-09-30',
        readTime: 200
      }
    ]
  }
})
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="books-content">
      <div class="books-container">
        <!-- 左侧个人信息面板 -->
        <div class="sidebar">
          <!-- 用户信息卡片 -->
          <n-card class="user-card" :bordered="false" v-if="authStore.isAuthenticated">
            <div class="user-header">
              <n-avatar round size="large">
                <template #fallback>
                  <n-icon :component="PersonOutline" />
                </template>
              </n-avatar>
              <div class="user-info">
                <h3 class="username">{{ authStore.user?.username }}</h3>
                <p class="user-bio">书籍爱好者</p>
              </div>
            </div>
            
            <n-divider />
            
            <!-- 书架统计 -->
            <div class="bookshelf-stats">
              <n-statistic label="我的书架" :value="userBookshelf.totalBooks">
                <template #prefix>
                  <n-icon :component="BookOutline" />
                </template>
              </n-statistic>
              <n-statistic label="正在阅读" :value="userBookshelf.reading.length">
                <template #prefix>📖</template>
              </n-statistic>
              <n-statistic label="收藏" :value="userBookshelf.favorites.length">
                <template #prefix>
                  <n-icon :component="HeartOutline" />
                </template>
              </n-statistic>
            </div>
          </n-card>

          <!-- 我的书架 -->
          <n-card class="bookshelf-card" :bordered="false">
            <template #header>
              <div class="bookshelf-header" @click="goToBookshelfDetail()">
                <span class="bookshelf-title">我的书架</span>
                <n-icon :component="ChevronForwardOutline" size="18" class="header-arrow" />
              </div>
            </template>
            <div class="bookshelf-sections">
              <!-- 正在阅读 -->
              <div class="bookshelf-section">
                <div class="section-header">
                  <h4>正在阅读</h4>
                  <span class="section-count">{{ userBookshelf.reading.length }}本</span>
                </div>
                <div class="bookshelf-list">
                  <div v-if="userBookshelf.reading.length === 0" class="empty-state">
                    <span class="empty-text">当前没有</span>
                  </div>
                  <div 
                    v-for="item in userBookshelf.reading.slice(0, 3)" 
                    :key="item.id"
                    class="bookshelf-item"
                    @click="goToBookDetail(item.bookId)"
                  >
                    <n-avatar round size="small" :src="item.bookCover" />
                    <div class="item-info">
                      <div class="item-title">{{ item.bookTitle }}</div>
                      <div class="item-author">{{ item.author }}</div>
                    </div>
                  </div>
                  <div 
                    v-if="userBookshelf.reading.length > 3" 
                    class="bookshelf-more"
                    @click="goToBookshelfDetail('reading')"
                  >
                    <n-icon :component="EllipsisHorizontalOutline" size="20" />
                    <span>查看更多 ({{ userBookshelf.reading.length - 3 }})</span>
                  </div>
                </div>
              </div>
              
              <!-- 已完成 -->
              <div class="bookshelf-section">
                <div class="section-header">
                  <h4>已完成</h4>
                  <span class="section-count">{{ userBookshelf.completed.length }}本</span>
                </div>
                <div class="bookshelf-list">
                  <div v-if="userBookshelf.completed.length === 0" class="empty-state">
                    <span class="empty-text">当前没有</span>
                  </div>
                  <div 
                    v-for="item in userBookshelf.completed.slice(0, 3)" 
                    :key="item.id"
                    class="bookshelf-item"
                    @click="goToBookDetail(item.bookId)"
                  >
                    <n-avatar round size="small" :src="item.bookCover" />
                    <div class="item-info">
                      <div class="item-title">{{ item.bookTitle }}</div>
                      <div class="item-author">{{ item.author }}</div>
                    </div>
                  </div>
                  <div 
                    v-if="userBookshelf.completed.length > 3" 
                    class="bookshelf-more"
                    @click="goToBookshelfDetail('completed')"
                  >
                    <n-icon :component="EllipsisHorizontalOutline" size="20" />
                    <span>查看更多 ({{ userBookshelf.completed.length - 3 }})</span>
                  </div>
                </div>
              </div>
              
              <!-- 阅读记录 -->
              <div class="bookshelf-section">
                <div class="section-header">
                  <h4>阅读记录</h4>
                  <span class="section-count">{{ readingRecords.length }}条</span>
                </div>
                <div class="reading-records">
                  <div v-if="readingRecords.length === 0" class="empty-state">
                    <span class="empty-text">当前没有</span>
                  </div>
                  <div 
                    v-for="record in readingRecords.slice(0, 3)" 
                    :key="record.id"
                    class="reading-record"
                    @click="goToBookDetail(record.bookId)"
                  >
                    <div class="record-title">{{ record.bookTitle }}</div>
                    <div class="record-progress">
                      <span>进度: {{ record.currentChapter }}/{{ record.totalChapters }}</span>
                      <span class="record-time">{{ record.lastReadAt }}</span>
                    </div>
                  </div>
                  <div 
                    v-if="readingRecords.length > 3" 
                    class="bookshelf-more"
                    @click="goToBookshelfDetail('records')"
                  >
                    <n-icon :component="EllipsisHorizontalOutline" size="20" />
                    <span>查看更多 ({{ readingRecords.length - 3 }})</span>
                  </div>
                </div>
              </div>
            </div>
          </n-card>
        </div>

        <!-- 右侧书籍展示区域 -->
        <div class="main-content">
          <!-- 搜索和筛选区域 -->
          <div class="search-section">
            <h2 class="section-title">书籍库</h2>
            <div class="search-controls">
              <n-input
                v-model:value="searchQuery"
                placeholder="搜索书籍标题、描述、作者或标签..."
                class="search-input"
                size="large"
                clearable
              >
                <template #prefix>
                  <n-icon :component="SearchOutline" />
                </template>
              </n-input>
              
              <div class="filter-controls">
                <n-button 
                  v-for="type in bookTypeOptions" 
                  :key="type.value"
                  :type="selectedType === type.value ? 'primary' : 'default'"
                  @click="selectedType = selectedType === type.value ? '' : type.value"
                  size="small"
                >
                  <span class="type-icon">{{ type.icon }}</span>
                  {{ type.label }}
                </n-button>
                
                <n-button @click="clearFilters" type="default" size="small">
                  清除筛选
                </n-button>
              </div>
            </div>

            <!-- 高级筛选 -->
            <div class="advanced-filters">
              <div class="filter-group">
                <span class="filter-label">风格:</span>
                <n-tag
                  v-for="style in bookStyleOptions"
                  :key="style.value"
                  :type="selectedStyle === style.value ? 'primary' : 'default'"
                  size="small"
                  @click="selectedStyle = selectedStyle === style.value ? '' : style.value"
                  class="style-tag"
                >
                  {{ style.label }}
                </n-tag>
              </div>
              
              <div class="filter-group">
                <span class="filter-label">状态:</span>
                <n-tag
                  v-for="status in bookStatusOptions"
                  :key="status.value"
                  :type="selectedStatus === status.value ? status.color : 'default'"
                  size="small"
                  @click="selectedStatus = selectedStatus === status.value ? '' : status.value"
                  class="status-tag"
                >
                  {{ status.label }}
                </n-tag>
              </div>
            </div>
          </div>

          <!-- 筛选结果统计 -->
          <div class="results-info">
            <span class="results-count">找到 {{ filteredCollections.length }} 个书籍合集</span>
            <span v-if="selectedType" class="selected-filter">类型: {{ bookTypeOptions.find(t => t.value === selectedType)?.label }}</span>
            <span v-if="selectedStyle" class="selected-filter">风格: {{ selectedStyle }}</span>
            <span v-if="selectedStatus" class="selected-filter">状态: {{ bookStatusOptions.find(s => s.value === selectedStatus)?.label }}</span>
            <span v-if="searchQuery" class="selected-filter">搜索: "{{ searchQuery }}"</span>
          </div>

          <!-- 书籍合集网格 -->
          <div class="collections-grid">
            <n-card 
              v-for="collection in filteredCollections" 
              :key="collection.id" 
              class="collection-card"
              @click="goToBookDetail(collection.id)"
            >
              <template #cover>
                <img :src="collection.coverImage" :alt="collection.title" class="collection-cover" />
              </template>

              <template #header>
                <div class="collection-header">
                  <h3 class="collection-title">{{ collection.title }}</h3>
                  <div class="collection-actions">
                    <n-button text @click.stop="toggleFavorite(collection)" class="action-button">
                      <n-icon :component="HeartOutline" />
                      <span class="action-count">{{ collection.favorites }}</span>
                    </n-button>
                  </div>
                </div>
              </template>

              <p class="collection-description">{{ collection.description }}</p>

              <div class="collection-meta">
                <div class="collection-tags">
                  <n-tag 
                    v-for="tag in collection.tags" 
                    :key="tag" 
                    size="small" 
                    type="primary"
                  >
                    {{ tag }}
                  </n-tag>
                </div>
                
                <div class="collection-info">
                  <span class="collection-books">共 {{ collection.totalBooks }} 本</span>
                  <span class="collection-chapters">{{ collection.totalChapters }} 章</span>
                  <n-tag 
                    :type="bookStatusOptions.find(s => s.value === collection.status)?.color || 'default'"
                    size="small"
                  >
                    {{ bookStatusOptions.find(s => s.value === collection.status)?.label }}
                  </n-tag>
                </div>
              </div>

              <div class="collection-footer">
                <div class="collection-author">
                  <n-avatar round size="small" />
                  <span class="author-name">{{ collection.author }}</span>
                </div>
                
                <div class="collection-stats">
                  <div class="stat-item">
                    <n-icon :component="EyeOutline" size="14" />
                    <span class="stat-count">{{ collection.popularity }}</span>
                  </div>
                  <div class="stat-item">
                    <n-icon :component="HeartOutline" size="14" />
                    <span class="stat-count">{{ collection.favorites }}</span>
                  </div>
                </div>
              </div>

              <template #action>
                <div class="collection-actions-footer">
                  <n-button 
                    type="primary" 
                    @click.stop="goToBookDetail(collection.id)"
                    class="read-button"
                  >
                    查看详情
                  </n-button>
                  
                  <n-button 
                    @click.stop="addToBookshelf(collection)"
                    class="bookshelf-button"
                  >
                    加入书架
                  </n-button>
                </div>
              </template>
            </n-card>
          </div>

          <!-- 无结果提示 -->
          <div v-if="filteredCollections.length === 0" class="no-results">
            <n-card>
              <div style="text-align: center; padding: 40px; color: #999;">
                <h3>没有找到相关书籍</h3>
                <p>尝试调整搜索关键词或选择其他筛选条件</p>
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
.books-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
  background-color: #f8f9fa;
}

.books-container {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 左侧个人信息面板 */
.sidebar {
  position: sticky;
  top: 84px;
  height: fit-content;
}

.user-card, .bookshelf-card {
  background: white;
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
  color: #1a1a1a;
}

.user-bio {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
}

.bookshelf-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.bookshelf-stats :deep(.n-statistic) {
  text-align: center;
}

.bookshelf-stats :deep(.n-statistic-label) {
  font-size: 0.8rem;
  color: #999;
}

.bookshelf-stats :deep(.n-statistic-value) {
  font-size: 1.1rem;
  font-weight: 600;
  color: #18a058;
}

.bookshelf-sections {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.section-header h4 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
}

.section-count {
  font-size: 0.8rem;
  color: #999;
}

.bookshelf-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 8px 0;
  transition: color 0.2s ease;
}

.bookshelf-header:hover {
  color: #1890ff;
}

.bookshelf-title {
  font-size: 1.1rem;
  font-weight: 600;
}

.header-arrow {
  color: #999;
  transition: transform 0.2s ease, color 0.2s ease;
}

.bookshelf-header:hover .header-arrow {
  color: #1890ff;
  transform: translateX(2px);
}

.empty-state {
  text-align: center;
  padding: 16px;
  color: #999;
}

.empty-text {
  font-size: 0.9rem;
}

.bookshelf-more {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  color: #1890ff;
  font-size: 0.9rem;
  justify-content: center;
  border: 1px dashed #d9d9d9;
  margin-top: 8px;
}

.bookshelf-more:hover {
  background-color: #f0f8ff;
  border-color: #1890ff;
}

.bookshelf-list, .reading-records {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.bookshelf-item, .reading-record {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.bookshelf-item:hover, .reading-record:hover {
  background-color: #f5f5f5;
}

.item-info {
  flex: 1;
}

.item-title {
  font-size: 0.9rem;
  font-weight: 500;
  color: #1a1a1a;
  line-height: 1.2;
}

.item-author, .record-title {
  font-size: 0.8rem;
  color: #666;
}

.record-progress {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #999;
}

.record-time {
  font-size: 0.7rem;
}

/* 右侧主内容区域 */
.main-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-section {
  margin-bottom: 24px;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
}

.search-controls {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-input {
  width: 100%;
}

.filter-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.type-icon {
  margin-right: 4px;
}

.advanced-filters {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #666;
  min-width: 40px;
}

.style-tag, .status-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.style-tag:hover, .status-tag:hover {
  transform: translateY(-1px);
}

/* 结果信息 */
.results-info {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 0.9rem;
  color: #666;
}

.results-count {
  font-weight: 600;
  color: #18a058;
}

.selected-filter {
  background: #e6f7ff;
  padding: 4px 8px;
  border-radius: 4px;
  color: #1890ff;
}

/* 书籍合集网格 */
.collections-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.collection-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  height: fit-content;
  cursor: pointer;
}

.collection-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.collection-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.collection-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.collection-title {
  margin: 0;
  font-size: 1.1rem;
  line-height: 1.3;
  flex: 1;
  font-weight: 600;
}

.collection-actions {
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

.collection-description {
  color: #666;
  margin-bottom: 16px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.collection-meta {
  margin-bottom: 16px;
}

.collection-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 8px;
}

.collection-info {
  display: flex;
  gap: 12px;
  font-size: 0.9rem;
  color: #999;
}

.collection-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.collection-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  color: #18a058;
  font-weight: 500;
}

.collection-stats {
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

.collection-actions-footer {
  display: flex;
  gap: 8px;
}

.read-button, .bookshelf-button {
  flex: 1;
}

.no-results {
  grid-column: 1 / -1;
  margin-top: 40px;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .books-container {
    grid-template-columns: 280px 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .books-content {
    padding: 16px;
  }

  .books-container {
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

  .search-controls {
    flex-direction: column;
    gap: 12px;
  }

  .filter-controls {
    justify-content: center;
  }

  .advanced-filters {
    padding: 12px;
  }

  .filter-group {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-label {
    min-width: auto;
  }

  .collections-grid {
    grid-template-columns: 1fr;
  }

  .results-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .collection-header {
    flex-direction: column;
    gap: 12px;
  }

  .collection-actions {
    align-self: flex-end;
  }

  .collection-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .collection-stats {
    width: 100%;
    justify-content: space-between;
  }

  .collection-actions-footer {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .books-content {
    padding: 12px;
  }

  .main-content {
    padding: 16px;
  }

  .section-title {
    font-size: 1.2rem;
  }

  .collection-title {
    font-size: 1rem;
  }

  .collection-description {
    font-size: 0.9rem;
  }

  .bookshelf-stats {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}
</style>