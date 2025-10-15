<template>
  <div id="appEditPage">
    <div class="page-header">
      <h1>编辑应用信息</h1>
    </div>
    <div class="page-container">
      <a-card :loading="loading" title="应用基础信息">
        <a-form
          layout="vertical"
          :model="formData"
          :rules="rules"
          @finish="handleSubmit"
          ref="formRef"
        >
          <a-form-item label="应用名称" name="appName">
            <a-input
              v-model:value="formData.appName"
              placeholder="请输入应用名称"
              maxlength="50"
              show-count
            />
          </a-form-item>
          <a-form-item
            v-if="isAdmin"
            label="应用封面"
            name="cover"
            extra="支持图片链接，建议尺寸：400x300"
          >
            <a-input v-model:value="formData.cover" placeholder="请输入应用封面" />
            <div v-if="formData.cover" class="preview-cover">
              <a-image
                class="preview-img"
                :src="formData.cover"
                :width="200"
                :height="150"
                fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
              />
            </div>
          </a-form-item>
          <a-form-item name="initPrompt" label="初始提示词" extra="初始提示词不可修改">
            <a-textarea
              v-model:value="formData.initPrompt"
              :rows="4"
              maxlength="1000"
              show-count
              disabled
            />
          </a-form-item>
          <a-form-item name="codeGenType" label="生成类型" extra="生成类型不可修改">
            <a-input v-model:value="formData.codeGenType" disabled placeholder="生成类型" />
          </a-form-item>
          <a-form-item label="部署密钥" name="deployKey" extra="部署密钥不可修改">
            <a-input v-model:value="formData.deployKey" disabled placeholder="部署密钥" />
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button type="primary" html-type="submit" :loading="submitting">
                保存修改
              </a-button>
              <a-button type="primary" ghost @click="resetForm"> 重置</a-button>
              <a-button type="link" @click="goToChat"> 前往应用</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>

      <!-- 应用信息 -->
      <a-card title="应用信息" style="margin-top: 16px">
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="应用 id">
            {{ appInfo?.id }}
          </a-descriptions-item>
          <a-descriptions-item label="创建者">
            <UserInfoModal :user="appInfo?.user" :size="'small'"/>
          </a-descriptions-item>
          <a-descriptions-item label="创建时间">
            {{ formatTime(appInfo?.createTime) }}
          </a-descriptions-item>
          <a-descriptions-item label="更新时间">
            {{ formatTime(appInfo?.updateTime) }}
          </a-descriptions-item>
          <a-descriptions-item label="部署时间">
            {{ appInfo?.deployedTime ? formatTime(appInfo?.deployedTime) : '未部署' }}
          </a-descriptions-item>
          <a-descriptions-item label="访问部署">
            <a-button v-if="appInfo?.deployKey" type="link" @click="openDeploy" size="small">
              查看部署
            </a-button>
            <span v-else>未部署</span>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { type FormInstance, message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { getAppById, updateApp, updateAppAdmin } from '@/api/appController.ts'
import { useLoginUserStore } from '@/stores/user.ts'
import { getDeployUrl } from '@/config/env.ts'
import UserInfoModal from '@/components/UserInfoModal.vue'
import { formatTime } from '../../utils/time.ts'

const loading = ref<boolean>(false)
const formRef = ref<FormInstance>()
const router = useRouter()
const route = useRoute()
const appInfo = ref<API.AppVO>()
const userStore = useLoginUserStore()
const submitting = ref<boolean>(false)

/**
 * 判断当前用户是否是管理员
 */
const isAdmin = computed(() => {
  return userStore.loginUser.userRole === 1
})

// 表单数据
const formData = reactive<API.AppVO>({
  appName: '',
  cover: '',
  initPrompt: '',
  codeGenType: '',
  deployKey: '',
  priority: 0,
})
// 表单字段规则
const rules = {
  appName: [
    { required: true, message: '请输入引用名称', trigger: 'blur' },
    { min: 1, max: 50, message: '应用名称长度应在 1-50 字符内', trigger: 'blur' },
  ],
  cover: [{ type: 'url', message: '请输入有效的 url', trigger: 'blur' }],
  priority: [{ min: 0, max: 99, message: '优先级范围在 0-99 内', trigger: 'blur' }],
}

/**
 * 前往对话页面
 */
const goToChat = async () => {
  if (!appInfo.value?.id) {
    message.error('应用不存在')
    await router.push('/')
    return
  }
  await router.push(`/app/generate/${appInfo.value?.id}`)
}

/**
 * 获取 app 信息
 */
const fetchAppInfo = async () => {
  const id = route.params.appId
  if (!id) {
    message.error('应用信息不存在')
    await router.push('/')
  }
  try {
    loading.value = true
    const res = await getAppById({ id: id as unknown as number })
    if (res.data.code === 0 && res.data.data) {
      appInfo.value = res.data.data

      if (!isAdmin && !(userStore.loginUser.id === appInfo.value?.id)) {
        message.error('您没有权限编辑该应用')
        await router.push('/')
      }

      // 更新表单数据
      formData.appName = res.data.data.appName
      formData.cover = res.data.data.cover
      formData.initPrompt = res.data.data.initPrompt
      formData.codeGenType = res.data.data.codeGenType
      formData.deployKey = res.data.data.deployKey
      formData.priority = res.data.data.priority
    } else {
      message.error('获取应用消息失败，' + res.data.message)
      await router.push('/')
    }
  } catch (e) {
    message.error('获取应用消息失败，' + e)
    await router.push('/')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!appInfo.value?.id) return
  try {
    submitting.value = true
    let res
    if (isAdmin.value) {
      res = await updateAppAdmin({
        id: appInfo.value?.id,
        appName: formData?.appName,
        cover: formData?.cover,
        priority: formData?.priority,
      })
    } else {
      res = await updateApp({
        id: appInfo.value?.id,
        appName: formData?.appName,
      })
    }
    if (res.data.code === 0) {
      message.success('应用信息更新成功')
      await fetchAppInfo()
    } else {
      message.error('应用信息更新失败, ' + res.data.message)
    }
  } catch (e) {
    message.error('应用信息更新失败, ', e)
  } finally {
    submitting.value = false
  }
}

/**
 * 打开部署页面
 */
const openDeploy = () => {
  if (appInfo.value?.deployKey) {
    const previewUrl = getDeployUrl(appInfo.value?.deployKey)
    window.open(previewUrl, '_blank')
  }
}

/**
 * 将用户已填写项清空
 */
const resetForm = () => {
  if (appInfo.value) {
    formData.appName = appInfo.value?.appName
    formData.priority = appInfo.value?.priority
    formData.cover = appInfo.value?.cover
  }
  formRef.value?.clearValidate()
}

onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
#appEditPage {
  max-width: 1000px;
  padding: 20px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.preview-cover {
  padding: 12px;
  border-radius: 6px;
}
</style>
