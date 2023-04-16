package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModificacionPerfilActivity extends AppCompatActivity {

    EditText edtNieDniPerfil, edtNombrePerfil, edtApellidosPerfil;
    Button btnElegirImagen,btnModificar;
    Spinner spinnerCafeterias;
    ImageView ivImagenPerfil;
    Drawable drawable;

    final int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;

    RequestQueue requestQueue;

    private static final String urlModificarPefil = "https://micafeteriaapp.000webhostapp.com/android_mysql/modificar_perfil.php";
    private static final String urlConsultarCafeterias = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_cafeteria.php";


    int idPerfil ;
    String idCafeteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificacion_perfil);
        requestQueue = Volley.newRequestQueue(this);

        btnModificar = findViewById(R.id.btn_modificar_perfil);
        btnElegirImagen = findViewById(R.id.btnBuscar);
        
        edtNieDniPerfil = findViewById(R.id.nie_dni_perfil);
        edtNombrePerfil = findViewById(R.id.nombre_perfil);
        edtApellidosPerfil = findViewById(R.id.apellidos_perfil);
        spinnerCafeterias = (Spinner) findViewById(R.id.spinner_mod_cafeteria);
        ivImagenPerfil = (ImageView) findViewById(R.id.imagen_perfil);


        Intent intent = getIntent();
        idPerfil= intent.getIntExtra("idPerfil", 0);
        int iIdCafeteria = intent.getIntExtra("idCafeteria", 0);
        String iDniNie = intent.getStringExtra("dni");
        String iNombre = intent.getStringExtra("nombrePerfil");
        String iApellidos = intent.getStringExtra("apellidos");
        String iImagenPerfil = intent.getStringExtra("imagenPerfil");


        edtNieDniPerfil.setText(iDniNie);
        edtNombrePerfil.setText(iNombre);
        edtApellidosPerfil.setText(iApellidos);
        try {
            Picasso.get().load(iImagenPerfil)
                    .error(R.drawable.usuario)
                    .into(ivImagenPerfil);
        } catch (Exception e) {
            e.printStackTrace();
        }
        idCafeteria= String.valueOf(iIdCafeteria);
        consultarCafeterias();


        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //drawable = ModificacionPerfilActivity.this.ivImagenPerfil.getDrawable();
                //bitmap = ((BitmapDrawable) drawable).getBitmap();

                String dni = edtNieDniPerfil.getText().toString().trim();
                String nombre = edtNombrePerfil.getText().toString().trim();
                String apellidos = edtApellidosPerfil.getText().toString().trim();
                String imagen = getStringImagen(bitmap);

                ModificarPerfil(dni, nombre, apellidos, imagen);

            }
        });
        btnElegirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    private void ModificarPerfil(String dni, String nombre, String apellidos, String imagen) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlModificarPefil,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ModificacionPerfilActivity.this, "Usuario modificado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ModificacionPerfilActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id_perfil", String.valueOf(idPerfil));
                params.put("nie_dni_perfil", dni);
                params.put("nombre_perfil", nombre);
                params.put("apellidos_perfil", apellidos);
                params.put("imagen_perfil", imagen);
                params.put("id_cafeteria", idCafeteria);

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

                            CafeteriaSpinnerAdapter adapter = new CafeteriaSpinnerAdapter(ModificacionPerfilActivity.this, listaCafeterias);
                            spinnerCafeterias.setAdapter(adapter);

                            // Establece como seleccionada la cafetería guardada en el perfil
                            int position = -1;
                            for (int i = 0; i < listaCafeterias.size(); i++) {
                                if (listaCafeterias.get(i).getId() == Integer.parseInt(idCafeteria)) {
                                    position = i;
                                    break;
                                }
                            }
                            if (position != -1) {
                                spinnerCafeterias.setSelection(position);
                            }
                            //

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
                        Toast.makeText(ModificacionPerfilActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                ivImagenPerfil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}