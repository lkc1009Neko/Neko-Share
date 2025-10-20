import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../view/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../view/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../view/Register.vue')
  },
  {
    path: '/article/:id',
    name: 'Article',
    component: () => import('../view/Article.vue')
  },
  {
    path: '/write',
    name: 'Write',
    component: () => import('../view/Write.vue')
  },
  {
    path: '/profile/:username',
    name: 'Profile',
    component: () => import('../view/Profile.vue')
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('../view/Notifications.vue')
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('../view/Categories.vue')
  },
  {
    path: '/resources',
    name: 'Resources',
    component: () => import('../view/Resources.vue')
  },
  {
    path: '/books',
    name: 'Books',
    component: () => import('../view/Books.vue')
  },
  {
    path: '/books/:collectionId',
    name: 'BookCollection',
    component: () => import('../view/BookDetail.vue')
  },
  {
    path: '/books/:collectionId/book/:bookId',
    name: 'BookDetail',
    component: () => import('../view/BookDetail.vue')
  },
  {
    path: '/bookshelf',
    name: 'BookshelfDetail',
    component: () => import('../view/BookshelfDetail.vue')
  },
  {
    path: '/plugins',
    name: 'Plugins',
    component: () => import('../view/Plugins.vue')
  },
  {
    path: '/games',
    name: 'Games',
    component: () => import('../view/Games.vue')
  },
  {
    path: '/voices',
    name: 'Voices',
    component: () => import('../view/Voices.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router