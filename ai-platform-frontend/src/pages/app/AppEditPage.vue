<template>
  <div id="appEditPage">
    <div class="page-header">
      <h1>编辑应用信息</h1>
    </div>
    <div class="page-container">
      <div class="base-content">
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
            </a-form-item>
            <div v-if="isAdmin" class="preview-cover">
              <a-image
                class="preview-img"
                :src="formData.cover"
                width="200"
                height="150"
                fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
              />
            </div>
            <a-form-item name="initPrompt" label="初始提示词">
              <a-textarea
                v-model:value="formData.initPrompt"
                :rows="4"
                maxlength="1000"
                show-count
                disabled
              />
            </a-form-item>
            <a-form-item name="codeGenType" label="生成类型">
              <a-input v-model:value="formData.codeGenType" disabled placeholder="生成类型" />
            </a-form-item>
          </a-form>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { type FormInstance, message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { getAppById, updateApp, updateAppAdmin } from '@/api/appController.ts'
import { useLoginUserStore } from '@/stores/user.ts'
import { codeGenTypeOptions } from '@/utils/codeGenType.ts'

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
        id: formData.id,
        appName: formData?.appName,
        cover: formData?.cover,
        priority: formData?.priority,
      })
    } else {
      res = await updateApp({
        id: formData?.id,
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

<style scoped></style>
