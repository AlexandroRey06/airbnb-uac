markdown
# Sistema de Gestión de Hoteles

## Descripción General

Sistema web integral desarrollado para la gestión hotelera, diseñado para optimizar las operaciones diarias de hoteles y facilitar el proceso de reservas. El sistema implementa una robusta autenticación de usuarios con diferentes niveles de acceso y roles específicos, garantizando la seguridad y el control adecuado de las operaciones.

### Funcionalidad Principal

El sistema se divide en dos interfaces principales:

#### Panel Administrativo
- **Gestión de Hoteles**: Permite crear y administrar múltiples propiedades hoteleras, incluyendo detalles como ubicación, servicios.
- **Control de Habitaciones**: Administración detallada de habitaciones, incluyendo tipos, tarifas, disponibilidad.
- **Gestión de Usuarios**: Administración completa de personal y sus niveles de acceso.
- **Gestión de Reservaciones**: Control centralizado de todas las reservas.

#### Portal de Usuarios
- **Búsqueda Inteligente**: Sistema de búsqueda avanzada con filtros por nombre y ubicación.
- **Reservas en Línea**: Proceso simplificado de reservación con confirmación inmediata.
- **Gestión de Cuenta**: Perfil de usuario con historial de reservas.
- **Sistema de Calificaciones**: Permite a los usuarios compartir sus experiencias y calificar los servicios.

### Características Destacadas

- **Arquitectura Distribuida**: Sistema basado en microservicios para mayor escalabilidad y mantenimiento.
- **Interfaz Responsiva**: Diseño adaptable a diferentes dispositivos y tamaños de pantalla.
- **Alta Disponibilidad**: Implementación en la nube con redundancia y balanceo de carga.
- **Seguridad Robusta**: 
  - Autenticación mediante JWT
  - Encriptación de datos sensibles
  - Control de acceso basado en roles
  - Protección contra ataques comunes

### Beneficios Clave

- **Para Administradores**:
  - Centralización de operaciones hoteleras
  - Reducción de errores en reservas
  - Optimización de la ocupación

- **Para Usuarios**:
  - Proceso de reserva simplificado
  - Acceso a información detallada
  - Gestión autónoma de reservaciones

Esta solución integral está pensada para adaptarse tanto a hoteles independientes como a cadenas hoteleras, con capacidad de escalar según las necesidades del negocio.

## Tecnologías Utilizadas

### Backend
- Java 11
- tomcat7 Servlets
- PostgreSQL (usuarios y roles)
- JWT para autenticación
- BCrypt para encriptación de contraseñas

### Frontend
- Vue.js 3
- Tailwind CSS
- Pinia (manejo de estado)
- Firebase (hoteles y reservas), Host
- Vue Router
- Axios

## Arquitectura

### Patrones de Diseño Implementados

#### 1. Patrón DAO (Data Access Object)
- `UsuarioDao.java` (interfaz)
- `UsuarioDaoImpl.java` (implementación)
- `RolDao.java` (interfaz)
- `RolDaoImpl.java` (implementación)

#### 2. Patrón DTO (Data Transfer Object)
- `UsuarioDTO.java`
- `RolDTO.java`
- `UsuarioRegistroDTO.java`
- `LoginDTO.java`

#### 3. Patrón Factory
- `DaoFactory.java` para creación de DAOs
- `DatabaseConnection.java` para conexión a base de datos

#### 4. Patrón MVC
- Modelos: `Usuario.java`, `Rol.java`
- Vistas: Componentes Vue.js
- Controladores: `UsuarioController.java`, `RolController.java`, `AuthController.java`

#### 5. Patrón Singleton
- `DatabaseConnection.java`: Gestión de conexiones a base de datos
- `DaoFactory.java`: Fábrica de objetos DAO
- `AuthenticationManager.java`: Gestión de autenticación
- `ConfigurationManager.java`: Gestión de configuraciones

### Principios SOLID Implementados

#### 1. Single Responsibility Principle (SRP)
Cada clase tiene una única responsabilidad:
- Los DAOs manejan exclusivamente el acceso a datos
- Los DTOs solo se encargan de transportar datos
- Los Controllers solo gestionan peticiones HTTP
- DatabaseConnection se encarga únicamente de las conexiones a la base de datos

#### 2. Open/Closed Principle (OCP)
El código está abierto a extensión pero cerrado a modificación:
- Las interfaces DAO permiten agregar nuevas implementaciones sin modificar el código existente
- El patrón Factory Method permite añadir nuevos tipos de DAOs sin cambiar el código actual

## Estructura de Base de Datos

### PostgreSQL

