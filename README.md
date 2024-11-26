```markdown
# Sistema de Gestión de Hoteles

## Descripción General
Sistema web desarrollado para la gestión de hoteles y reservas, que implementa autenticación de usuarios con diferentes roles. El sistema permite administrar hoteles, habitaciones y reservas, con una interfaz intuitiva tanto para administradores como para usuarios finales.

## Tecnologías Utilizadas

### Backend
- Java 11
- Jakarta Servlets
- PostgreSQL (usuarios y roles)
- Firebase (hoteles y reservas)
- JWT para autenticación
- BCrypt para encriptación de contraseñas

### Frontend
- Vue.js 3
- Tailwind CSS
- Pinia (manejo de estado)
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

### Firebase

```javascript
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
    │           imagen: string
    │       }
    │   ]
```

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

## Configuración del Entorno

### Variables de Entorno Backend
```env
DB_URL=jdbc:postgresql://localhost:5432/hotel_db
DB_USER=postgres
DB_PASSWORD=
JWT_SECRET=your_jwt_secret
```

### Variables de Entorno Frontend
```env
VITE_API_URL=http://localhost:8080/api
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
- Visualización de estadísticas

### Usuarios
- Búsqueda de hoteles
- Filtrado por ciudad y características
- Visualización de detalles de habitaciones
- Gestión de reservas

## Contribución
1. Fork del repositorio
2. Crear rama para nueva funcionalidad (`git checkout -b feature/nueva-funcionalidad`)
3. Commit de cambios (`git commit -am 'Añadir nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## Autor
Alex Rey

## Licencia
Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles
```