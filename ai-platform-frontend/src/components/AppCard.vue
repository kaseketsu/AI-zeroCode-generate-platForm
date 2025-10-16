<template>
  <div id="appCard">
    <div class="app-content">
      <div class="preview-area">
        <a-image v-if="app.cover" :src="app.cover" class="cover-image" />
        <div v-else class="app-placeholder">
          <span> 🤖 </span>
        </div>
        <div class="app-overlay">
          <a-space>
            <a-button type="primary" @click="handleViewChat">查看对话</a-button>
            <a-button type="default" @click="handleViewWork">查看作品</a-button>
          </a-space>
        </div>
      </div>
      <div class="app-info">
        <div class="app-info-left">
          <a-avatar :src="app.user?.userAvatar" size="default">
            {{ app.user?.userName?.charAt(0) || 'U' }}
          </a-avatar>
        </div>
        <div class="app-info-right">
          <h3>{{ app.appName || '未命名应用' }}</h3>
          <p>
            {{ app.user?.userName || (featured ? '官方' : '未知用户') }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  app: API.AppVO
  // 判断是否官方
  featured?: boolean
}

interface Emits {
  (e: 'view-chat', appId: string | number | undefined): void

  (e: 'view-work', deployKey: string | number | undefined): void
}

const emits = defineEmits<Emits>()

const props = withDefaults(defineProps<Props>(), {
  featured: false,
})

const handleViewChat = () => {
  emits('view-chat', props.app.id)
}

const handleViewWork = () => {
  emits('view-work', props.app.deployKey)
}
</script>

<style scoped>
.app-content {
  cursor: pointer;
  border-radius: 18px;
  backdrop-filter: blur(10px);
  transition: all 0.3s;
  overflow: hidden;
  border: 1px rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.app-content:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.25);
}

.preview-area {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  height: 160px;
  background: #f5f5f5;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.app-placeholder {
  font-size: 48px;
}

.app-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  transition: opacity 0.3s;
  opacity: 0;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
}

.app-content:hover .app-overlay {
  opacity: 1;
}

.app-info {
  display: flex;
  padding: 10px;
  align-items: center;
  gap: 12px;
}
</style>
