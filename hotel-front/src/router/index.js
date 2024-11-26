import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Importación de vistas
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import UsersView from '@/views/admin/UsersView.vue'
import RolesView from '@/views/admin/RolesView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresGuest: true }
  },
  {
    path: '/hotels',
    name: 'hotels',
    component: () => import('@/views/hotel/HotelsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/reservations',
    name: 'reservations',
    component: () => import('@/views/hotel/ReservasionesView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/hotels/:id/habitaciones',
    name: 'habitaciones',
    component: () => import('@/views/hotel/HabitacionesView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/hoteles/:id/detalle',
    name: 'hotel-detail',
    component: () => import('@/views/hotel/HotelDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'admin',
    meta: { 
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: 'users',
        name: 'users',
        component: UsersView
      },
      {
        path: 'roles',
        name: 'roles',
        component: RolesView
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Verificar autenticación actual
  await authStore.checkAuth()

  // Rutas que requieren no estar autenticado (login, register)
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next({ name: 'home' })
    return
  }

  // Rutas que requieren autenticación
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ 
      name: 'login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // Rutas que requieren ser admin
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next({ name: 'home' })
    return
  }

  next()
})

export default router;