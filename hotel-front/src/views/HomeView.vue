<template>
    <div class="min-h-screen bg-gray-100">
      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div class="px-4 py-6 sm:px-0">
          <div class="bg-white overflow-hidden shadow-xl sm:rounded-lg">
            <div class="p-6">
              <h1 class="text-3xl font-bold text-gray-900 mb-4">
                Bienvenido al Sistema de Hotel
              </h1>
              <p class="text-gray-600 mb-4">
                Selecciona una opción para comenzar
              </p>
  
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-1 gap-6 mt-8">
                <!-- Card para Administración -->
                <div v-if="isAdmin" class="border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow">
                  <h2 class="text-xl font-semibold text-gray-900 mb-2">Administración</h2>
                  <p class="text-gray-600 mb-4">Gestiona usuarios y roles del sistema</p>
                  <div class="space-y-2">
                    <router-link 
                      to="/admin/users"
                      class="block text-indigo-600 hover:text-indigo-500"
                    >
                      Gestionar Usuarios →
                    </router-link>
                    <router-link 
                      to="/admin/roles"
                      class="block text-indigo-600 hover:text-indigo-500"
                    >
                      Gestionar Roles →
                    </router-link>
                  </div>
                </div>
  
                <!-- Vista de Usuario -->
                <div v-else class="px-4 py-6 sm:px-0">
                  <div class="mb-6">
                    <h1 class="text-3xl font-bold text-gray-900">Hoteles Disponibles</h1>
                    
                    <!-- Filtros -->
                    <div class="bg-white p-6 rounded-lg shadow-md mb-8">
                      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        <!-- Búsqueda -->
                        <div>
                          <label class="block text-sm font-medium text-gray-700 mb-2">Buscar</label>
                          <input
                            v-model="filters.search"
                            type="text"
                            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-indigo-500 focus:border-indigo-500"
                            placeholder="Nombre del hotel..."
                          >
                        </div>

                        <!-- Ciudad -->
                        <div>
                          <label class="block text-sm font-medium text-gray-700 mb-2">Ciudad</label>
                          <select
                            v-model="filters.ciudad"
                            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-indigo-500 focus:border-indigo-500"
                          >
                            <option value="">Todas las ciudades</option>
                            <option v-for="ciudad in ciudadesDisponibles" :key="ciudad" :value="ciudad">
                              {{ ciudad }}
                            </option>
                          </select>
                        </div>

                        <!-- Ordenar por -->
                        <div>
                          <label class="block text-sm font-medium text-gray-700 mb-2">Ordenar por</label>
                          <select
                            v-model="filters.sortBy"
                            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-indigo-500 focus:border-indigo-500"
                          >
                            <option value="nombre">Nombre</option>
                            <option value="ciudad">Ciudad</option>
                            <option value="habitaciones">Habitaciones disponibles</option>
                          </select>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Lista de Hoteles -->
                  <div class="space-y-6">
                  <!-- Cada hotel en una fila -->
                    <div v-for="hotel in hotelesFiltrados" 
                        :key="hotel.id" 
                        class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
                      <div class="flex flex-col md:flex-row">
                        <!-- Imagen del hotel -->
                        <div class="w-full md:w-1/3">
                          <img 
                            :src="hotel.imagen || 'https://collezione.starhotels.com/assets/uploads/Starhotels-Collezione/Hotel-d-inghilterra/GALLERY/hir-exterior-2023.jpg'" 
                            :alt="hotel.nombre"
                            class="w-full h-64 md:h-full object-cover"
                          >
                        </div>

                        <!-- Información del hotel -->
                        <div class="w-full md:w-2/3 p-6">
                          <div class="flex justify-between items-start">
                            <div>
                              <h3 class="text-2xl font-bold text-gray-900">{{ hotel.nombre }}</h3>
                              <p class="text-lg text-gray-600 mt-1">{{ hotel.ciudad }}</p>
                              <div class="flex items-center mt-2">
                                <span class="text-yellow-400">★★★★★</span>
                                <span class="ml-2 text-gray-600">Muy bueno</span>
                              </div>
                            </div>
                            <div class="text-right">
                              <p class="text-lg font-semibold text-indigo-600">
                                {{ getHabitacionesDisponibles(hotel) }} habitaciones disponibles
                              </p>
                            </div>
                          </div>

                          <p class="mt-4 text-gray-600">{{ hotel.descripcion }}</p>

                          <div class="mt-6 flex justify-between items-center">
                            <div class="flex space-x-4">
                              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                Disponible
                              </span>
                              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-800">
                                WiFi gratis
                              </span>
                            </div>
                            <router-link 
                              :to="`/hoteles/${hotel.id}/detalle`"
                              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                            >
                              Ver detalles
                            </router-link>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Mensaje cuando no hay resultados -->
                  <div v-if="hotelesFiltrados.length === 0" class="text-center py-12">
                    <p class="text-gray-500">No se encontraron hoteles con los filtros seleccionados</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useHotelStore } from '@/stores/hotels'

export default {
  name: 'HomeView',
  setup() {
    const authStore = useAuthStore()
    const hotelStore = useHotelStore()

    const filters = ref({
      search: '',
      ciudad: '',
      sortBy: 'nombre'
    })

    // Obtener ciudades únicas de los hoteles
    const ciudadesDisponibles = computed(() => {
      const ciudades = hotelStore.hotels.map(hotel => hotel.ciudad)
      return [...new Set(ciudades)].sort()
    })

    // Filtrar y ordenar hoteles
    const hotelesFiltrados = computed(() => {
      let resultado = hotelStore.hotels

      // Filtrar por búsqueda
      if (filters.value.search) {
        const searchTerm = filters.value.search.toLowerCase()
        resultado = resultado.filter(hotel => 
          hotel.nombre.toLowerCase().includes(searchTerm) ||
          hotel.ciudad.toLowerCase().includes(searchTerm) ||
          hotel.descripcion.toLowerCase().includes(searchTerm)
        )
      }

      // Filtrar por ciudad
      if (filters.value.ciudad) {
        resultado = resultado.filter(hotel => 
          hotel.ciudad === filters.value.ciudad
        )
      }

      // Ordenar resultados
      resultado = resultado.sort((a, b) => {
        switch (filters.value.sortBy) {
          case 'nombre':
            return a.nombre.localeCompare(b.nombre)
          case 'ciudad':
            return a.ciudad.localeCompare(b.ciudad)
          case 'habitaciones':
            return getHabitacionesDisponibles(b) - getHabitacionesDisponibles(a)
          default:
            return 0
        }
      })

      return resultado
    })

    const getHabitacionesDisponibles = (hotel) => {
      return hotel.habitaciones?.filter(h => h.disponible)?.length || 0
    }

    return {
      authStore,
      hotelStore,
      filters,
      ciudadesDisponibles,
      hotelesFiltrados,
      getHabitacionesDisponibles
    }
  },
  async created() {
    await this.hotelStore.fetchHotels()
  }
}
</script>