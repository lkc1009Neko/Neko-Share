<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { NLayout, NLayoutContent, NCard, NButton, NAvatar, NTag, NDivider, NStatistic, NImage, useMessage } from 'naive-ui'
import { ArrowBackOutline, HeartOutline, BookOutline, EyeOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import type { BookCollection, Book } from '../types/book'
import { BookTypes, BookStatus, BookStyles } from '../types/book'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const message = useMessage()

const collectionId = route.params.collectionId as string
const bookId = route.params.bookId as string

// 当前书籍合集和书籍
const currentCollection = ref<BookCollection | null>(null)
const currentBook = ref<Book | null>(null)
const relatedCollections = ref<BookCollection[]>([])

// 模拟数据 - 在实际应用中应该从API获取
const mockCollections: BookCollection[] = [
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
        description: '第一卷：入学篇，讲述主角进入魔法学院的奇妙经历',
        tags: ['魔法', '学院', '冒险', '成长'],
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
        description: '第二卷：试炼篇，主角面临各种魔法试炼',
        tags: ['魔法', '学院', '试炼', '战斗'],
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
    tags: ['魔法', '学院', '冒险', '成长', '试炼'],
    styles: [BookStyles.MAGIC, BookStyles.FANTASY],
    type: BookTypes.LIGHT_NOVEL,
    createdAt: '2024-01-01',
    updatedAt: '2024-08-01'
  }
]

// 相关推荐数据
const mockRelatedCollections: BookCollection[] = [
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
]

// 计算属性：当前显示的书籍
const displayBook = computed(() => {
  return currentBook.value || (currentCollection.value?.books[0] || null)
})

// 计算属性：书籍状态标签
const bookStatusOptions = [
  { value: BookStatus.COMPLETED, label: '已完结', color: 'success' as const },
  { value: BookStatus.SERIALIZING, label: '连载中', color: 'warning' as const },
  { value: BookStatus.SUSPENDED, label: '停更', color: 'error' as const },
  { value: BookStatus.HIATUS, label: '暂停', color: 'default' as const }
]

// 返回上一页
function goBack() {
  router.back()
}

// 阅读书籍
function readBook(book?: Book) {
  const targetBook = book || displayBook.value
  if (!targetBook) return
  
  if (!authStore.isAuthenticated) {
    message.error('请先登录')
    router.push('/login')
    return
  }
  
  // 在实际应用中应该跳转到阅读页面
  message.success(`开始阅读: ${targetBook.title}`)
  // router.push(`/books/${collectionId}/book/${targetBook.id}/read`)
}

// 添加到书架
function addToBookshelf() {
  if (!authStore.isAuthenticated) {
    message.error('请先登录')
    router.push('/login')
    return
  }
  
  if (!currentCollection.value) return
  
  message.success(`已添加到书架: ${currentCollection.value.title}`)
}

// 收藏书籍
function toggleFavorite() {
  if (!authStore.isAuthenticated) {
    message.error('请先登录')
    router.push('/login')
    return
  }
  
  if (!currentCollection.value) return
  
  message.success(currentCollection.value.favorites % 2 === 0 ? '已收藏' : '已取消收藏')
}

// 查看书籍详情
function viewBookDetail(book: Book) {
  router.push(`/books/${collectionId}/book/${book.id}`)
}

