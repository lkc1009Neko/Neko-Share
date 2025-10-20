<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { loadOml2d } from 'oh-my-live2d'

const isVisible = ref(true)
const live2dRef = ref<HTMLElement>()

// Live2D配置 - 简化版本
const live2dConfig = {
  models: [
    {
      path: 'https://cdn.jsdelivr.net/gh/guansss/pixi-live2d-display/test/assets/shizuku/shizuku.model.json',
      scale: 0.12
    }
  ],
  display: {
    width: 300,
    height: 400,
    position: 'left',
    hOffset: 20,
    vOffset: -20
  }
}

let live2dInstance: any = null

// 初始化Live2D
async function initLive2D() {
  if (!live2dRef.value) return
  
  try {
    // 创建容器
    const container = live2dRef.value
    container.style.position = 'fixed'
    container.style.left = '20px'
    container.style.bottom = '20px'
    container.style.zIndex = '1000'
    container.style.pointerEvents = 'auto'
    container.style.width = '300px'
    container.style.height = '400px'
    
    // 加载Live2D
    live2dInstance = await loadOml2d(live2dConfig)
    
    // 将Live2D实例添加到容器
    if (live2dInstance && live2dInstance.canvas) {
      container.appendChild(live2dInstance.canvas)
    }
    
    // 添加点击事件
    container.addEventListener('click', handleClick)
    
  } catch (error) {
    console.error('Live2D初始化失败:', error)
  }
}

// 处理点击事件
function handleClick() {
  // 简单的点击反馈
  const container = live2dRef.value
  if (container) {
    container.style.transform = 'scale(0.95)'
    setTimeout(() => {
      if (container) {
        container.style.transform = 'scale(1)'
      }
    }, 100)
  }
  
  // 显示提示
  const tips = [
    '哎呀，别碰我！',
    '人家在认真工作呢~',
    '嘻嘻，被你发现了'
  ]
  const randomTip = tips[Math.floor(Math.random() * tips.length)]
  console.log(randomTip)
}

// 切换显示状态
function toggleVisibility() {
  isVisible.value = !isVisible.value
  const container = live2dRef.value
  if (container) {
    container.style.display = isVisible.value ? 'block' : 'none'
  }
}

// 销毁Live2D
function destroyLive2D() {
  if (live2dInstance) {
    // oh-my-live2d可能没有destroy方法，直接移除元素
    live2dInstance = null
  }
  
  const container = live2dRef.value
  if (container) {
    container.removeEventListener('click', handleClick)
    container.innerHTML = ''
  }
}

onMounted(() => {
  if (isVisible.value) {
    initLive2D()
  }
})

onUnmounted(() => {
  destroyLive2D()
})
</script>

<template>
  <div class="live2d-widget">
    <!-- Live2D容器 -->
    <div ref="live2dRef" class="live2d-container"></div>
    
    <!-- 控制按钮 -->
    <div class="live2d-controls">
      <button 
        @click="toggleVisibility" 
        class="control-btn"
        :title="isVisible ? '隐藏看板娘' : '显示看板娘'"
      >
        {{ isVisible ? '👋' : '👀' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.live2d-widget {
  position: fixed;
  left: 20px;
  bottom: 20px;
  z-index: 1000;
  pointer-events: none;
}

.live2d-container {
  width: 300px;
  height: 400px;
  pointer-events: auto;
  transition: transform 0.2s ease;
}

.live2d-container:hover {
  transform: scale(1.05);
}

.live2d-controls {
  position: absolute;
  top: -40px;
  right: 0;
  pointer-events: auto;
}

.control-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .live2d-widget {
    left: 10px;
    bottom: 10px;
  }
  
  .live2d-container {
    width: 200px;
    height: 300px;
  }
}

@media (max-width: 480px) {
  .live2d-widget {
    left: 5px;
    bottom: 5px;
  }
  
  .live2d-container {
    width: 150px;
    height: 250px;
  }
  
  .control-btn {
    width: 28px;
    height: 28px;
    font-size: 14px;
  }
}
</style>