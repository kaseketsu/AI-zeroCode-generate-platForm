<template>
  <div id="appCard">
    <div class="app-content">
      <div class="preview-area">
        <a-image v-if="app.cover" :src="app.cover" class="cover-image" />
        <div v-else class="app-placeholder">
          <span> 🤖 </span>
        </div>
      </div>
      <div class="app-overlay">
        <a-space>
          <a-button type="primary" @click="">
            查看对话
          </a-button>
          <a-button type="default" @click="">
            查看作品
          </a-button>
        </a-space>
      </div>
      <div class="app-info">
        <div class="app-info-left">
          <a-avatar :src="app.user?.userAvatar" size="default">
            {{ app.user?.userName?.charAt  (0) || 'U' }}
          </a-avatar>
        </div>
        <div class="app-info-right">
          <h3> {{ app.appName || '未命名应用' }} </h3>
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

const props = defineProps<Props>()
</script>

<style scoped></style>
