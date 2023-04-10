package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {

    private List<Perfil> listaPerfiles;
    private Context contexto;

    public PerfilAdapter(Context contexto, List<Perfil> listaPerfiles) {
        this.contexto = contexto;
        this.listaPerfiles = listaPerfiles;
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.perfil_item, parent, false);
        return new PerfilViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        Perfil perfil = listaPerfiles.get(position);
        holder.idPerfil.setText(String.valueOf(perfil.getIdPerfil()));
        holder.nombreApellidos.setText(perfil.getNombrePerfil() + " " + perfil.getApellidosPerfil());
        holder.cafeteria.setText(perfil.getNombreCafeteria());
        holder.idCafeteria.setText(String.valueOf(perfil.getIdCafeteria()));

        try {
            Picasso.get().load(perfil.getImagenPerfil())
                    .error(R.drawable.usuario)
                    .into(holder.imagenPerfil);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexto, QrActivity.class);
                intent.putExtra("idPerfil", perfil.getIdPerfil());
                intent.putExtra("nombreApellidos", perfil.getNombrePerfil() + " " + perfil.getApellidosPerfil());

                contexto.startActivity(intent);
                ((Activity) contexto).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPerfiles.size();
    }

    public static class PerfilViewHolder extends RecyclerView.ViewHolder {
        private TextView idPerfil, nombreApellidos, cafeteria, idCafeteria;
        private ImageView imagenPerfil;
        private Button btnQR;

        public PerfilViewHolder(View vista) {
            super(vista);
            idPerfil = vista.findViewById(R.id.textViewIdPerfil);
            nombreApellidos = vista.findViewById(R.id.textViewNombreCompleto);
            cafeteria = vista.findViewById(R.id.textViewCafeteria);
            idCafeteria = vista.findViewById(R.id.textViewIdCafeteria);
            imagenPerfil = vista.findViewById(R.id.imageViewPerfil);
            btnQR = vista.findViewById(R.id.btnQR);
        }
    }
}
