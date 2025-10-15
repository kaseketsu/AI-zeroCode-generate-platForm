<script setup lang="ts">
import { Flex, message } from 'ant-design-vue'
import { Sender } from 'ant-design-x-vue'
import { ref } from 'vue'
import { useLoginUserStore } from '@/stores/user.ts'
import { useRouter } from 'vue-router'
import { addApp } from '@/api/appController.ts'

defineOptions({ name: 'DialogBox' })

const value = ref<string>()
const loginUserStore = useLoginUserStore()
const router = useRouter()
/**
 * 点击提交按钮后的回调函数
 * @param values
 */
const doSubmit = async (values: string) => {
  if (!loginUserStore.loginUser || !loginUserStore.loginUser?.id) {
    message.error('请先登录')
    await router.push(`/user/login`)
    return
  }
  // 根据提示词创建应用
  try {
    const res = await addApp({
      initialPrompt: values,
    })
    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功！')
      const appId = String(res.data.data)
      // 跳转到应用生成页面
      await router.push(`/app/generate/${appId}`)
    } else {
      message.error('创建失败' + res.data.message)
    }
  } catch (error) {
    message.error('应用创建失败')
  }
}

/**
 * 快速生成 prompt
 * @param prompt
 */
const setPrompt = (prompt: string) => {
  value.value = prompt
}
</script>
<template>
  <Flex vertical gap="middle">
    <Sender
      class="sender"
      v-model:value="value"
      :auto-size="{ minRows: 2, maxRows: 6 }"
      @submit="doSubmit"
      placeholder="输入点什么吧~"
    />
  </Flex>
  <div class="input-actions">
    <a-button
      type="default"
      @click="
        setPrompt(
          '创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。',
        )
      "
    >
      个人博客网站
    </a-button>

    <a-button
      type="default"
      @click="
        setPrompt(
          '创建一个高端的企业官网，展示公司简介、核心业务、产品服务、团队介绍、新闻动态和联系方式等内容。采用专业、可信的设计风格，支持中英文切换和响应式布局，首页突出企业品牌形象和主打产品。',
        )
      "
    >
      企业官网
    </a-button>

    <a-button
      type="default"
      @click="
        setPrompt(
          '创建一个沉浸式的游戏官网，包含游戏介绍、下载入口、新闻公告、活动中心、角色资料、社区论坛和玩家支持页面。采用动态视觉效果与暗色主题风格，支持响应式布局和视频背景展示，突出游戏特色与玩家互动。',
        )
      "
    >
      游戏官网
    </a-button>

    <a-button
      type="default"
      @click="
        setPrompt(
          '创建一个精美的作品展示网站，用于设计师或摄影师展示个人作品集。包含作品分类、图片/视频展示、项目详情、个人简介、联系方式和社交媒体链接。采用极简设计、瀑布流布局，支持响应式与灯箱预览效果。',
        )
      "
    >
      作品展示网站
    </a-button>
  </div>
</template>
<style scoped>
.sender {
  width: 600px;
  min-height: 100px;
  margin: 0 auto;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.sender:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
}

.input-actions {
  display: flex;
  flex-direction: row;
  margin-top: 12px;
  gap: 10px;
  align-items: center;
  justify-content: center;
}

.input-actions .ant-btn {
  border-radius: 25px;
  padding: 8px 20px;
  height: auto;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(59, 130, 246, 0.2);
  color: #475569;
  backdrop-filter: blur(15px);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.input-actions .ant-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.1), transparent);
  transition: left 0.5s;
}

.input-actions .ant-btn:hover::before {
  left: 100%;
}

.input-actions .ant-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(59, 130, 246, 0.4);
  color: #3b82f6;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.2);
}
</style>
