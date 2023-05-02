package com.example.android_pro;

public class Perfil {
    private int idPerfil;
    private int idCliente;
    private String nieDniPerfil;
    private String nombrePerfil;
    private String apellidosPerfil;
    private String imagenPerfil;
    private int idCafeteria;
    private String nombreCafeteria;


    public Perfil(int idPerfil, int idCliente, String nieDniPerfil, String nombrePerfil, String apellidosPerfil, String imagenPerfil, int idCafeteria,String nombreCafeteria) {
        this.idPerfil = idPerfil;
        this.idCliente = idCliente;
        this.nieDniPerfil = nieDniPerfil;
        this.nombrePerfil = nombrePerfil;
        this.apellidosPerfil = apellidosPerfil;
        this.imagenPerfil = imagenPerfil;
        this.idCafeteria = idCafeteria;
        this.nombreCafeteria = nombreCafeteria;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNieDniPerfil() {
        return nieDniPerfil;
    }

    public void setNieDniPerfil(String nieDniPerfil) {
        this.nieDniPerfil = nieDniPerfil;
    }

    public String getNombrePerfil() {
        return nombrePerfil;
    }

    public void setNombrePerfil(String nombrePerfil) {
        this.nombrePerfil = nombrePerfil;
    }

    public String getApellidosPerfil() {
        return apellidosPerfil;
    }

    public void setApellidosPerfil(String apellidosPerfil) {
        this.apellidosPerfil = apellidosPerfil;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public int getIdCafeteria() {
        return idCafeteria;
    }

    public void setIdCafeteria(int idCafeteria) {
        this.idCafeteria = idCafeteria;
    }

    public String getNombreCafeteria() {
        return nombreCafeteria;
    }

    public void setNombreCafeteria(String nombreCafeteria) {
        this.nombreCafeteria = nombreCafeteria;
    }
}
