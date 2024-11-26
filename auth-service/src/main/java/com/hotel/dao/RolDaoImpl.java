package com.hotel.dao;

import com.hotel.model.Rol;
import com.hotel.exception.DatabaseException;
import com.hotel.exception.EntityNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDaoImpl implements RolDao {
    private final Connection connection;

    public RolDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void crear(Rol rol) {
        try {
            String sql = "INSERT INTO roles (nombre) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, rol.getNombre());
                statement.executeUpdate();

                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    rol.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al crear rol", e);
        }
    }

    @Override
    public Rol obtenerPorId(Long id) {
        try {
            String sql = "SELECT * FROM roles WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return mapearRol(rs);
                }
                throw new EntityNotFoundException("Rol", id);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener rol", e);
        }
    }

    @Override
    public List<Rol> obtenerTodos() {
        List<Rol> roles = new ArrayList<>();
        try {
            String sql = "SELECT * FROM roles";
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    roles.add(mapearRol(rs));
                }
            }
            return roles;
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener roles", e);
        }
    }

    @Override
    public void actualizar(Rol rol) {
        try {
            String sql = "UPDATE roles SET nombre = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, rol.getNombre());
                statement.setLong(2, rol.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar rol", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            String sql = "DELETE FROM roles WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar rol", e);
        }
    }

    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getLong("id"));
        rol.setNombre(rs.getString("nombre"));
        return rol;
    }
}