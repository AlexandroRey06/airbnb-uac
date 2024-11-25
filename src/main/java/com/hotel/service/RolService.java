package com.hotel.service;

import com.hotel.dto.RolDTO;
import java.util.List;

public interface RolService {
    RolDTO crear(RolDTO rolDTO);
    RolDTO obtenerPorId(Long id);
    List<RolDTO> obtenerTodos();
    RolDTO actualizar(Long id, RolDTO rolDTO);
    void eliminar(Long id);
}