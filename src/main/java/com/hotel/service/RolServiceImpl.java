package com.hotel.service;

import com.hotel.dao.RolDao;
import com.hotel.dto.RolDTO;
import com.hotel.model.Rol;
import com.hotel.exception.ValidationException;
import com.hotel.factory.DaoFactory;

import java.util.List;
import java.util.stream.Collectors;

public class RolServiceImpl implements RolService {
    private final RolDao rolDao;

    public RolServiceImpl() {
        this.rolDao = DaoFactory.createReplicatedRolDao();
    }

    @Override
    public RolDTO crear(RolDTO rolDTO) {
        validarRolDTO(rolDTO);
        
        Rol rol = new Rol();
        rol.setNombre(rolDTO.getNombre());

        rolDao.crear(rol);
        return convertirADTO(rol);
    }

    @Override
    public RolDTO obtenerPorId(Long id) {
        Rol rol = rolDao.obtenerPorId(id);
        return convertirADTO(rol);
    }

    @Override
    public List<RolDTO> obtenerTodos() {
        return rolDao.obtenerTodos().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public RolDTO actualizar(Long id, RolDTO rolDTO) {
        if (!id.equals(rolDTO.getId())) {
            throw new ValidationException("El ID no coincide con el rol a actualizar");
        }

        Rol rol = rolDao.obtenerPorId(id);
        rol.setNombre(rolDTO.getNombre());

        rolDao.actualizar(rol);
        return convertirADTO(rol);
    }

    @Override
    public void eliminar(Long id) {
        rolDao.eliminar(id);
    }

    private RolDTO convertirADTO(Rol rol) {
        return new RolDTO(rol.getId(), rol.getNombre());
    }

    private void validarRolDTO(RolDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre del rol es requerido");
        }
    }
}