package com.hotel.dao;

import com.hotel.model.Usuario;
import java.util.List;

//**Open/Closed Principle (Principio Abierto/Cerrado)
// La interfaz DAO permite extensión sin modificación

// **Principio de Segregación de Interfaces
// Interfaces específicas en lugar de una grande
// Métodos específicos para Usuario
public interface UsuarioDao {
    void crear(Usuario usuario);
    Usuario obtenerPorId(Long id);
    Usuario obtenerPorEmail(String email);
    List<Usuario> obtenerTodos();
    void actualizar(Usuario usuario);
    void eliminar(Long id);
}