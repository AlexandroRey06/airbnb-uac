<template>
    <div>
      <RegisterForm />
    </div>
  </template>
  
  <script>
  import RegisterForm from '@/components/auth/RegisterForm.vue'
  
  export default {
    name: 'RegisterView',
    components: {
      RegisterForm
    }
  }
  </script>