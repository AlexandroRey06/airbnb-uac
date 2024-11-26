import { initializeApp } from 'firebase/app';
import { getFirestore } from 'firebase/firestore';

const firebaseConfig = {
    apiKey: "AIzaSyBhE503otBm7INySqzN1RKrO3yRvD0qJR0",
    authDomain: "hotels-b4dc8.firebaseapp.com",
    projectId: "hotels-b4dc8",
    storageBucket: "hotels-b4dc8.firebasestorage.app",
    messagingSenderId: "193379853132",
    appId: "1:193379853132:web:5c27a2871e869b26d665e9"
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

export { db };