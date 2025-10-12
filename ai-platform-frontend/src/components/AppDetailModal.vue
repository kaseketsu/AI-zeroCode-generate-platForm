<template>
  <div id="appDetailModal">
    <a-modal title="应用详情" v-model:open="visible" :footer="null" width="500px">
      <!-- 基本信息 -->
      <div class="basic-info">
        <div class="info-item">
          <span class="info-label">创建者: </span>
          <UserInfoModal :user="app?.user" :size="'small'" :show-name="true" />
        </div>
        <div class="info-item">
          <span class="info-label">创建日期: </span>
          <span>{{ formatTime(app?.createTime) }}</span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div v-if="showActions" class="actions">
        <a-space>
          <a-button type="primary" @click="handleEdit">
            <template #icon>
              <EditOutlined />
            </template>
            编辑
          </a-button>
          <a-popconfirm
            title="确定要删除该应用吗？"
            @confirm="handleDelete"
            ok-text="确定"
            cancel-text="取消"
          >
            <a-button danger>
              <template #icon>
                <DeleteOutlined />
              </template>
              删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import UserInfoModal from '@/components/UserInfoModal.vue'
import { formatTime } from '../utils/time.ts'
import { EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'

interface Props {
  open: boolean
  app?: API.AppVO
  showActions: boolean
}

interface Emits {
  (e: 'update:open', value: boolean): void

  (e: 'edit'): void

  (e: 'delete'): void
}

const props = withDefaults(defineProps<Props>(), {
  showActions: false,
})

const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.open,
  set: (value) => emit('update:open', value),
})

const handleEdit = () => {
  emit('edit')
}

const handleDelete = () => {
  emit('delete')
}
</script>

<style scoped>
#appDetailModal {
  padding: 16px;
}

.basic-info {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-label {
  width: 80px;
  color: #666;
  font-size: 14px;
  flex-shrink: 0;
}

.actions {
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
