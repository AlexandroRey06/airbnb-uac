package com.hotel.service;

import com.hotel.dao.UsuarioDao;
import com.hotel.model.Usuario;
import com.hotel.dto.LoginDTO;
import com.hotel.dto.UsuarioRegistroDTO;
import com.hotel.exception.ValidationException;
import com.hotel.factory.DaoFactory;
import com.hotel.util.JwtUtil;
import com.hotel.util.PasswordUtil;
import java.util.HashMap;
import java.util.Map;

//Single Responsibility Principle (Principio de Responsabilidad Única)
// Solo se encarga de la lógica de autenticación
public class AuthService {
    private final UsuarioDao usuarioDao;

    public AuthService() {
        this.usuarioDao = DaoFactory.createReplicatedUsuarioDao();
    }

    public Map<String, Object> login(LoginDTO loginDTO) {
        Usuario usuario = usuarioDao.obtenerPorEmail(loginDTO.getEmail());
        
        if (usuario == null || !verificarPassword(loginDTO.getPassword(), usuario.getPassword())) {
            throw new ValidationException("Credenciales inválidas");
        }

        String token = JwtUtil.generateToken(usuario);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        usuario.setPassword(null); // No enviar la contraseña
        response.put("user", usuario);
        
        return response;
    }

    public Map<String, Object> register(UsuarioRegistroDTO registroDTO) {
        try {
            // Validaciones
            if (registroDTO.getEmail() == null || registroDTO.getEmail().trim().isEmpty()) {
                throw new ValidationException("El email es requerido");
            }
            if (registroDTO.getPassword() == null || registroDTO.getPassword().trim().isEmpty()) {
                throw new ValidationException("La contraseña es requerida");
            }
            if (registroDTO.getRolId() == null) {
                throw new ValidationException("El rol es requerido");
            }

            // Verificar si el usuario ya existe
            Usuario existingUser = usuarioDao.obtenerPorEmail(registroDTO.getEmail());
            if (existingUser != null) {
                throw new ValidationException("El email ya está registrado");
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(registroDTO.getEmail());
            nuevoUsuario.setPassword(PasswordUtil.hashPassword(registroDTO.getPassword()));
            nuevoUsuario.setNombre(registroDTO.getNombre());
            nuevoUsuario.setApellido(registroDTO.getApellido());
            nuevoUsuario.setTelefono(registroDTO.getTelefono());
            nuevoUsuario.setRolId(registroDTO.getRolId());

            usuarioDao.crear(nuevoUsuario);

            String token = JwtUtil.generateToken(nuevoUsuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            nuevoUsuario.setPassword(null); // No devolver la contraseña
            response.put("user", nuevoUsuario);
            
            return response;
        } catch (Exception e) {
            // Log detallado del error
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (e.getCause() != null) {
                errorMessage += " Causa: " + e.getCause().getMessage();
            }
            throw new ValidationException("Error al registrar usuario: " + errorMessage);
        }
    }

    public Map<String, Object> getCurrentUser(String token) {
        String email = JwtUtil.getEmailFromToken(token);
        Usuario usuario = usuarioDao.obtenerPorEmail(email);
        
        if (usuario == null) {
            throw new ValidationException("Usuario no encontrado");
        }

        Map<String, Object> response = new HashMap<>();
        usuario.setPassword(null); // No enviar la contraseña
        response.put("user", usuario);
        
        return response;
    }

    private boolean verificarPassword(String plainTextPassword, String hashedPassword) {
        return PasswordUtil.checkPassword(plainTextPassword, hashedPassword);
    }
}