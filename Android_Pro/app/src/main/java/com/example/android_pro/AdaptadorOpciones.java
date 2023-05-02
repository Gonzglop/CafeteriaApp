package com.example.android_pro;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class OpcionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private TextView titulo;
    private TextView precio;
    private ImageView icono;

    public OpcionViewHolder(View itemView){
        super(itemView);
        titulo = (TextView) itemView.findViewById(R.id.LblTitulo);
        precio = (TextView) itemView.findViewById(R.id.LblPrecio);
        icono = (ImageView) itemView.findViewById(R.id.icono);
        icono.setOnClickListener(this);
    }
    public void bindOpcion(Opcion t){
        titulo.setText(t.getTitulo());
        precio.setText(t.getPrecio());
        icono.setImageResource(t.getIcono());
    }

    @Override
    public void onClick(View view) {
        mostrarMenuContextual(view);
    }

    private void mostrarMenuContextual(View view){
        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
        popupMenu.inflate(R.menu.menu_contextual2);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }
}



class AdaptadorOpciones extends RecyclerView.Adapter<OpcionViewHolder> implements View.OnClickListener {
    private ArrayList<Opcion> datos;
    private View.OnClickListener listener;

    AdaptadorOpciones(ArrayList<Opcion> datos) {
        this.datos = datos;
    }

    @Override
    public OpcionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_interno, viewGroup, false);
        itemView.setOnClickListener(this);
        OpcionViewHolder ovh = new OpcionViewHolder(itemView);
        return ovh;
    }

    @Override
    public void onBindViewHolder(OpcionViewHolder viewHolder, int pos) {
        Opcion item = datos.get(pos);
        viewHolder.bindOpcion(item);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick(view);
    }
}