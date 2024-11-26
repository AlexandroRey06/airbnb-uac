<!-- src/views/ReservacionesView.vue -->
<template>
    <div class="min-h-screen bg-gray-100">
      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div class="px-4 py-6 sm:px-0">
          <!-- Header -->
          <div class="mb-6">
            <h1 class="text-2xl font-bold text-gray-900">Mis Reservaciones</h1>
          </div>
          
          <!-- Lista de reservaciones -->
          <ReservacionList />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import ReservacionList from '@/components/reservacion/ReservacionList.vue'
  // Nota: Cambié la ubicación del componente a una carpeta específica de reservaciones
  
  export default {
    name: 'ReservacionesView',
    components: {
      ReservacionList
    }
  }
  </script>