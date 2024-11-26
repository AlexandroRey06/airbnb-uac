package com.hotel.service;

import com.hotel.dto.UsuarioDTO;
import com.hotel.dto.UsuarioRegistroDTO;
import java.util.List;

public interface UsuarioService {
    UsuarioDTO crear(UsuarioRegistroDTO registroDTO);
    UsuarioDTO obtenerPorId(Long id);
    List<UsuarioDTO> obtenerTodos();
    UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO);
    void eliminar(Long id);
    UsuarioDTO obtenerPorEmail(String email);
}