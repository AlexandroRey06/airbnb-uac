<script>
import { ref, computed, onMounted } from 'vue' // Agregado computed
import { useReservationStore } from '@/stores/reservations'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { useHotelStore } from '@/stores/hotels';
import ServiceRating from './ServiceRating.vue';
import VModal from '../common/VModal.vue';

export default {
  components: {
    VModal,
    ServiceRating
    },
  setup() {
    const reservationStore = useReservationStore()
    const hotelStore = useHotelStore()
    const authStore = useAuthStore()
    const router = useRouter()
    const loading = ref(false)
    const error = ref(null)
    const filtroEstado = ref('') // Agregado el ref para filtroEstado
    const showRatingModal = ref(false)
    const selectedReservation = ref(null)

    onMounted(async () => {
      try {
        loading.value = true
        
        if (!authStore.user) {
          router.push('/login')
          return
        }

        console.log('Cargando reservaciones para usuario:', authStore.user.id)
        await reservationStore.fetchUserReservations()
      } catch (err) {
        console.error('Error cargando reservaciones:', err)
        error.value = err.message
      } finally {
        loading.value = false
      }
    })

    // Agregado computed para reservaciones filtradas
    const reservacionesFiltradas = computed(() => {
      let reservaciones = reservationStore.userReservations
      if (filtroEstado.value) {
        reservaciones = reservaciones.filter(r => r.estado === filtroEstado.value)
      }
      return reservaciones
    })

    // Métodos agregados
    const formatDate = (date) => {
      return new Date(date).toLocaleDateString()
    }

    const getEstadoClase = (estado) => {
      return {
        'bg-blue-100 text-green-800': estado === 'reservada',
        'bg-red-100 text-red-800': estado === 'cancelada',
        'bg-green-100 text-green-800': estado === 'completada',
      }
    }

    const handleCancelar = async (id, hotelId, habitacionId) => {
      try {
        if (!confirm('¿Estás seguro de que deseas cancelar esta reservación?')) {
          return
        }
        
        loading.value = true

        await reservationStore.cancelReservation(id)

        await hotelStore.updateHabitacion(
              hotelId,
              habitacionId,
              {
                estado: 'disponible'
              }
            )
            await reservationStore.fetchUserReservations()
      } catch (err) {
        error.value = err.message
      } finally {
        loading.value = false
      }
    }

    const openRatingModal = (reservation) => {
      selectedReservation.value = reservation
      showRatingModal.value = true
    }

    const closeModal = () => {
      showRatingModal.value = false
    }

    const handleRatingSubmitted = async (data) => {
      showRatingModal.value = false
      await hotelStore.updateHabitacion(
              data.hotelId,
              data.roomId,
              {
                estado: 'disponible'
              }
            )
            await reservationStore.fetchUserReservations()
    }

    return {
      reservationStore,
      loading,
      error,
      filtroEstado,
      reservacionesFiltradas,
      formatDate,
      getEstadoClase,
      handleCancelar,
      openRatingModal,
      handleRatingSubmitted,
      selectedReservation,
      showRatingModal,
      closeModal
    }
  }
}
</script>

<template>
  <div class="space-y-6">
    <!-- Filtros -->
    <div class="flex justify-end">
      <select 
        v-model="filtroEstado" 
        class="rounded-md border-gray-300 shadow-sm px-4 py-2"
      >
        <option value="">Todos los estados</option>
        <option value="pendiente">Pendiente</option>
        <option value="confirmada">Confirmada</option>
        <option value="cancelada">Cancelada</option>
        <option value="completada">Completada</option>
      </select>
    </div>
    <!-- Loading state -->
    <div v-if="loading" class="text-center py-8">
      <div class="text-gray-500">Cargando reservaciones...</div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="text-red-600 text-center py-8">
      {{ error }}
    </div>

    <!-- Empty state -->
     
    <div v-else-if="reservacionesFiltradas.length === 0" class="text-center py-8">
      <p class="text-gray-500">
        No tienes reservaciones {{ filtroEstado ? `en estado ${filtroEstado}` : '' }}
      </p>
    </div>

    <!-- Lista de reservaciones -->
    <div v-else class="space-y-4">
      <div v-for="reservacion in reservacionesFiltradas" 
           :key="reservacion.id" 
           class="bg-white rounded-lg shadow p-6 hover:shadow-md transition-shadow duration-200"
      >
        <div class="flex justify-between items-start">
          <div>
            <h3 class="text-lg font-medium text-gray-900">
              {{ reservacion.hotelNombre }}
            </h3>
            <p class="text-sm text-gray-500">
              Habitación {{ reservacion.numero }}
            </p>
          </div>
          <span 
            class="px-3 py-1 text-sm rounded-full"
            :class="getEstadoClase(reservacion.estado)"
          >
            {{ reservacion.estado }}
          </span>
        </div>

        <div class="mt-4 flex justify-between items-center">
          <span class="font-bold text-lg text-indigo-600">
            ${{ reservacion.precio.toLocaleString() }}
          </span>
          <button 
            v-if="reservacion.disponible === true"
            @click="handleCancelar(reservacion.idReservacion, reservacion.hotelId, reservacion.id)"
            class="px-4 py-2 text-red-600 hover:text-red-800 transition-colors duration-200"
          >
            Cancelar reservación
          </button>
          <!-- Botón de calificación -->
          <button 
            v-if="reservacion.disponible === true"
            @click="openRatingModal(reservacion)"
            class="btn btn-primary"
            :class="reservacion.estado === 'completada' ? 'text-gray-200' : ''"
           :disabled="reservacion.estado === 'completada'" 
          >
            Calificar Servicio
          </button>
          
          <!-- Modal para calificación -->
          <VModal :show="showRatingModal" @close="closeModal">
            <service-rating
              :reservation-id="selectedReservation.idReservacion"
              :hotel-id="selectedReservation.hotelId"
              :room-id="selectedReservation.id"
              @rating-submitted="(data) => handleRatingSubmitted(data)"
            />
          </VModal>
        </div>
      </div>
    </div>
  </div>
</template>