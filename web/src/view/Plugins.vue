<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../store/auth'
import { 
  NLayout, 
  NLayoutContent, 
  NCard, 
  NButton, 
  NAvatar, 
  NInput, 
  NEmpty,
  NIcon,
  NModal,
  useMessage,
  NTag,
  NGrid,
  NGi,
  NStatistic,
  NDivider
} from 'naive-ui'
import { SearchOutline, HeartOutline, CloudDownloadOutline, TimeOutline, PersonOutline, InformationCircleOutline } from '@vicons/ionicons5'
import Header from '../components/Header.vue'

const authStore = useAuthStore()
const message = useMessage()

// 搜索关键词
const searchKeyword = ref('')

// 插件详情弹窗
const pluginDetailVisible = ref(false)
const selectedPlugin = ref<any>(null)
const subscribing = ref(false)

// 模拟插件数据
const plugins = ref([
  {
    id: '1',
    name: '个人数据同步',
    description: '自动同步您的阅读进度和个人设置到云端',
    version: '1.2.3',
    category: '个人数据',
    subscriptionRequired: true,
    uploadDate: '2024-10-15',
    lastUpdate: '2024-10-18',
    availability: '稳定',
    subscriptionCount: 1560,
    author: {
      name: '插件开发者',
      avatar: '/images/avatars/developer1.jpg'
    },
    isSubscribed: false,
    tags: ['数据同步', '云存储', '自动备份']
  },
  {
    id: '2',
    name: '实验性阅读模式',
    description: '提供多种实验性的阅读界面和交互方式',
    version: '0.8.1',
    category: '实验性',
    subscriptionRequired: true,
    uploadDate: '2024-10-10',
    lastUpdate: '2024-10-16',
    availability: '测试中',
    subscriptionCount: 890,
    author: {
      name: '创新实验室',
      avatar: '/images/avatars/lab.jpg'
    },
    isSubscribed: true,
    tags: ['实验功能', '阅读模式', 'UI优化']
  },
  {
    id: '3',
    name: '夜间模式增强',
    description: '提供更舒适的夜间阅读体验',
    version: '2.1.0',
    category: '界面优化',
    subscriptionRequired: false,
    uploadDate: '2024-09-28',
    lastUpdate: '2024-10-12',
    availability: '稳定',
    subscriptionCount: 2340,
    author: {
      name: 'UI设计师',
      avatar: '/images/avatars/designer.jpg'
    },
    isSubscribed: false,
    tags: ['夜间模式', '护眼', '主题']
  },
  {
    id: '4',
    name: '智能书签',
    description: '基于AI的智能书签和阅读进度管理',
    version: '1.5.2',
    category: 'AI功能',
    subscriptionRequired: true,
    uploadDate: '2024-10-05',
    lastUpdate: '2024-10-14',
    availability: '稳定',
    subscriptionCount: 1780,
    author: {
      name: 'AI团队',
      avatar: '/images/avatars/ai-team.jpg'
    },
    isSubscribed: false,
    tags: ['人工智能', '书签', '智能推荐']
  },
  {
    id: '5',
    name: '阅读统计',
    description: '详细的阅读数据统计和分析报告',
    version: '1.0.0',
    category: '数据分析',
    subscriptionRequired: false,
    uploadDate: '2024-09-20',
    lastUpdate: '2024-10-08',
    availability: '稳定',
    subscriptionCount: 3120,
    author: {
      name: '数据分析师',
      avatar: '/images/avatars/analyst.jpg'
    },
    isSubscribed: true,
    tags: ['统计', '分析', '报告']
  }
])

// 用户插件数据
const userPlugins = ref({
  uploaded: [
    {
      id: '6',
      name: '自定义主题',
      description: '允许用户自定义界面主题颜色',
      version: '1.0.0',
      category: '界面优化',
      subscriptionRequired: false,
      uploadDate: '2024-10-12',
      lastUpdate: '2024-10-12',
      availability: '稳定',
      subscriptionCount: 120,
      author: {
        name: authStore.user?.username || '我',
        avatar: authStore.user?.avatar || '/images/avatars/default.jpg'
      },
      isSubscribed: false,
      tags: ['主题', '自定义', '界面']
    }
  ],
  subscribed: plugins.value.filter(p => p.isSubscribed)
})

