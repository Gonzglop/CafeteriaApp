package com.example.android_pro;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Producto> listaProductos;
    private ArrayList<Producto> listaProductosMostrados;
    private ArrayList<DetallePedido> listaDetallesPedidos;
    private int numProductosSeleccionados;
    private double precioTotal;

    public ProductoAdapter(Context context, ArrayList<Producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
        this.listaProductosMostrados = new ArrayList<>(listaProductos);
        this.listaDetallesPedidos = new ArrayList<>();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = listaProductosMostrados.get(position);
        holder.nombreProducto.setText(producto.getNombre());
        holder.descripcionProducto.setText(producto.getDescripcion());
        holder.precioProducto.setText(String.valueOf(producto.getPrecio()));
        //  cargar la imagen del producto con una librería como Picasso

        holder.checkboxProducto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.cantidadProducto.setEnabled(true);
                holder.cantidadProducto.setText("1");

                DetallePedido detallePedido = new DetallePedido(1, producto.getIdProducto());
                listaDetallesPedidos.add(detallePedido);
            } else {
                holder.cantidadProducto.setText("0");
                holder.cantidadProducto.setEnabled(false);

                for (DetallePedido detallePedido : listaDetallesPedidos) {
                    if (detallePedido.getIdProducto() == producto.getIdProducto()) {
                        listaDetallesPedidos.remove(detallePedido);
                        break;
                    }
                }
            }
        });

        holder.cantidadProducto.addTextChangedListener(new TextWatcher() {
            int cantidad ;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String cantidadTexto = String.valueOf(holder.cantidadProducto.getText());
                if (!cantidadTexto.isEmpty()) {
                    cantidad = Integer.parseInt(cantidadTexto);
                }else{
                    cantidad = 0;
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                int nuevaCantidad;
                String cantidadTexto = String.valueOf(holder.cantidadProducto.getText());
                if (!cantidadTexto.isEmpty()) {
                    nuevaCantidad = Integer.parseInt(cantidadTexto);

                    if (cantidad != nuevaCantidad) {
                            cantidad = nuevaCantidad - cantidad;
                            numProductosSeleccionados += cantidad;
                            precioTotal += cantidad * producto.getPrecio();
                        if (nuevaCantidad == 0){
                            holder.checkboxProducto.setChecked(false);
                        }
                    }
                }

                Intent intent = new Intent("ProductosSeleccionados");
                intent.putExtra("precioTotal", precioTotal);
                intent.putExtra("numProductosSeleccionados", numProductosSeleccionados);
                intent.putParcelableArrayListExtra("listaDetallesPedidos", (ArrayList<? extends Parcelable>) listaDetallesPedidos);

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaProductosMostrados.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreProducto;
        private TextView descripcionProducto;
        private TextView precioProducto;
        private CheckBox checkboxProducto;
        private EditText cantidadProducto;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.textViewNombreProducto);
            descripcionProducto = itemView.findViewById(R.id.textViewDescripcionProducto);
            precioProducto = itemView.findViewById(R.id.textViewPrecioProducto);
            checkboxProducto = itemView.findViewById(R.id.checkboxProducto);
            cantidadProducto = itemView.findViewById(R.id.editTextCantidad);
        }
    }
}

