package com.example.android_pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Pedido> listaPedidos;

    public PedidoAdapter(Context context, ArrayList<Pedido> listaPedidos) {
        this.context = context;
        this.listaPedidos = listaPedidos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);

        holder.idPedidoTextView.setText(String.valueOf(pedido.getIdPedido()));
        holder.fechaPedidoTextView.setText(pedido.getFechaPedido());
        holder.detallesPedidoTextView.setText(pedido.getDetallesPedido());
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView idPedidoTextView;
        public TextView fechaPedidoTextView;
        public TextView detallesPedidoTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            idPedidoTextView = itemView.findViewById(R.id.idPedidoTextView);
            fechaPedidoTextView = itemView.findViewById(R.id.fechaPedidoTextView);
            detallesPedidoTextView = itemView.findViewById(R.id.detallesPedidoTextView);
        }
    }
}
