<template>
  <div>
    <Navbar />
    <main>
      <router-view />
    </main>
  </div>
</template>

<script>
import Navbar from '@/components/common/VNavbar.vue'

export default {
  name: 'App',
  components: {
    Navbar
  }
}
</script>