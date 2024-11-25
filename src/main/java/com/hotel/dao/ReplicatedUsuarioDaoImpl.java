package com.hotel.dao;

import com.hotel.model.Usuario;
import java.util.List;

public class ReplicatedUsuarioDaoImpl implements UsuarioDao {
    private final UsuarioDao primaryDao;
    private final UsuarioDao secondaryDao;

    public ReplicatedUsuarioDaoImpl(UsuarioDao primaryDao, UsuarioDao secondaryDao) {
        this.primaryDao = primaryDao;
        this.secondaryDao = secondaryDao;
    }

    @Override
    public void crear(Usuario usuario) {
        // Crear en ambas bases de datos
        primaryDao.crear(usuario);
        try {
            secondaryDao.crear(usuario);
        } catch (Exception e) {
            // Log del error pero permitir continuar
            e.printStackTrace();
        }
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        try {
            return primaryDao.obtenerPorId(id);
        } catch (Exception e) {
            // Si falla la primaria, intentar con la secundaria
            return secondaryDao.obtenerPorId(id);
        }
    }

    @Override
    public Usuario obtenerPorEmail(String email) {
        try {
            return primaryDao.obtenerPorEmail(email);
        } catch (Exception e) {
            // Si falla la primaria, intentar con la secundaria
            return secondaryDao.obtenerPorEmail(email);
        }
    }

    @Override
    public List<Usuario> obtenerTodos() {
        try {
            return primaryDao.obtenerTodos();
        } catch (Exception e) {
            // Si falla la primaria, intentar con la secundaria
            return secondaryDao.obtenerTodos();
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        // Actualizar en ambas bases de datos
        primaryDao.actualizar(usuario);
        try {
            secondaryDao.actualizar(usuario);
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