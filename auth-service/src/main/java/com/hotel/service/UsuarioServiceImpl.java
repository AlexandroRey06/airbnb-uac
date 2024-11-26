package com.hotel.service;

import com.hotel.dao.UsuarioDao;
import com.hotel.model.Usuario;
import com.hotel.dto.UsuarioDTO;
import com.hotel.dto.UsuarioRegistroDTO;
import com.hotel.exception.ValidationException;
import com.hotel.factory.DaoFactory;
import com.hotel.exception.EntityNotFoundException;
import com.hotel.exception.DuplicateEntityException;
import com.hotel.util.PasswordUtil;
import java.util.List;
import java.util.stream.Collectors;

// **Principio de Inversión de Dependencias
// Los servicios dependen de abstracciones, no de implementaciones
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioDao usuarioDao; // Depende de la interfaz

    public UsuarioServiceImpl() {
        this.usuarioDao = DaoFactory.createReplicatedUsuarioDao();
    }

    @Override
    public UsuarioDTO crear(UsuarioRegistroDTO registroDTO) {
        validarRegistroDTO(registroDTO);
        
        // Verificar si ya existe un usuario con ese email
        if (usuarioDao.obtenerPorEmail(registroDTO.getEmail()) != null) {
            throw new DuplicateEntityException("Usuario", "email", registroDTO.getEmail());
        }

        Usuario usuario = convertirRegistroAEntidad(registroDTO);
        usuario.setPassword(PasswordUtil.hashPassword(registroDTO.getPassword()));
        
        usuarioDao.crear(usuario);
        return convertirADTO(usuario);
    }

    @Override
    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioDao.obtenerPorId(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario", id);
        }
        return convertirADTO(usuario);
    }

    @Override
    public UsuarioDTO obtenerPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("El email es requerido");
        }
        
        Usuario usuario = usuarioDao.obtenerPorEmail(email);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario con email: " + email);
        }
        return convertirADTO(usuario);
    }

    @Override
    public List<UsuarioDTO> obtenerTodos() {
        List<Usuario> usuarios = usuarioDao.obtenerTodos();
        return usuarios.stream()
                      .map(this::convertirADTO)
                      .collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO) {
        validarUsuarioDTO(usuarioDTO);
        
        Usuario usuarioExistente = usuarioDao.obtenerPorId(id);
        if (usuarioExistente == null) {
            throw new EntityNotFoundException("Usuario", id);
        }

        // Verificar si el nuevo email ya está en uso por otro usuario
        Usuario usuarioConEmail = usuarioDao.obtenerPorEmail(usuarioDTO.getEmail());
        if (usuarioConEmail != null && !usuarioConEmail.getId().equals(id)) {
            throw new DuplicateEntityException("Usuario", "email", usuarioDTO.getEmail());
        }

        Usuario usuario = convertirAEntidad(usuarioDTO);
        usuario.setId(id);
        
        // Si se está actualizando la contraseña, hashearla
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            usuario.setPassword(PasswordUtil.hashPassword(usuario.getPassword()));
        } else {
            // Mantener la contraseña existente si no se proporciona una nueva
            usuario.setPassword(usuarioExistente.getPassword());
        }

        usuarioDao.actualizar(usuario);
        return convertirADTO(usuario);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = usuarioDao.obtenerPorId(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario", id);
        }
        usuarioDao.eliminar(id);
    }

    private void validarUsuarioDTO(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            throw new ValidationException("Los datos del usuario no pueden ser nulos");
        }

        if (usuarioDTO.getEmail() == null || usuarioDTO.getEmail().trim().isEmpty()) {
            throw new ValidationException("El email es requerido");
        }

        if (!usuarioDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("El formato del email no es válido");
        }

        if (usuarioDTO.getNombre() == null || usuarioDTO.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre es requerido");
        }

        if (usuarioDTO.getApellido() == null || usuarioDTO.getApellido().trim().isEmpty()) {
            throw new ValidationException("El apellido es requerido");
        }

        if (usuarioDTO.getRolId() == null) {
            throw new ValidationException("El rol es requerido");
        }
    }

    private Usuario convertirAEntidad(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setEmail(dto.getEmail());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRolId(dto.getRolId());
        return usuario;
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        if (usuario == null) return null;
        
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setTelefono(usuario.getTelefono());
        dto.setRolId(usuario.getRolId());
        // No incluimos la contraseña en el DTO
        return dto;
    }

    private Usuario convertirRegistroAEntidad(UsuarioRegistroDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRolId(dto.getRolId() != null ? dto.getRolId() : 2L); // Rol por defecto si no se especifica
        return usuario;
    }

    private void validarRegistroDTO(UsuarioRegistroDTO registroDTO) {
        if (registroDTO == null) {
            throw new ValidationException("Los datos del usuario no pueden ser nulos");
        }

        if (registroDTO.getEmail() == null || registroDTO.getEmail().trim().isEmpty()) {
            throw new ValidationException("El email es requerido");
        }

        if (!registroDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("El formato del email no es válido");
        }

        if (registroDTO.getPassword() == null || registroDTO.getPassword().trim().isEmpty()) {
            throw new ValidationException("La contraseña es requerida");
        }

        if (registroDTO.getPassword().length() < 6) {
            throw new ValidationException("La contraseña debe tener al menos 6 caracteres");
        }

        if (registroDTO.getNombre() == null || registroDTO.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre es requerido");
        }

        if (registroDTO.getApellido() == null || registroDTO.getApellido().trim().isEmpty()) {
            throw new ValidationException("El apellido es requerido");
        }
    }
}