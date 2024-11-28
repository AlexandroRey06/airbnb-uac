<template>
  <nav class="bg-gray-800">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <router-link to="/" class="text-white font-bold text-xl">
              Hotel App
            </router-link>
          </div>
          <!-- Menú Desktop -->
          <div class="hidden md:block">
            <div class="ml-10 flex items-baseline space-x-4">
              <!-- Tus links actuales -->
            </div>
          </div>
        </div>
        <!-- Usuario y Logout Desktop -->
        <div class="hidden md:block">
          <!-- Tu código actual para usuario y logout -->
        </div>

        <!-- Botón móvil -->
        <div class="md:hidden">
          <button 
            @click="isOpen = !isOpen" 
            class="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-white hover:bg-gray-700 focus:outline-none"
          >
            <VMenu v-if="!isOpen" class="h-6 w-6" />
            <X v-else class="h-6 w-6" />
          </button>
        </div>
      </div>
    </div>

    <!-- Menú móvil -->
    <div :class="{'block': isOpen, 'hidden': !isOpen}" class="md:hidden">
      <div class="px-2 pt-2 pb-3 space-y-1 sm:px-3">
        <router-link 
          to="/" 
          class="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium"
        >
          Inicio
        </router-link>
        
        <template v-if="authStore.isAdmin">
          <router-link 
            to="/admin/users" 
            class="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium"
          >
            Usuarios
          </router-link>
          
          <router-link 
            to="/admin/roles" 
            class="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium"
          >
            Roles
          </router-link>

          <router-link 
            to="/hotels" 
            class="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium"
          >
            Hoteles
          </router-link>
        </template>

        <router-link 
          to="/reservations" 
          class="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium"
          v-if="authStore.isAuthenticated"
        >
          Mis reservas
        </router-link>
      </div>

      <!-- Usuario y Logout móvil -->
      <div class="pt-4 pb-3 border-t border-gray-700">
        <template v-if="authStore.isAuthenticated">
          <div class="flex items-center px-5">
            <div class="flex-shrink-0">
              <User class="h-8 w-8 text-gray-300" />
            </div>
            <div class="ml-3">
              <div class="text-base font-medium leading-none text-white">
                {{ authStore.user?.nombre }}
              </div>
            </div>
          </div>
          <div class="mt-3 px-2">
            <button 
              @click="logout" 
              class="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-gray-400 hover:text-white hover:bg-gray-700"
            >
              Cerrar Sesión
            </button>
          </div>
        </template>
        <template v-else>
          <div class="mt-3 px-2">
            <router-link 
              to="/login" 
              class="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-gray-400 hover:text-white hover:bg-gray-700"
            >
              Iniciar Sesión
            </router-link>
          </div>
        </template>
      </div>
    </div>
  </nav>
</template>

<script>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { User, Menu as VMenu, X } from 'lucide-vue-next';

export default {
  name: 'VNavbar',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const isOpen = ref(false)

    return { 
      authStore, 
      router,
      isOpen 
    }
  },
  components: {
    User,
    VMenu,
    X
  },
  methods: {
    async logout() {
      this.authStore.logout()
      this.router.push('/login')
      this.isOpen = false
    }
  }
}
</script>