onMounted(() => {
  // 模拟加载数据
  const collection = mockCollections.find(c => c.id === collectionId)
  if (collection) {
    currentCollection.value = collection
    
    if (bookId) {
      const book = collection.books.find(b => b.id === bookId)
      if (book) {
        currentBook.value = book
      }
    }
  }
  
  relatedCollections.value = mockRelatedCollections
})
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="book-detail-content">
      <div class="book-detail-container">
        <!-- 返回按钮 -->
        <div class="back-section">
          <n-button @click="goBack" type="primary" text class="back-button">
            <template #icon>
              <n-icon :component="ArrowBackOutline" />
            </template>
            返回
          </n-button>
        </div>

        <!-- 书籍详情主体 -->
        <div class="book-detail-main">
          <!-- 左侧书籍封面和信息 -->
          <div class="book-info-section">
            <n-card class="book-info-card">
              <div class="book-cover-section">
                <n-image
                  :src="displayBook?.coverImage"
                  :alt="displayBook?.title"
                  class="book-cover"
                  object-fit="cover"
                />
              </div>
              
              <div class="book-basic-info">
                <h1 class="book-title">{{ displayBook?.title }}</h1>
                <p class="book-author">作者: {{ displayBook?.author }}</p>
                
                <div class="book-tags">
                  <n-tag 
                    v-for="tag in displayBook?.tags" 
                    :key="tag" 
                    size="small" 
                    type="primary"
                  >
                    {{ tag }}
                  </n-tag>
                </div>
                
                <div class="book-stats">
                  <n-statistic label="章节数" :value="displayBook?.chapters || 0">
                    <template #prefix>
                      <n-icon :component="BookOutline" />
                    </template>
                  </n-statistic>
                  
                  <n-statistic label="字数" :value="displayBook?.words || 0">
                    <template #suffix>字</template>
                  </n-statistic>
                  
                  <n-statistic label="热度" :value="displayBook?.popularity || 0">
                    <template #prefix>
                      <n-icon :component="EyeOutline" />
                    </template>
                  </n-statistic>
                  
                  <n-statistic label="收藏" :value="displayBook?.favorites || 0">
                    <template #prefix>
                      <n-icon :component="HeartOutline" />
                    </template>
                  </n-statistic>
                </div>
                
                <div class="book-actions">
                  <n-button 
                    type="primary" 
                    size="large" 
                    @click="readBook()"
                    class="read-button"
                  >
                    开始阅读
                  </n-button>
                  
                  <n-button 
                    @click="addToBookshelf"
                    size="large"
                    class="bookshelf-button"
                  >
                    加入书架
                  </n-button>
                  
                  <n-button 
                    @click="toggleFavorite"
                    :type="currentCollection?.favorites && currentCollection.favorites % 2 === 0 ? 'primary' : 'default'"
                    size="large"
                    class="favorite-button"
                  >
                    <template #icon>
                      <n-icon :component="HeartOutline" />
                    </template>
                    收藏
                  </n-button>
                </div>
              </div>
            </n-card>

            <!-- 作者简介 -->
            <n-card class="author-info-card" title="作者简介">
              <div class="author-info">
                <n-avatar round size="large" />
                <div class="author-details">
                  <h3 class="author-name">{{ displayBook?.author }}</h3>
                  <p class="author-bio">知名轻小说作家，擅长创作奇幻冒险题材作品，文笔细腻，情节引人入胜。</p>
                </div>
              </div>
            </n-card>
          </div>

          <!-- 右侧书籍描述和相关信息 -->
          <div class="book-details-section">
            <!-- 书籍描述 -->
            <n-card class="description-card" title="作品介绍">
              <p class="book-description">{{ displayBook?.description }}</p>
              
              <n-divider />
              
              <!-- 作品信息 -->
              <div class="work-info">
                <h4>作品信息</h4>
                <div class="info-grid">
                  <div class="info-item">
                    <span class="info-label">作品类型:</span>
                    <span class="info-value">{{ BookTypes[displayBook?.type as keyof typeof BookTypes] || displayBook?.type }}</span>
                  </div>
                  
                  <div class="info-item">
                    <span class="info-label">作品风格:</span>
                    <div class="info-value">
                      <n-tag 
                        v-for="style in displayBook?.styles" 
                        :key="style" 
                        size="small"
                      >
                        {{ style }}
                      </n-tag>
                    </div>
                  </div>
                  
                  <div class="info-item">
                    <span class="info-label">作品状态:</span>
                    <n-tag 
                      :type="bookStatusOptions.find(s => s.value === displayBook?.status)?.color || 'default'"
                      size="small"
                    >
                      {{ bookStatusOptions.find(s => s.value === displayBook?.status)?.label }}
                    </n-tag>
                  </div>
                  
                  <div class="info-item">
                    <span class="info-label">创建时间:</span>
                    <span class="info-value">{{ displayBook?.createdAt }}</span>
                  </div>
                  
                  <div class="info-item">
                    <span class="info-label">更新时间:</span>
                    <span class="info-value">{{ displayBook?.updatedAt }}</span>
                  </div>
                </div>
              </div>
            </n-card>

            <!-- 合集信息（如果查看的是合集） -->
            <n-card v-if="currentCollection && !bookId" class="collection-info-card" title="合集信息">
              <div class="collection-info">
                <p class="collection-description">{{ currentCollection.description }}</p>
                
                <div class="collection-stats">
                  <div class="stat-item">
                    <span class="stat-label">合集包含:</span>
                    <span class="stat-value">{{ currentCollection.totalBooks }} 本书</span>
                  </div>
                  
                  <div class="stat-item">
                    <span class="stat-label">总章节:</span>
                    <span class="stat-value">{{ currentCollection.totalChapters }} 章</span>
                  </div>
                  
                  <div class="stat-item">
                    <span class="stat-label">总字数:</span>
                    <span class="stat-value">{{ currentCollection.totalWords }} 字</span>
                  </div>
                  
                  <div class="stat-item">
                    <span class="stat-label">合集状态:</span>
                    <n-tag 
                      v-if="currentCollection"
                      :type="bookStatusOptions.find(s => s.value === currentCollection!.status)?.color || 'default'"
                      size="small"
                    >
                      {{ bookStatusOptions.find(s => s.value === currentCollection!.status)?.label }}
                    </n-tag>
                  </div>
                </div>
              </div>
            </n-card>
          </div>
        </div>

        <!-- 书籍列表（如果是合集） -->
        <div v-if="currentCollection && currentCollection.books.length > 1" class="books-list-section">
          <n-card title="书籍列表">
            <div class="books-grid">
              <n-card 
                v-for="book in currentCollection.books" 
                :key="book.id"
                class="book-card"
                @click="viewBookDetail(book)"
              >
                <template #cover>
                  <img :src="book.coverImage" :alt="book.title" class="book-list-cover" />
                </template>

                <template #header>
                  <h3 class="book-list-title">{{ book.title }}</h3>
                </template>

                <p class="book-list-description">{{ book.description }}</p>

                <div class="book-list-meta">
                  <div class="book-list-tags">
                    <n-tag 
                      v-for="tag in book.tags.slice(0, 2)" 
                      :key="tag" 
                      size="small" 
                      type="primary"
                    >
                      {{ tag }}
                    </n-tag>
                  </div>
                  
                  <div class="book-list-info">
                    <span class="book-list-chapters">{{ book.chapters }} 章</span>
                    <n-tag 
                      :type="bookStatusOptions.find(s => s.value === book.status)?.color || 'default'"
                      size="small"
                    >
                      {{ bookStatusOptions.find(s => s.value === book.status)?.label }}
                    </n-tag>
                  </div>
                </div>

                <template #action>
                  <div class="book-list-actions">
                    <n-button 
                      type="primary" 
                      @click.stop="readBook(book)"
                      class="book-read-button"
                    >
                      阅读
                    </n-button>
                  </div>
                </template>
              </n-card>
            </div>
          </n-card>
        </div>

        <!-- 相关推荐 -->
        <div class="related-section">
          <n-card title="相关推荐">
            <div class="related-grid">
              <n-card 
                v-for="collection in relatedCollections" 
                :key="collection.id"
                class="related-card"
                @click="router.push(`/books/${collection.id}`)"
              >
                <template #cover>
                  <img :src="collection.coverImage" :alt="collection.title" class="related-cover" />
                </template>

                <template #header>
                  <h3 class="related-title">{{ collection.title }}</h3>
                </template>

                <p class="related-description">{{ collection.description }}</p>

                <div class="related-meta">
                  <div class="related-tags">
                    <n-tag 
                      v-for="tag in collection.tags.slice(0, 3)" 
                      :key="tag" 
                      size="small" 
                      type="primary"
                    >
                      {{ tag }}
                    </n-tag>
                  </div>
                  
                  <div class="related-info">
                    <span class="related-books">{{ collection.totalBooks }} 本</span>
                    <n-tag 
                      :type="bookStatusOptions.find(s => s.value === collection.status)?.color || 'default'"
                      size="small"
                    >
                      {{ bookStatusOptions.find(s => s.value === collection.status)?.label }}
                    </n-tag>
                  </div>
                </div>
              </n-card>
            </div>
          </n-card>
        </div>
      </div>
    </n-layout-content>
  </n-layout>
