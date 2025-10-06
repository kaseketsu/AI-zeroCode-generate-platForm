<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="www.bilibili.com/"> Made by F1ower </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/layouts/GlobalHeader.vue'
</script>

<style scoped>
#basicLayout .content {
  width: 100%;
}

#basicLayout ::v-deep .header {
  background: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 15px;
}

#basicLayout .footer {
  background: #efefef;
  text-align: center;
  left: 0;
  right: 0;
  bottom: 0;
  position: fixed;
}

#basicLayout .content {
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 20px;
}
</style>
