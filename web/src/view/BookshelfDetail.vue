<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { 
  NLayout, 
  NLayoutContent, 
  NCard, 
  NButton, 
  NTabs, 
  NTabPane, 
  NEmpty,
  NIcon
} from 'naive-ui'
import { ArrowBackOutline, EyeOutline, TimeOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'
import type { BookshelfItem, ReadingRecord } from '../types/book'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 当前激活的选项卡，从路由参数获取
const activeTab = ref('reading')

// 模拟数据
const userBookshelf = ref({
  reading: [] as BookshelfItem[],
  completed: [] as BookshelfItem[],
  favorites: [] as BookshelfItem[],
  wantToRead: [] as BookshelfItem[]
})

const readingRecords = ref<ReadingRecord[]>([])

// 计算属性：获取当前选项卡的数据
const currentTabData = computed(() => {
  switch (activeTab.value) {
    case 'reading':
      return userBookshelf.value.reading
    case 'completed':
      return userBookshelf.value.completed
    case 'favorites':
      return userBookshelf.value.favorites
    case 'records':
      return readingRecords.value
    default:
      return []
  }
})

// 跳转到书籍详情
function goToBookDetail(bookId: string) {
  router.push(`/books/collection/book/${bookId}`)
}

// 返回上一页
function goBack() {
  router.back()
}

// 模拟加载数据
onMounted(() => {
  // 从路由查询参数获取选项卡
  if (route.query.tab) {
    activeTab.value = route.query.tab as string
  }

  if (authStore.isAuthenticated) {
    // 模拟正在阅读数据
    userBookshelf.value.reading = [
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
      },
      {
        id: '5',
        bookId: '5-1',
        bookTitle: '悬疑推理小说',
        bookCover: '/images/books/mystery.jpg',
        author: '推理作家',
        addedAt: '2024-09-20',
        lastReadAt: '2024-10-08',
        isFavorite: true,
        tags: ['悬疑', '推理', '犯罪']
      }
    ]

    // 模拟已完成数据
    userBookshelf.value.completed = [
      {
        id: '6',
        bookId: '6-1',
        bookTitle: '历史传记',
        bookCover: '/images/books/history.jpg',
        author: '历史作家',
        addedAt: '2024-08-15',
        lastReadAt: '2024-09-30',
        isFavorite: true,
        tags: ['历史', '传记', '文化']
      },
      {
        id: '7',
        bookId: '7-1',
        bookTitle: '哲学思考',
        bookCover: '/images/books/philosophy.jpg',
        author: '哲学作家',
        addedAt: '2024-08-10',
        lastReadAt: '2024-09-25',
        isFavorite: false,
        tags: ['哲学', '思考', '人生']
      }
    ]

    // 模拟收藏数据
    userBookshelf.value.favorites = [
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
      },
      {
        id: '5',
        bookId: '5-1',
        bookTitle: '悬疑推理小说',
        bookCover: '/images/books/mystery.jpg',
        author: '推理作家',
        addedAt: '2024-09-20',
        lastReadAt: '2024-10-08',
        isFavorite: true,
        tags: ['悬疑', '推理', '犯罪']
      }
    ]

    // 模拟阅读记录数据
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
      }
    ]
  }
})
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="bookshelf-detail-content">
      <div class="bookshelf-detail-container">
        <!-- 返回按钮 -->
        <div class="back-section">
          <n-button @click="goBack" type="primary" text>
            <template #icon>
              <n-icon :component="ArrowBackOutline" />
            </template>
            返回书籍页面
          </n-button>
        </div>

        <!-- 主内容区域 -->
        <div class="main-content">
          <n-card class="bookshelf-card">
            <template #header>
              <h2 class="section-title">我的书架详情</h2>
            </template>

            <!-- 选项卡 -->
            <n-tabs v-model:value="activeTab" type="line" animated>
              <!-- 正在阅读选项卡 -->
              <n-tab-pane name="reading" tab="正在阅读">
                <div class="tab-content">
                  <div v-if="currentTabData.length > 0" class="books-grid">
                    <n-card 
                      v-for="item in currentTabData" 
                      :key="item.id" 
                      class="book-card"
                      @click="goToBookDetail(item.bookId)"
                    >
                      <template #cover>
                        <img :src="(item as BookshelfItem).bookCover" :alt="item.bookTitle" class="book-cover" />
                      </template>

                      <template #header>
                        <h3 class="book-title">{{ item.bookTitle }}</h3>
                      </template>

                      <div class="book-info">
                        <p class="book-author">{{ (item as BookshelfItem).author }}</p>
                        <div class="book-tags">
                          <span 
                            v-for="tag in (item as BookshelfItem).tags.slice(0, 2)" 
                            :key="tag" 
                            class="tag"
                          >
                            {{ tag }}
                          </span>
                          <span v-if="(item as BookshelfItem).tags.length > 2" class="more-tags">...</span>
                        </div>
                      </div>

                      <template #action>
                        <div class="book-footer">
                          <span class="added-date">添加于 {{ (item as BookshelfItem).addedAt }}</span>
                          <span v-if="(item as BookshelfItem).lastReadAt" class="last-read">
                            最后阅读 {{ (item as BookshelfItem).lastReadAt }}
                          </span>
                        </div>
                      </template>
                    </n-card>
                  </div>
                  <n-empty v-else description="当前没有正在阅读的书籍">
                    <template #extra>
                      <n-button type="primary" @click="router.push('/books')">
                        去发现更多书籍
                      </n-button>
                    </template>
                  </n-empty>
                </div>
              </n-tab-pane>

              <!-- 已完成选项卡 -->
              <n-tab-pane name="completed" tab="已完成">
                <div class="tab-content">
                  <div v-if="currentTabData.length > 0" class="books-grid">
                    <n-card 
                      v-for="item in currentTabData" 
                      :key="item.id" 
                      class="book-card"
                      @click="goToBookDetail(item.bookId)"
                    >
                      <template #cover>
                        <img :src="(item as BookshelfItem).bookCover" :alt="item.bookTitle" class="book-cover" />
                      </template>

                      <template #header>
                        <h3 class="book-title">{{ item.bookTitle }}</h3>
                      </template>

                      <div class="book-info">
                        <p class="book-author">{{ (item as BookshelfItem).author }}</p>
                        <div class="book-tags">
                          <span 
                            v-for="tag in (item as BookshelfItem).tags.slice(0, 2)" 
                            :key="tag" 
                            class="tag"
                          >
                            {{ tag }}
                          </span>
                          <span v-if="(item as BookshelfItem).tags.length > 2" class="more-tags">...</span>
                        </div>
                      </div>

                      <template #action>
                        <div class="book-footer">
                          <span class="completed-date">完成于 {{ (item as BookshelfItem).lastReadAt }}</span>
                        </div>
                      </template>
                    </n-card>
                  </div>
                  <n-empty v-else description="当前没有已完成的书籍">
                    <template #extra>
                      <n-button type="primary" @click="router.push('/books')">
                        去发现更多书籍
                      </n-button>
                    </template>
                  </n-empty>
                </div>
              </n-tab-pane>

              <!-- 收藏选项卡 -->
              <n-tab-pane name="favorites" tab="我的收藏">
                <div class="tab-content">
                  <div v-if="currentTabData.length > 0" class="books-grid">
                    <n-card 
                      v-for="item in currentTabData" 
                      :key="item.id" 
                      class="book-card"
                      @click="goToBookDetail(item.bookId)"
                    >
                      <template #cover>
                        <img :src="(item as BookshelfItem).bookCover" :alt="item.bookTitle" class="book-cover" />
                      </template>

                      <template #header>
                        <h3 class="book-title">{{ item.bookTitle }}</h3>
                      </template>

                      <div class="book-info">
                        <p class="book-author">{{ (item as BookshelfItem).author }}</p>
                        <div class="book-tags">
                          <span 
                            v-for="tag in (item as BookshelfItem).tags.slice(0, 2)" 
                            :key="tag" 
                            class="tag"
                          >
                            {{ tag }}
                          </span>
                          <span v-if="(item as BookshelfItem).tags.length > 2" class="more-tags">...</span>
                        </div>
                      </div>

                      <template #action>
                        <div class="book-footer">
                          <span class="favorite-date">收藏于 {{ (item as BookshelfItem).addedAt }}</span>
                        </div>
                      </template>
                    </n-card>
                  </div>
                  <n-empty v-else description="当前没有收藏的书籍">
                    <template #extra>
                      <n-button type="primary" @click="router.push('/books')">
                        去发现更多书籍
                      </n-button>
                    </template>
                  </n-empty>
                </div>
              </n-tab-pane>

              <!-- 阅读记录选项卡 -->
              <n-tab-pane name="records" tab="阅读记录">
                <div class="tab-content">
                  <div v-if="currentTabData.length > 0" class="records-list">
                    <n-card 
                      v-for="record in currentTabData" 
                      :key="record.id" 
                      class="record-card"
                      @click="goToBookDetail(record.bookId)"
                    >
                      <div class="record-content">
                        <div class="record-info">
                          <h4 class="record-title">{{ record.bookTitle }}</h4>
                          <div class="record-progress">
                            <div class="progress-bar">
                              <div 
                                class="progress-fill" 
                                :style="{ width: (record as ReadingRecord).progress + '%' }"
                              ></div>
                            </div>
                            <span class="progress-text">
                              第{{ (record as ReadingRecord).currentChapter }}章 / 共{{ (record as ReadingRecord).totalChapters }}章
                              ({{ (record as ReadingRecord).progress }}%)
                            </span>
                          </div>
                        </div>
                        <div class="record-meta">
                          <div class="record-time">
                            <n-icon :component="TimeOutline" size="16" />
                            <span>阅读 {{ (record as ReadingRecord).readTime }} 分钟</span>
                          </div>
                          <div class="record-date">
                            <n-icon :component="EyeOutline" size="16" />
                            <span>{{ record.lastReadAt }}</span>
                          </div>
                        </div>
                      </div>
                    </n-card>
                  </div>
                  <n-empty v-else description="当前没有阅读记录">
                    <template #extra>
                      <n-button type="primary" @click="router.push('/books')">
                        开始阅读第一本书
                      </n-button>
                    </template>
                  </n-empty>
                </div>
              </n-tab-pane>
            </n-tabs>
          </n-card>
        </div>
      </div>
    </n-layout-content>
  </n-layout>
</template>

<style scoped>
.bookshelf-detail-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
  background-color: #f8f9fa;
}

.bookshelf-detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-section {
  margin-bottom: 20px;
}

.main-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
}

.tab-content {
  padding: 20px 0;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.book-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  cursor: pointer;
}

.book-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.book-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.book-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-info {
  margin: 12px 0;
}

.book-author {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 0.9rem;
}

.book-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #666;
}

.more-tags {
  color: #999;
  font-size: 0.8rem;
}

.book-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: #999;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  cursor: pointer;
}

.record-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.record-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.record-info {
  flex: 1;
}

.record-title {
  margin: 0 0 8px 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
}

.record-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-bar {
  width: 200px;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #18a058;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.9rem;
  color: #666;
}

.record-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-end;
}

.record-time, .record-date {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.8rem;
  color: #999;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .bookshelf-detail-content {
    padding: 16px;
  }

  .books-grid {
    grid-template-columns: 1fr;
  }

  .record-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .record-meta {
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
  }

  .progress-bar {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .bookshelf-detail-content {
    padding: 12px;
  }

  .section-title {
    font-size: 1.2rem;
  }

  .book-title {
    font-size: 1rem;
  }
}
</style>