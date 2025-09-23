import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<any>({
    userName: '未登录',
  })

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  async function fetchLoginUser() {
    setTimeout(() => {
      loginUser.value = {
        username: 'anon',
        id: 1,
      }
    }, 3000)
  }

  return { loginUser, setLoginUser, fetchLoginUser }
})
