<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8">
        <div>
          <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Registro de Usuario
          </h2>
        </div>
        <form class="mt-8 space-y-6" @submit.prevent="handleSubmit">
          <div class="rounded-md shadow-sm space-y-4">
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
              <input 
                v-model="formData.email" 
                id="email" 
                name="email" 
                type="email" 
                required 
                class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" 
                placeholder="Email"
                :disabled="authStore.loading"
              >
            </div>
  
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700">Contraseña</label>
              <input 
                v-model="formData.password" 
                id="password" 
                name="password" 
                type="password" 
                required 
                class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" 
                placeholder="Contraseña"
                :disabled="authStore.loading"
              >
            </div>
  
            <div>
              <label for="nombre" class="block text-sm font-medium text-gray-700">Nombre</label>
              <input 
                v-model="formData.nombre" 
                id="nombre" 
                name="nombre" 
                type="text" 
                required 
                class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" 
                placeholder="Nombre"
                :disabled="authStore.loading"
              >
            </div>
  
            <div>
              <label for="apellido" class="block text-sm font-medium text-gray-700">Apellido</label>
              <input 
                v-model="formData.apellido" 
                id="apellido" 
                name="apellido" 
                type="text" 
                required 
                class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" 
                placeholder="Apellido"
                :disabled="authStore.loading"
              >
            </div>
  
            <div>
              <label for="telefono" class="block text-sm font-medium text-gray-700">Teléfono</label>
              <input 
                v-model="formData.telefono" 
                id="telefono" 
                name="telefono" 
                type="tel" 
                required 
                class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" 
                placeholder="Teléfono"
                :disabled="authStore.loading"
              >
            </div>

            <div>
          <label for="rolId" class="block text-sm font-medium text-gray-700">Rol</label>
          <select 
            v-model="formData.rolId"
            id="rolId"
            name="rolId"
            required
            class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            :disabled="roleStore.loading"
          >
            <option value="" disabled>Seleccione un rol</option>
            <option 
              v-for="rol in roleStore.roles" 
              :key="rol.id" 
              :value="rol.id"
            >
              {{ rol.nombre }}
            </option>
          </select>
        </div>
          </div>
  
          <div>
            <button 
              type="submit" 
              class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              :disabled="authStore.loading"
            >
              <span v-if="authStore.loading">Registrando...</span>
              <span v-else>Registrarse</span>
            </button>
          </div>
  
          <div v-if="authStore.error" class="mt-4 text-red-600 text-center">
            {{ authStore.error }}
          </div>
  
          <div class="text-center">
            <router-link 
              to="/login" 
              class="font-medium text-indigo-600 hover:text-indigo-500"
            >
              ¿Ya tienes cuenta? Inicia sesión
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
import { useAuthStore } from '@/stores/auth'
import { useRolesStore } from '@/stores/roles'
import { useRouter } from 'vue-router'

export default {
  name: 'RegisterForm',
  setup() {
    const authStore = useAuthStore()
    const roleStore = useRolesStore()
    const router = useRouter()

    return { authStore, roleStore, router }
  },
  data() {
    return {
      formData: {
        email: '',
        password: '',
        nombre: '',
        apellido: '',
        telefono: '',
        rolId: ''  // Inicialmente vacío
      }
    }
  },
  async created() {
    // Cargar roles cuando el componente se crea
    try {
        await this.roleStore.fetchRoles()
      } catch (error) {
        console.error('Error cargando roles:', error)
        alert("error")
      }
  },
  methods: {
    async handleSubmit() {
      try {
        await this.authStore.register(this.formData)
        this.$router.push('/')
      } catch (error) {
        console.error('Error de registro:', error)
      }
    }
  }
}
</script>