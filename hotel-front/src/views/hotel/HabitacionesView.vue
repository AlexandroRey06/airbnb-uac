<template>
    <div class="min-h-screen bg-gray-100">
      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div class="px-4 py-6 sm:px-0">
          <HabitacionList :hotel-id="$route.params.id" />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import HabitacionList from '@/components/hotel/HabitacionList.vue'
  
  export default {
    name: 'HabitacionesView',
    components: {
      HabitacionList
    }
  }
  </script>