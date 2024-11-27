<template>
    <div class="room-ratings">
      <h3>Calificaciones del Servicio</h3>
      
      <div class="ratings-summary">
        <div class="rating-average">
          <span class="rating-number">{{ averageRating }}</span>
          <div class="stars">
            <span v-for="star in 5" :key="star"
                  :class="{ 'star-filled': star <= Math.round(averageRating) }"
            >
              ★
            </span>
          </div>
          <span class="total-ratings">{{ ratings.length }} calificaciones</span>
        </div>
  
        <div class="rating-aspects">
          <div class="aspect">
            <span>Limpieza</span>
            <div class="aspect-bar">
              <div class="aspect-fill" :style="{ width: `${cleanlinessAvg * 20}%` }"></div>
            </div>
            <span>{{ cleanlinessAvg.toFixed(1) }}</span>
          </div>
          <div class="aspect">
            <span>Atención</span>
            <div class="aspect-bar">
              <div class="aspect-fill" :style="{ width: `${staffAvg * 20}%` }"></div>
            </div>
            <span>{{ staffAvg.toFixed(1) }}</span>
          </div>
          <div class="aspect">
            <span>Comodidad</span>
            <div class="aspect-bar">
              <div class="aspect-fill" :style="{ width: `${comfortAvg * 20}%` }"></div>
            </div>
            <span>{{ comfortAvg.toFixed(1) }}</span>
          </div>
        </div>
      </div>
  
      <div class="ratings-list">
        <div v-for="rating in ratings" :key="rating.id" class="rating-item">
          <div class="rating-header">
            <div class="stars">
              <span v-for="star in 5" :key="star"
                    :class="{ 'star-filled': star <= rating.rating }">
                ★
              </span>
            </div>
            <span class="rating-date">{{ formatDate(rating.createdAt) }}</span>
          </div>
          <p class="rating-comment">{{ rating.comment }}</p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted } from 'vue'
  import { collection, query, where, getDocs } from 'firebase/firestore'
  import { db } from '@/config/firebase'
  
  export default {
    name: 'RoomRatingsView',
    props: {
      roomId: {
        type: String,
        required: true
      }
    },
    setup(props, { emit }) {
      const ratings = ref([])
  
      const loadRatings = async () => {
        try {
          console.log('Cargando calificaciones para habitación:', props.roomId); // Debug

          const q = query(
            collection(db, 'reservacionesCalificadas'), // Cambiado a tu colección correcta
            where('roomId', '==', props.roomId)
          );

          const querySnapshot = await getDocs(q);
          console.log('Calificaciones encontradas:', querySnapshot.size); // Debug

          ratings.value = querySnapshot.docs.map(doc => ({
            id: doc.id,
            ...doc.data()
          }));

          // Debug - ver las calificaciones cargadas
          console.log('Calificaciones cargadas:', ratings.value);

          // Calcular promedio si hay calificaciones
          if (ratings.value.length > 0) {
            averageRating.value = ratings.value.reduce((acc, curr) => acc + curr.rating, 0) / ratings.value.length
          }

          emit('ratings-loaded', ratings.value)
        } catch (error) {
          console.error('Error loading ratings:', error);
        }
      };
  
      // Calcular promedios
      const averageRating = computed(() => {
        if (!ratings.value.length) return 0
        const sum = ratings.value.reduce((acc, curr) => acc + curr.rating, 0)
        return (sum / ratings.value.length).toFixed(1)
      })
  
      const cleanlinessAvg = computed(() => {
        if (!ratings.value.length) return 0
        return ratings.value.reduce((acc, curr) => acc + curr.cleanliness, 0) / ratings.value.length
      })
  
      const staffAvg = computed(() => {
        if (!ratings.value.length) return 0
        return ratings.value.reduce((acc, curr) => acc + curr.staffService, 0) / ratings.value.length
      })
  
      const comfortAvg = computed(() => {
        if (!ratings.value.length) return 0
        return ratings.value.reduce((acc, curr) => acc + curr.comfort, 0) / ratings.value.length
      })
  
      const formatDate = (date) => {
        return new Date(date.seconds * 1000).toLocaleDateString()
      }
  
      onMounted(() => {
        loadRatings()
      })
  
      return {
        ratings,
        averageRating,
        cleanlinessAvg,
        staffAvg,
        comfortAvg,
        formatDate
      }
    }
  }
  </script>
  
  <style scoped>
  .room-ratings {
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    max-height: 50vh;
    overflow: auto;
  }
  
  .ratings-summary {
    display: flex;
    gap: 40px;
    margin-bottom: 30px;
  }
  
  .rating-average {
    text-align: center;
  }
  
  .rating-number {
    font-size: 48px;
    font-weight: bold;
    color: #2c3e50;
  }
  
  .stars {
    color: #eee;
    font-size: 24px;
  }
  
  .star-filled {
    color: #ffd700;
  }
  
  .total-ratings {
    color: #666;
    font-size: 14px;
  }
  
  .rating-aspects {
    flex-grow: 1;
  }
  
  .aspect {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
  }
  
  .aspect-bar {
    flex-grow: 1;
    height: 8px;
    background: #eee;
    border-radius: 4px;
    overflow: hidden;
  }
  
  .aspect-fill {
    height: 100%;
    background: #4CAF50;
    border-radius: 4px;
  }
  
  .ratings-list {
    margin-top: 20px;
  }
  
  .rating-item {
    padding: 15px;
    border-bottom: 1px solid #eee;
  }
  
  .rating-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
  }
  
  .rating-date {
    color: #666;
    font-size: 14px;
  }
  
  .rating-comment {
    color: #2c3e50;
    line-height: 1.5;
  }
  </style>