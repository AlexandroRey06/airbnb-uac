// src/services/imageService.js
const IMGBB_API_KEY = '57007065d04fb46ed42f384cf8d0b954'; // Registrarte en imgbb.com para obtener una

export const imageService = {
    async upload(file) {
        try {
            const formData = new FormData();
            formData.append('image', file);

            const response = await fetch(`https://api.imgbb.com/1/upload?key=${IMGBB_API_KEY}`, {
                method: 'POST',
                body: formData
            });

            const data = await response.json();
            if (data.success) {
                return data.data.url;
            } else {
                throw new Error('Error al subir imagen');
            }
        } catch (error) {
            console.error('Error:', error);
            throw new Error('Error al subir la imagen');
        }
    }
};