<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { NCard, NButton, NSlider, NIcon, useMessage } from 'naive-ui'
import { 
  PlayOutline, 
  PauseOutline, 
  VolumeHighOutline, 
  VolumeMuteOutline, 
  ShuffleOutline, 
  RepeatOutline,
  CloseOutline,
  TimeOutline,
  ListOutline,
  PlaySkipForwardOutline,
  PlaySkipBackOutline
} from '@vicons/ionicons5'
import type { PlayerState, MusicResource, PlaylistState } from '../types/resource'

const props = defineProps<{
  resource: MusicResource
  visible: boolean
}>()

const emit = defineEmits<{
  close: []
  minimize: []
}>()

const message = useMessage()
const audioRef = ref<HTMLAudioElement | null>(null)
const showPlaylist = ref(false)

// 播放器状态
const playerState = ref<PlayerState>({
  isPlaying: false,
  currentTime: 0,
  duration: 0,
  volume: 0.7,
  isMuted: false,
  isLooping: false,
  isShuffling: false
})

// 播放列表状态
const playlistState = ref<PlaylistState>({
  currentTrackIndex: 0,
  tracks: [],
  isPlaying: false
})

// 当前播放的曲目
const currentTrack = computed(() => {
  if (playlistState.value.tracks.length > 0) {
    return playlistState.value.tracks[playlistState.value.currentTrackIndex]
  }
  return null
})



// 格式化时间
const formatTime = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// 当前播放时间
const currentTimeFormatted = computed(() => formatTime(playerState.value.currentTime))
const durationFormatted = computed(() => formatTime(playerState.value.duration))

// 播放/暂停
const togglePlay = () => {
  if (!audioRef.value) return

  if (playerState.value.isPlaying) {
    audioRef.value.pause()
  } else {
    audioRef.value.play().catch(error => {
      message.error('播放失败: ' + error.message)
    })
  }
}

// 设置播放时间
const setCurrentTime = (value: number) => {
  if (audioRef.value) {
    audioRef.value.currentTime = value
    playerState.value.currentTime = value
  }
}

// 设置音量
const setVolume = (value: number) => {
  if (audioRef.value) {
    audioRef.value.volume = value
    playerState.value.volume = value
  }
}

// 静音/取消静音
const toggleMute = () => {
  if (audioRef.value) {
    audioRef.value.muted = !playerState.value.isMuted
    playerState.value.isMuted = !playerState.value.isMuted
  }
}

// 切换循环
const toggleLoop = () => {
  if (audioRef.value) {
    audioRef.value.loop = !playerState.value.isLooping
    playerState.value.isLooping = !playerState.value.isLooping
  }
}

// 切换随机播放
const toggleShuffle = () => {
  playerState.value.isShuffling = !playerState.value.isShuffling
  // 随机播放逻辑需要在播放列表中实现
}

// 初始化播放列表
const initPlaylist = (resource: MusicResource) => {
  if (resource.tracks && resource.tracks.length > 0) {
    // 有播放列表
    playlistState.value.tracks = resource.tracks
    playlistState.value.currentTrackIndex = 0
    playlistState.value.isPlaying = false
  } else {
    // 单曲，创建单曲播放列表
    playlistState.value.tracks = [{
      id: resource.id,
      title: resource.title,
      artist: resource.artist,
      album: resource.album,
      duration: resource.duration,
      audioUrl: resource.audioUrl,
      coverImage: resource.coverImage
    }]
    playlistState.value.currentTrackIndex = 0
    playlistState.value.isPlaying = false
  }
}

// 切换曲目
const switchTrack = (index: number) => {
  if (index >= 0 && index < playlistState.value.tracks.length) {
    playlistState.value.currentTrackIndex = index
    playerState.value.currentTime = 0
    playerState.value.isPlaying = false
    
    // 延迟播放新曲目
    setTimeout(() => {
      if (audioRef.value) {
        audioRef.value.play().catch(error => {
          message.error('播放失败: ' + error.message)
        })
      }
    }, 100)
  }
}

// 下一首
const nextTrack = () => {
  const nextIndex = (playlistState.value.currentTrackIndex + 1) % playlistState.value.tracks.length
  switchTrack(nextIndex)
}

// 上一首
const prevTrack = () => {
  const prevIndex = playlistState.value.currentTrackIndex === 0 
    ? playlistState.value.tracks.length - 1 
    : playlistState.value.currentTrackIndex - 1
  switchTrack(prevIndex)
}

// 音频事件处理
const handleLoadedMetadata = () => {
  if (audioRef.value) {
    playerState.value.duration = audioRef.value.duration
  }
}

const handleTimeUpdate = () => {
  if (audioRef.value) {
    playerState.value.currentTime = audioRef.value.currentTime
  }
}

