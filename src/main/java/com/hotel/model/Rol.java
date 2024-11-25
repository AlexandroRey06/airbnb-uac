package com.hotel.model;

public class Rol {
    private Long id;
    private String nombre;

    // Constructor vacío
    public Rol() {
    }

    // Constructor con campos
    public Rol(String nombre) {
        this.nombre = nombre;
    }

    // Constructor completo
    public Rol(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}