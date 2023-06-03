package com.example.myapplication;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InicioActivity extends AppCompatActivity {

    TextView txtSaludo , txtSaldo, txtISaldo;
    Button btnCrearPerfil, btnRecargarMonedero;
    ImageView imagenCliente;

    String idCliente;
    RequestQueue requestQueue;

    RecyclerView recyclerView;

    private static final String urlConsultarCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_cliente.php?id_cliente=";
    private static final String urlConsultarPerfil = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_perfil.php?id_cliente=";


    ActivityResultLauncher resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        txtSaludo = (TextView) findViewById(R.id.txtSaludo);
        txtSaldo = (TextView) findViewById(R.id.txtSaldo);
        txtISaldo = (TextView) findViewById(R.id.txtISaldo);

        imagenCliente = (ImageView) findViewById(R.id.imagen_cliente_inicio);
        btnCrearPerfil = (Button) findViewById(R.id.btn_crear_perfil);
        btnRecargarMonedero = (Button) findViewById(R.id.btn_recargar_monedero);
        recyclerView = findViewById(R.id.recycler_perfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        idCliente = preferences.getString("idCliente", "");

        consultarUsuario();
        consultarPerfiles();

        btnCrearPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistroPerfilActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        btnRecargarMonedero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MonederoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){


        switch (item.getItemId()){

            case R.id.Mn1:
                Intent actInicio = new Intent(InicioActivity.this,ModificacionClienteActivity.class);
                startActivity(actInicio);
                finish();
                return true;

            case R.id.Mn2:
                Intent intent = new Intent(getApplicationContext(), MonederoActivity.class);
                intent.putExtra("iSaldo","hola");
                System.out.println(txtISaldo.getText());
                intent.putExtra("idCliente",idCliente);
                startActivity(intent);
                finish();
                return true;

            case R.id.Mn3:

                return true;

            case R.id.MnOp1:

                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:36.692540, -4.440447?z=18"));
                startActivity(intent1);
                return true;

            case R.id.MnOp2:

                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_SUBJECT,"Problema con la aplicación");
                intent2.putExtra(Intent.EXTRA_TEXT,"No consigo acceder a mi monedero");
                intent2.putExtra(Intent.EXTRA_EMAIL, new String[]{"soporte@g.micafeteria.es"});
                startActivity(intent2);

                return true;

            case R.id.Mn4:

                SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
                preferences.edit().clear().commit();
                Intent intent3 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent3);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void consultarUsuario() {
        String URL = urlConsultarCliente + idCliente;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String dni,  nombre,  apellidos,  direccion,  telefono,  email,  password,  imagen, saldo;
                        try {
                            dni = response.getString("dni_cliente");
                            nombre = response.getString("nombre_cliente");
                            apellidos = response.getString("apellidos_cliente");
                            direccion = response.getString("direccion_cliente");
                            telefono = response.getString("telefono_cliente");
                            email = response.getString("email_cliente");
                            password = response.getString("password_cliente");
                            imagen = response.getString("imagen_cliente");
                            saldo = response.getString("monedero_cliente");

                            txtSaludo.setText("¡Hola " + nombre + "!");
                            txtSaldo.setText("Su saldo actual es de : " + saldo + "€");

                            txtISaldo.setText(saldo);


                            try {
                                Picasso.get().load(imagen)
                                        .error(R.drawable.usuario)
                                        .into(imagenCliente);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InicioActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void consultarPerfiles() {
        String URL = urlConsultarPerfil + idCliente;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<Perfil> listaPerfiles = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject joPerfil = response.getJSONObject(i);
                                int idPerfil = joPerfil.getInt("id_perfil");
                                int idCliente = joPerfil.getInt("id_cliente");
                                String nieDni = joPerfil.getString("nie_dni_perfil");
                                String nombre = joPerfil.getString("nombre_perfil");
                                String apellidos = joPerfil.getString("apellidos_perfil");
                                String imagen = joPerfil.getString("imagen_perfil");
                                int idCafeteria = joPerfil.getInt("id_cafeteria");
                                String nombreCafeteria = joPerfil.getString("nombre_cafeteria");
                                Perfil perfil = new Perfil(idPerfil, idCliente, nieDni, nombre, apellidos, imagen, idCafeteria, nombreCafeteria);
                                listaPerfiles.add(perfil);
                            }

                            PerfilAdapter adapter = new PerfilAdapter(InicioActivity.this,listaPerfiles);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(InicioActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

}