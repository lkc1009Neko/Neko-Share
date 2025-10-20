// 书籍类型常量
export const BookTypes = {
  LIGHT_NOVEL: 'light_novel',
  MANGA: 'manga',
  MAGAZINE: 'magazine',
  NOVEL: 'novel',
  COMIC: 'comic',
  OTHER: 'other'
} as const

export type BookType = typeof BookTypes[keyof typeof BookTypes]

// 书籍风格标签
export const BookStyles = {
  FANTASY: '奇幻',
  MAGIC: '魔法',
  ROMANCE: '恋爱',
  ACTION: '动作',
  ADVENTURE: '冒险',
  COMEDY: '喜剧',
  DRAMA: '剧情',
  HORROR: '恐怖',
  MYSTERY: '悬疑',
  SCI_FI: '科幻',
  SLICE_OF_LIFE: '日常',
  SPORTS: '运动',
  SUPERNATURAL: '超自然'
} as const

export type BookStyle = typeof BookStyles[keyof typeof BookStyles]

// 书籍状态
export const BookStatus = {
  COMPLETED: 'completed',
  SERIALIZING: 'serializing',
  SUSPENDED: 'suspended',
  HIATUS: 'hiatus'
} as const

export type BookStatusType = typeof BookStatus[keyof typeof BookStatus]

// 单本书籍
export interface Book {
  id: string
  title: string
  coverImage: string
  author: string
  description: string
  tags: string[]
  type: BookType
  styles: BookStyle[]
  status: BookStatusType
  chapters: number
  words: number
  popularity: number
  favorites: number
  rating: number
  readCount: number
  createdAt: string
  updatedAt: string
}

// 书籍合集
export interface BookCollection {
  id: string
  title: string
  description: string
  coverImage: string
  author: string
  books: Book[]
  totalBooks: number
  totalChapters: number
  totalWords: number
  status: BookStatusType
  popularity: number
  favorites: number
  tags: string[]
  styles: BookStyle[]
  type: BookType
  createdAt: string
  updatedAt: string
}

// 阅读记录
export interface ReadingRecord {
  id: string
  bookId: string
  bookTitle: string
  currentChapter: number
  totalChapters: number
  progress: number
  lastReadAt: string
  readTime: number
}

// 书架项
export interface BookshelfItem {
  id: string
  bookId: string
  bookTitle: string
  bookCover: string
  author: string
  addedAt: string
  lastReadAt?: string
  isFavorite: boolean
  tags: string[]
}

// 用户书架
export interface UserBookshelf {
  totalBooks: number
  reading: BookshelfItem[]
  completed: BookshelfItem[]
  favorites: BookshelfItem[]
  wantToRead: BookshelfItem[]
}

// 搜索筛选条件
export interface BookSearchFilters {
  type?: BookType
  styles?: BookStyle[]
  status?: BookStatusType
  tags?: string[]
  minChapters?: number
  maxChapters?: number
  minRating?: number
  maxRating?: number
  sortBy: 'popularity' | 'rating' | 'favorites' | 'createdAt' | 'updatedAt'
  sortOrder: 'asc' | 'desc'
}