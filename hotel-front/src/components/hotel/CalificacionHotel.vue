<template>
    <div class="bg-white p-4 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">Calificar Hotel</h3>
        
        <!-- Promedio general -->
        <div class="mb-6 text-center">
            <div class="text-3xl font-bold text-yellow-400">
                {{ calificacionStore.promedio.toFixed(1) }} ★
            </div>
            <div class="text-sm text-gray-500">
                {{ calificacionStore.calificaciones.length }} calificaciones
            </div>
        </div>

        <!-- Formulario de calificación -->
        <div v-if="authStore.isAuthenticated" class="border-t pt-4">
            <!-- Estrellas para calificación -->
            <div class="flex items-center justify-center mb-4">
                <template v-for="n in 5" :key="n">
                    <button 
                        @click="rating = n"
                        class="text-2xl focus:outline-none transition-colors duration-200"
                        :class="n <= rating ? 'text-yellow-400' : 'text-gray-300 hover:text-yellow-200'"
                    >
                        ★
                    </button>
                </template>
            </div>

            <!-- Comentario -->
            <div class="mb-4">
                <textarea 
                    v-model="comentario"
                    rows="3"
                    placeholder="Escribe tu comentario..."
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                ></textarea>
            </div>

            <button 
                @click="enviarCalificacion"
                class="w-full bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
                :disabled="calificacionStore.loading || !rating"
            >
                {{ calificacionStore.loading ? 'Enviando...' : 'Enviar Calificación' }}
            </button>

            <p v-if="calificacionStore.error" class="mt-2 text-sm text-red-600">
                {{ calificacionStore.error }}
            </p>
        </div>
        <div v-else class="text-center text-gray-500">
            <router-link to="/login" class="text-indigo-600 hover:text-indigo-500">
                Inicia sesión
            </router-link>
            para dejar una calificación
        </div>

        <!-- Lista de calificaciones -->
        <div class="mt-6 space-y-4">
            <div v-if="calificacionStore.loading" class="text-center text-gray-500">
                Cargando calificaciones...
            </div>
            
            <div v-else-if="calificacionStore.calificaciones.length === 0" class="text-center text-gray-500">
                Sé el primero en calificar este hotel
            </div>
            
            <div v-else v-for="cal in calificacionStore.calificaciones" :key="cal.id" 
                 class="bg-gray-50 p-4 rounded-lg">
                <div class="flex items-center justify-between">
                    <div class="text-yellow-400">
                        {{ '★'.repeat(cal.rating) }}
                        <span class="text-gray-300">{{ '★'.repeat(5 - cal.rating) }}</span>
                    </div>
                    <span class="text-sm text-gray-500">
                        {{ new Date(cal.fecha).toLocaleDateString() }}
                    </span>
                </div>
                <p class="mt-2 text-gray-700">{{ cal.comentario }}</p>
                <p class="mt-1 text-sm text-gray-500">
                    Por: {{ cal.usuarioNombre }}
                </p>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useCalificacionStore } from '@/stores/calificaciones'
import { useAuthStore } from '@/stores/auth'

export default {
    name: 'CalificacionHotel',
    props: {
        hotelId: {
            type: String,
            required: true
        }
    },
    setup(props) {
        const calificacionStore = useCalificacionStore()
        const authStore = useAuthStore()
        const rating = ref(0)
        const comentario = ref('')

        const enviarCalificacion = async () => {
            if (!authStore.isAuthenticated) {
                return
            }

            try {
                await calificacionStore.crearCalificacion({
                    hotelId: props.hotelId,
                    rating: rating.value,
                    comentario: comentario.value
                })
                
                // Limpiar formulario
                rating.value = 0
                comentario.value = ''
            } catch (error) {
                console.error('Error al enviar calificación:', error)
            }
        }

        onMounted(() => {
            calificacionStore.fetchCalificaciones(props.hotelId)
        })

        return {
            calificacionStore,
            authStore,
            rating,
            comentario,
            enviarCalificacion
        }
    }
}
</script>