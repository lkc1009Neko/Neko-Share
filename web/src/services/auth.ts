import apiClient from '../utils/api'
import type { User } from '../types'

// 登录请求参数
export interface LoginRequest {
  username: string
  password: string
}

// 注册请求参数
export interface RegisterRequest {
  username: string
  email: string
  password: string
  confirmPassword: string
}

// 登录响应
export interface LoginResponse {
  user: User
  token: string
  expiresIn: number
}

// 认证服务类
class AuthService {
  // 用户登录
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    return apiClient.post<LoginResponse>('/auth/login', credentials, {
      showError: true,
      errorConfig: {
        message: '登录失败，请检查用户名和密码',
        level: 'error' as any
      }
    })
  }

  // 用户注册
  async register(userData: RegisterRequest): Promise<User> {
    return apiClient.post<User>('/auth/register', userData, {
      showError: true,
      errorConfig: {
        message: '注册失败，请检查输入信息',
        level: 'error' as any
      }
    })
  }

  // 获取当前用户信息
  async getCurrentUser(): Promise<User> {
    return apiClient.get<User>('/auth/me', {
      showError: true
    })
  }

  // 刷新token
  async refreshToken(): Promise<{ token: string; expiresIn: number }> {
    return apiClient.post<{ token: string; expiresIn: number }>('/auth/refresh')
  }

  // 退出登录
  async logout(): Promise<void> {
    return apiClient.post<void>('/auth/logout', {}, {
      showError: false // 退出登录失败不显示错误
    })
  }

  // 修改密码
  async changePassword(oldPassword: string, newPassword: string): Promise<void> {
    return apiClient.post<void>('/auth/change-password', {
      oldPassword,
      newPassword
    }, {
      showError: true,
      errorConfig: {
        message: '密码修改失败',
        level: 'error' as any
      }
    })
  }
}

export const authService = new AuthService()
export default authService