import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: {
      external: ['fs', 'path'],
      onwarn(warning, warn) {
        // 忽略oh-my-live2d相关的CommonJS警告
        if (warning.code === 'COMMONJS_VARIABLE_IN_ESM' && 
            warning.id && warning.id.includes('oh-my-live2d')) {
          return
        }
        warn(warning)
      }
    },
    chunkSizeWarningLimit: 1000
  }
})
