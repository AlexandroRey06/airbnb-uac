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
            <div class="hidden md:block">
              <div class="ml-10 flex items-baseline space-x-4">
                <router-link 
                  to="/" 
                  class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                >
                  Inicio
                </router-link>
                
                <template v-if="authStore.isAdmin">
                  <router-link 
                    to="/admin/users" 
                    class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                  >
                    Usuarios
                  </router-link>
                  
                  <router-link 
                    to="/admin/roles" 
                    class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                  >
                    Roles
                  </router-link>

                  <router-link 
                    to="/hotels" 
                    class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                    >
                    Hoteles
                    </router-link>
                </template>

                  <router-link 
                    to="/reservations" 
                    class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                    v-if="authStore.isAuthenticated"
                    >
                    Mis reservas
                  </router-link>
              </div>
            </div>
          </div>
          <div class="hidden md:block">
            <div class="ml-4 flex items-center md:ml-6">
              <template v-if="authStore.isAuthenticated">
                <span class="flex items-center gap-2 px-3 py-1.5 bg-gray-800/50 rounded-md">
                  <User class="h-4 w-4 text-gray-300" />
                  <span class="text-gray-300 font-medium">
                    {{ authStore.user?.nombre }}
                  </span>
                </span>
                <button 
                  @click="logout" 
                  class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                >
                  Cerrar Sesión
                </button>
              </template>
              <template v-else>
                <router-link 
                  to="/login" 
                  class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                >
                  Iniciar Sesión
                </router-link>
              </template>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </template>
  
  <script>
  import { useAuthStore } from '@/stores/auth'
  import { useRouter } from 'vue-router'
  import { User } from 'lucide-vue-next';
  
  export default {
    name: 'VNavbar',
    setup() {
      const authStore = useAuthStore()
      const router = useRouter()
      return { authStore, router }
    },
    components: {
      User
    },
    methods: {
      async logout() {
        this.authStore.logout()
        this.router.push('/login')
      }
    }
  }
  </script>