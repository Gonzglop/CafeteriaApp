package com.example.android_pro;

public class Pedido {
    private int idPedido;
    private String fechaPedido;
    private String detallesPedido;

    public Pedido(int idPedido, String fechaPedido, String detallesPedido) {
        this.idPedido = idPedido;
        this.fechaPedido = fechaPedido;
        this.detallesPedido = detallesPedido;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getDetallesPedido() {
        return detallesPedido;
    }

    public void setDetallesPedido(String detallesPedido) {
        this.detallesPedido = detallesPedido;
    }
}
