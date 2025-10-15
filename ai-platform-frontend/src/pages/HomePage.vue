<template>
  <div id="homePage">
    <div class="dialog">
      <DialogBoxTitle text1="一句话" text2="呈所想"/>
      <div class="dialogBox">
        <DialogBox/>
      </div>
      <div class="display-userWork">
        <h2>我的作品</h2>

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
#homePage {
  width: 1000px;
  margin: 0 auto;
}
.display-userWork h2 {
  margin-top: 16px;
}

</style>
