<template>
  <div id="globalHeader">
    <RouterLink to="/">
      <div class="title-bar">
        <img src="@/assets/logo5.png" alt="logo" class="logo"/>
        <div class="title">AI智能体</div>
      </div>
    </RouterLink>
    <a-menu v-model:selectedKeys="current" mode="horizontal" :items="items" />
  </div>
</template>

<script setup lang="ts">
import { h, ref } from 'vue';
import { HomeOutlined, AppstoreOutlined, SettingOutlined } from '@ant-design/icons-vue';
import { MenuProps }   from 'ant-design-vue';
const current = ref<string[]>(['mail']);
const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/about',
    label: '关于',
    title: '关于',
  },
]);
</script>

<style scoped>
#globalHeader {
  display: flex;
  align-items: center;
  padding-inline: 20px;
}
#globalHeader .title-bar {
  display: flex;
  align-items: center;
  margin-right: 30px;
}
#globalHeader .title {
  color: black;
  margin-left: 15px;
}
#globalHeader .logo {
  height: 40px;
}
</style>
