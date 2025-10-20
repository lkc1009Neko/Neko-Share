import apiClient from '../utils/api'
import type { Resource, ResourceListRequest, ResourceListResponse, ResourceCategory } from '../types/resource'

// 创建资源请求
export interface CreateResourceRequest {
  title: string
  description: string
  type: string
  category: string
  tags: string[]
  file?: File
  fileUrl?: string
  size?: string
  format?: string
  duration?: string
  coverImage?: string
  // 音乐特定字段
  artist?: string
  album?: string
  audioUrl?: string
  // 视频特定字段
  videoUrl?: string
  resolution?: string
  // 软件/游戏特定字段
  version?: string
  platform?: string[]
  systemRequirements?: string
  genre?: string[]
  // 文档特定字段
  pageCount?: number
  language?: string
}

// 更新资源请求
export interface UpdateResourceRequest extends Partial<CreateResourceRequest> {
  id: string
}

// 资源服务类
class ResourceService {
  // 获取资源列表
  async getResources(params?: ResourceListRequest): Promise<ResourceListResponse> {
    return apiClient.get<ResourceListResponse>('/resources', {
      params,
      showError: true
    })
  }

  // 获取资源详情
  async getResource(id: string): Promise<Resource> {
    return apiClient.get<Resource>(`/resources/${id}`, {
      showError: true,
      errorConfig: {
        message: '获取资源失败',
        level: 'error'
      }
    })
  }

  // 创建资源
  async createResource(resourceData: CreateResourceRequest): Promise<Resource> {
    const formData = new FormData()
    
    // 添加基本字段
    Object.keys(resourceData).forEach(key => {
      if (key !== 'file' && resourceData[key as keyof CreateResourceRequest] !== undefined) {
        formData.append(key, String(resourceData[key as keyof CreateResourceRequest]))
      }
    })
    
    // 添加文件
    if (resourceData.file) {
      formData.append('file', resourceData.file)
    }

    return apiClient.post<Resource>('/resources', formData, {
      showError: true,
      errorConfig: {
        message: '创建资源失败',
        level: 'error'
      }
    })
  }

  // 更新资源
  async updateResource(resourceData: UpdateResourceRequest): Promise<Resource> {
    const formData = new FormData()
    
    Object.keys(resourceData).forEach(key => {
      if (key !== 'id' && key !== 'file' && resourceData[key as keyof UpdateResourceRequest] !== undefined) {
        formData.append(key, String(resourceData[key as keyof UpdateResourceRequest]))
      }
    })
    
    if (resourceData.file) {
      formData.append('file', resourceData.file)
    }

    return apiClient.put<Resource>(`/resources/${resourceData.id}`, formData, {
      showError: true,
      errorConfig: {
        message: '更新资源失败',
        level: 'error'
      }
    })
  }

  // 删除资源
  async deleteResource(id: string): Promise<void> {
    return apiClient.delete<void>(`/resources/${id}`, {
      showError: true,
      errorConfig: {
        message: '删除资源失败',
        level: 'error'
      }
    })
  }

  // 下载资源
  async downloadResource(id: string): Promise<Blob> {
    return apiClient.download(`/resources/${id}/download`, {
      showError: true,
      errorConfig: {
        message: '下载资源失败',
        level: 'error'
      }
    })
  }

  // 点赞资源
  async likeResource(id: string): Promise<{ likes: number; isLiked: boolean }> {
    return apiClient.post<{ likes: number; isLiked: boolean }>(`/resources/${id}/like`, {}, {
      showError: true
    })
  }

  // 取消点赞
  async unlikeResource(id: string): Promise<{ likes: number; isLiked: boolean }> {
    return apiClient.delete<{ likes: number; isLiked: boolean }>(`/resources/${id}/like`, {
      showError: true
    })
  }

  // 收藏资源
  async bookmarkResource(id: string): Promise<{ isBookmarked: boolean }> {
    return apiClient.post<{ isBookmarked: boolean }>(`/resources/${id}/bookmark`, {}, {
      showError: true
    })
  }

  // 取消收藏
  async unbookmarkResource(id: string): Promise<{ isBookmarked: boolean }> {
    return apiClient.delete<{ isBookmarked: boolean }>(`/resources/${id}/bookmark`, {
      showError: true
    })
  }

  // 获取资源分类
  async getCategories(): Promise<ResourceCategory[]> {
    return apiClient.get<ResourceCategory[]>('/resources/categories', {
      showError: false
    })
  }

  // 搜索资源
  async searchResources(query: string, params?: Omit<ResourceListRequest, 'search'>): Promise<ResourceListResponse> {
    return apiClient.get<ResourceListResponse>('/resources/search', {
      params: { ...params, q: query },
      showError: true
    })
  }

  // 根据标签获取资源
  async getResourcesByTag(tag: string, params?: Omit<ResourceListRequest, 'tags'>): Promise<ResourceListResponse> {
    return apiClient.get<ResourceListResponse>(`/tags/${tag}/resources`, {
      params,
      showError: true
    })
  }

  // 获取热门资源
  async getPopularResources(limit: number = 10): Promise<Resource[]> {
    return apiClient.get<Resource[]>('/resources/popular', {
      params: { limit },
      showError: false
    })
  }

  // 获取用户上传的资源
  async getUserResources(userId: string, params?: Omit<ResourceListRequest, 'authorId'>): Promise<ResourceListResponse> {
    return apiClient.get<ResourceListResponse>(`/users/${userId}/resources`, {
      params,
      showError: true
    })
  }

  // 获取用户收藏的资源
  async getUserBookmarks(userId: string, params?: Omit<ResourceListRequest, 'authorId'>): Promise<ResourceListResponse> {
    return apiClient.get<ResourceListResponse>(`/users/${userId}/resource-bookmarks`, {
      params,
      showError: true
    })
  }
}

export const resourceService = new ResourceService()
export default resourceService