<template>
  <div id="homePage">
    <div class="dialog">
      <DialogBoxTitle text1="一句话" text2="呈所想" />
      <div class="dialogBox">
        <DialogBox />
      </div>
      <div class="display-userWork" v-if="loginUserStore.loginUser.id">
        <h2>我的作品</h2>
        <div class="app-grid">
          <AppCard
            v-for="app in myWorks"
            :app="app"
            :key="app.id"
            @viewChat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="display-pagination">
          <a-pagination
            v-model:current="myWorkPages.current"
            :total="myWorkPages.total"
            v-model:page-size="myWorkPages.pageSize"
            :show-total="(total: number) => `共 ${total} 个应用`"
            :show-size-changer="false"
            @change="getMyWorks"
          />
        </div>
        <div class="display-featuredWord" v-if="featuredWorks.length">
          <h2>精选样例</h2>
          <div class="app-grid">
            <AppCard
              v-for="app in featuredWorks"
              :app="app"
              :key="app.id"
              :featured="true"
              @viewChat="viewChat"
              @view-work="viewWork"
            />
          </div>
          <div class="display-pagination">
            <a-pagination
              v-model:current="myWorkPages.current"
              :total="myWorkPages.total"
              v-model:page-size="myWorkPages.pageSize"
              :show-total="(total: number) => `共 ${total} 个应用`"
              :show-size-changer="false"
              @change="getMyWorks"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import DialogBoxTitle from '@/components/DialogBoxTitle.vue'
import DialogBox from '@/components/DialogBox.vue'
import { useRouter } from 'vue-router'
import { getDeployUrl } from '@/config/env.ts'
import { onMounted, reactive, ref } from 'vue'
import { getGoodAppDetail, getMyAppDetail } from '@/api/appController.ts'
import { useLoginUserStore } from '@/stores/user.ts'
import { message } from 'ant-design-vue'
import AppCard from '@/components/AppCard.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()
// 用户相关
const myWorks = ref<API.AppVO[]>([])
const myWorkPages = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})
// 管理员相关
const featuredWorks = ref<API.AppVO[]>([])
const featuredWorkPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})
const viewChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/generate/${appId}`)
  }
}

const viewWork = (deployKey: string | number | undefined) => {
  if (deployKey) {
    const deployUrl = getDeployUrl(deployKey)
    window.open(deployUrl, '_blank')
  }
}

/**
 * 获取精选样例
 */
const getFeaturedWorks = async () => {
  try {
    const res = await getGoodAppDetail({
      pageNum: featuredWorkPage.current,
      pageSize: featuredWorkPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'ascend',
    })
    if (res.data.code === 0 && res.data.data) {
      featuredWorks.value = res.data.data.records || []
      featuredWorkPage.total = res.data.data.totalRow || 0
    }
  } catch (e) {
    console.error('加在我的应用失败. ' + e)
  }
}

/**
 * 获得我的应用
 */
const getMyWorks = async () => {
  if (!loginUserStore.loginUser.id) return
  try {
    const res = await getMyAppDetail({
      pageNum: myWorkPages.current,
      pageSize: myWorkPages.pageSize,
      sortField: 'createTime',
      sortOrder: 'ascend',
    })
    if (res.data.code === 0 && res.data.data) {
      myWorks.value = res.data.data.records || []
      myWorkPages.total = res.data.data.totalRow || 0
    }
  } catch (e) {
    console.error('加在我的应用失败. ' + e)
  }
}

onMounted(() => {
   getMyWorks()
})
</script>

<style scoped>
.dialogBox {
  margin-top: 20px;
}

#homePage {
  width: 1000px;
  margin: 0 auto;
}

.display-userWork {
  margin-top: 42px;
}

.display-userWork h2 {
  margin-top: 16px;
  margin-bottom: 30px;
}

/* 我的作品网格 */
.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
}

.display-pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
</style>
