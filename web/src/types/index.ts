export interface User {
  id: string
  username: string
  email: string
  avatar?: string
  bio?: string
  followers: number
  following: number
  createdAt: string
}

export interface Article {
  id: string
  title: string
  content: string
  excerpt: string
  author: User
  tags: string[]
  likes: number
  comments: number
  views: number
  isLiked: boolean
  isBookmarked: boolean
  createdAt: string
  updatedAt: string
}

export interface Notification {
  id: string
  type: 'like' | 'comment' | 'follow' | 'system'
  title: string
  content: string
  isRead: boolean
  createdAt: string
  relatedId?: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  email: string
  password: string
  confirmPassword: string
}