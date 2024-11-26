import { defineStore } from 'pinia'
import { calificacionService } from '@/services/calificacionService'
import { useAuthStore } from '@/stores/auth'

export const useCalificacionStore = defineStore('calificaciones', {
    state: () => ({
        calificaciones: [],
        promedio: 0,
        loading: false,
        error: null
    }),

    actions: {
        async crearCalificacion(calificacionData) {
            this.loading = true;
            this.error = null;
            try {
                const authStore = useAuthStore();
                
                if (!authStore.isAuthenticated) {
                    throw new Error('Debes iniciar sesión para calificar');
                }

                await calificacionService.create({
                    ...calificacionData,
                    usuarioId: authStore.user.id,
                    usuarioNombre: `${authStore.user.nombre} ${authStore.user.apellido}`
                });

                await this.fetchCalificaciones(calificacionData.hotelId);
            } catch (error) {
                this.error = error.message;
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async fetchCalificaciones(hotelId) {
            this.loading = true;
            this.error = null;
            try {
                const calificaciones = await calificacionService.getByHotel(hotelId);
                this.calificaciones = calificaciones;
                
                // Calcular promedio
                if (calificaciones.length > 0) {
                    const suma = calificaciones.reduce((acc, cal) => acc + cal.rating, 0);
                    this.promedio = suma / calificaciones.length;
                } else {
                    this.promedio = 0;
                }
            } catch (error) {
                this.error = error.message;
                throw error;
            } finally {
                this.loading = false;
            }
        }
    }
});