<template>
  <div id="appDeployDetail">
    <a-modal title="应用部署成功" :open="visible" :footer="null" width="600px">
      <div class="deploy-success">
        <div class="success-icon">
          <CheckCircleOutlined style="color: #52c41a; font-size: 48px" />
        </div>
        <h3>您的网站已部署成功</h3>
        <p>您可以通过以下链接进行访问:</p>
        <a-input :value="deployUrl" readonly>
          <template #suffix>
            <a-button type="primary" @click="handelCopyUrl">
              <CopyOutlined />
            </a-button>
          </template>
        </a-input>
      </div>
      <div class="actions">
        <a-button type="primary" @click="openSite"> 访问网站 </a-button>
        <a-button type="primary" @click="close" ghost> 关闭 </a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { CheckCircleOutlined, CopyOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

interface Props {
  open: boolean
  deployUrl: string
}

interface Emits {
  (e: 'update:open', value: boolean): void

  (e: 'open-site'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.open,
  set: (value) => emit('update:open', value),
})

const close = () => {
  emit('update:open', false)
}

const openSite = () => {
  emit('open-site')
}

const handelCopyUrl = async () => {
  try {
    await navigator.clipboard.writeText(props.deployUrl)
    message.success('url 复制成功！')
  } catch (e) {
    message.error('url复制失败, ' + e)
  }
}
</script>

<style scoped>

.deploy-success {
  text-align: center;
  padding: 16px;
}
.deploy-success h3 {
  margin-top: 12px;
  margin-bottom: 12px;
}
.actions {
  margin-top: 6px;
  display: flex;
  gap: 12px;
  justify-content: center;
}


</style>
