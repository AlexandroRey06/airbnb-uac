<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
      <div class="max-w-md w-full px-6 py-12 bg-white shadow-md rounded-lg">
        <div class="text-center">
          <h1 class="text-6xl font-bold text-gray-400">404</h1>
          <h2 class="mt-4 text-2xl font-semibold text-gray-900">Página no encontrada</h2>
          <p class="mt-2 text-gray-600">La página que estás buscando no existe o ha sido movida.</p>
          <div class="mt-6">
            <router-link 
              to="/" 
              class="text-indigo-600 hover:text-indigo-500"
            >
              Volver al inicio →
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'NotFoundView'
  }
  </script>