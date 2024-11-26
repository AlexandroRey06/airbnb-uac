import { defineStore } from 'pinia'
import { hotelService } from '@/services/hotelService'

export const useHotelStore = defineStore('hotels', {
    state: () => ({
        hotels: [],
        currentHotel: null,
        loading: false,
        error: null
    }),

    getters: {
        getHotelById: (state) => (id) => {
            return state.hotels.find(hotel => hotel.id === id)
        }
    },

    actions: {
        async fetchHotels() {
            this.loading = true
            try {
                const hotels = await hotelService.getAll()
                this.hotels = hotels
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        },

        async fetchHotelById(id) {
            this.loading = true
            try {
                const hotel = await hotelService.getById(id)
                this.currentHotel = hotel
                return hotel
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        },

        async createHotel(hotelData) {
            this.loading = true
            try {
                const hotel = await hotelService.create(hotelData)
                this.hotels.push(hotel)
                return hotel
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        },

        async updateHotel(id, hotelData) {
            this.loading = true
            try {
                const hotel = await hotelService.update(id, hotelData)
                const index = this.hotels.findIndex(h => h.id === id)
                if (index !== -1) {
                    this.hotels[index] = hotel
                }
                return hotel
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        },

        async deleteHotel(id) {
            this.loading = true
            try {
                await hotelService.delete(id)
                this.hotels = this.hotels.filter(h => h.id !== id)
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        },

        async addHabitacion(hotelId, habitacionData) {
            this.loading = true
            try {
              await hotelService.addHabitacion(hotelId, habitacionData)
              await this.fetchHotelById(hotelId)
            } catch (error) {
              this.error = error.message
              throw error
            } finally {
              this.loading = false
            }
          },
        
          async updateHabitacion(hotelId, habitacionId, habitacionData) {
            this.loading = true
            try {
              await hotelService.updateHabitacion(hotelId, habitacionId, habitacionData)
              await this.fetchHotelById(hotelId)
            } catch (error) {
              this.error = error.message
              throw error
            } finally {
              this.loading = false
            }
          },
        
          async deleteHabitacion(hotelId, habitacionId) {
            this.loading = true
            try {
              const hotel = this.currentHotel
              if (hotel) {
                hotel.habitaciones = hotel.habitaciones.filter(h => h.id !== habitacionId)
                await hotelService.update(hotelId, hotel)
                this.currentHotel = hotel
              }
            } catch (error) {
              this.error = error.message
              throw error
            } finally {
              this.loading = false
            }
          },

          async fetchReservations(hotelId) {
            this.loading = true
            try {
                const reservations = await hotelService.getReservations(hotelId)
                this.currentReservations = reservations
                return reservations
            } catch (error) {
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        }
          
    }
})