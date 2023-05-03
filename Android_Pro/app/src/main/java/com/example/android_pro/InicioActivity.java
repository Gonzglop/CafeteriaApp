package com.example.android_pro;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class InicioActivity extends AppCompatActivity {

    TextView txtSaludo , txtSaldo, txtISaldo;
    Button btnCrearPerfil, btnLectorQR;
    ImageView imagenCliente;

    String idCafeteria;
    RequestQueue requestQueue;

    RecyclerView recyclerView;

    private static final String urlConsultarCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_cafeteria.php?id_cafeteria=";

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
        btnLectorQR = (Button) findViewById(R.id.btn_lector_qr);
        recyclerView = findViewById(R.id.recycler_perfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        idCafeteria = preferences.getString("idCafeteria", "");

        consultarUsuario();

        btnCrearPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistroPerfilActivity.class);
                startActivity(intent);
            }
        });
        btnLectorQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), QrActivity.class);
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
                return true;

            case R.id.Mn2:
                Intent intent = new Intent(getApplicationContext(), MonederoActivity.class);
                intent.putExtra("iSaldo","hola");
                System.out.println(txtISaldo.getText());
                intent.putExtra("idCafeteria",idCafeteria);
                startActivity(intent);
                //finish();
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
        String URL = urlConsultarCliente + idCafeteria;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String dni,  nombre,  apellidos,  direccion,  telefono,  email,  password,  imagen, saldo;
                        try {
                            nombre = response.getString("nombre_cafeteria");
                            imagen = response.getString("logo_cafeteria");

                            txtSaludo.setText(nombre);

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


}