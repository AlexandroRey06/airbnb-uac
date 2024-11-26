<template>
    <div class="flex items-center justify-center py-4">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      <span class="ml-2 text-gray-600">{{ message }}</span>
    </div>
  </template>
  
  <script>
  export default {
    name: 'VLoading',
    props: {
      message: {
        type: String,
        default: 'Cargando...'
      }
    }
  }
  </script>