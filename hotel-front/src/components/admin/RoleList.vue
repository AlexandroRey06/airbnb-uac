<template>
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- Loading state -->
        <div v-if="rolesStore.loading" class="flex justify-center items-center py-8">
          <div class="text-gray-500">Cargando roles...</div>
        </div>
  
        <!-- Error state -->
        <div v-else-if="rolesStore.error" class="text-red-600 text-center py-8">
          {{ rolesStore.error }}
        </div>
  
        <!-- Content -->
        <div v-else>
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-2xl font-bold text-gray-900">Roles</h2>
            <button 
              @click="openModal()"
              class="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
            >
              Nuevo Rol
            </button>
          </div>
  
          <div class="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    ID
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Nombre
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="role in rolesStore.roles" :key="role.id">
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ role.id }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ role.nombre }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button 
                      @click="openModal(role)"
                      class="text-indigo-600 hover:text-indigo-900 mr-2"
                    >
                      Editar
                    </button>
                    <button 
                      @click="confirmDelete(role.id)"
                      class="text-red-600 hover:text-red-900"
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
  
        <!-- Modal -->
        <Modal 
          :show="showModal" 
          :title="editingRole ? 'Editar Rol' : 'Nuevo Rol'"
          @close="closeModal"
        >
          <form @submit.prevent="handleSubmit">
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700">Nombre</label>
              <input 
                v-model="formData.nombre" 
                type="text" 
                required 
                class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                :disabled="rolesStore.loading"
              >
            </div>
            <div class="flex justify-end space-x-3">
              <button 
                type="button"
                @click="closeModal"
                class="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300"
                :disabled="rolesStore.loading"
              >
                Cancelar
              </button>
              <button 
                type="submit"
                class="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
                :disabled="rolesStore.loading"
              >
                {{ rolesStore.loading ? 'Guardando...' : 'Guardar' }}
              </button>
            </div>
          </form>
        </Modal>
      </div>
    </div>
  </template>
  
  <script>
  import { useRolesStore } from '@/stores/roles'
  import Modal from '@/components/common/VModal.vue'
  
  export default {
    name: 'RoleList',
    components: {
      Modal
    },
    setup() {
      const rolesStore = useRolesStore()
      return { rolesStore }
    },
    data() {
      return {
        showModal: false,
        editingRole: null,
        formData: {
          nombre: ''
        }
      }
    },
    async created() {
      await this.loadRoles()
    },
    methods: {
      async loadRoles() {
        await this.rolesStore.fetchRoles()
      },
      openModal(role = null) {
        this.editingRole = role
        if (role) {
          this.formData.nombre = role.nombre
        } else {
          this.formData.nombre = ''
        }
        this.showModal = true
      },
      closeModal() {
        this.showModal = false
        this.editingRole = null
        this.formData.nombre = ''
      },
      async handleSubmit() {
        try {
          if (this.editingRole) {
            await this.rolesStore.updateRole(this.editingRole.id, this.formData)
          } else {
            await this.rolesStore.createRole(this.formData)
          }
          this.closeModal()
        } catch (error) {
          // El error ya está manejado en el store
          console.error('Save role error:', error)
        }
      },
      async confirmDelete(id) {
        if (confirm('¿Está seguro de eliminar este rol?')) {
          try {
            await this.rolesStore.deleteRole(id)
          } catch (error) {
            // El error ya está manejado en el store
            console.error('Delete role error:', error)
          }
        }
      }
    }
  }
  </script>