import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Layout.vue'),
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'questions',
        name: 'Questions',
        component: () => import('@/views/admin/Questions.vue')
      },
      {
        path: 'papers',
        name: 'Papers',
        component: () => import('@/views/admin/Papers.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'records',
        name: 'Records',
        component: () => import('@/views/admin/Records.vue')
      }
    ]
  },
  {
    path: '/student',
    name: 'Student',
    component: () => import('@/views/student/Layout.vue'),
    children: [
      {
        path: '',
        redirect: '/student/exams'
      },
      {
        path: 'exams',
        name: 'Exams',
        component: () => import('@/views/student/Exams.vue')
      },
      {
        path: 'exam/:id',
        name: 'Exam',
        component: () => import('@/views/student/Exam.vue')
      },
      {
        path: 'records',
        name: 'StudentRecords',
        component: () => import('@/views/student/Records.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
