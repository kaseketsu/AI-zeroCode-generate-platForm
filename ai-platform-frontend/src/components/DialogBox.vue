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
</template>
<style scoped>
.sender {
  width: 450px;
  min-height: 100px;
  margin: 0 auto;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.sender:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
}
</style>
