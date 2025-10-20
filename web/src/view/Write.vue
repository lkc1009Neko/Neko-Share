<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { NLayout, NLayoutHeader, NLayoutContent, NCard, NInput, NButton, NSelect, NSpace, useMessage } from 'naive-ui'
import { QuillEditor } from '@vueup/vue-quill'
import Header from '../components/Header.vue'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const router = useRouter()
const authStore = useAuthStore()
const message = useMessage()

const title = ref('')
const content = ref('')
const tags = ref<string[]>([])

const loading = ref(false)

const tagOptions = [
  { label: 'Vue', value: 'Vue' },
  { label: 'JavaScript', value: 'JavaScript' },
  { label: 'TypeScript', value: 'TypeScript' },
  { label: '前端', value: '前端' },
  { label: '后端', value: '后端' },
  { label: '数据库', value: '数据库' },
  { label: '算法', value: '算法' },
  { label: '设计模式', value: '设计模式' }
]

async function handlePublish() {
  if (!title.value.trim()) {
    message.error('请输入文章标题')
    return
  }

  if (!content.value.trim()) {
    message.error('请输入文章内容')
    return
  }

  if (!authStore.isAuthenticated) {
    message.error('请先登录')
    router.push('/login')
    return
  }

  loading.value = true

  try {
    // 模拟发布成功
    message.success('文章发布成功')
    router.push('/')
  } catch (error) {
    message.error('发布失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function handleSaveDraft() {
  if (!title.value.trim() && !content.value.trim()) {
    message.error('请输入文章内容')
    return
  }

  // 模拟保存草稿
  message.success('草稿已保存')
}
</script>

<template>
  <n-layout>
    <Header />
    
    <n-layout-header style="padding: 16px 20px; background: white; border-bottom: 1px solid #e8e8e8;">
      <n-space justify="space-between" align="center">
        <h2 style="margin: 0; color: #18a058;">写文章</h2>
        <n-space>
          <n-button @click="handleSaveDraft">保存草稿</n-button>
          <n-button type="primary" :loading="loading" @click="handlePublish">发布文章</n-button>
        </n-space>
      </n-space>
    </n-layout-header>

    <n-layout-content style="padding: 20px;" class="container">
      <n-card>
        <n-input
          v-model:value="title"
          placeholder="请输入文章标题..."
          style="font-size: 24px; font-weight: bold; margin-bottom: 20px;"
          size="large"
        />

        <n-select
          v-model:value="tags"
          multiple
          filterable
          tag
          placeholder="选择或输入标签"
          :options="tagOptions"
          style="margin-bottom: 20px;"
        />

        <QuillEditor
          v-model:content="content"
          contentType="html"
          theme="snow"
          style="height: 400px; margin-bottom: 20px;"
          placeholder="开始写作..."
        />

        <n-space justify="end">
          <n-button @click="router.back()">取消</n-button>
          <n-button @click="handleSaveDraft">保存草稿</n-button>
          <n-button type="primary" :loading="loading" @click="handlePublish">发布文章</n-button>
        </n-space>
      </n-card>
    </n-layout-content>
  </n-layout>
</template>