
// 显示是否初次获取用户
import router from '@/router'
import { useLoginUserStore } from '@/stores/user.ts'
import { message } from 'ant-design-vue'

let isFirst = true

// 路由守卫在进入页面前校验参数
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  if (isFirst) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    isFirst = false
  }
  const path = to.fullPath
  if (path.startsWith("/admin")) {
    if (!loginUser.id || loginUser.userRole !== 1) {
      message.error("无权限")
      next({ path: `/user/login?redirect=${to.fullPath}` })
      return
    }
  }
  next()
})
