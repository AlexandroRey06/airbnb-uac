package com.hotel.dto;

// Principio de Sustitución de Liskov
// La clase UsuarioRegistroDTO extiende de UsuarioDTO sin romper el comportamiento
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private Long rolId;

    // Constructor vacío
    public UsuarioDTO() {}

    // Constructor con campos
    public UsuarioDTO(Long id, String email, String nombre, String apellido, String telefono, Long rolId) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.rolId = rolId;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }
}