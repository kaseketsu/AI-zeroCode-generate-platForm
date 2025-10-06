<template>
  <div id="dialogBoxTitle">
    <div class="title-bar">
      <div class="text">{{ text1 }}</div>
      <img class="img" src="@/assets/dialogPic.png" alt="图片" />
      <div class="text">{{ text2 }}</div>
    </div>
    <div class="desc">与 AI 对话轻松创建应用和网站</div>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'DialogBoxTitle' })
const { text1, text2 } = defineProps<{
  text1: string
  text2: string
}>()
</script>

<style scoped>

.title-bar {
  display: flex;
  align-items: center;
  justify-content: center;
}

.text {
  font-size: 25px;
  font-family: 'SimSun', sans-serif;
  font-weight: bold;
  text-shadow:
    0.5px 0 0 currentColor,
    -0.5px 0 0 currentColor;
}

.img {
  width: 55px;
  height: auto;
}

.desc {
  color: #bbb;
  text-align: center;
  margin-top: 10px;
}
</style>
