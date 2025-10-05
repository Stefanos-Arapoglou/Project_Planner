import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '@/components/LoginPage.vue'
import TryPage from '@/components/TryPage.vue'
import TryPage2 from '@/components/TryPage2.vue'
import ProjectCalendar from '@/components/ProjectCalendar.vue'
import ProjectDash from '@/components/ProjectDash.vue'
import PersonelDash from '@/components/PersonelDash.vue'
import PersonelCalendar from '@/components/PersonelCalendar.vue'
import VehiclesDash from '@/components/VehiclesDash.vue'
import VehiclesCalendar from '@/components/VehiclesCalendar.vue'

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: LoginPage },
    { path: '/try', name: 'Try', component: TryPage },
    { path: '/try2', name: 'Try2', component: TryPage2 },
    { path: '/project_calendar', name: 'ProjectCalendar', component: ProjectCalendar, meta: { requiresAuth: true } },
    { path: '/project_dashboard', name: 'ProjectDash', component: ProjectDash, meta: { requiresAuth: true } },
    { path: '/personel_dashboard', name: 'PersonelDash', component: PersonelDash, meta: { requiresAuth: true }},
    { path: '/personel_calendar', name: 'PersonelCalendar', component: PersonelCalendar, meta: { requiresAuth: true }},
    { path: '/vehicles_dashboard', name: 'VehiclesDash', component: VehiclesDash, meta: { requiresAuth: true }},
    { path: '/vehicles_calendar', name: 'VehiclesCalendar', component: VehiclesCalendar, meta: { requiresAuth: true }}

]
const router = createRouter({
       history :createWebHistory(),
       routes
}) 

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login');
  } else {
    next();
  }
});
export default router