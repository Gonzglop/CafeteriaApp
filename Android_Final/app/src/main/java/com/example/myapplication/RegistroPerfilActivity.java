package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistroPerfilActivity extends AppCompatActivity {

    EditText edtNieDni, edtNombre, edtApellidos, edtImagen;
    Button btnInsertar, btnBuscar;
    Spinner spinnerCafeterias;
    ImageView imagenPerfil;

    final int PICK_IMAGE_REQUEST = 1;
    String idCliente, idCafeteria;
    Bitmap bitmap;

    RequestQueue requestQueue;



    private static final String urlConsultarCafeterias = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_cafeterias.php";
    private static final String urlInsertarPerfil = "https://micafeteriaapp.000webhostapp.com/android_mysql/insertar_perfil.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_perfil);

        requestQueue = Volley.newRequestQueue(this);

        edtNieDni = (EditText)findViewById(R.id.nie_dni_perfil);
        edtNombre = (EditText)findViewById(R.id.nombre_perfil);
        edtApellidos = (EditText)findViewById(R.id.apellidos_perfil);

        btnInsertar = (Button)findViewById(R.id.btn_insertar_perfil);
        btnBuscar  = (Button)findViewById(R.id.btnBuscar);

        imagenPerfil = (ImageView) findViewById(R.id.imageView);

        spinnerCafeterias = (Spinner) findViewById(R.id.spinner_cafeteria);


        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        idCliente = preferences.getString("idCliente", "");

        consultarCafeterias();


        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nieDni = edtNieDni.getText().toString().trim();
                String nombre = edtNombre.getText().toString().trim();
                String apellidos = edtApellidos.getText().toString().trim();
                String imagen = getStringImagen(bitmap);

                crearPerfil(nieDni, nombre, apellidos, imagen);
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    private void crearPerfil(String dni, String nombre, String apellidos, String imagen) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlInsertarPerfil,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegistroPerfilActivity.this, "Perfil creado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistroPerfilActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("nie_dni_perfil", dni);
                params.put("nombre_perfil", nombre);
                params.put("apellidos_perfil", apellidos);
                params.put("id_cliente", idCliente);
                params.put("id_cafeteria", idCafeteria);
                params.put("imagen_perfil", imagen);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    private void consultarCafeterias() {
        String URL = urlConsultarCafeterias;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<Cafeteria> listaCafeterias = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject joCafeteria = response.getJSONObject(i);
                                int id = joCafeteria.getInt("id_cafeteria");
                                String nombre = joCafeteria.getString("nombre_cafeteria");
                                Cafeteria cafeteria = new Cafeteria(id, nombre);
                                listaCafeterias.add(cafeteria);
                            }

                            CafeteriaSpinnerAdapter adapter = new CafeteriaSpinnerAdapter(RegistroPerfilActivity.this, listaCafeterias);
                            spinnerCafeterias.setAdapter(adapter);
                            spinnerCafeterias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Cafeteria cafeteriaSeleccionada = (Cafeteria) parent.getItemAtPosition(position);
                                    idCafeteria = String.valueOf(cafeteriaSeleccionada.getId());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistroPerfilActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private String getStringImagen(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imagenPerfil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}