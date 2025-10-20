import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import type { MessageApi } from 'naive-ui'

// API响应数据结构
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  success: boolean
}

// 错误等级常量
export const ErrorLevel = {
  INFO: 'info',
  SUCCESS: 'success',
  WARNING: 'warning',
  ERROR: 'error',
  LOADING: 'loading'
} as const

// 错误等级类型
export type ErrorLevelType = 'info' | 'success' | 'warning' | 'error' | 'loading'

// 错误信息配置
export interface ErrorConfig {
  message: string
  level: ErrorLevelType
  duration?: number
}

// API配置
export interface ApiConfig extends AxiosRequestConfig {
  showError?: boolean
  errorConfig?: ErrorConfig
}

class ApiClient {
  private instance: AxiosInstance
  private message: MessageApi | null = null

  constructor(baseURL: string = '/api') {
    this.instance = axios.create({
      baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json'
      }
    })

    this.setupInterceptors()
  }

  // 设置消息实例（需要在Vue组件中调用）
  setMessageInstance(message: MessageApi) {
    this.message = message
  }

  // 设置请求拦截器
  private setupInterceptors() {
    // 请求拦截器
    this.instance.interceptors.request.use(
      (config) => {
        // 添加认证token
        const token = localStorage.getItem('auth_token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // 响应拦截器
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => {
        return response
      },
      (error) => {
        this.handleError(error)
        return Promise.reject(error)
      }
    )
  }

  // 错误处理
  private handleError(error: any) {
    if (!this.message) return

    let errorMessage = '网络请求失败'
    let errorLevel: ErrorLevelType = 'error'

    if (error.response) {
      // 服务器返回错误状态码
      const status = error.response.status
      const data = error.response.data

      switch (status) {
        case 400:
          errorMessage = data?.message || '请求参数错误'
          errorLevel = ErrorLevel.WARNING
          break
        case 401:
          errorMessage = '未授权，请重新登录'
          errorLevel = ErrorLevel.WARNING
          // 清除token并跳转到登录页
          localStorage.removeItem('auth_token')
          window.location.href = '/login'
          break
        case 403:
          errorMessage = '权限不足'
          errorLevel = ErrorLevel.WARNING
          break
        case 404:
          errorMessage = '请求的资源不存在'
          errorLevel = ErrorLevel.WARNING
          break
        case 500:
          errorMessage = '服务器内部错误'
          errorLevel = ErrorLevel.ERROR
          break
        case 502:
          errorMessage = '网关错误'
          errorLevel = ErrorLevel.ERROR
          break
        case 503:
          errorMessage = '服务不可用'
          errorLevel = ErrorLevel.ERROR
          break
        default:
          errorMessage = data?.message || `服务器错误 (${status})`
          errorLevel = ErrorLevel.ERROR
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      errorMessage = '网络连接失败，请检查网络设置'
      errorLevel = ErrorLevel.ERROR
    } else {
      // 请求配置错误
      errorMessage = error.message || '请求配置错误'
      errorLevel = ErrorLevel.ERROR
    }

    // 显示错误消息
    this.showMessage(errorMessage, errorLevel)
  }

  // 显示消息
  private showMessage(message: string, level: ErrorLevelType, duration?: number) {
    if (!this.message) return

    const messageDuration = duration || (level === ErrorLevel.ERROR ? 5000 : 3000)

    switch (level) {
      case ErrorLevel.INFO:
        this.message.info(message, { duration: messageDuration })
        break
      case ErrorLevel.SUCCESS:
        this.message.success(message, { duration: messageDuration })
        break
      case ErrorLevel.WARNING:
        this.message.warning(message, { duration: messageDuration })
        break
      case ErrorLevel.ERROR:
        this.message.error(message, { duration: messageDuration })
        break
      case ErrorLevel.LOADING:
        this.message.loading(message, { duration: messageDuration })
        break
    }
  }

  // 通用请求方法
  private async request<T = any>(config: ApiConfig): Promise<T> {
    try {
      const response = await this.instance.request<ApiResponse<T>>(config)
      const data = response.data

      // 检查业务逻辑错误
      if (data.code !== 200 && data.code !== 0) {
        if (config.showError !== false) {
          const errorConfig = config.errorConfig || {
            message: data.message || '操作失败',
            level: ErrorLevel.ERROR
          }
          this.showMessage(errorConfig.message, errorConfig.level, errorConfig.duration)
        }
        throw new Error(data.message || '操作失败')
      }

      // 显示成功消息（如果配置了）
      if (config.showError && data.message) {
        this.showMessage(data.message, ErrorLevel.SUCCESS)
      }

      return data.data
    } catch (error) {
      // 错误已经在拦截器中处理，这里直接抛出
      throw error
    }
  }

  // GET请求
  async get<T = any>(url: string, config?: ApiConfig): Promise<T> {
    return this.request<T>({
      method: 'GET',
      url,
      ...config
    })
  }

  // POST请求
  async post<T = any>(url: string, data?: any, config?: ApiConfig): Promise<T> {
    return this.request<T>({
      method: 'POST',
      url,
      data,
      ...config
    })
  }

  // PUT请求
  async put<T = any>(url: string, data?: any, config?: ApiConfig): Promise<T> {
    return this.request<T>({
      method: 'PUT',
      url,
      data,
      ...config
    })
  }

  // DELETE请求
  async delete<T = any>(url: string, config?: ApiConfig): Promise<T> {
    return this.request<T>({
      method: 'DELETE',
      url,
      ...config
    })
  }

  // PATCH请求
  async patch<T = any>(url: string, data?: any, config?: ApiConfig): Promise<T> {
    return this.request<T>({
      method: 'PATCH',
      url,
      data,
      ...config
    })
  }

  // 上传文件
  async upload<T = any>(url: string, formData: FormData, config?: ApiConfig): Promise<T> {
    return this.request<T>({
      method: 'POST',
      url,
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      ...config
    })
  }

  // 下载文件
  async download(url: string, config?: ApiConfig): Promise<Blob> {
    const response = await this.instance.request({
      method: 'GET',
      url,
      responseType: 'blob',
      ...config
    })
    return response.data
  }
}

// 创建全局实例
const apiClient = new ApiClient(import.meta.env.VITE_API_BASE_URL || '/api')

export { ApiClient, apiClient }
export default apiClient