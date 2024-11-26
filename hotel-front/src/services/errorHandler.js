export const handleApiError = (error) => {
    if (error.response) {
        // El servidor respondió con un código de estado fuera del rango 2xx
        const message = error.response.data?.mensaje || 'Ha ocurrido un error'
        return message
    } else if (error.request) {
        // La petición fue hecha pero no se recibió respuesta
        return 'No se pudo conectar con el servidor'
    } else {
        // Algo sucedió en la configuración de la petición
        return 'Error en la petición'
    }
}