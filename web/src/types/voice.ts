export interface Voice {
  id: string
  title: string
  author: string
  description: string
  coverImage: string
  audioUrl: string
  duration: number
  rating: number
  downloads: number
  favorites: number
  plays: number
  tags: string[]
  category: string
  createdAt: string
  updatedAt: string
}

export interface VoiceCollection {
  id: string
  name: string
  description: string
  voices: Voice[]
  createdAt: string
}

export interface VoiceStats {
  totalVoices: number
  totalPlays: number
  totalDownloads: number
  totalFavorites: number
}

export interface VoiceFilter {
  sortBy: 'hot' | 'favorites' | 'plays' | 'downloads' | 'latest'
  category: string
  searchKeyword: string
}