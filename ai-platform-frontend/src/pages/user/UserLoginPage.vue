<template>
  <div id="userLoginPage">
    <div class="title">
      <h2>AI 智能体 - 智能代码生成器</h2>
    </div>
    <div class="desc">解放大脑算力 释放无限潜能</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="onFinish">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号！' }]">
        <a-input v-model:value="formState.userAccount" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码！' },
          { min: 8, message: '密码长度不能小于 8 位！' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" />
      </a-form-item>
      <div class="tips">
        还没账号？
        <router-link to="/user/register">去注册</router-link>
      </div>
      <a-form-item>
        <a-button style="width: 100%" type="primary" html-type="submit">Submit</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/user.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'


const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const loginUserStore = useLoginUserStore()
const router = useRouter()

/**
 * 表单提交
 * @param values 表单数据
 */
const onFinish = async (values: any) => {
  // 用户登录
  const res = await userLogin(values)
  if (res.data.code === 0 && res.data.data) {
    loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  width: 400px;
  margin: 0 auto;
}

#userLoginPage .title{
  text-align: center;
  margin-bottom: 16px;
}

#userLoginPage .desc {
  color: #bbb;
  text-align: center;
  margin-bottom: 16px;
}

#userLoginPage .tips {
  text-align: right;
  margin-bottom: 16px;
}
</style>
