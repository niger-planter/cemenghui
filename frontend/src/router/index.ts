import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import Meetings from '../views/MeetingList.vue';
import HomeView from '../views/HomeView.vue'
// 为了解决找不到声明文件的问题，使用 @ts-ignore 临时忽略类型检查
// @ts-ignore
import MeetingList from '../views/MeetingList.vue'
import CourseAIHelper from '../views/CourseAIHelper.vue'
import CourseList from '../views/CourseList.vue'
import IndustryNews from '../views/IndustryNews.vue'
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/meetings', name: 'Meetings', component: MeetingList, meta: { requiresAuth: true } },
  {
    path: '/ai-helper',
    name: 'CourseAIHelper',
    component: CourseAIHelper
  },
  {
    path: '/courses',
    name: 'Courses',
    component: CourseList
  },
  {
    path: '/news',
    name: 'IndustryNews',
    component: IndustryNews
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/meetings'
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  if (to.meta.requiresAuth && !user) {
    next('/login');
  } else {
    next();
  }
});

export default router;
