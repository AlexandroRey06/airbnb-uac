package com.hotel.dao;

import com.hotel.model.Rol;
import java.util.List;

//**Open/Closed Principle (Principio Abierto/Cerrado)
// La interfaz DAO permite extensión sin modificación

// **Principio de Segregación de Interfaces
// Interfaces específicas en lugar de una grande
// Métodos específicos para Roles
public interface RolDao {
   void crear(Rol rol);
   Rol obtenerPorId(Long id);
   List<Rol> obtenerTodos();
   void actualizar(Rol rol);
   void eliminar(Long id);
}