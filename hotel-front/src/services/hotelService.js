import { db } from '@/config/firebase'
import {
    collection,
    doc,
    getDocs,
    getDoc,
    addDoc,
    updateDoc
} from 'firebase/firestore'

const COLLECTION_NAME = 'hoteles'


export const hotelService = {
    async create(hotelData) {
        try {
            const docData = {
                ...hotelData,
                createdAt: new Date().toISOString(),
                habitaciones: [],
                activo: true
            };

            const docRef = await addDoc(collection(db, COLLECTION_NAME), docData);
            
            return {
                id: docRef.id,
                ...docData
            };
        } catch (error) {
            console.error('Error específico:', error);
            if (error.code === 'permission-denied') {
                throw new Error('No tienes permisos para crear hoteles');
            }
            throw new Error('Error al crear el hotel: ' + error.message);
        }
    },

    async getAll() {
        try {
            const querySnapshot = await getDocs(collection(db, COLLECTION_NAME));
            return querySnapshot.docs
                .filter(doc => doc.data().activo) // Solo hoteles activos
                .map(doc => ({
                    id: doc.id,
                    ...doc.data()
                }));
        } catch (error) {
            console.error('Error específico:', error);
            throw new Error('Error al obtener los hoteles: ' + error.message);
        }
    },

    async getById(id) {
        try {
            const docRef = doc(db, COLLECTION_NAME, id);
            const docSnap = await getDoc(docRef);
            
            if (!docSnap.exists()) {
                throw new Error('Hotel no encontrado');
            }

            const data = docSnap.data();
            if (!data.activo) {
                throw new Error('Hotel no disponible');
            }

            if (!data.habitaciones) {
                data.habitaciones = [];
            }

            return {
                id: docSnap.id,
                ...data
            };
        } catch (error) {
            console.error('Error específico:', error);
            throw new Error('Error al obtener el hotel: ' + error.message); // Cambiado a singular
        }
    },

    async update(id, hotelData) {
        try {
            const docRef = doc(db, COLLECTION_NAME, id);
            const updateData = {
                ...hotelData,
                updatedAt: new Date().toISOString()
            };
            
            await updateDoc(docRef, updateData);
            return {
                id,
                ...updateData
            };
        } catch (error) {
            console.error('Error específico:', error);
            throw new Error('Error al actualizar el hotel: ' + error.message);
        }
    },

    async delete(id) {
        try {
            // Soft delete
            const docRef = doc(db, COLLECTION_NAME, id);
            await updateDoc(docRef, {
                activo: false,
                deletedAt: new Date().toISOString()
            });
        } catch (error) {
            console.error('Error específico:', error);
            throw new Error('Error al eliminar el hotel: ' + error.message);
        }
    },

    async getHabitacionesByHotel(hotelId) {
        const docRef = doc(db, COLLECTION_NAME, hotelId)
        const docSnap = await getDoc(docRef)
        if (docSnap.exists()) {
            return docSnap.data().habitaciones || []
        }
        return []
    },

    async addHabitacion(hotelId, habitacionData) {
        try {
            const docRef = doc(db, COLLECTION_NAME, hotelId)
            const docSnap = await getDoc(docRef)
            if (!docSnap.exists()) {
                throw new Error('Hotel no encontrado')
            }

            const hotel = docSnap.data()
            const habitaciones = Array.isArray(hotel.habitaciones) ? hotel.habitaciones : []
            
            const nuevaHabitacion = {
                id: Date.now().toString(),
                ...habitacionData
            }
            
            habitaciones.push(nuevaHabitacion)
            await updateDoc(docRef, { habitaciones })
            return habitaciones
        } catch (error) {
            console.error('Error al agregar habitación:', error)
            throw new Error('Error al agregar la habitación: ' + error.message)
        }
    },

    async updateHabitacion(hotelId, habitacionId, habitacionData) {
        try {
            const docRef = doc(db, COLLECTION_NAME, hotelId)
            const docSnap = await getDoc(docRef)
            if (!docSnap.exists()) {
                throw new Error('Hotel no encontrado')
            }

            const hotel = docSnap.data()
            const habitaciones = Array.isArray(hotel.habitaciones) ? hotel.habitaciones : []
            
            const index = habitaciones.findIndex(h => h && h.id === habitacionId)
            if (index === -1) {
                throw new Error('Habitación no encontrada')
            }

            habitaciones[index] = {
                ...habitaciones[index],
                ...habitacionData
            }
            
            await updateDoc(docRef, { habitaciones })
            return habitaciones
        } catch (error) {
            console.error('Error al actualizar habitación:', error)
            throw new Error('Error al actualizar la habitación: ' + error.message)
        }
    }
}