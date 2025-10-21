<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

import { 
  NLayout, 
  NLayoutContent, 
  NCard, 
  NButton, 
  NAvatar, 
  NInput, 
  NEmpty,
  NIcon,
  NSelect,
  NSlider,
  NStatistic,
  NGrid,
  NGi,
  NTag,
  useMessage,
  NSpin
} from 'naive-ui'
import { SearchOutline, GameControllerOutline, PeopleOutline, TrophyOutline, TimeOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'

const message = useMessage()

// 搜索和筛选状态
const searchKeyword = ref('')
const selectedPlatform = ref('all')
const selectedCategory = ref('all')
const priceRange = ref<[number, number]>([0, 100])

// 加载状态
const loading = ref(false)

// 平台选项
const platformOptions = [
  { label: '全部平台', value: 'all' },
  { label: 'Steam', value: 'steam' },
  { label: 'Epic Games', value: 'epic' },
  { label: 'GOG', value: 'gog' },
  { label: 'Ubisoft', value: 'ubisoft' },
  { label: 'Origin', value: 'origin' }
]

// 游戏分类选项
const categoryOptions = [
  { label: '全部类型', value: 'all' },
  { label: '动作', value: 'action' },
  { label: '冒险', value: 'adventure' },
  { label: '角色扮演', value: 'rpg' },
  { label: '策略', value: 'strategy' },
  { label: '模拟', value: 'simulation' },
  { label: '体育', value: 'sports' },
  { label: '竞速', value: 'racing' },
  { label: '独立', value: 'indie' },
  { label: '休闲', value: 'casual' }
]

// 模拟Steam用户数据
const steamUser = ref({
  steamId: '76561198000000000',
  username: '游戏玩家',
  avatar: '/images/avatars/gamer.jpg',
  level: 42,
  gamesCount: 156,
  playtime: 1250,
  achievements: 843,
  status: '在线',
  lastOnline: '2小时前'
})

// 模拟游戏数据（从Steam API获取）
const games = ref([
  {
    id: '1',
    name: 'Cyberpunk 2077',
    platform: 'steam',
    category: 'rpg',
    price: 298,
    discount: 50,
    finalPrice: 149,
    rating: 4.2,
    releaseDate: '2020-12-10',
    developer: 'CD Projekt Red',
    publisher: 'CD Projekt',
    description: '开放世界动作冒险RPG游戏，故事发生在夜之城，一个权力更迭和身体改造的城市。',
    image: '/images/games/cyberpunk2077.jpg',
    tags: ['RPG', '开放世界', '赛博朋克', '动作'],
    metacritic: 86,
    players: 25400
  },
  {
    id: '2',
    name: 'Elden Ring',
    platform: 'steam',
    category: 'rpg',
    price: 298,
    discount: 0,
    finalPrice: 298,
    rating: 4.8,
    releaseDate: '2022-02-25',
    developer: 'FromSoftware',
    publisher: 'Bandai Namco',
    description: '黑暗幻想风格动作RPG游戏，以广阔的世界和深度的战斗系统为特色。',
    image: '/images/games/eldenring.jpg',
    tags: ['RPG', '黑暗幻想', '动作', '开放世界'],
    metacritic: 96,
    players: 18200
  },
  {
    id: '3',
    name: 'Counter-Strike 2',
    platform: 'steam',
    category: 'action',
    price: 0,
    discount: 0,
    finalPrice: 0,
    rating: 4.5,
    releaseDate: '2023-09-27',
    developer: 'Valve',
    publisher: 'Valve',
    description: '经典的团队竞技射击游戏，全球最受欢迎的电子竞技游戏之一。',
    image: '/images/games/cs2.jpg',
    tags: ['FPS', '竞技', '多人', '免费'],
    metacritic: 88,
    players: 1250000
  },
  {
    id: '4',
    name: 'Baldur\'s Gate 3',
    platform: 'steam',
    category: 'rpg',
    price: 298,
    discount: 10,
    finalPrice: 268,
    rating: 4.9,
    releaseDate: '2023-08-03',
    developer: 'Larian Studios',
    publisher: 'Larian Studios',
    description: '基于龙与地下城规则的史诗级角色扮演游戏，拥有丰富的剧情和选择。',
    image: '/images/games/baldursgate3.jpg',
    tags: ['RPG', '奇幻', '回合制', '剧情丰富'],
    metacritic: 96,
    players: 89000
  },
  {
    id: '5',
    name: 'Red Dead Redemption 2',
    platform: 'steam',
    category: 'adventure',
    price: 249,
    discount: 67,
    finalPrice: 82,
    rating: 4.7,
    releaseDate: '2019-12-05',
    developer: 'Rockstar Games',
    publisher: 'Rockstar Games',
    description: '美国西部题材的开放世界动作冒险游戏，讲述亚瑟·摩根的故事。',
    image: '/images/games/rdr2.jpg',
    tags: ['开放世界', '西部', '动作', '冒险'],
    metacritic: 93,
    players: 45600
  },
  {
    id: '6',
    name: 'The Witcher 3: Wild Hunt',
    platform: 'steam',
    category: 'rpg',
    price: 127,
    discount: 80,
    finalPrice: 25,
    rating: 4.9,
    releaseDate: '2015-05-19',
    developer: 'CD Projekt Red',
    publisher: 'CD Projekt',
    description: '获奖无数的奇幻RPG游戏，讲述猎魔人杰洛特的冒险故事。',
    image: '/images/games/witcher3.jpg',
    tags: ['RPG', '奇幻', '开放世界', '剧情'],
    metacritic: 93,
    players: 32400
  }
])

// 过滤后的游戏列表
const filteredGames = computed(() => {
  let result = games.value

  // 平台筛选
  if (selectedPlatform.value !== 'all') {
    result = result.filter(game => game.platform === selectedPlatform.value)
  }

  // 分类筛选
  if (selectedCategory.value !== 'all') {
    result = result.filter(game => game.category === selectedCategory.value)
  }

  // 价格筛选
  result = result.filter(game => 
    game.finalPrice >= priceRange.value[0] && 
    game.finalPrice <= priceRange.value[1]
  )

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(game => 
      game.name.toLowerCase().includes(keyword) ||
      game.description.toLowerCase().includes(keyword) ||
      game.tags.some(tag => tag.toLowerCase().includes(keyword)) ||
      game.developer.toLowerCase().includes(keyword) ||
      game.publisher.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 获取游戏价格显示
function getPriceDisplay(game: any) {
  if (game.price === 0) {
    return '免费'
  }
  
  if (game.discount > 0) {
    return `¥${game.finalPrice}`
  }
  
  return `¥${game.price}`
}

// 获取折扣标签颜色
function getDiscountColor(discount: number) {
  if (discount >= 75) return 'error'
  if (discount >= 50) return 'warning'
  if (discount >= 25) return 'info'
  return 'success'
}

// 获取评分颜色
function getRatingColor(rating: number) {
  if (rating >= 4.5) return 'success'
  if (rating >= 4.0) return 'warning'
  return 'error'
}

// 模拟从Steam API获取数据
async function fetchSteamData() {
  loading.value = true
  
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 这里应该是实际的Steam API调用
    // const response = await fetch(`https://api.steampowered.com/...`)
    // const data = await response.json()
    
    message.success('游戏数据加载成功')
  } catch (error) {
    message.error('获取游戏数据失败')
  } finally {
    loading.value = false
  }
}

// 连接到Steam账户
function connectSteam() {
  message.info('Steam账户连接功能开发中...')
}

onMounted(() => {
  fetchSteamData()
})
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="games-content">
      <div class="games-container">
        <!-- Steam个人信息组件 -->
        <n-card class="steam-profile-card" :bordered="false">
          <div class="steam-profile">
            <div class="profile-header">
              <n-avatar
                round
                :size="80"
                :src="steamUser.avatar"
                :fallback-src="'/images/avatars/default.jpg'"
              />
              <div class="profile-info">
                <h2 class="username">{{ steamUser.username }}</h2>
                <div class="steam-id">Steam ID: {{ steamUser.steamId }}</div>
                <div class="status">
                  <n-tag :type="steamUser.status === '在线' ? 'success' : 'default'" size="small">
                    {{ steamUser.status }}
                  </n-tag>
                  <span class="last-online">最后在线: {{ steamUser.lastOnline }}</span>
                </div>
              </div>
              <n-button type="primary" @click="connectSteam" class="connect-button">
                <template #icon>
                  <n-icon :component="GameControllerOutline" />
                </template>
                连接Steam账户
              </n-button>
            </div>

            <n-divider />

            <div class="profile-stats">
              <n-grid :cols="4" responsive="screen">
                <n-gi>
                  <n-statistic label="Steam等级" :value="steamUser.level">
                    <template #prefix>
                      <n-icon :component="TrophyOutline" />
                    </template>
                  </n-statistic>
                </n-gi>
                <n-gi>
                  <n-statistic label="游戏数量" :value="steamUser.gamesCount">
                    <template #prefix>
                      <n-icon :component="GameControllerOutline" />
                    </template>
                  </n-statistic>
                </n-gi>
                <n-gi>
                  <n-statistic label="游戏时长" :value="steamUser.playtime" suffix="小时">
                    <template #prefix>
                      <n-icon :component="TimeOutline" />
                    </template>
                  </n-statistic>
                </n-gi>
                <n-gi>
                  <n-statistic label="成就数量" :value="steamUser.achievements">
                    <template #prefix>
                      <n-icon :component="TrophyOutline" />
                    </template>
                  </n-statistic>
                </n-gi>
              </n-grid>
            </div>
          </div>
        </n-card>

        <!-- 搜索和筛选区域 -->
        <n-card class="search-filter-card" :bordered="false">
          <div class="search-filter">
            <!-- 搜索框 -->
            <div class="search-section">
              <n-input
                v-model:value="searchKeyword"
                placeholder="搜索游戏名称、描述、开发商或标签..."
                clearable
                size="large"
              >
                <template #prefix>
                  <n-icon :component="SearchOutline" />
                </template>
              </n-input>
            </div>

            <!-- 筛选条件 -->
            <div class="filter-section">
              <div class="filter-group">
                <label class="filter-label">平台:</label>
                <n-select
                  v-model:value="selectedPlatform"
                  :options="platformOptions"
                  size="small"
                  style="width: 140px"
                />
              </div>

              <div class="filter-group">
                <label class="filter-label">类型:</label>
                <n-select
                  v-model:value="selectedCategory"
                  :options="categoryOptions"
                  size="small"
                  style="width: 140px"
                />
              </div>

              <div class="filter-group">
                <label class="filter-label">价格范围: ¥{{ priceRange[0] }} - ¥{{ priceRange[1] }}</label>
                <n-slider
                  v-model:value="priceRange"
                  :min="0"
                  :max="500"
                  :step="10"
                  range
                  style="width: 200px"
                />
              </div>
            </div>
          </div>
        </n-card>

        <!-- 游戏列表区域 -->
        <div class="games-main">
          <!-- 左侧筛选结果统计 -->
          <div class="games-sidebar">
            <n-card title="筛选结果" :bordered="false">
              <div class="results-info">
                <p>找到 {{ filteredGames.length }} 个游戏</p>
                <n-button text size="small" @click="() => {
                  searchKeyword = ''
                  selectedPlatform = 'all'
                  selectedCategory = 'all'
                  priceRange = [0, 100]
                }">
                  重置筛选
                </n-button>
              </div>
            </n-card>
          </div>

          <!-- 右侧游戏列表 -->
          <div class="games-list">
            <n-spin :show="loading">
              <div v-if="filteredGames.length > 0" class="games-grid">
                <n-card 
                  v-for="game in filteredGames" 
                  :key="game.id" 
                  class="game-card"
                  :bordered="false"
                >
                  <div class="game-content">
                    <!-- 游戏图片 -->
                    <div class="game-image">
                      <img :src="game.image" :alt="game.name" class="game-cover" />
                      <div v-if="game.discount > 0" class="discount-badge">
                        <n-tag :type="getDiscountColor(game.discount)" size="small">
                          -{{ game.discount }}%
                        </n-tag>
                      </div>
                    </div>

                    <!-- 游戏信息 -->
                    <div class="game-info">
                      <h3 class="game-title">{{ game.name }}</h3>
                      
                      <div class="game-meta">
                        <div class="meta-item">
                          <n-icon :component="PeopleOutline" size="14" />
                          <span>{{ game.players.toLocaleString() }} 在线</span>
                        </div>
                        <div class="meta-item">
                          <n-tag :type="getRatingColor(game.rating)" size="small">
                            {{ game.rating }}/5
                          </n-tag>
                        </div>
                        <div v-if="game.metacritic" class="meta-item">
                          <n-tag type="info" size="small">
                            Metacritic: {{ game.metacritic }}
                          </n-tag>
                        </div>
                      </div>

                      <p class="game-description">{{ game.description }}</p>

                      <div class="game-details">
                        <div class="detail-item">
                          <strong>开发商:</strong> {{ game.developer }}
                        </div>
                        <div class="detail-item">
                          <strong>发行商:</strong> {{ game.publisher }}
                        </div>
                        <div class="detail-item">
                          <strong>发行日期:</strong> {{ game.releaseDate }}
                        </div>
                      </div>

                      <div class="game-tags">
                        <n-tag 
                          v-for="tag in game.tags.slice(0, 3)" 
                          :key="tag" 
                          size="small"
                          type="info"
                        >
                          {{ tag }}
                        </n-tag>
                      </div>

                      <!-- 价格信息 -->
                      <div class="game-price">
                        <div v-if="game.discount > 0" class="price-original">
                          ¥{{ game.price }}
                        </div>
                        <div class="price-final">
                          {{ getPriceDisplay(game) }}
                        </div>
                      </div>
                    </div>
                  </div>
                </n-card>
              </div>

              <n-empty v-else description="没有找到符合条件的游戏">
                <template #extra>
                  <n-button type="primary" @click="() => {
                    searchKeyword = ''
                    selectedPlatform = 'all'
                    selectedCategory = 'all'
                    priceRange = [0, 100]
                  }">
                    显示所有游戏
                  </n-button>
                </template>
              </n-empty>
            </n-spin>
          </div>
        </div>
      </div>
    </n-layout-content>
  </n-layout>
</template>

<style scoped>
.games-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
}

