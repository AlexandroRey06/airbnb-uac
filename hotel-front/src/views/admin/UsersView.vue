<template>
    <div class="min-h-screen bg-gray-100">
      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div class="px-4 py-6 sm:px-0">
          <div class="mb-4">
            <h1 class="text-2xl font-semibold text-gray-900">
              Administración de Usuarios
            </h1>
            <p class="mt-1 text-sm text-gray-600">
              Gestiona los usuarios del sistema
            </p>
          </div>
          
          <UserList />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import UserList from '@/components/admin/UserList.vue'
  
  export default {
    name: 'UsersView',
    components: {
      UserList
    }
  }
  </script>