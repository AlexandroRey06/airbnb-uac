<template>
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">

        <!-- Loading state -->
        <div v-if="hotelStore.loading" class="flex justify-center items-center py-8">
          <div class="text-gray-500">Cargando hoteles...</div>
        </div>
  
        <!-- Error state -->
        <div v-else-if="hotelStore.error" class="text-red-600 text-center py-8">
          {{ hotelStore.error }}
        </div>
  
        <!-- Content -->
        <div v-else>
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-2xl font-bold text-gray-900">Hoteles</h2>
            <button 
              @click="showModal('create')"
              class="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
            >
              Nuevo Hotel
            </button>
          </div>
  
          <!-- Grid de hoteles -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div v-for="hotel in hotelStore.hotels" :key="hotel.id" 
                 class="bg-white rounded-lg shadow-md overflow-hidden">
              <img 
                :src="hotel.imagen || 'https://collezione.starhotels.com/assets/uploads/Starhotels-Collezione/Hotel-d-inghilterra/GALLERY/hir-exterior-2023.jpg'" 
                :alt="hotel.nombre"
                class="w-full h-48 object-cover"
              >
              <div class="p-4">
                <h3 class="text-lg font-semibold text-gray-900">{{ hotel.nombre }}</h3>
                <p class="text-gray-600 mt-1">{{ hotel.ciudad }}</p>
                <p class="text-sm text-gray-500 mt-2">{{ hotel.descripcion }}</p>
                <div class="mt-4 flex justify-between items-center">
                  <span class="text-indigo-600 font-semibold">
                    {{ hotel.habitaciones?.length || 0 }} habitaciones
                  </span>
                  <div class="space-x-2">
                    <button 
                      @click="showModal('edit', hotel)"
                      class="text-indigo-600 hover:text-indigo-900"
                    >
                      Editar
                    </button>
                    <button 
                      @click="confirmDelete(hotel.id)"
                      class="text-red-600 hover:text-red-900"
                    >
                      Eliminar
                    </button>
                    <router-link 
                        :to="`/hotels/${hotel.id}/habitaciones`"
                        class="text-green-600 hover:text-green-900 mr-2"
                        >
                        Habitaciones
                    </router-link>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- Modal para crear/editar hotel -->
          <Modal 
            :show="showModalFlag"
            :title="isEditing ? 'Editar Hotel' : 'Nuevo Hotel'"
            @close="closeModal"
          >
            <form @submit.prevent="handleSubmit" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">Nombre</label>
                <input 
                  v-model="formData.nombre" 
                  type="text" 
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                >
              </div>
  
              <div>
                <label class="block text-sm font-medium text-gray-700">Ciudad</label>
                <input 
                  v-model="formData.ciudad" 
                  type="text" 
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                >
              </div>
  
              <div>
                <label class="block text-sm font-medium text-gray-700">Descripción</label>
                <textarea 
                  v-model="formData.descripcion" 
                  rows="3" 
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                ></textarea>
              </div>
  
              <div>
                <label class="block text-sm font-medium text-gray-700">Dirección</label>
                <input 
                  v-model="formData.direccion" 
                  type="text" 
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                >
              </div>
  
              <div>
                    <label class="block text-sm font-medium text-gray-700">Imagen</label>
                    <ImageUpload 
                        v-model="formData.imagen"
                        :path="`hoteles/${currentHotelId || 'nuevo'}`"
                    />
                </div>
  
              <div class="flex justify-end space-x-3 pt-4">
                <button 
                  type="button"
                  @click="closeModal"
                  class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button 
                  type="submit"
                  class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
                  :disabled="hotelStore.loading"
                >
                  {{ hotelStore.loading ? 'Guardando...' : 'Guardar' }}
                </button>
              </div>
            </form>
          </Modal>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { useHotelStore } from '@/stores/hotels'
  import Modal from '@/components/common/VModal.vue'
  import ImageUpload from '@/components/common/ImageUpload.vue'
  
  export default {
    name: 'HotelList',
    components: {
      Modal,
      ImageUpload
    },
    setup() {
      const hotelStore = useHotelStore()
      return { hotelStore }
    },
    data() {
      return {
        showModalFlag: false,
        isEditing: false,
        formData: {
          nombre: '',
          ciudad: '',
          descripcion: '',
          direccion: '',
          imagen: ''
        },
        currentHotelId: null
      }
    },
    async created() {
      await this.loadHotels()
    },
    methods: {
      async loadHotels() {
        await this.hotelStore.fetchHotels()
      },
      showModal(action, hotel = null) {
        this.isEditing = action === 'edit'
        if (hotel) {
          this.currentHotelId = hotel.id
          this.formData = { ...hotel }
        } else {
          this.currentHotelId = null
          this.formData = {
            nombre: '',
            ciudad: '',
            descripcion: '',
            direccion: '',
            imagen: ''
          }
        }
        this.showModalFlag = true
      },
      closeModal() {
        this.showModalFlag = false
        this.isEditing = false
        this.currentHotelId = null
        this.formData = {
          nombre: '',
          ciudad: '',
          descripcion: '',
          direccion: '',
          imagen: ''
        }
      },
      async handleSubmit() {
        try {
          if (this.isEditing) {
            await this.hotelStore.updateHotel(this.currentHotelId, this.formData)
          } else {
            await this.hotelStore.createHotel(this.formData)
          }
          this.closeModal()
        } catch (error) {
          console.error('Error al guardar hotel:', error)
        }
      },
      async confirmDelete(id) {
        if (confirm('¿Está seguro de eliminar este hotel?')) {
          try {
            await this.hotelStore.deleteHotel(id)
          } catch (error) {
            console.error('Error al eliminar hotel:', error)
          }
        }
      }
    }
  }
  </script>