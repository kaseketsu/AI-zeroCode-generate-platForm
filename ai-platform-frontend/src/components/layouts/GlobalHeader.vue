<template>
  <div id="globalHeader">
    <a-col flex="200px">
      <RouterLink to="/">
        <div class="title-bar">
          <img src="@/assets/logo5.png" alt="logo" class="logo" />
          <div class="title">AI智能体</div>
        </div>
      </RouterLink>
    </a-col>
    <a-col flex="auto">
      <a-menu
        v-model:selectedKeys="current"
        mode="horizontal"
        :items="items"
        @click="doMenuClick"
      />
    </a-col>
    <a-col flex="120px">
      <div class="user-login">
        <div class="user-icon" v-if="loginUserStore.loginUser.id">
          {{ loginUserStore.loginUser.username ?? 'anon' }}
        </div>
        <a-button type="primary" href="/user/login"> 登录 </a-button>
      </div>
    </a-col>
  </div>
</template>

<script setup lang="ts">
import { h, ref } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/user.ts'

const current = ref<string[]>([])
const router = useRouter()
const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()
const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/about',
    label: '关于',
    title: '关于',
  },
])

// 定义跳转事件
const doMenuClick = ({ key }: { key: string }): void => {
  router.push({ path: key })
}

// 保证菜单高粱
router.afterEach((to, from) => {
  current.value = [to.path]
})
</script>

<style scoped>
#globalHeader {
  display: flex;
  align-items: center;
  padding-inline: 20px;
}

#globalHeader .title-bar {
  display: flex;
  align-items: center;
  margin-right: 30px;
}

#globalHeader .title {
  color: black;
  margin-left: 15px;
}

#globalHeader .user-login {
  display: flex;
  align-items: center;
}

#globalHeader .logo {
  height: 40px;
}

#globalHeader .user-icon {
  margin-right: 20px;
}
</style>
