import apiClient from '../utils/api'
import type { Article } from '../types'

// 文章列表请求参数
export interface ArticleListRequest {
  page?: number
  limit?: number
  search?: string
  tags?: string[]
  authorId?: string
  sortBy?: 'createdAt' | 'updatedAt' | 'likes' | 'views'
  sortOrder?: 'asc' | 'desc'
}

// 文章列表响应
export interface ArticleListResponse {
  articles: Article[]
  total: number
  page: number
  limit: number
  totalPages: number
}

// 创建文章请求
export interface CreateArticleRequest {
  title: string
  content: string
  tags: string[]
  excerpt?: string
  coverImage?: string
}

// 更新文章请求
export interface UpdateArticleRequest extends Partial<CreateArticleRequest> {
  id: string
}

// 文章服务类
class ArticleService {
  // 获取文章列表
  async getArticles(params?: ArticleListRequest): Promise<ArticleListResponse> {
    return apiClient.get<ArticleListResponse>('/articles', {
      params,
      showError: true
    })
  }

  // 获取文章详情
  async getArticle(id: string): Promise<Article> {
    return apiClient.get<Article>(`/articles/${id}`, {
      showError: true,
      errorConfig: {
        message: '获取文章失败',
        level: 'error' as any
      }
    })
  }

  // 创建文章
  async createArticle(articleData: CreateArticleRequest): Promise<Article> {
    return apiClient.post<Article>('/articles', articleData, {
      showError: true,
      errorConfig: {
        message: '创建文章失败',
        level: 'error' as any
      }
    })
  }

  // 更新文章
  async updateArticle(articleData: UpdateArticleRequest): Promise<Article> {
    return apiClient.put<Article>(`/articles/${articleData.id}`, articleData, {
      showError: true,
      errorConfig: {
        message: '更新文章失败',
        level: 'error' as any
      }
    })
  }

  // 删除文章
  async deleteArticle(id: string): Promise<void> {
    return apiClient.delete<void>(`/articles/${id}`, {
      showError: true,
      errorConfig: {
        message: '删除文章失败',
        level: 'error' as any
      }
    })
  }

  // 点赞文章
  async likeArticle(id: string): Promise<{ likes: number; isLiked: boolean }> {
    return apiClient.post<{ likes: number; isLiked: boolean }>(`/articles/${id}/like`, {}, {
      showError: true
    })
  }

  // 取消点赞
  async unlikeArticle(id: string): Promise<{ likes: number; isLiked: boolean }> {
    return apiClient.delete<{ likes: number; isLiked: boolean }>(`/articles/${id}/like`, {
      showError: true
    })
  }

  // 收藏文章
  async bookmarkArticle(id: string): Promise<{ isBookmarked: boolean }> {
    return apiClient.post<{ isBookmarked: boolean }>(`/articles/${id}/bookmark`, {}, {
      showError: true
    })
  }

  // 取消收藏
  async unbookmarkArticle(id: string): Promise<{ isBookmarked: boolean }> {
    return apiClient.delete<{ isBookmarked: boolean }>(`/articles/${id}/bookmark`, {
      showError: true
    })
  }

  // 获取用户收藏的文章
  async getUserBookmarks(userId: string, params?: Omit<ArticleListRequest, 'authorId'>): Promise<ArticleListResponse> {
    return apiClient.get<ArticleListResponse>(`/users/${userId}/bookmarks`, {
      params,
      showError: true
    })
  }

  // 搜索文章
  async searchArticles(query: string, params?: Omit<ArticleListRequest, 'search'>): Promise<ArticleListResponse> {
    return apiClient.get<ArticleListResponse>('/articles/search', {
      params: { ...params, q: query },
      showError: true
    })
  }

  // 根据标签获取文章
  async getArticlesByTag(tag: string, params?: Omit<ArticleListRequest, 'tags'>): Promise<ArticleListResponse> {
    return apiClient.get<ArticleListResponse>(`/tags/${tag}/articles`, {
      params,
      showError: true
    })
  }

  // 获取热门标签
  async getPopularTags(limit: number = 10): Promise<{ tag: string; count: number }[]> {
    return apiClient.get<{ tag: string; count: number }[]>('/tags/popular', {
      params: { limit },
      showError: false // 获取标签失败不显示错误
    })
  }
}

export const articleService = new ArticleService()
export default articleService