// src/services/reservationService.js
import { db } from '@/config/firebase'
import {
    collection,
    doc,
    getDocs,
    addDoc,
    updateDoc,
    query,
    where
} from 'firebase/firestore'

const COLLECTION_NAME = 'reservaciones'

export const reservationService = {
    // Obtener reservaciones de un usuario
    async getUserReservations(userId) {
        try {
            const reservationsRef = collection(db, 'reservaciones')
            // Usar el ID del usuario de tu backend
            const q = query(reservationsRef, where('userId', '==', userId))
            const snapshot = await getDocs(q)
            
            return snapshot.docs.map(doc => ({
                id: doc.id,
                ...doc.data()
            }))
        } catch (error) {
            console.error('Error específico:', error)
            throw new Error('Error al obtener las reservaciones: ' + error.message)
        }
    },


    // Crear una nueva reservación
    async create(reservationData) {
        try {
            const reservationsRef = collection(db, COLLECTION_NAME)
            const docRef = await addDoc(reservationsRef, {
                ...reservationData,
                createdAt: new Date().toISOString()
            })

            await updateDoc(docRef, {
                idReservacion: docRef.id
            })

            return {
                id: docRef.id,
                ...reservationData
            }
        } catch (error) {
            console.error('Error específico:', error)
            throw new Error('Error al crear la reservación: ' + error.message)
        }
    },

    // Actualizar una reservación
    async update(id, reservationData) {
        try {
            const docRef = doc(db, COLLECTION_NAME, id)
            await updateDoc(docRef, {
                ...reservationData,
                updatedAt: new Date().toISOString()
            })
            
            return {
                id,
                ...reservationData
            }
        } catch (error) {
            console.error('Error específico:', error)
            throw new Error('Error al actualizar la reservación: ' + error.message)
        }
    },

    // Cancelar una reservación
    async cancel(id) {
        try {
            const docRef = doc(db, COLLECTION_NAME, id)
            await updateDoc(docRef, {
                estado: 'cancelada',
                canceledAt: new Date().toISOString()
            })
        } catch (error) {
            console.error('Error específico:', error)
            throw new Error('Error al cancelar la reservación: ' + error.message)
        }
    },

    // Verificar disponibilidad de habitación
    async checkAvailability(hotelId, habitacionId, fechaEntrada, fechaSalida) {
        try {
            const reservationsRef = collection(db, COLLECTION_NAME)
            const q = query(
                reservationsRef,
                where('hotelId', '==', hotelId),
                where('habitacionId', '==', habitacionId),
                where('estado', '!=', 'cancelada')
            )
            
            const snapshot = await getDocs(q)
            const reservations = snapshot.docs.map(doc => doc.data())
            
            const fechaEntradaObj = new Date(fechaEntrada)
            const fechaSalidaObj = new Date(fechaSalida)
            
            return !reservations.some(res => {
                const resEntrada = new Date(res.fechaEntrada)
                const resSalida = new Date(res.fechaSalida)
                return (
                    (fechaEntradaObj <= resSalida && fechaSalidaObj >= resEntrada)
                )
            })
        } catch (error) {
            console.error('Error específico:', error)
            throw new Error('Error al verificar disponibilidad: ' + error.message)
        }
    }
}