.games-container {
  max-width: 1400px;
  margin: 0 auto;
}

.steam-profile-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.steam-profile {
  padding: 8px 0;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.profile-info {
  flex: 1;
}

.username {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
}

.steam-id {
  margin: 4px 0;
  font-size: 0.9rem;
  color: #666;
}

.status {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.last-online {
  font-size: 0.85rem;
  color: #999;
}

.connect-button {
  align-self: flex-start;
}

.profile-stats {
  margin-top: 16px;
}

.search-filter-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-filter {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-section {
  width: 100%;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 0.9rem;
  color: #666;
  min-width: 60px;
}

.games-main {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 24px;
}

.games-sidebar {
  height: fit-content;
}

.games-list {
  min-height: 600px;
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.game-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.game-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.game-content {
  display: flex;
  gap: 16px;
}

.game-image {
  position: relative;
  flex-shrink: 0;
}

.game-cover {
  width: 120px;
  height: 160px;
  object-fit: cover;
  border-radius: 6px;
}

.discount-badge {
  position: absolute;
  top: 8px;
  right: 8px;
}

.game-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.game-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.3;
}

.game-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.85rem;
  color: #666;
}

.game-description {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.4;
  color: #555;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.game-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item {
  font-size: 0.85rem;
  color: #666;
}

.detail-item strong {
  color: #333;
}

.game-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 8px 0;
}

.game-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: auto;
}

.price-original {
  font-size: 0.9rem;
  color: #999;
  text-decoration: line-through;
}

.price-final {
  font-size: 1.1rem;
  font-weight: 600;
  color: #18a058;
}

.results-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .games-main {
    grid-template-columns: 1fr;
  }
  
  .games-sidebar {
    order: 2;
  }
  
  .games-list {
    order: 1;
  }
}

@media (max-width: 768px) {
  .games-content {
    padding: 16px;
  }
  
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .connect-button {
    align-self: stretch;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .games-grid {
    grid-template-columns: 1fr;
  }
  
  .game-content {
    flex-direction: column;
  }
  
  .game-image {
    align-self: center;
  }
}

@media (max-width: 480px) {
  .games-content {
    padding: 12px;
  }
  
  .game-card {
    padding: 12px;
  }
}
</style>