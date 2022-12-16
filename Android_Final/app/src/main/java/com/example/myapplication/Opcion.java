package com.example.myapplication;

public class Opcion {
    private String titulo;
    private String precio;
    private int icono;

    public Opcion(String titulo, String precio, int icono) {
        this.setTitulo(titulo);
        this.setPrecio(precio);
        this.setIcono(icono);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }
}
    // Definimos los getters y los setters
