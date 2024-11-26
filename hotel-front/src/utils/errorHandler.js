export const handleApiError = (error) => {
    if (error.response) {
        // El servidor respondió con un estado de error
        return error.response.data?.mensaje || 
               error.response.data?.error || 
               'Ha ocurrido un error en el servidor'
    } 
    if (error.request) {
        // La petición fue hecha pero no se recibió respuesta
        return 'No se pudo conectar con el servidor'
    }
    // Algo sucedió al configurar la petición
    return 'Error al procesar la solicitud'
}

export const handleError = (error) => {
    console.error('Error:', error)
    return typeof error === 'string' ? error : 'Ha ocurrido un error inesperado'
}