const handlePlay = () => {
  playerState.value.isPlaying = true
  playlistState.value.isPlaying = true
}

const handlePause = () => {
  playerState.value.isPlaying = false
  playlistState.value.isPlaying = false
}

const handleEnded = () => {
  playerState.value.isPlaying = false
  playerState.value.currentTime = 0
  playlistState.value.isPlaying = false
  
  // 自动播放下一首
  if (playlistState.value.tracks.length > 1) {
    nextTrack()
  }
}

// 切换播放列表显示
const togglePlaylist = () => {
  showPlaylist.value = !showPlaylist.value
}

// 关闭播放器
const handleClose = () => {
  if (audioRef.value) {
    audioRef.value.pause()
    audioRef.value.currentTime = 0
  }
  // 重置播放列表
  playlistState.value = {
    currentTrackIndex: 0,
    tracks: [],
    isPlaying: false
  }
  emit('close')
}

// 最小化播放器
const handleMinimize = () => {
  emit('minimize')
}

// 监听资源变化，重置播放列表
watch(() => props.resource, (newResource) => {
  if (newResource) {
    initPlaylist(newResource)
    playerState.value.currentTime = 0
    playerState.value.isPlaying = false
  }
}, { immediate: true })

onMounted(() => {
  // 初始化音量
  if (audioRef.value) {
    audioRef.value.volume = playerState.value.volume
  }
  
  // 初始化播放列表
  if (props.resource) {
    initPlaylist(props.resource)
  }
})

onUnmounted(() => {
  // 清理资源
  if (audioRef.value) {
    audioRef.value.pause()
    audioRef.value = null
  }
})
</script>

<template>
  <div v-if="visible" class="music-player">
    <n-card class="player-card" :bordered="false">
      <!-- 播放器头部 -->
      <div class="player-header">
        <div class="track-info">
          <h4 class="track-title">{{ currentTrack?.title || resource.title }}</h4>
          <p class="track-artist">{{ currentTrack?.artist || resource.artist }}</p>
          <p v-if="playlistState.tracks.length > 1" class="playlist-info">
            播放列表: {{ playlistState.currentTrackIndex + 1 }}/{{ playlistState.tracks.length }}
          </p>
        </div>
        <div class="player-actions">
          <n-button text @click="togglePlaylist" :title="showPlaylist ? '隐藏播放列表' : '显示播放列表'" class="action-button">
            <n-icon :component="ListOutline" />
          </n-button>
          <n-button text @click="handleMinimize" class="action-button">
            <n-icon :component="TimeOutline" />
          </n-button>
          <n-button text @click="handleClose" class="action-button">
            <n-icon :component="CloseOutline" />
          </n-button>
        </div>
      </div>

      <!-- 音频控制 -->
      <div class="audio-controls">
        <audio
          ref="audioRef"
          :src="resource.audioUrl"
          @loadedmetadata="handleLoadedMetadata"
          @timeupdate="handleTimeUpdate"
          @play="handlePlay"
          @pause="handlePause"
          @ended="handleEnded"
        ></audio>

        <!-- 播放控制 -->
        <div class="playback-controls">
          <n-button 
            text 
            @click="prevTrack" 
            class="control-button"
            :disabled="playlistState.tracks.length <= 1"
          >
            <n-icon :component="PlaySkipBackOutline" />
          </n-button>
          <n-button 
            circle 
            @click="togglePlay" 
            class="play-button"
            :type="playerState.isPlaying ? 'primary' : 'default'"
          >
            <n-icon :component="playerState.isPlaying ? PauseOutline : PlayOutline" />
          </n-button>
          <n-button 
            text 
            @click="nextTrack" 
            class="control-button"
            :disabled="playlistState.tracks.length <= 1"
          >
            <n-icon :component="PlaySkipForwardOutline" />
          </n-button>
        </div>

        <!-- 进度条 -->
        <div class="progress-section">
          <span class="time-display">{{ currentTimeFormatted }}</span>
          <n-slider
            :value="playerState.currentTime"
            :max="playerState.duration"
            @update:value="setCurrentTime"
            class="progress-slider"
          />
          <span class="time-display">{{ durationFormatted }}</span>
        </div>

        <!-- 音量控制 -->
        <div class="volume-controls">
          <n-button text @click="toggleMute" class="volume-button">
            <n-icon :component="playerState.isMuted ? VolumeMuteOutline : VolumeHighOutline" />
          </n-button>
          <n-slider
            :value="playerState.volume * 100"
            :min="0"
            :max="100"
            @update:value="setVolume"
            class="volume-slider"
          />
        </div>

        <!-- 播放模式 -->
        <div class="playback-modes">
          <n-button 
            text 
            @click="toggleShuffle" 
            class="mode-button"
            :type="playerState.isShuffling ? 'primary' : 'default'"
          >
            <n-icon :component="ShuffleOutline" />
          </n-button>
          <n-button 
            text 
            @click="toggleLoop" 
            class="mode-button"
            :type="playerState.isLooping ? 'primary' : 'default'"
          >
            <n-icon :component="RepeatOutline" />
          </n-button>
        </div>
      </div>

      <!-- 播放列表 -->
      <div v-if="showPlaylist && playlistState.tracks.length > 1" class="playlist-section">
        <h4 class="playlist-title">播放列表</h4>
        <div class="playlist-tracks">
          <div
            v-for="(track, index) in playlistState.tracks"
            :key="index"
            class="playlist-track"
            :class="{ active: index === playlistState.currentTrackIndex }"
            @click="switchTrack(index)"
          >
            <div class="track-number">{{ index + 1 }}</div>
            <div class="track-info">
              <div class="track-title">{{ track.title }}</div>
              <div class="track-artist">{{ track.artist }}</div>
              <div class="track-duration">{{ track.duration }}</div>
            </div>
            <n-icon v-if="index === playlistState.currentTrackIndex" :component="PlayOutline" />
          </div>
        </div>
      </div>

      <!-- 专辑信息 -->
      <div class="album-info" v-if="currentTrack?.album || resource.album">
        <p class="album-name">专辑: {{ currentTrack?.album || resource.album }}</p>
      </div>
    </n-card>
  </div>
