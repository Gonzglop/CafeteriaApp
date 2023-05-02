package com.example.android_pro;

public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private String ingredientes;
    private double precio;
    private String tipo;
    private String imagen;
    private int idCafeteria;

    public Producto(int idProducto, String nombre, String descripcion, String ingredientes, double precio, String tipo, String imagen, int idCafeteria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ingredientes = ingredientes;
        this.precio = precio;
        this.tipo = tipo;
        this.imagen = imagen;
        this.idCafeteria = idCafeteria;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public double getPrecio() {
        return precio;
    }

    public String getTipo() {
        return tipo;
    }

    public String getImagen() {
        return imagen;
    }

    public int getIdCafeteria() {
        return idCafeteria;
    }

}
