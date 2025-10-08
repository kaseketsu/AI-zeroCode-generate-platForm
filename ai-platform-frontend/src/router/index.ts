import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import AppGeneratePage from '@/pages/app/AppGeneratePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/pages/HomePage.vue'),
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue'),
    },
    {
      path: '/user/login',
      name: 'login',
      component: () => import('@/pages/user/UserLoginPage.vue'),
    },
    {
      path: '/user/register',
      name: 'register',
      component: () => import('@/pages/user/UserRegisterPage.vue'),
    },
    {
      path: '/admin/userManage',
      name: 'manage',
      component: () => import('@/pages/admin/UserManagePage.vue'),
    },
    {
      path: '/app/generate/:appId',
      name: 'generateApp',
      component: AppGeneratePage
    }
  ],
})

export default router
