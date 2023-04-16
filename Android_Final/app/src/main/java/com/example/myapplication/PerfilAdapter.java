package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {

    private List<Perfil> listaPerfiles;
    private Context contexto;
    RequestQueue requestQueue;

    private static final String urlEliminarPerfil = "https://micafeteriaapp.000webhostapp.com/android_mysql/eliminar_perfil.php";


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
                //((Activity) contexto).finish();
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexto, EncuestaActivity.class);
                intent.putExtra("idPerfil", perfil.getIdPerfil());
                intent.putExtra("idCafeteria", perfil.getIdCafeteria());
                intent.putExtra("nombrePerfil", perfil.getNombrePerfil());

                contexto.startActivity(intent);
                //((Activity) contexto).finish();
            }
        });


        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((Activity) contexto).registerForContextMenu(v);

                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.menu_contextual_modificar_perfil);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.MnModificar:
                                Intent intent = new Intent(contexto, ModificacionPerfilActivity.class);
                                intent.putExtra("idPerfil", perfil.getIdPerfil());
                                intent.putExtra("idCafeteria", perfil.getIdCafeteria());
                                intent.putExtra("dni", perfil.getNieDniPerfil());
                                intent.putExtra("nombrePerfil", perfil.getNombrePerfil());
                                intent.putExtra("apellidos", perfil.getApellidosPerfil());
                                intent.putExtra("imagenPerfil", perfil.getImagenPerfil());

                                contexto.startActivity(intent);
                                //((Activity)contexto).finish();
                                return true;
                            case R.id.MnEliminar:
                                eliminarPerfil(perfil.getIdPerfil());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
                return true;
            }
        });

    }

    private void eliminarPerfil(int idPerfil) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlEliminarPerfil,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(contexto, "Perfil eliminado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(contexto.getApplicationContext(), InicioActivity.class);
                        contexto.startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(contexto, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id_perfil", String.valueOf(idPerfil));

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return listaPerfiles.size();
    }


    public class PerfilViewHolder extends RecyclerView.ViewHolder {
        private TextView idPerfil, nombreApellidos, cafeteria, idCafeteria;
        private ImageView imagenPerfil;
        private Button btnQR;
        private CardView cardView;

        public PerfilViewHolder(View vista) {
            super(vista);
            idPerfil = vista.findViewById(R.id.textViewIdPerfil);
            nombreApellidos = vista.findViewById(R.id.textViewNombreCompleto);
            cafeteria = vista.findViewById(R.id.textViewCafeteria);
            idCafeteria = vista.findViewById(R.id.textViewIdCafeteria);
            imagenPerfil = vista.findViewById(R.id.imageViewPerfil);
            btnQR = vista.findViewById(R.id.btnQR);
            cardView = vista.findViewById(R.id.cardViewPerfil);

            requestQueue = Volley.newRequestQueue(contexto);
        }
    }

}
