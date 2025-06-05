package com.example.cooperadora_escuela.models;

public class Activities {
    private String titulo;
    private String descripcion;

    public Activities(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
