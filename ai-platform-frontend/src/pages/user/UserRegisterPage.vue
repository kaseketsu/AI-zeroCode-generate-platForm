<template>
  <div id="userRegisterPage">
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
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请确认密码！' },
          { min: 8, message: '密码长度不能小于 8 位！' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" />
      </a-form-item>
      <div class="tips">
        已有账号？
        <router-link to="/user/login">去登录</router-link>
      </div>
      <a-form-item>
        <a-button style="width: 100%" type="primary" html-type="submit">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { userRegister } from '@/api/userController.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const router = useRouter()

/**
 * 表单提交
 * @param values 表单数据
 */
const onFinish = async (values: any) => {
  // 用户登录
  const res = await userRegister(values)
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成功')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('注册失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
  width: 400px;
  margin: 0 auto;
}

#userRegisterPage .title {
  text-align: center;
  margin-bottom: 16px;
}

#userRegisterPage .desc {
  color: #bbb;
  text-align: center;
  margin-bottom: 16px;
}

#userRegisterPage .tips {
  text-align: right;
  margin-bottom: 16px;
}
</style>