// 过滤后的插件列表
const filteredPlugins = computed(() => {
  if (!searchKeyword.value) {
    return plugins.value
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return plugins.value.filter(plugin => 
    plugin.name.toLowerCase().includes(keyword) ||
    plugin.description.toLowerCase().includes(keyword) ||
    plugin.category.toLowerCase().includes(keyword) ||
    plugin.tags.some(tag => tag.toLowerCase().includes(keyword))
  )
})

// 打开插件详情
function openPluginDetail(plugin: any) {
  selectedPlugin.value = plugin
  pluginDetailVisible.value = true
}

// 关闭插件详情
function closePluginDetail() {
  pluginDetailVisible.value = false
  selectedPlugin.value = null
}

// 订阅插件
async function subscribePlugin(plugin: any) {
  if (!authStore.isAuthenticated) {
    message.warning('请先登录')
    return
  }
  
  if (plugin.isSubscribed) {
    message.info('您已经订阅了该插件')
    return
  }
  
  subscribing.value = true
  
  try {
    // 模拟订阅请求
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    plugin.isSubscribed = true
    userPlugins.value.subscribed.push(plugin)
    message.success('订阅成功！')
    
    if (pluginDetailVisible.value) {
      closePluginDetail()
    }
  } catch (error) {
    message.error('订阅失败，请重试')
  } finally {
    subscribing.value = false
  }
}

// 取消订阅插件
async function unsubscribePlugin(plugin: any) {
  subscribing.value = true
  
  try {
    // 模拟取消订阅请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    plugin.isSubscribed = false
    userPlugins.value.subscribed = userPlugins.value.subscribed.filter(p => p.id !== plugin.id)
    message.success('取消订阅成功')
  } catch (error) {
    message.error('取消订阅失败，请重试')
  } finally {
    subscribing.value = false
  }
}

// 获取可用性标签颜色
function getAvailabilityColor(availability: string) {
  switch (availability) {
    case '稳定': return 'success'
    case '测试中': return 'warning'
    case '维护中': return 'error'
    default: return 'default'
  }
}

onMounted(() => {
  // 初始化数据
  if (!authStore.isAuthenticated) {
    userPlugins.value.subscribed = []
  }
})
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-content class="plugins-content">
      <div class="plugins-container">
        <!-- 搜索栏 -->
        <div class="search-section">
          <n-input
            v-model:value="searchKeyword"
            placeholder="搜索插件名称、描述、分类或标签..."
            clearable
            size="large"
          >
            <template #prefix>
              <n-icon :component="SearchOutline" />
            </template>
          </n-input>
        </div>

        <!-- 主内容区域 -->
        <div class="main-content">
          <!-- 左侧：我的插件 -->
          <div class="sidebar">
            <!-- 我上传的插件 -->
            <n-card class="plugins-card" title="我上传的插件" :bordered="false">
              <div v-if="userPlugins.uploaded.length > 0" class="plugins-list">
                <n-card 
                  v-for="plugin in userPlugins.uploaded" 
                  :key="plugin.id" 
                  class="plugin-card"
                >
                  <div class="plugin-header">
                    <n-avatar
                      round
                      :size="40"
                      :src="plugin.author.avatar"
                      :fallback-src="'/images/avatars/default.jpg'"
                    />
                    <div class="plugin-info">
                      <h4 class="plugin-name">{{ plugin.name }}</h4>
                      <p class="plugin-version">v{{ plugin.version }}</p>
                    </div>
                  </div>
                  <p class="plugin-description">{{ plugin.description }}</p>
                  <div class="plugin-footer">
                    <n-tag :type="getAvailabilityColor(plugin.availability)" size="small">
                      {{ plugin.availability }}
                    </n-tag>
                    <span class="upload-date">{{ plugin.uploadDate }}</span>
                  </div>
                </n-card>
              </div>
              <n-empty v-else description="您还没有上传过插件">
                <template #extra>
                  <n-button type="primary">
                    上传第一个插件
                  </n-button>
                </template>
              </n-empty>
            </n-card>

            <!-- 我订阅的插件 -->
            <n-card class="plugins-card" title="我订阅的插件" :bordered="false">
              <div v-if="userPlugins.subscribed.length > 0" class="plugins-list">
                <n-card 
                  v-for="plugin in userPlugins.subscribed" 
                  :key="plugin.id" 
                  class="plugin-card"
                >
                  <div class="plugin-header">
                    <n-avatar
                      round
                      :size="40"
                      :src="plugin.author.avatar"
                      :fallback-src="'/images/avatars/default.jpg'"
                    />
                    <div class="plugin-info">
                      <h4 class="plugin-name">{{ plugin.name }}</h4>
                      <p class="plugin-version">v{{ plugin.version }}</p>
                    </div>
                  </div>
                  <p class="plugin-description">{{ plugin.description }}</p>
                  <div class="plugin-footer">
                    <n-tag :type="getAvailabilityColor(plugin.availability)" size="small">
                      {{ plugin.availability }}
                    </n-tag>
                    <n-button 
                      type="error" 
                      size="small" 
                      @click="unsubscribePlugin(plugin)"
                      :loading="subscribing"
                    >
                      取消订阅
                    </n-button>
                  </div>
                </n-card>
              </div>
              <n-empty v-else description="您还没有订阅任何插件">
                <template #extra>
                  <n-button type="primary" @click="searchKeyword = ''">
                    浏览所有插件
                  </n-button>
                </template>
              </n-empty>
            </n-card>
          </div>

          <!-- 右侧：所有插件列表 -->
          <div class="content">
            <n-card class="all-plugins-card" title="所有插件" :bordered="false">
              <div v-if="filteredPlugins.length > 0" class="plugins-grid">
                <n-card 
                  v-for="plugin in filteredPlugins" 
                  :key="plugin.id" 
                  class="plugin-card detailed"
                  @click="openPluginDetail(plugin)"
                >
                  <div class="plugin-header">
                    <n-avatar
                      round
                      :size="50"
                      :src="plugin.author.avatar"
                      :fallback-src="'/images/avatars/default.jpg'"
                    />
                    <div class="plugin-info">
                      <h3 class="plugin-name">{{ plugin.name }}</h3>
                      <p class="plugin-author">
                        <n-icon :component="PersonOutline" size="14" />
                        {{ plugin.author.name }}
                      </p>
                    </div>
                    <n-button 
                      v-if="plugin.isSubscribed"
                      type="success"
                      size="small"
                      ghost
                      @click.stop="unsubscribePlugin(plugin)"
                      :loading="subscribing"
                    >
                      <template #icon>
                        <n-icon :component="HeartOutline" />
                      </template>
                      已订阅
                    </n-button>
                    <n-button 
                      v-else
                      type="primary"
                      size="small"
                      @click.stop="subscribePlugin(plugin)"
                      :loading="subscribing"
                    >
                      <template #icon>
                        <n-icon :component="CloudDownloadOutline" />
                      </template>
                      订阅
                    </n-button>
                  </div>

                  <p class="plugin-description">{{ plugin.description }}</p>

                  <div class="plugin-meta">
                    <n-grid :cols="2" :x-gap="12" :y-gap="8">
                      <n-gi>
                        <n-statistic label="版本" :value="plugin.version" size="small" />
                      </n-gi>
                      <n-gi>
                        <n-statistic label="订阅数" :value="plugin.subscriptionCount" size="small" />
                      </n-gi>
                      <n-gi>
                        <n-statistic label="分类" :value="plugin.category" size="small" />
                      </n-gi>
                      <n-gi>
                        <n-statistic label="状态" :value="plugin.availability" size="small" />
                      </n-gi>
                    </n-grid>
                  </div>

                  <div class="plugin-tags">
                    <n-tag 
                      v-for="tag in plugin.tags.slice(0, 3)" 
                      :key="tag" 
                      size="small"
                      type="info"
                    >
                      {{ tag }}
                    </n-tag>
                    <span v-if="plugin.tags.length > 3" class="more-tags">...</span>
                  </div>

                  <div class="plugin-dates">
                    <div class="date-item">
                      <n-icon :component="TimeOutline" size="14" />
                      <span>上传: {{ plugin.uploadDate }}</span>
                    </div>
                    <div class="date-item">
                      <n-icon :component="TimeOutline" size="14" />
                      <span>更新: {{ plugin.lastUpdate }}</span>
                    </div>
                  </div>

                  <div class="plugin-actions">
                    <n-button 
                      type="primary" 
                      size="small" 
                      ghost
                      @click.stop="openPluginDetail(plugin)"
                    >
                      <template #icon>
                        <n-icon :component="InformationCircleOutline" />
                      </template>
                      详细信息
                    </n-button>
                  </div>
                </n-card>
              </div>
              <n-empty v-else description="没有找到相关插件">
                <template #extra>
                  <n-button type="primary" @click="searchKeyword = ''">
                    显示所有插件
                  </n-button>
                </template>
              </n-empty>
            </n-card>
          </div>
        </div>
      </div>

      <!-- 插件详情弹窗 -->
      <n-modal
        v-model:show="pluginDetailVisible"
        :mask-closable="true"
        preset="card"
        :style="{ width: '600px', maxWidth: '90vw' }"
        title="插件详细信息"
        :bordered="false"
        size="huge"
      >
        <div v-if="selectedPlugin" class="plugin-detail">
          <div class="detail-header">
            <n-avatar
              round
              :size="60"
              :src="selectedPlugin.author.avatar"
              :fallback-src="'/images/avatars/default.jpg'"
            />
            <div class="detail-info">
              <h2>{{ selectedPlugin.name }}</h2>
              <p class="author">
                <n-icon :component="PersonOutline" size="16" />
                {{ selectedPlugin.author.name }}
              </p>
            </div>
          </div>

          <n-divider />

          <div class="detail-content">
            <p class="description">{{ selectedPlugin.description }}</p>
            
            <n-grid :cols="2" :x-gap="16" :y-gap="12">
              <n-gi>
                <n-statistic label="版本号" :value="selectedPlugin.version" />
              </n-gi>
              <n-gi>
                <n-statistic label="分类" :value="selectedPlugin.category" />
              </n-gi>
              <n-gi>
                <n-statistic label="订阅要求" :value="selectedPlugin.subscriptionRequired ? '需要订阅' : '免费'" />
              </n-gi>
              <n-gi>
                <n-statistic label="可用性" :value="selectedPlugin.availability" />
              </n-gi>
              <n-gi>
                <n-statistic label="订阅数量" :value="selectedPlugin.subscriptionCount" />
              </n-gi>
              <n-gi>
                <n-statistic label="上传日期" :value="selectedPlugin.uploadDate" />
              </n-gi>
              <n-gi>
                <n-statistic label="最后更新" :value="selectedPlugin.lastUpdate" />
              </n-gi>
            </n-grid>

            <div class="detail-tags">
              <h4>标签</h4>
              <div class="tags">
                <n-tag 
                  v-for="tag in selectedPlugin.tags" 
                  :key="tag" 
                  size="medium"
                  type="info"
                >
                  {{ tag }}
                </n-tag>
              </div>
            </div>
          </div>

          <n-divider />

          <div class="detail-actions">
            <n-button 
              v-if="selectedPlugin.isSubscribed"
              type="error"
              size="large"
              @click="unsubscribePlugin(selectedPlugin)"
              :loading="subscribing"
            >
              <template #icon>
                <n-icon :component="HeartOutline" />
              </template>
              取消订阅
            </n-button>
            <n-button 
              v-else
              type="primary"
              size="large"
              @click="subscribePlugin(selectedPlugin)"
              :loading="subscribing"
            >
              <template #icon>
                <n-icon :component="CloudDownloadOutline" />
              </template>
              {{ selectedPlugin.subscriptionRequired ? '订阅插件' : '免费获取' }}
            </n-button>
          </div>
        </div>
      </n-modal>
    </n-layout-content>
  </n-layout>
</template>

<style scoped>
.plugins-content {
  padding: 20px;
  min-height: calc(100vh - 64px);
  background-color: #f8f9fa;
}

.plugins-container {
  max-width: 1400px;
  margin: 0 auto;
}

.search-section {
  margin-bottom: 24px;
}

.main-content {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content {
  min-height: 600px;
}

.plugins-card,
.all-plugins-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.plugins-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plugins-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.plugin-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 8px;
  cursor: pointer;
}

.plugin-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.plugin-card.detailed {
  padding: 16px;
}

.plugin-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.plugin-info {
  flex: 1;
}

.plugin-name {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
}

.plugin-version {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.plugin-author {
  margin: 4px 0 0 0;
  font-size: 0.9rem;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.plugin-description {
  margin: 0 0 12px 0;
  font-size: 0.95rem;
  line-height: 1.4;
  color: #555;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.plugin-meta {
  margin: 12px 0;
}

.plugin-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-date {
  font-size: 0.8rem;
  color: #999;
}

.plugin-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 12px 0;
}

.more-tags {
  color: #999;
  font-size: 0.9rem;
}

.plugin-dates {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin: 12px 0;
}

.date-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: #666;
}

.plugin-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

/* 插件详情弹窗样式 */
.plugin-detail {
  padding: 8px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.detail-info h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
}

.author {
  margin: 4px 0 0 0;
  font-size: 1rem;
  color: #666;
  display: flex;
  align-items: center;
  gap: 6px;
}

.description {
  font-size: 1rem;
  line-height: 1.6;
  color: #555;
  margin-bottom: 20px;
}

.detail-tags {
  margin-top: 20px;
}

.detail-tags h4 {
  margin: 0 0 12px 0;
  font-size: 1.1rem;
  font-weight: 600;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    order: 2;
  }
  
  .content {
    order: 1;
  }
}

@media (max-width: 768px) {
  .plugins-content {
    padding: 16px;
  }
  
  .plugin-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .plugin-footer {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .detail-header {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .plugins-content {
    padding: 12px;
  }
  
  .plugin-card.detailed {
    padding: 12px;
  }
}
</style>