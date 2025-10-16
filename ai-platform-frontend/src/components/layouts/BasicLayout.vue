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
#basicLayout {
  background: none;
}

#basicLayout .content {
  width: 100%;
  padding: 0;
  margin: 0;

  /* 🌈 柔和渐变 + 轻光晕 */
  background:
    radial-gradient(800px 600px at 20% 30%, rgba(123, 163, 255, 0.25), transparent 60%),
    radial-gradient(700px 500px at 80% 70%, rgba(255, 170, 210, 0.2), transparent 60%),
    linear-gradient(180deg, #f7f9fc 0%, #f0f4ff 100%);

  background-attachment: fixed;
  background-repeat: no-repeat;
  animation: contentGlow 24s ease-in-out infinite alternate;
}

/* ✨ 背景轻微呼吸动画（不会晃眼） */
@keyframes contentGlow {
  0% {
    background-position:
      0% 0%,
      100% 100%,
      center;
  }
  50% {
    background-position:
      2% 1%,
      98% 99%,
      center;
  }
  100% {
    background-position:
      0% 0%,
      100% 100%,
      center;
  }
}

#basicLayout .header {
  background: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 15px;
}

#basicLayout .footer {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  text-align: center;
  padding: 20px;
  margin-top: 40px;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
}
</style>
