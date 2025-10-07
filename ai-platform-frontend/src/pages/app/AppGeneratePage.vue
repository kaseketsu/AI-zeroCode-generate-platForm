<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { getAppById } from '@/api/appController.ts'
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  CloudUploadOutlined,
  SendOutlined,
  ExportOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons-vue'

const route = useRoute()
const routers = useRouter()
const appId = ref<string>()
const appInfo = ref<API.AppVO>()

/**
 * 获取 app 信息
 */
const fetchAppInfo = async () => {
  const id = route.params.appId
  console.log(id)
  if (!id) {
    message.error('应用 id 不存在！')
    await routers.push('/')
    return
  }
  appId.value = id as string
  console.log(appId)
  try {
    const res = await getAppById({
      id: id as unknown as number,
    })
    if (res.data.code === 0 && res.data.data) {
      appInfo.value = res.data.data
    }
  } catch (e) {
    message.error('获取应用信息失败，' + e)
  }
}
// 自动挂载获取 app 函数
onMounted(() => {
  fetchAppInfo()
})

// console.log(appId)
</script>
<template>
  <div id="appGeneratePage">
    <!-- 顶部栏 -->
    <div class="header">
      <div class="title-bar">
        <div class="title">
          <div class="app-name">
          {{ appInfo?.appName ?? '应用生成器'}}
          </div>
        </div>
        <div class="buttons">
          <a-button type="default" @click="">
            <template #icon>
              <InfoCircleOutlined />
            </template>
            应用详情
          </a-button>
          <a-button type="primary" @click="" ghost>
            <template #icon>
              <CloudUploadOutlined />
            </template>
            部署
          </a-button>
        </div>
      </div>
    </div>
    <!-- 主要区域 -->
    <div class="main-content">
      <!-- 对话区域 -->
      <div class="chat-area">

      </div>
      <!-- 渲染区域 -->
      <div class="preview-area"></div>
    </div>
  </div>
</template>

<style scoped>
#appGeneratePage {
  display: flex;
  padding: 16px;
  height: 100vh;
  flex-direction: column;
  background: #fdfdfd;
}
/* 顶部栏 */
.title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  display: flex;
}

.app-name {
  margin-left: 18px;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.buttons {
  display: flex;
  gap: 16px;
}
/* 内容区 */
.main-content {
  flex: 1;
  display: flex;
  padding: 16px;
  overflow: hidden;
}
.chat-area {
  display: flex;
  flex: 2;
  flex-direction: column;
  background: white;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 渲染区 */
.preview-area {
  display: flex;
  flex: 3;
}
</style>
