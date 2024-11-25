package com.hotel.dao;

import com.hotel.model.Usuario;
import com.hotel.exception.DatabaseException;
import com.hotel.exception.EntityNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements UsuarioDao {
    private final Connection connection;

    public UsuarioDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void crear(Usuario usuario) {
        try {
            String sql = "INSERT INTO usuarios (email, password, nombre, apellido, telefono, rol_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, usuario.getEmail());
                statement.setString(2, usuario.getPassword());
                statement.setString(3, usuario.getNombre());
                statement.setString(4, usuario.getApellido());
                statement.setString(5, usuario.getTelefono());
                statement.setLong(6, usuario.getRolId());
                
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DatabaseException("No se pudo crear el usuario");
                }

                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getLong(1));
                } else {
                    throw new DatabaseException("No se pudo obtener el ID generado para el usuario");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al crear usuario", e);
        }
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        try {
            String sql = "SELECT * FROM usuarios WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
                throw new EntityNotFoundException("Usuario", id);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener usuario", e);
        }
    }

    @Override
    public Usuario obtenerPorEmail(String email) {
        try {
            String sql = "SELECT * FROM usuarios WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener usuario por email", e);
        }
    }

    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            String sql = "SELECT * FROM usuarios";
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }
            return usuarios;
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener usuarios", e);
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        try {
            String sql = "UPDATE usuarios SET email = ?, nombre = ?, apellido = ?, telefono = ?, rol_id = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, usuario.getEmail());
                statement.setString(2, usuario.getNombre());
                statement.setString(3, usuario.getApellido());
                statement.setString(4, usuario.getTelefono());
                statement.setLong(5, usuario.getRolId());
                statement.setLong(6, usuario.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar usuario", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            String sql = "DELETE FROM usuarios WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar usuario", e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setRolId(rs.getLong("rol_id"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            usuario.setCreatedAt(createdAt);
        }
        return usuario;
    }
}