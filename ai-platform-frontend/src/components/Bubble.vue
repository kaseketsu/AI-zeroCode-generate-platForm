<script setup lang="ts">
import { UserOutlined } from '@ant-design/icons-vue'
import { type BubbleListProps, type BubbleProps, Sender } from 'ant-design-x-vue'
import { BubbleList } from 'ant-design-x-vue'
import type { SwitchProps } from 'ant-design-vue'
import { Flex } from 'ant-design-vue'
import { h, ref } from 'vue'

defineOptions({ name: 'Bubble' })

const rolesAsObject: BubbleListProps['roles'] = {
  ai: {
    placement: 'start',
    avatar: { icon: h(UserOutlined), style: { background: '#fde3cf' } },
    typing: { step: 5, interval: 20 },
    style: {
      maxWidth: '600px',
    },
  },
  user: {
    placement: 'end',
    avatar: { icon: h(UserOutlined), style: { background: '#87d068' } },
  },
}

const rolesAsFunction = (bubbleData: BubbleProps, index: number) => {
  const RenderIndex: BubbleProps['messageRender'] = (content) =>
    h(Flex, null, () => [h('text', null, `#${index}: ${content}`)])
  switch (bubbleData.role) {
    case 'ai':
      return {
        placement: 'start' as const,
        avatar: { icon: h(UserOutlined), style: { background: '#fde3cf' } },
        typing: { step: 5, interval: 20 },
        style: {
          maxWidth: 600,
        },
        messageRender: RenderIndex,
      }
    case 'user':
      return {
        placement: 'end' as const,
        avatar: { icon: h(UserOutlined), style: { background: '#87d068' } },
        messageRender: RenderIndex,
      }
    default:
      return { messageRender: RenderIndex }
  }
}

const count = ref<number>(3)
const useRolesAsFunction = ref(false)

// const listRef = useTemplateRef<InstanceType<typeof BubbleList>>(null);
const listRef = ref<InstanceType<typeof BubbleList>>(null)

const handleChange = (checked: SwitchProps['checked']) => {
  useRolesAsFunction.value = checked as boolean
}
</script>

<template>
  <Flex vertical gap="small">

    <BubbleList
      class="bubbleList"
      ref="listRef"
      :style="{ maxHeight: '300px', paddingInline: '16px' }"
      :roles="useRolesAsFunction ? rolesAsFunction : rolesAsObject"
      :items="
        Array.from({ length: count }).map((_, i) => {
          const isAI = !!(i % 2)
          const content = isAI ? 'Mock AI content. '.repeat(20) : 'Mock user content.'

          return {
            key: i,
            role: isAI ? 'ai' : 'user',
            content,
          }
        })
      "
    />
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
.bubbleList {
  flex: 1;
}

</style>
