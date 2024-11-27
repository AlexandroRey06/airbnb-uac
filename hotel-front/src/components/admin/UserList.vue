<template>
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- Loading state -->
        <div v-if="usersStore.loading" class="flex justify-center items-center py-8">
          <div class="text-gray-500">Cargando usuarios...</div>
        </div>
  
        <!-- Error state -->
        <div v-else-if="usersStore.error" class="text-red-600 text-center py-8">
          {{ usersStore.error }}
        </div>
  
        <!-- Content -->
        <div v-else>
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-2xl font-bold text-gray-900">Usuarios</h2>
            <button 
              @click="openModal()"
              class="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
            >
              Nuevo Usuario
            </button>
          </div>
  
          <div class="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Email
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Nombre
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Rol
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="user in usersStore.users" :key="user.id">
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ user.email }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ user.nombre }} {{ user.apellido }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ getRoleName(user.rolId) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button 
                      @click="openModal(user)"
                      class="text-indigo-600 hover:text-indigo-900 mr-2"
                    >
                      Editar
                    </button>
                    <button 
                      @click="confirmDelete(user.id)"
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
          :title="editingUser ? 'Editar Usuario' : 'Nuevo Usuario'"
          @close="closeModal"
        >
          <form @submit.prevent="handleSubmit">
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">Email</label>
                <input 
                  v-model="formData.email" 
                  type="email" 
                  required 
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                  :disabled="usersStore.loading"
                >
              </div>
  
              <div>
                <label class="block text-sm font-medium text-gray-700">Nombre</label>
                <input 
                  v-model="formData.nombre" 
                  type="text" 
                  required 
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                  :disabled="usersStore.loading"
                >
              </div>
  
              <div>
                <label class="block text-sm font-medium text-gray-700">Apellido</label>
                <input 
                  v-model="formData.apellido" 
                  type="text" 
                  required 
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                  :disabled="usersStore.loading"
                >
              </div>
  
              <div>
                <label class="block text-sm font-medium text-gray-700">Teléfono</label>
                <input 
                  v-model="formData.telefono" 
                  type="tel" 
                  required 
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                  :disabled="usersStore.loading"
                >
              </div>
  
              <div>
                <label class="block text-sm font-medium text-gray-700">Rol</label>
                <select 
                  v-model="formData.rolId" 
                  required 
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                  :disabled="usersStore.loading"
                >
                  <option v-for="rol in rolesStore.roles" :key="rol.id" :value="rol.id">
                    {{ rol.nombre }}
                  </option>
                </select>
              </div>
  
              <div v-if="!editingUser">
                <label class="block text-sm font-medium text-gray-700">Contraseña</label>
                <input 
                  v-model="formData.password" 
                  type="password" 
                  :required="!editingUser"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                  :disabled="usersStore.loading"
                >
              </div>
            </div>
  
            <div class="flex justify-end space-x-3 mt-4">
              <button 
                type="button"
                @click="closeModal"
                class="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300"
                :disabled="usersStore.loading"
              >
                Cancelar
              </button>
              <button 
                type="submit"
                class="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
                :disabled="usersStore.loading"
              >
                {{ usersStore.loading ? 'Guardando...' : 'Guardar' }}
              </button>
            </div>
          </form>
        </Modal>
      </div>
    </div>
  </template>
  
  <script>
  import { useUsersStore } from '@/stores/users'
  import { useRolesStore } from '@/stores/roles'
  import Modal from '@/components/common/VModal.vue'
  
  export default {
    name: 'UserList',
    components: {
      Modal
    },
    setup() {
      const usersStore = useUsersStore()
      const rolesStore = useRolesStore()
      return { usersStore, rolesStore }
    },
    data() {
      return {
        showModal: false,
        editingUser: null,
        formData: {
          email: '',
          nombre: '',
          apellido: '',
          telefono: '',
          rolId: '',
          password: ''
        }
      }
    },
    async created() {
      await Promise.all([
        this.loadUsers(),
        this.loadRoles()
      ])
    },
    methods: {
      async loadUsers() {
        await this.usersStore.fetchUsers()
      },
      async loadRoles() {
        await this.rolesStore.fetchRoles()
      },
      getRoleName(rolId) {
        const rol = this.rolesStore.getRoleById(rolId)
        return rol ? rol.nombre : 'N/A'
      },
      openModal(user = null) {
        this.editingUser = user
        if (user) {
          this.formData = {
            ...user,
            password: '' // No incluimos la contraseña en la edición
          }
        } else {
          this.formData = {
            email: '',
            nombre: '',
            apellido: '',
            telefono: '',
            rolId: this.rolesStore.roles[0]?.id || '',
            password: ''
          }
        }
        this.showModal = true
      },
      closeModal() {
        this.showModal = false
        this.editingUser = null
        this.formData = {
          email: '',
          nombre: '',
          apellido: '',
          telefono: '',
          rolId: '',
          password: ''
        }
      },
      async handleSubmit() {
        try {
          if (this.editingUser) {
            const body = {
                "id": this.formData.id,
                "email": this.formData.email,
                "nombre": this.formData.nombre,
                "apellido": this.formData.apellido,
                "telefono": this.formData.telefono,
                "rolId": this.formData.rolId
            }
            await this.usersStore.updateUser(this.editingUser.id, body)
          } else {
            await this.usersStore.createUser(this.formData)
          }
          this.closeModal()
        } catch (error) {
          console.error('Save user error:', error)
        }
      },
      async confirmDelete(id) {
        if (confirm('¿Está seguro de eliminar este usuario?')) {
          try {
            await this.usersStore.deleteUser(id)
          } catch (error) {
            console.error('Delete user error:', error)
          }
        }
      }
    }
  }
  </script>