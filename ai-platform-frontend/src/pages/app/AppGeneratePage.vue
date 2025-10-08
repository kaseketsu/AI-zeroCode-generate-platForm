<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { getAppById } from '@/api/appController.ts'
import { nextTick, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  CloudUploadOutlined,
  SendOutlined,
  ExportOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/user.ts'
import logo5 from '@/assets/logo5.png'
import MarkdownRender from '@/components/MarkdownRender.vue'
import { API_BASE_URL } from '@/config/env.ts'
import request from '@/request'

/**
 * 自定义消息类型
 */
interface Message {
  type: 'ai' | 'user'
  content: string
  loading: boolean
}

const route = useRoute()
const routers = useRouter()
const appId = ref<string>()
const appInfo = ref<API.AppVO>()
const messages = ref<Message[]>([])
const messagesContainer = ref<HTMLElement>()
const loginUserStore = useLoginUserStore()
const isGenerating = ref<boolean>(false)
const hasInitialConversation = ref<boolean>(false)

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

      const viewModel = route.query.view === '1'

      // 如果是第一次对话，发送初始对话消息
      if (!viewModel && appInfo.value.initPrompt && !hasInitialConversation.value) {
        hasInitialConversation.value = true
        await sendInitialMessage(appInfo.value.initPrompt)
      }

    } else {
      message.error('获取应用消息失败')
      await routers.push('/')
    }
  } catch (e) {
    message.error('获取应用信息失败，' + e)
  }
}

/**
 * 根据 prompt 生成初始信息
 * @param prompt
 */
const sendInitialMessage = async (prompt: string) => {
  // 添加用户信息
  messages.value.push({
    type: 'user',
    content: prompt,
    loading: false,
  })

  // 添加 AI 消息占位符
  const aiMessageIndex = messages.value.length
  messages.value.push({
    type: 'ai',
    content: '',
    loading: true,
  })

  // 保证 dom 最新
  await nextTick()
  scrollToBottom()

  // 生成代码
  isGenerating.value = true
  generateMessage(prompt, aiMessageIndex)
}

/**
 * sse 生成消息内容
 * @param prompt 用户提示词
 * @param aiMessageIndex ai 消息索引
 */
const generateMessage = async (prompt: string, aiMessageIndex: number) => {
  let eventSource: EventSource | null = null
  let streamCompleted: boolean = false
  try {
    // 生成请求参数
    const params = new URLSearchParams({
      appId: appId.value || '',
      message: prompt
    })
    const baseUrl = request.defaults.baseURL || API_BASE_URL
    // 生成 url
    const requestUrl = `${baseUrl}/user/chat/gen/?${params}`
    // 对 sse 接口发起请求
    eventSource = new EventSource(requestUrl, {
      withCredentials: true,
    })
    // 完整对话内容
    let fullContent = ''
    // 流式接收
    eventSource.onmessage = (event: MessageEvent) => {
      if (streamCompleted) return
      try {
        // 解析为哈希表
        const parsed = JSON.parse(event.data)
        const data = parsed.d
        // 拼接数据
        if (data !== undefined && data !== null) {
          fullContent += data
          messages.value[aiMessageIndex].content = fullContent
          messages.value[aiMessageIndex].loading = false
          scrollToBottom()
        }
      } catch (error) {
        handleError(error, aiMessageIndex)
      }
    }

    // 注册监听器，监听 done 事件
    eventSource.addEventListener('done', () => {
      if (streamCompleted) return
      streamCompleted = true
      isGenerating.value = false
      eventSource?.close()

      // 延迟更新，确保后端落库
      setTimeout(async () => {
       await fetchAppInfo()
      }, 1000)
    })

    // 处理错误
    eventSource.onerror = () => {
      if (streamCompleted || !isGenerating.value) return
      if (eventSource?.readyState === EventSource.CONNECTING) {
        isGenerating.value = false
        streamCompleted = true
        eventSource?.close()

        setTimeout(async () => {
          await fetchAppInfo()
        }, 1000)
      } else {
        handleError(new Error('SSE 连接错误'), aiMessageIndex)
      }
    }
  } catch (error) {
    handleError(error, aiMessageIndex)
  }
}

const handleError = (err: any, aiMessageIndex: number) => {
  message.error('消息生成失败, ' + err.message)
  messages.value[aiMessageIndex].content = '抱歉，消息生成过程中出现了错误'
  messages.value[aiMessageIndex].loading = false
  isGenerating.value = false
}

/**
 * 滚动到底部
 */
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
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
            {{ appInfo?.appName ?? '应用生成器' }}
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
      <div class="chat-area">
        <!-- 对话区域 -->
        <div class="message-container" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" class="message-item">
            <div v-if="message.type === 'user'" class="user-message">
              <div class="message-avatar">
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              </div>
              <div class="message-content">
                {{ message.content }}
              </div>
            </div>
            <div v-else class="ai-message">
              <div class="message-avatar">
                <a-avatar :src="logo5" />
              </div>
              <div class="message-content">
                <MarkdownRender v-if="message.content" :content="message.content" />
                <div v-else class="loading-indicator">
                  <a-spin size="small" />
                  <span>AI 思考ing...</span>
                </div>
              </div>
            </div>
          </div>
        </div>
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

.message-container {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  scroll-behavior: smooth;
}

.user-message {
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  gap: 8px;
}

.message-content {
  width: 70%;
  padding: 12px 16px;
  overflow-wrap: break-word;
  line-height: 1.5;
  border-radius: 12px;
}

.message-avatar {
  flex-shrink: 0;
}

.loading-indicator {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 渲染区 */
.preview-area {
  display: flex;
  flex: 3;
}
</style>
