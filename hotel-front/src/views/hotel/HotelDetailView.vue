<template>
    <div class="min-h-screen bg-gray-100">
      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <!-- Loading state -->
        <div v-if="hotelStore.loading" class="flex justify-center items-center py-8">
          <div class="text-gray-500">Cargando información del hotel...</div>
        </div>
  
        <!-- Error state -->
        <div v-else-if="hotelStore.error" class="text-red-600 text-center py-8">
          {{ hotelStore.error }}
        </div>
  
        <!-- Content -->
        <div v-else-if="hotel" class="bg-white shadow-lg rounded-lg overflow-hidden">
          <!-- Hotel Info -->
          <div class="relative">
            <img 
              :src="hotel.imagen || 'https://collezione.starhotels.com/assets/uploads/Starhotels-Collezione/Hotel-d-inghilterra/GALLERY/hir-exterior-2023.jpg'" 
              :alt="hotel.nombre"
              class="w-full h-96 object-cover"
            >
            <div class="absolute inset-0 bg-black bg-opacity-40 flex items-end">
              <div class="p-8 text-white">
                <h1 class="text-4xl font-bold">{{ hotel.nombre }}</h1>
                <p class="text-xl mt-2">{{ hotel.ciudad }}</p>
              </div>
            </div>
          </div>
  
          <!-- Hotel Details -->
          <div class="p-8">
            <div class="mb-8">
              <h2 class="text-2xl font-semibold text-gray-900 mb-4">Sobre el hotel</h2>
              <p class="text-gray-600">{{ hotel.descripcion }}</p>
            </div>
  
            <!-- Habitaciones disponibles -->
            <div>
              <h2 class="text-2xl font-semibold text-gray-900 mb-4">Habitaciones disponibles</h2>
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div v-for="habitacion in habitacionesDisponibles" 
                    :key="habitacion.id"
                    class="border rounded-lg overflow-hidden">
                  <img 
                    :src="habitacion.imagen || 'https://www.usatoday.com/gcdn/-mm-/05b227ad5b8ad4e9dcb53af4f31d7fbdb7fa901b/c=0-64-2119-1259/local/-/media/USATODAY/USATODAY/2014/08/13/1407953244000-177513283.jpg'" 
                    :alt="'Habitación ' + habitacion.numero"
                    class="w-full h-48 object-cover"
                  >
                  <div class="p-4">
                    <div class="flex justify-between items-start">
                      <h3 class="text-lg font-semibold">Habitación {{ habitacion.numero }}</h3>
                      <!-- Botón para ver calificaciones -->
                      <button 
                        @click="verCalificaciones(habitacion)"
                        class="text-indigo-600 hover:text-indigo-800 flex items-center gap-1"
                      >
                        <span class="text-yellow-400">★</span>
                        {{ obtenerPromedioCalificaciones(habitacion.id) }}
                        <span class="text-sm text-gray-500">({{ obtenerTotalCalificaciones(habitacion.id) }})</span>
                      </button>
                    </div>
                    <p class="text-gray-600">{{ habitacion.tipo }}</p>
                    <div class="mt-2">
                      <p class="text-sm text-gray-500">{{ habitacion.descripcion }}</p>
                      <div class="mt-2 flex items-center">
                        <span class="text-sm text-gray-500">
                          {{ habitacion.capacidad }} personas • {{ habitacion.camas }} camas
                        </span>
                      </div>
                    </div>
                    <div class="mt-4 flex justify-between items-center">
                      <span class="text-lg font-bold text-indigo-600">
                        ${{ habitacion.precio }}
                      </span>
                      <span 
                        class="px-3 py-1 text-sm rounded-full"
                        :class="getEstadoClase(habitacion?.estado ?? '')"
                      >
                        {{ habitacion?.estado }}
                      </span>
                      <button 
                        v-if="habitacion?.estado == 'disponible'"
                        class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
                        @click="reservarHabitacion(habitacion)"
                      >
                        Reservar
                      </button>
                    </div>
                  </div>

                  <!-- Modal de calificaciones -->
                  <Modal :show="habitacionSeleccionada === habitacion.id" 
                      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
                      @close="cerrarModal">
                      <div class="flex justify-between items-start mb-4">
                          <h2 class="text-xl font-semibold">
                           Habitación {{ habitacion.numero }}
                          </h2>
                        </div>
                        
                        <room-ratings :room-id="habitacion.id" @ratings-loaded="handleRatingsLoaded" />
                  </Modal>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-8">
                        <CalificacionHotel :hotel-id="$route.params.id" />
                      </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { computed, ref } from 'vue'
  import { useHotelStore } from '@/stores/hotels'
  import CalificacionHotel from '@/components/hotel/CalificacionHotel.vue'
  import { useReservationStore } from '@/stores/reservations'
  import Modal from '@/components/common/VModal.vue';
  import RoomRatings from '@/views/hotel/RoomRatingsView.vue'
  import { collection, query, getDocs } from 'firebase/firestore'
  import { db } from '@/config/firebase'
  
  export default {
    name: 'HotelDetailView',
    components: {
      CalificacionHotel,
      Modal,
      RoomRatings
    },
    setup() {
      const hotelStore = useHotelStore()
      const reservationStore = useReservationStore()
      const hotel = computed(() => hotelStore.currentHotel)
      const habitacionSeleccionada = ref(null)
      const calificaciones = ref({})
      
      const habitacionesDisponibles = computed(() => 
        hotel.value?.habitaciones?.filter(h => h.disponible) || []
      )
  
      const reservarHabitacion = async (habitacion) => {
        await hotelStore.updateHabitacion(
              hotel.value.id,
              habitacion.id,
              {
                estado: 'reservada'
              }
            )

        await reservationStore.createReservation(
          {...habitacion, estado: 'reservada', hotelId: hotel.value.id, hotelNombre: hotel.value.nombre }
        )
      }

      const getEstadoClase = (estado) => {
      return {
        'bg-green-100 text-green-800': estado === 'disponible',
        'bg-red-100 text-red-800': estado === 'reservada',
        }
      }

      // Cargar calificaciones
      const cargarCalificaciones = async () => {
        const q = query(collection(db, 'reservacionesCalificadas'))
        const querySnapshot = await getDocs(q)
        
        // Agrupar calificaciones por habitación
        querySnapshot.docs.forEach(doc => {
          const data = doc.data()
          if (!calificaciones.value[data.roomId]) {
            calificaciones.value[data.roomId] = []
          }
          calificaciones.value[data.roomId].push({
            id: doc.id,
            ...data
          })
        })
      }

      const obtenerPromedioCalificaciones = (habitacionId) => {
        const ratings = calificaciones.value[habitacionId] || []
        if (!ratings.length) return 'Sin calificaciones'
        
        const promedio = ratings.reduce((acc, curr) => acc + curr.rating, 0) / ratings.length
        return promedio.toFixed(1)
      }

      const obtenerTotalCalificaciones = (habitacionId) => {
        return (calificaciones.value[habitacionId] || []).length
      }

      const verCalificaciones = (habitacion) => {
        habitacionSeleccionada.value = habitacion.id
      }

      const cerrarModal = () => {
        habitacionSeleccionada.value = null
      }

      const handleRatingsLoaded = (ratings) => {
        console.log('Calificaciones cargadas para habitación:', ratings)
      }

  
      return {
        hotelStore,
        hotel,
        habitacionesDisponibles,
        reservarHabitacion,
        getEstadoClase,
        habitacionSeleccionada,
        verCalificaciones,
        cerrarModal,
        obtenerPromedioCalificaciones,
        obtenerTotalCalificaciones,
        cargarCalificaciones,
        handleRatingsLoaded
      }
    },
    async created() {
      await this.hotelStore.fetchHotelById(this.$route.params.id)
      await this.cargarCalificaciones()
    }
  }
  </script>