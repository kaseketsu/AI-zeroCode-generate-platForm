<template>
  <div id="">
    <h1>hollow word!</h1>
    <h2>{{ JSON.stringify(loginUserStore.loginUser) }}</h2>
  </div>
</template>

<script setup lang="ts">
import { hello } from '@/api/test.ts'
import { useLoginUserStore } from '@/stores/user.ts'

hello().then((res) => console.log(res))

const loginUserStore = useLoginUserStore()



</script>

<style scoped></style>
