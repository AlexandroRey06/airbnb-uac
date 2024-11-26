<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>Calificar Servicio</h2>
        <button class="close-button" @click="closeModal">&times;</button>
      </div>

      <div class="rating-section">
        <div class="stars-container">
          <span v-for="star in 5" 
                :key="star" 
                @click="setRating(star)"
                :class="{ 'star-filled': star <= rating }"
                class="star">★</span>
        </div>
      </div>

      <div class="comment-section">
        <h3>Comentario sobre el servicio</h3>
        <textarea 
          v-model="comment"
          placeholder="Cuéntanos sobre tu experiencia..."
          class="comment-input"
        ></textarea>
      </div>

      <div class="aspects-section">
        <h3>Califica los siguientes aspectos:</h3>
        
        <div class="aspect-item">
          <div class="aspect-label">Limpieza</div>
          <div class="aspect-stars">
            <span v-for="star in 5" 
                  :key="star"
                  @click="setCleanlinessRating(star)"
                  :class="{ 'star-filled': star <= cleanlinessRating }"
                  class="star-small">★</span>
          </div>
        </div>

        <div class="aspect-item">
          <div class="aspect-label">Atención del personal</div>
          <div class="aspect-stars">
            <span v-for="star in 5" 
                  :key="star"
                  @click="setStaffRating(star)"
                  :class="{ 'star-filled': star <= staffRating }"
                  class="star-small">★</span>
          </div>
        </div>

        <div class="aspect-item">
          <div class="aspect-label">Comodidad</div>
          <div class="aspect-stars">
            <span v-for="star in 5" 
                  :key="star"
                  @click="setComfortRating(star)"
                  :class="{ 'star-filled': star <= comfortRating }"
                  class="star-small">★</span>
          </div>
        </div>
      </div>

      <button 
        class="submit-button"
        :disabled="!isValid"
        @click="submitRating">
        Enviar Calificación
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { collection, addDoc, updateDoc, doc } from 'firebase/firestore'
import { db } from '@/config/firebase'

export default {
  name: 'ServiceRating',
  props: {
    reservationId: {
      type: String,
      required: true
    },
    hotelId: {
      type: String,
      required: true
    },
    roomId: {
      type: String,
      required: true
    }
  },
  setup(props, { emit }) {
    // Ratings
    const rating = ref(0)
    const cleanlinessRating = ref(0)
    const staffRating = ref(0)
    const comfortRating = ref(0)
    const comment = ref('')

    // Funciones para establecer ratings
    const setRating = (value) => {
      rating.value = value
    }

    const setCleanlinessRating = (value) => {
      cleanlinessRating.value = value
    }

    const setStaffRating = (value) => {
      staffRating.value = value
    }

    const setComfortRating = (value) => {
      comfortRating.value = value
    }

    // Validación
    const isValid = computed(() => 
      rating.value > 0 && 
      cleanlinessRating.value > 0 && 
      staffRating.value > 0 && 
      comfortRating.value > 0 && 
      comment.value.trim().length > 0
    )

    // Cerrar modal
    const closeModal = () => {
      emit('close')
    }

    // Enviar calificación
    const submitRating = async () => {
      try {
        const ratingData = {
          rating: rating.value,
          cleanliness: cleanlinessRating.value,
          staffService: staffRating.value,
          comfort: comfortRating.value,
          comment: comment.value,
          hotelId: props.hotelId,
          roomId: props.roomId,
          reservationId: props.reservationId,
          createdAt: new Date(),
        }

        // Guardar calificación
        await addDoc(collection(db, 'reservacionesCalificadas'), ratingData)

        // Actualizar estado de la reserva
        await updateDoc(doc(db, 'reservaciones', props.reservationId), {
          estado: 'completada'
        })
        
        emit('rating-submitted', ratingData)
        closeModal()
      } catch (error) {
        console.error('Error al enviar calificación:', error)
      }
    }

    return {
      rating,
      cleanlinessRating,
      staffRating,
      comfortRating,
      comment,
      isValid,
      setRating,
      setCleanlinessRating,
      setStaffRating,
      setComfortRating,
      closeModal,
      submitRating
    }
  }
}
</script>
  
  <style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  margin: 0;
  color: #2c3e50;
  font-weight: 600;
}

.close-button {
  border: none;
  background: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.rating-section {
  text-align: center;
  margin-bottom: 24px;
}

.stars-container {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.star {
  font-size: 32px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s ease;
}

.star-small {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s ease;
}

.star-filled {
  color: #ffd700;
}

.comment-section {
  margin-bottom: 24px;
}

.comment-section h3 {
  color: #2c3e50;
  font-size: 16px;
  margin-bottom: 8px;
}

.comment-input {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: vertical;
  font-family: inherit;
}

.aspects-section {
  margin-bottom: 24px;
}

.aspects-section h3 {
  color: #2c3e50;
  font-size: 16px;
  margin-bottom: 16px;
}

.aspect-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.aspect-label {
  color: #2c3e50;
  font-weight: 500;
}

.aspect-stars {
  display: flex;
  gap: 4px;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.submit-button:hover {
  background-color: #45a049;
}

.submit-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

@media (max-width: 480px) {
  .modal-content {
    width: 95%;
    padding: 16px;
  }

  .star {
    font-size: 28px;
  }

  .star-small {
    font-size: 20px;
  }

  .aspect-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>