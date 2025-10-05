import { createRouter, createWebHistory } from 'vue-router'
import TryPage from '@/components/TryPage.vue'
import TryPage2 from '@/components/TryPage2.vue'
import ProjectCalendar from '@/components/ProjectCalendar.vue'
import ProjectDash from '@/components/ProjectDash.vue'
import PersonelDash from '@/components/PersonelDash.vue'
import PersonelCalendar from '@/components/PersonelCalendar.vue'
import VehiclesDash from '@/components/VehiclesDash.vue'
import VehiclesCalendar from '@/components/VehiclesCalendar.vue'

const routes = [
    { path: '/try', name: 'Try', component: TryPage },
    { path: '/try2', name: 'Try2', component: TryPage2 },
    { path: '/project_calendar', name: 'ProjectCalendar', component: ProjectCalendar },
    { path: '/project_dashboard', name: 'ProjectDash', component: ProjectDash },
    { path: '/personel_dashboard', name: 'PersonelDash', component: PersonelDash},
    { path: '/personel_calendar', name: 'PersonelCalendar', component: PersonelCalendar},
    { path: '/vehicles_dashboard', name: 'VehiclesDash', component: VehiclesDash},
    { path: '/vehicles_calendar', name: 'VehiclesCalendar', component: VehiclesCalendar}

]
const router = createRouter({
       history :createWebHistory(),
       routes
}) 
export default router