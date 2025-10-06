<template>
  <div id="home-page">
    <div class="dialog">
      <DialogBoxTitle text1="一句话" text2="呈所想"/>
      <div class="dialogBox">
        <DialogBox/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import DialogBoxTitle from '@/components/DialogBoxTitle.vue'
import DialogBox from '@/components/DialogBox.vue'
</script>

<style scoped>
.dialogBox {
  margin-top: 20px;
}
</style>
