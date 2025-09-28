<template>
  <div class="app">
    <BasicLayout />
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/components/layouts/BasicLayout.vue'
import { useLoginUserStore } from '@/stores/user.ts'

// 获取用户信息
const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()

</script>

<style scoped></style>
