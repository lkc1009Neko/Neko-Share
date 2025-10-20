import apiClient from '../utils/api'
import type { User, Notification } from '../types'

// 用户信息更新请求
export interface UpdateUserRequest {
  username?: string
  email?: string
  bio?: string
  avatar?: string
}

// 关注用户请求
export interface FollowUserRequest {
  targetUserId: string
}

// 用户服务类
class UserService {
  // 获取用户信息
  async getUser(username: string): Promise<User> {
    return apiClient.get<User>(`/users/${username}`, {
      showError: true,
      errorConfig: {
        message: '获取用户信息失败',
        level: 'error' as any
      }
    })
  }

  // 更新用户信息
  async updateUser(userData: UpdateUserRequest): Promise<User> {
    return apiClient.put<User>('/users/profile', userData, {
      showError: true,
      errorConfig: {
        message: '更新用户信息失败',
        level: 'error' as any
      }
    })
  }

  // 关注用户
  async followUser(targetUserId: string): Promise<{ isFollowing: boolean; followers: number }> {
    return apiClient.post<{ isFollowing: boolean; followers: number }>('/users/follow', {
      targetUserId
    }, {
      showError: true
    })
  }

  // 取消关注
  async unfollowUser(targetUserId: string): Promise<{ isFollowing: boolean; followers: number }> {
    return apiClient.delete<{ isFollowing: boolean; followers: number }>('/users/follow', {
      data: { targetUserId },
      showError: true
    })
  }

  // 获取用户关注列表
  async getFollowing(userId: string, page?: number, limit?: number): Promise<{ users: User[]; total: number }> {
    return apiClient.get<{ users: User[]; total: number }>(`/users/${userId}/following`, {
      params: { page, limit },
      showError: true
    })
  }

  // 获取用户粉丝列表
  async getFollowers(userId: string, page?: number, limit?: number): Promise<{ users: User[]; total: number }> {
    return apiClient.get<{ users: User[]; total: number }>(`/users/${userId}/followers`, {
      params: { page, limit },
      showError: true
    })
  }

  // 获取用户通知
  async getNotifications(page?: number, limit?: number): Promise<{ notifications: Notification[]; total: number; unreadCount: number }> {
    return apiClient.get<{ notifications: Notification[]; total: number; unreadCount: number }>('/users/notifications', {
      params: { page, limit },
      showError: true
    })
  }

  // 标记通知为已读
  async markNotificationAsRead(notificationId: string): Promise<void> {
    return apiClient.put<void>(`/users/notifications/${notificationId}/read`, {}, {
      showError: false // 标记已读失败不显示错误
    })
  }

  // 标记所有通知为已读
  async markAllNotificationsAsRead(): Promise<void> {
    return apiClient.put<void>('/users/notifications/read-all', {}, {
      showError: false
    })
  }

  // 删除通知
  async deleteNotification(notificationId: string): Promise<void> {
    return apiClient.delete<void>(`/users/notifications/${notificationId}`, {
      showError: false
    })
  }

  // 获取用户统计数据
  async getUserStats(userId: string): Promise<{
    articleCount: number
    totalLikes: number
    totalViews: number
    followerCount: number
    followingCount: number
  }> {
    return apiClient.get<{
      articleCount: number
      totalLikes: number
      totalViews: number
      followerCount: number
      followingCount: number
    }>(`/users/${userId}/stats`, {
      showError: false
    })
  }
}

export const userService = new UserService()
export default userService