```sql
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    telefono VARCHAR(20),
    rol_id INT REFERENCES roles(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Firebase Collections

#### 1. Hoteles

##### javascript

hoteles/
   ├── {hotelId}/
   │   ├── nombre: string
   │   ├── ciudad: string
   │   ├── descripcion: string
   │   ├── imagen: string
   │   ├── activo: boolean
   │   └── habitaciones: [
   │       {
   │           id: string,
   │           numero: string,
   │           tipo: string,
   │           precio: number,
   │           capacidad: number,
   │           disponible: boolean,
   │           imagen: string,
   │           estado: string,  // 'disponible', 'ocupada', 'mantenimiento'
   │           camas: number,
   │           descripcion: string
   │       }
   │   ]
reservaciones/
    ├── {reservacionId}/
    │   ├── id: string
    │   ├── idReservacion: string
    │   ├── userId: string
    │   ├── hotelId: string
    │   ├── hotelNombre: string
    │   ├── camas: number
    │   ├── capacidad: number
    │   ├── tipo: string
    │   ├── numero: string
    │   ├── precio: number
    │   ├── imagen: string
    │   ├── descripcion: string
    │   ├── estado: string  // 'pendiente', 'confirmada', 'cancelada', 'completada'
    │   ├── disponible: boolean
    │   ├── createdAt: timestamp
    │   └── canceledAt: timestamp (opcional)
reservacionesCalificadas/
    ├── {ratingId}/
    │   ├── roomId: string
    │   ├── hotelId: string
    │   ├── reservationId: string
    │   ├── rating: number
    │   ├── comment: string
    │   ├── createdAt: timestamp
    │   ├── cleanliness: number,
    │   ├── staffService: number,
    │   └── comfort: number

## API Endpoints

### Autenticación
- `POST /api/auth/login`: Iniciar sesión
- `POST /api/auth/register`: Registrar nuevo usuario
- `GET /api/auth/me`: Obtener usuario actual

### Usuarios
- `GET /api/usuarios`: Listar usuarios
- `GET /api/usuarios/{id}`: Obtener usuario específico
- `POST /api/usuarios`: Crear usuario
- `PUT /api/usuarios/{id}`: Actualizar usuario
- `DELETE /api/usuarios/{id}`: Eliminar usuario

### Roles
- `GET /api/roles`: Listar roles
- `GET /api/roles/{id}`: Obtener rol específico
- `POST /api/roles`: Crear rol
- `PUT /api/roles/{id}`: Actualizar rol
- `DELETE /api/roles/{id}`: Eliminar rol

## Seguridad

### Autenticación
- Implementación de JWT (JSON Web Tokens)
- Tokens con expiración de 24 horas
- Almacenamiento seguro en localStorage

### Encriptación
- Uso de BCrypt para hash de contraseñas
- Salt automático para mayor seguridad

### Autorización
- Control de acceso basado en roles
- Middleware de validación de JWT
- Protección de rutas según rol de usuario

## Diagramas del Sistema

### Diagrama de Arquitectura
El siguiente diagrama muestra la arquitectura completa del sistema, incluyendo la comunicación entre el frontend, los diferentes servicios y las bases de datos:

![Diagrama de Arquitectura](/docs/images/Dg.jpeg)

### Diagrama de Clases
El diagrama de clases representa la estructura de las entidades principales del sistema y sus relaciones:

![Diagrama de Clases](/docs/images/Cl.jpeg)

### Diagrama de UML
El siguiente diagrama muestra el flujo de interacciones para los procesos principales del sistema:

![Diagrama de UML](/docs/images/uml.jpeg)

## Configuración del Entorno

### Variables de Entorno Backend
```env
DB_URL_PRIMARY=dbc:postgresql://hotel-db.c982saawuip8.us-east-1.rds.amazonaws.com:5432/hotel_db
DB_USER=postgres
DB_PASSWORD=
JWT_SECRET=your_jwt_secret

DB_URL_SECONDARY=jdbc:jdbc:postgresql://hotel-db-backup.c982saawuip8.us-east-1.rds.amazonaws.com:5432/hotel_db_backup
DB_USER=postgres
DB_PASSWORD=
JWT_SECRET=your_jwt_secret
```

### Variables de Entorno Frontend
```env
VITE_API_URL=https://api-system-app-byfvg9bkf8c9f5g9.canadacentral-01.azurewebsites.net/api
VITE_FIREBASE_API_KEY=your_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_domain
VITE_FIREBASE_PROJECT_ID=your_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_storage_bucket
VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
VITE_FIREBASE_APP_ID=your_app_id
```

## Instrucciones de Instalación

### Backend
1. Clonar el repositorio
2. Configurar base de datos PostgreSQL
3. Configurar variables de entorno
4. Ejecutar:
```bash
mvn clean install
mvn tomcat7:run
```

### Frontend
1. Navegar a la carpeta del frontend
2. Instalar dependencias:
```bash
npm install
```
3. Configurar variables de entorno
4. Ejecutar en desarrollo:
```bash
npm run dev
```
5. Compilar para producción:
```bash
npm run build
```

## Funcionalidades Principales

### Administradores
- Gestión completa de usuarios
- Gestión de roles
- Administración de hoteles y habitaciones

### Usuarios
- Búsqueda de hoteles
- Filtrado por ciudad y nombre
- Visualización de detalles de habitaciones
- Gestión de reservas

## Autor
Alex Rey
```