<template>
  <div id="appGeneratePage">
    <div class="left">
      <Bubble/>
    </div>
    <div class="right">
      111
    </div>
  </div>
</template>

<script setup lang="ts">

import Bubble from '@/components/Bubble.vue'
</script>

<style scoped>
#appGeneratePage {
  display: flex;
}
.left {
  flex: 0 0 40%; /* ✅ 占据父容器宽度的 30% */
}
.right {
  flex: 1;
}
</style>
