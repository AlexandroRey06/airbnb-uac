package com.hotel.dao;

import com.hotel.model.Rol;
import java.util.List;

public class ReplicatedRolDaoImpl implements RolDao {
    private final RolDao primaryDao;
    private final RolDao secondaryDao;

    public ReplicatedRolDaoImpl(RolDao primaryDao, RolDao secondaryDao) {
        this.primaryDao = primaryDao;
        this.secondaryDao = secondaryDao;
    }

    @Override
    public void crear(Rol rol) {
        // Crear en ambas bases de datos
        primaryDao.crear(rol);
        try {
            secondaryDao.crear(rol);
        } catch (Exception e) {
            // Log del error pero permitir continuar
            e.printStackTrace();
        }
    }

    @Override
    public Rol obtenerPorId(Long id) {
        try {
            return primaryDao.obtenerPorId(id);
        } catch (Exception e) {
            // Si falla la primaria, intentar con la secundaria
            return secondaryDao.obtenerPorId(id);
        }
    }

    @Override
    public List<Rol> obtenerTodos() {
        try {
            return primaryDao.obtenerTodos();
        } catch (Exception e) {
            // Si falla la primaria, intentar con la secundaria
            return secondaryDao.obtenerTodos();
        }
    }

    @Override
    public void actualizar(Rol rol) {
        // Actualizar en ambas bases de datos
        primaryDao.actualizar(rol);
        try {
            secondaryDao.actualizar(rol);
        } catch (Exception e) {
            // Log del error pero permitir continuar
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) {
        // Eliminar en ambas bases de datos
        primaryDao.eliminar(id);
        try {
            secondaryDao.eliminar(id);
        } catch (Exception e) {
            // Log del error pero permitir continuar
            e.printStackTrace();
        }
    }
}