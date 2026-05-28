import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台' }
      },
      // Book Module
      {
        path: 'book/catalog',
        name: 'BookCatalog',
        component: () => import('@/views/book/catalog/index.vue'),
        meta: { title: '图书编目' }
      },
      {
        path: 'book/list',
        name: 'BookList',
        component: () => import('@/views/book/list/index.vue'),
        meta: { title: '图书管理' }
      },
      {
        path: 'book/inventory',
        name: 'BookInventory',
        component: () => import('@/views/book/inventory/index.vue'),
        meta: { title: '库存查看' }
      },
      // Borrow Module
      {
        path: 'borrow/borrow',
        name: 'BorrowBook',
        component: () => import('@/views/borrow/borrow/index.vue'),
        meta: { title: '图书借阅' }
      },
      {
        path: 'borrow/return',
        name: 'ReturnBook',
        component: () => import('@/views/borrow/return/index.vue'),
        meta: { title: '图书归还' }
      },
      {
        path: 'borrow/renew',
        name: 'RenewBook',
        component: () => import('@/views/borrow/renew/index.vue'),
        meta: { title: '续借管理' }
      },
      {
        path: 'borrow/reserve',
        name: 'ReserveBook',
        component: () => import('@/views/borrow/reserve/index.vue'),
        meta: { title: '预约管理' }
      },
      {
        path: 'borrow/overdue',
        name: 'OverdueList',
        component: () => import('@/views/borrow/overdue/index.vue'),
        meta: { title: '逾期管理' }
      },
      // Reader Module
      {
        path: 'reader/list',
        name: 'ReaderList',
        component: () => import('@/views/reader/list/index.vue'),
        meta: { title: '读者管理' }
      },
      {
        path: 'reader/register',
        name: 'ReaderRegister',
        component: () => import('@/views/reader/register/index.vue'),
        meta: { title: '读者注册' }
      },
      {
        path: 'reader/card',
        name: 'ReaderCard',
        component: () => import('@/views/reader/card/index.vue'),
        meta: { title: '借阅证管理' }
      },
      // Statistics Module
      {
        path: 'statistics/borrow',
        name: 'BorrowStats',
        component: () => import('@/views/statistics/borrow/index.vue'),
        meta: { title: '借阅统计' }
      },
      {
        path: 'statistics/popular',
        name: 'PopularBooks',
        component: () => import('@/views/statistics/popular/index.vue'),
        meta: { title: '热门图书' }
      },
      {
        path: 'statistics/reader',
        name: 'ReaderStats',
        component: () => import('@/views/statistics/reader/index.vue'),
        meta: { title: '活跃读者' }
      },
      // System Module
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/log',
        name: 'SystemLog',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志' }
      },
      // Profile
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人信息' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
