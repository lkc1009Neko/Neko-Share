// 资源类型常量
export const ResourceTypes = {
  MUSIC: 'music',
  VIDEO: 'video',
  DOCUMENT: 'document',
  SOFTWARE: 'software',
  GAME: 'game',
  TOOL: 'tool',
  OTHER: 'other'
} as const

export type ResourceType = typeof ResourceTypes[keyof typeof ResourceTypes]

// 资源类型定义
export interface Resource {
  id: string
  title: string
  description: string
  type: ResourceType
  category: string
  tags: string[]
  fileUrl?: string
  downloadCount: number
  size?: string
  format?: string
  duration?: string // 音频/视频时长
  coverImage?: string
  audioUrl?: string // 音频文件URL
  tracks?: MusicTrack[] // 音乐播放列表
  author: {
    id: string
    username: string
    avatar?: string
  }
  isLiked: boolean
  likes: number
  isBookmarked: boolean
  createdAt: string
  updatedAt: string
}

// 单首音乐曲目
export interface MusicTrack {
  id: string
  title: string
  artist: string
  album?: string
  duration: string
  audioUrl: string
  coverImage?: string
}

// 音乐资源扩展（支持播放列表）
export interface MusicResource extends Resource {
  type: typeof ResourceTypes.MUSIC
  artist: string
  album?: string
  duration: string
  audioUrl: string
  // 支持单曲或播放列表
  tracks?: MusicTrack[]
  isPlaylist?: boolean
}

// 视频资源扩展
export interface VideoResource extends Resource {
  type: typeof ResourceTypes.VIDEO
  duration: string
  videoUrl: string
  resolution?: string
}

// 文档资源扩展
export interface DocumentResource extends Resource {
  type: typeof ResourceTypes.DOCUMENT
  pageCount?: number
  language?: string
}

// 软件资源扩展
export interface SoftwareResource extends Resource {
  type: typeof ResourceTypes.SOFTWARE
  version: string
  platform: string[]
  systemRequirements?: string
}

// 游戏资源扩展
export interface GameResource extends Resource {
  type: typeof ResourceTypes.GAME
  version: string
  platform: string[]
  genre: string[]
  systemRequirements?: string
}

// 工具资源扩展
export interface ToolResource extends Resource {
  type: typeof ResourceTypes.TOOL
  version?: string
  platform?: string[]
}

// 资源分类
export interface ResourceCategory {
  id: string
  name: string
  description: string
  icon: string
  color: string
  resourceCount: number
}

// 资源列表请求参数
export interface ResourceListRequest {
  page?: number
  limit?: number
  search?: string
  type?: ResourceType
  category?: string
  tags?: string[]
  sortBy?: 'createdAt' | 'updatedAt' | 'downloadCount' | 'likes'
  sortOrder?: 'asc' | 'desc'
}

// 资源列表响应
export interface ResourceListResponse {
  resources: Resource[]
  total: number
  page: number
  limit: number
  totalPages: number
}

// 播放器状态
export interface PlayerState {
  isPlaying: boolean
  currentTime: number
  duration: number
  volume: number
  isMuted: boolean
  isLooping: boolean
  isShuffling: boolean
}

// 播放列表状态
export interface PlaylistState {
  currentTrackIndex: number
  tracks: MusicTrack[]
  isPlaying: boolean
}

// 当前播放的资源
export interface NowPlaying {
  resource: MusicResource
  playerState: PlayerState
  playlistState?: PlaylistState
}