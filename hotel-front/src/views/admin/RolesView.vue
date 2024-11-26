<template>
    <div class="min-h-screen bg-gray-100">
      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div class="px-4 py-6 sm:px-0">
          <div class="mb-4">
            <h1 class="text-2xl font-semibold text-gray-900">
              Administración de Roles
            </h1>
            <p class="mt-1 text-sm text-gray-600">
              Gestiona los roles del sistema
            </p>
          </div>
          
          <RoleList />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import RoleList from '@/components/admin/RoleList.vue'
  
  export default {
    name: 'RolesView',
    components: {
      RoleList
    }
  }
  </script>