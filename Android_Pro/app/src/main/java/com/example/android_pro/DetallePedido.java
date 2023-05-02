package com.example.android_pro;

import android.os.Parcel;
import android.os.Parcelable;

public class DetallePedido implements Parcelable {
    private int idDetallePedido;
    private int cantidadDetallePedido;
    private int idProducto;
    private int idPedido;

    public DetallePedido(int cantidadDetallePedido, int idProducto) {
        this.cantidadDetallePedido = cantidadDetallePedido;
        this.idProducto = idProducto;
    }

    protected DetallePedido(Parcel in) {
        idDetallePedido = in.readInt();
        cantidadDetallePedido = in.readInt();
        idProducto = in.readInt();
        idPedido = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idDetallePedido);
        dest.writeInt(cantidadDetallePedido);
        dest.writeInt(idProducto);
        dest.writeInt(idPedido);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DetallePedido> CREATOR = new Creator<DetallePedido>() {
        @Override
        public DetallePedido createFromParcel(Parcel in) {
            return new DetallePedido(in);
        }

        @Override
        public DetallePedido[] newArray(int size) {
            return new DetallePedido[size];
        }
    };

    public int getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(int idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public int getCantidadDetallePedido() {
        return cantidadDetallePedido;
    }

    public void setCantidadDetallePedido(int cantidadDetallePedido) {
        this.cantidadDetallePedido = cantidadDetallePedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
}
