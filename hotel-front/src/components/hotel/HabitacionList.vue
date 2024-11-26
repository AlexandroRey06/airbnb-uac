<template>
    <div class="max-w-7xl mx-auto py-6">
      <!-- Header con información del hotel -->
      <div class="mb-6">
        <div class="flex justify-between items-center">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">{{ hotel?.nombre }}</h2>
            <p class="text-gray-600">{{ hotel?.ciudad }} - {{ hotel?.direccion }}</p>
          </div>
          <button 
            @click="showModal('create')"
            class="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
          >
            Nueva Habitación
          </button>
        </div>
      </div>
  
      <!-- Loading state -->
      <div v-if="hotelStore.loading" class="flex justify-center items-center py-8">
        <div class="text-gray-500">Cargando habitaciones...</div>
      </div>
  
      <!-- Error state -->
      <div v-else-if="hotelStore.error" class="text-red-600 text-center py-8">
        {{ hotelStore.error }}
      </div>
  
      <!-- Grid de habitaciones -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="habitacion in habitaciones" :key="habitacion.id" 
             class="bg-white rounded-lg shadow-md overflow-hidden">
          <img 
            :src="habitacion.imagen || 'https://www.usatoday.com/gcdn/-mm-/05b227ad5b8ad4e9dcb53af4f31d7fbdb7fa901b/c=0-64-2119-1259/local/-/media/USATODAY/USATODAY/2014/08/13/1407953244000-177513283.jpg'" 
            :alt="habitacion.numero"
            class="w-full h-48 object-cover"
          >
          <div class="p-4">
            <div class="flex justify-between items-start">
              <div>
                <h3 class="text-lg font-semibold text-gray-900">
                  Habitación {{ habitacion.numero }}
                </h3>
                <p class="text-sm text-gray-600">{{ habitacion.tipo }}</p>
              </div>
              <div class="text-right">
                <span class="text-lg font-bold text-indigo-600">
                  ${{ habitacion.precio }}
                </span>
                <p class="text-sm text-gray-500">por noche</p>
              </div>
            </div>
            
            <div class="mt-4">
              <div class="flex items-center text-sm text-gray-500">
                <span class="mr-2">{{ habitacion.capacidad }} personas</span>
                <span class="mx-2">•</span>
                <span>{{ habitacion.camas }} camas</span>
              </div>
              <p class="mt-2 text-sm text-gray-600">{{ habitacion.descripcion }}</p>
            </div>
            <div class="mt-4 flex items-center justify-between">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    :class="habitacion.estado == 'disponible' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                {{ habitacion.estado == 'disponible' ? 'Disponible' : 'No disponible' }}
              </span>
              <div class="space-x-2">
                <button 
                  @click="showModal('edit', habitacion)"
                  class="text-indigo-600 hover:text-indigo-900"
                >
                  Editar
                </button>
                <button 
                  @click="confirmDelete(habitacion.id)"
                  class="text-red-600 hover:text-red-900"
                >
                  Eliminar
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Modal para crear/editar habitación -->
      <Modal 
        :show="showModalFlag"
        :title="isEditing ? 'Editar Habitación' : 'Nueva Habitación'"
        @close="closeModal"
      >
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">Número</label>
            <input 
              v-model="formData.numero" 
              type="text" 
              class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            >
          </div>
  
          <div>
            <label class="block text-sm font-medium text-gray-700">Tipo</label>
            <select 
              v-model="formData.tipo"
              class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            >
              <option value="Individual">Individual</option>
              <option value="Doble">Doble</option>
              <option value="Suite">Suite</option>
              <option value="Familiar">Familiar</option>
            </select>
          </div>
  
          <div>
            <label class="block text-sm font-medium text-gray-700">Precio por noche</label>
            <input 
              v-model.number="formData.precio" 
              type="number" 
              min="0"
              step="0.01"
              class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            >
          </div>
  
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Capacidad</label>
              <input 
                v-model.number="formData.capacidad" 
                type="number" 
                min="1"
                class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                required
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Camas</label>
              <input 
                v-model.number="formData.camas" 
                type="number" 
                min="1"
                class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                required
              >
            </div>
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
            <label class="block text-sm font-medium text-gray-700">Imagen</label>
            <ImageUpload 
                v-model="formData.imagen"
                :path="`hoteles/${hotelId}/habitaciones/${currentHabitacionId || 'nuevo'}`"
            />
            </div>
  
          <div class="flex items-center">
            <input 
              type="checkbox" 
              v-model="formData.disponible"
              class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
            >
            <label class="ml-2 block text-sm text-gray-900">
              Disponible
            </label>
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
              :disabled="loading"
            >
              {{ loading ? 'Guardando...' : 'Guardar' }}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  </template>
  
  <script>
  import { useHotelStore } from '@/stores/hotels'
  import Modal from '@/components/common/VModal.vue'
  import ImageUpload from '@/components/common/ImageUpload.vue'
  
  export default {
    name: 'HabitacionList',
    components: {
      Modal,
      ImageUpload
    },
    props: {
      hotelId: {
        type: String,
        required: true
      }
    },
    setup() {
      const hotelStore = useHotelStore()
      return { hotelStore }
    },
    data() {
      return {
        showModalFlag: false,
        isEditing: false,
        loading: false,
        formData: {
          numero: '',
          tipo: 'Individual',
          precio: 0,
          capacidad: 1,
          camas: 1,
          descripcion: '',
          imagen: '',
          disponible: true
        },
        currentHabitacionId: null
      }
    },
    computed: {
      hotel() {
        return this.hotelStore.currentHotel
      },
      habitaciones() {
        return this.hotel?.habitaciones || []
      }
    },
    async created() {
      await this.loadHotel()
    },
    methods: {
      async loadHotel() {
        await this.hotelStore.fetchHotelById(this.hotelId)
      },
      showModal(action, habitacion = null) {
        this.isEditing = action === 'edit'
        if (habitacion) {
          this.currentHabitacionId = habitacion.id
          this.formData = { ...habitacion }
        } else {
          this.currentHabitacionId = null
          this.formData = {
            numero: '',
            tipo: 'Individual',
            precio: 0,
            capacidad: 1,
            camas: 1,
            descripcion: '',
            imagen: '',
            disponible: true
          }
        }
        this.showModalFlag = true
      },
      closeModal() {
        this.showModalFlag = false
        this.isEditing = false
        this.currentHabitacionId = null
        this.formData = {
          numero: '',
          tipo: 'Individual',
          precio: 0,
          capacidad: 1,
          camas: 1,
          descripcion: '',
          imagen: '',
          disponible: true
        }
      },
      async handleSubmit() {
        try {
          this.loading = true
          if (this.isEditing) {
            await this.hotelStore.updateHabitacion(
              this.hotelId,
              this.currentHabitacionId,
              this.formData
            )
          } else {
            await this.hotelStore.addHabitacion(this.hotelId, {...this.formData, estado: 'disponible'})
          }
          this.closeModal()
        } catch (error) {
          console.error('Error al guardar habitación:', error)
        } finally {
          this.loading = false
        }
      },
      async confirmDelete(id) {
        if (confirm('¿Está seguro de eliminar esta habitación?')) {
          try {
            await this.hotelStore.deleteHabitacion(this.hotelId, id)
          } catch (error) {
            console.error('Error al eliminar habitación:', error)
          }
        }
      }
    }
  }
  </script>