</template>

<style scoped>
.book-detail-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
  background-color: #f8f9fa;
}

.book-detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-section {
  margin-bottom: 20px;
}

.back-button {
  font-size: 1rem;
}

.book-detail-main {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

/* 左侧书籍信息 */
.book-info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.book-info-card, .author-info-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.book-cover-section {
  text-align: center;
  margin-bottom: 20px;
}

.book-cover {
  width: 100%;
  max-width: 280px;
  height: 350px;
  border-radius: 8px;
  object-fit: cover;
}

.book-basic-info {
  text-align: center;
}

.book-title {
  margin: 0 0 8px 0;
  font-size: 1.4rem;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.3;
}

.book-author {
  margin: 0 0 16px 0;
  color: #666;
  font-size: 1rem;
}

.book-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
  margin-bottom: 20px;
}

.book-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.book-stats :deep(.n-statistic) {
  text-align: center;
}

.book-stats :deep(.n-statistic-label) {
  font-size: 0.8rem;
  color: #999;
}

.book-stats :deep(.n-statistic-value) {
  font-size: 1.1rem;
  font-weight: 600;
  color: #18a058;
}

.book-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.read-button, .bookshelf-button, .favorite-button {
  width: 100%;
}

/* 作者信息 */
.author-info {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.author-details {
  flex: 1;
}

.author-name {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
}

.author-bio {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

/* 右侧书籍详情 */
.book-details-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.description-card, .collection-info-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.book-description {
  margin: 0;
  color: #333;
  line-height: 1.6;
  font-size: 1rem;
}

.work-info h4 {
  margin: 0 0 16px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.info-label {
  min-width: 80px;
  font-weight: 500;
  color: #666;
}

.info-value {
  flex: 1;
  color: #333;
}

/* 合集信息 */
.collection-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.collection-description {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.collection-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  color: #666;
}

.stat-value {
  font-weight: 500;
  color: #333;
}

/* 书籍列表 */
.books-list-section, .related-section {
  margin-bottom: 32px;
}

.books-grid, .related-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.book-card, .related-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  cursor: pointer;
}

.book-card:hover, .related-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.book-list-cover, .related-cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.book-list-title, .related-title {
  margin: 0;
  font-size: 1rem;
  line-height: 1.3;
  font-weight: 600;
}

.book-list-description, .related-description {
  color: #666;
  margin-bottom: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-list-meta, .related-meta {
  margin-bottom: 12px;
}

.book-list-tags, .related-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 8px;
}

.book-list-info, .related-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: #999;
}

.book-list-actions {
  display: flex;
  gap: 8px;
}

.book-read-button {
  width: 100%;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .book-detail-main {
    grid-template-columns: 300px 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .book-detail-content {
    padding: 16px;
  }

  .book-detail-main {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .book-cover {
    max-width: 100%;
    height: 300px;
  }

  .book-stats {
    grid-template-columns: repeat(4, 1fr);
    gap: 12px;
  }

  .books-grid, .related-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 16px;
  }

  .collection-stats {
    grid-template-columns: 1fr;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .info-label {
    min-width: auto;
  }
}

@media (max-width: 480px) {
  .book-detail-content {
    padding: 12px;
  }

  .book-title {
    font-size: 1.2rem;
  }

  .book-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .books-grid, .related-grid {
    grid-template-columns: 1fr;
  }

  .book-actions {
    gap: 8px;
  }

  .read-button, .bookshelf-button, .favorite-button {
    font-size: 0.9rem;
  }
}
</style>