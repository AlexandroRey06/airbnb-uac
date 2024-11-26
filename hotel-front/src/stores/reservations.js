import { defineStore } from 'pinia'
import { reservationService } from '@/services/reservationService'
import { useAuthStore } from '@/stores/auth' // Para obtener el usuario actual

export const useReservationStore = defineStore('reservations', {
    state: () => ({
        userReservations: [],
        currentReservation: null,
        loading: false,
        error: null
    }),

    getters: {
        // Obtener reservaciones por estado
        getReservationsByStatus: (state) => (status) => {
            return state.userReservations.filter(res => res.estado === status)
        },

        // Obtener una reservación específica
        getReservationById: (state) => (id) => {
            return state.userReservations.find(res => res.id === id)
        },

        // Obtener total de reservaciones
        totalReservations: (state) => state.userReservations.length,

        // Obtener reservaciones activas (no canceladas ni completadas)
        activeReservations: (state) => {
            return state.userReservations.filter(res => 
                res.estado !== 'cancelada' && res.estado !== 'completada'
            )
        }
    },

    actions: {
        // Cargar reservaciones del usuario
        async fetchUserReservations() {
            this.loading = true
            try {
                const authStore = useAuthStore()
                if (!authStore.user) {
                    throw new Error('Usuario no autenticado')
                }

                const reservations = await reservationService.getUserReservations(
                    authStore.user.id // O el campo que uses para identificar al usuario
                )
                this.userReservations = reservations
            } catch (error) {
                console.error('Error fetching reservations:', error)
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        },

        // Crear nueva reservación
        async createReservation(reservationData) {
            this.loading = true
            try {
                const authStore = useAuthStore()
                if (!authStore.user) {
                    throw new Error('Usuario no autenticado')
                }

                const reservation = await reservationService.create({
                    ...reservationData,
                    userId: authStore.user.id // O el campo que uses para identificar al usuario
                })
                
                this.userReservations.push(reservation)
                return reservation
            } catch (error) {
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        },

        // Cancelar reservación
        async cancelReservation(id) {
            this.loading = true
            try {
                await reservationService.cancel(id)
                const index = this.userReservations.findIndex(r => r.id === id)
                if (index !== -1) {
                    this.userReservations[index].estado = 'cancelada'
                    this.userReservations[index].canceledAt = new Date().toISOString()
                }
            } catch (error) {
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        },

        // Actualizar reservación
        async updateReservation(id, updateData) {
            this.loading = true
            try {
                const updatedReservation = await reservationService.update(id, updateData)
                const index = this.userReservations.findIndex(r => r.id === id)
                if (index !== -1) {
                    this.userReservations[index] = {
                        ...this.userReservations[index],
                        ...updatedReservation
                    }
                }
                return updatedReservation
            } catch (error) {
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        },

        // Limpiar errores
        clearError() {
            this.error = null
        },

        // Resetear store
        resetStore() {
            this.userReservations = []
            this.currentReservation = null
            this.loading = false
            this.error = null
        }
    }
})