<template>
  <div class="space-y-4">
      <!-- Preview -->
      <div class="relative w-full h-48 bg-gray-100 rounded-lg overflow-hidden">
          <img
              v-if="preview || modelValue"
              :src="preview || modelValue"
              class="w-full h-full object-cover"
              alt="Preview"
          />
          <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
              <span>Sin imagen</span>
          </div>
          
          <!-- Loading overlay -->
          <div v-if="loading" class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
              <div class="text-white">Subiendo...</div>
          </div>
      </div>

      <!-- Input y botones -->
      <div class="flex space-x-4">
          <input
              type="file"
              ref="fileInput"
              class="hidden"
              accept="image/*"
              @change="handleFileChange"
          />
          
          <button
              type="button"
              @click="$refs.fileInput.click()"
              class="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
              :disabled="loading"
          >
              Seleccionar imagen
          </button>
          
          <button
              v-if="preview || modelValue"
              type="button"
              @click="removeImage"
              class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
              :disabled="loading"
          >
              Eliminar
          </button>
      </div>

      <!-- Error message -->
      <p v-if="error" class="text-red-600 text-sm">{{ error }}</p>
  </div>
</template>

<script>
import { ref } from 'vue'
import { imageService } from '@/services/imageService'

export default {
  name: 'ImageUpload',
  props: {
      modelValue: {
          type: String,
          default: ''
      }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
      const loading = ref(false)
      const error = ref('')
      const preview = ref('')
      const fileInput = ref(null)

      const handleFileChange = async (event) => {
          const file = event.target.files[0]
          if (!file) return

          if (!file.type.startsWith('image/')) {
              error.value = 'Por favor selecciona una imagen válida'
              return
          }

          if (file.size > 5 * 1024 * 1024) { // 5MB
              error.value = 'La imagen debe ser menor a 5MB'
              return
          }

          try {
              loading.value = true
              error.value = ''

              preview.value = URL.createObjectURL(file)

              const imageUrl = await imageService.upload(file)
              emit('update:modelValue', imageUrl)

              URL.revokeObjectURL(preview.value)
              preview.value = ''
          } catch (err) {
              error.value = err.message
              preview.value = ''
          } finally {
              loading.value = false
          }
      }

      const removeImage = () => {
          emit('update:modelValue', '')
          preview.value = ''
          if (fileInput.value) {
              fileInput.value.value = ''
          }
      }

      return {
          loading,
          error,
          preview,
          fileInput,
          handleFileChange,
          removeImage
      }
  }
}
</script>