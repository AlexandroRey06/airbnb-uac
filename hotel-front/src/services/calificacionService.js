import { db } from '@/config/firebase'
import { 
    collection, 
    addDoc, 
    query, 
    where, 
    getDocs,
    Timestamp,
    orderBy 
} from 'firebase/firestore'

export const calificacionService = {
    async create(calificacionData) {
        try {
            // Usando el token JWT que ya tenemos
            const calificacionRef = await addDoc(collection(db, 'calificaciones'), {
                ...calificacionData,
                fecha: Timestamp.now(),
            });
            
            return {
                id: calificacionRef.id,
                ...calificacionData,
                fecha: new Date()
            };
        } catch (error) {
            console.error('Error completo:', error);
            throw new Error('Error al crear calificación: ' + error.message);
        }
    },

    async getByHotel(hotelId) {
        try {
            const q = query(
                collection(db, 'calificaciones'),
                where('hotelId', '==', hotelId),
                orderBy('fecha', 'desc')
            );
            const querySnapshot = await getDocs(q);
            return querySnapshot.docs.map(doc => ({
                id: doc.id,
                ...doc.data(),
                fecha: doc.data().fecha.toDate()
            }));
        } catch (error) {
            throw new Error('Error al obtener calificaciones: ' + error.message);
        }
    }
}