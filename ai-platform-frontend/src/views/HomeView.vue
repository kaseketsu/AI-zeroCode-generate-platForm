<template>
  <div id="">
    <h1>hollow word!</h1>
    <h2>{{ JSON.stringify(loginUserStore.loginUser) }}</h2>
    <DialogBoxTitle
    />
  </div>
</template>

<script setup lang="ts">
import { useLoginUserStore } from '@/stores/user.ts'
import DialogBoxTitle from '@/components/DialogBoxTitle.vue'


const loginUserStore = useLoginUserStore()



</script>

<style scoped></style>
