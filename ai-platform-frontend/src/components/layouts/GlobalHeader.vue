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
          <a-dropdown>
            <a-space>
              <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              {{ loginUserStore.loginUser.userName ?? 'anon' }}
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else>
          <a-button type="primary" href="/user/login">登录</a-button>
        </div>
      </div>
    </a-col>
  </div>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/user.ts'
import { LogoutOutlined } from '@ant-design/icons-vue'
import { userLogOut } from '@/api/userController.ts'

const current = ref<string[]>([])
const router = useRouter()

// 获取登录用户
const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()

const originItems = [
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
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  }
]

// 过滤菜单项
const filterItems = (menus = [] as MenuProps['items']) => {
  return menus?.filter((item) => {
    const key = item?.key as string
    if (key.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (loginUser.userRole === 0 || !loginUser.id) {
        return false
      }
    }
    return true
  })
}

const items = computed(() => filterItems(originItems))
// 定义跳转事件
const doMenuClick = ({ key }: { key: string }): void => {
  router.push({ path: key })
}

// 保证菜单高粱
router.afterEach((to, from) => {
  current.value = [to.path]
})

// 账户注销
const doLogout = async () => {
  const res = await userLogOut()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功！')
    await router.push({ path: '/user/login' })
  }
}
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
  color: blue;
  font-size: 20px;
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