</template>

<style scoped>
.music-player {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
  width: 400px;
  max-width: 90vw;
}

.player-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.player-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.track-info {
  flex: 1;
  margin-right: 16px;
}

.track-title {
  margin: 0 0 4px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.3;
}

.track-artist {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.player-actions {
  display: flex;
  gap: 4px;
}

.action-button {
  padding: 4px;
}

.audio-controls {
  display: grid;
  grid-template-columns: auto 1fr auto auto;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
}

.playback-controls {
  display: flex;
  justify-content: center;
}

.play-button {
  width: 48px;
  height: 48px;
  font-size: 1.2rem;
}

.progress-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.time-display {
  font-size: 0.8rem;
  color: #666;
  min-width: 40px;
  text-align: center;
}

.progress-slider {
  flex: 1;
}

.volume-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 120px;
}

.volume-button {
  padding: 4px;
}

.volume-slider {
  flex: 1;
}

.playback-modes {
  display: flex;
  gap: 4px;
}

.mode-button {
  padding: 4px 8px;
}

.playlist-section {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: 16px;
}

.playlist-title {
  margin: 0 0 12px 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
}

.playlist-tracks {
  max-height: 200px;
  overflow-y: auto;
}

.playlist-track {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-bottom: 4px;
}

.playlist-track:hover {
  background-color: #f5f5f5;
}

.playlist-track.active {
  background-color: #e6f7ff;
  border-left: 3px solid #1890ff;
}

.track-number {
  width: 24px;
  font-size: 0.8rem;
  color: #999;
  text-align: center;
  margin-right: 12px;
}

.playlist-track .track-info {
  flex: 1;
  margin-right: 12px;
}

.playlist-track .track-title {
  font-size: 0.9rem;
  font-weight: 500;
  margin: 0 0 2px 0;
  color: #1a1a1a;
}

.playlist-track .track-artist {
  font-size: 0.8rem;
  color: #666;
  margin: 0 0 2px 0;
}

.playlist-track .track-duration {
  font-size: 0.7rem;
  color: #999;
  margin: 0;
}

.album-info {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: 16px;
}

.album-name {
  margin: 0;
  font-size: 0.9rem;
  color: #999;
  text-align: center;
}

.playlist-info {
  margin: 4px 0 0 0;
  font-size: 0.8rem;
  color: #1890ff;
  font-weight: 500;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .music-player {
    bottom: 10px;
    right: 10px;
    left: 10px;
    width: auto;
  }

  .audio-controls {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .progress-section {
    order: 1;
  }

  .playback-controls {
    order: 2;
  }

  .volume-controls {
    order: 3;
    width: 100%;
    justify-content: center;
  }

  .playback-modes {
    order: 4;
    justify-content: center;
  }

  .player-header {
    flex-direction: column;
    gap: 8px;
  }

  .track-info {
    margin-right: 0;
    text-align: center;
  }

  .player-actions {
    align-self: flex-end;
  }
}

@media (max-width: 480px) {
  .music-player {
    bottom: 5px;
    right: 5px;
    left: 5px;
  }

  .player-card {
    padding: 12px;
  }

  .track-title {
    font-size: 1rem;
  }

  .track-artist {
    font-size: 0.8rem;
  }

  .play-button {
    width: 40px;
    height: 40px;
    font-size: 1rem;
  }

  .time-display {
    font-size: 0.7rem;
    min-width: 35px;
  }
}
</style>