package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModificacionClienteActivity extends AppCompatActivity {

    EditText edtDni, edtNombre, edtApellidos, edtDireccion, edtTelefono, edtEmail, edtPassword;
    Button btnModificar, btnEliminar,btnElegirImagen;
    ImageView imagenCliente;
    Drawable drawable;

    final int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    String idCliente;

    RequestQueue requestQueue;

    private static final String urlModificarCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/modificar_cliente.php";
    private static final String urlConsultarCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_cliente.php?id_cliente=";
    private static final String urlEliminarCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/eliminar_cliente.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificacion_cliente);

        requestQueue = Volley.newRequestQueue(this);

        edtDni = (EditText)findViewById(R.id.dni_cliente);
        edtNombre = (EditText)findViewById(R.id.nombre_cliente);
        edtApellidos = (EditText)findViewById(R.id.apellidos_cliente);
        edtDireccion = (EditText)findViewById(R.id.direccion_cliente);
        edtTelefono = (EditText)findViewById(R.id.telefono_cliente);
        edtEmail = (EditText)findViewById(R.id.email_cliente);
        edtPassword = (EditText)findViewById(R.id.password_cliente);
        imagenCliente = (ImageView) findViewById(R.id.imagen_cliente);

        btnModificar = (Button)findViewById(R.id.btn_modificar_cliente);
        btnEliminar = (Button)findViewById(R.id.btn_eliminar_cliente);
        btnElegirImagen = (Button)findViewById(R.id.btnElegirImagen);

        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        idCliente = preferences.getString("idCliente", "");

        ConsultarUsuario();



        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawable = imagenCliente.getDrawable();
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                
                String dni = edtDni.getText().toString().trim();
                String nombre = edtNombre.getText().toString().trim();
                String apellidos = edtApellidos.getText().toString().trim();
                String direccion = edtDireccion.getText().toString().trim();
                String telefono = edtTelefono.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String imagen = getStringImagen(bitmap);

                ModificarUsuario(dni, nombre, apellidos, direccion, telefono, email, password, imagen);

            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EliminarUsuario();
                SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
                preferences.edit().clear().commit();
            }
        });

        btnElegirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFileChooser();
            }
        });
    }

    private void EliminarUsuario() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlEliminarCliente,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ModificacionClienteActivity.this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ModificacionClienteActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id_cliente", idCliente);

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void ModificarUsuario(String dni, String nombre, String apellidos, String direccion, String telefono, String email, String password, String imagen) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlModificarCliente,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ModificacionClienteActivity.this, "Usuario modificado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                        startActivity(intent);
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ModificacionClienteActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id_cliente", idCliente);
                params.put("dni_cliente", dni);
                params.put("nombre_cliente", nombre);
                params.put("apellidos_cliente", apellidos);
                params.put("direccion_cliente", direccion);
                params.put("telefono_cliente", telefono);
                params.put("email_cliente", email);
                params.put("password_cliente", password);
                params.put("imagen_cliente", imagen);

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void ConsultarUsuario() {
        String URL = urlConsultarCliente + idCliente;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String dni,  nombre,  apellidos,  direccion,  telefono,  email,  password,  imagen;
                        try {
                            dni = response.getString("dni_cliente");
                            nombre = response.getString("nombre_cliente");
                            apellidos = response.getString("apellidos_cliente");
                            direccion = response.getString("direccion_cliente");
                            telefono = response.getString("telefono_cliente");
                            email = response.getString("email_cliente");
                            password = response.getString("password_cliente");
                            imagen = response.getString("imagen_cliente");

                            edtDni.setText(dni);
                            edtNombre.setText(nombre);
                            edtApellidos.setText(apellidos);
                            edtDireccion.setText(direccion);
                            edtTelefono.setText(telefono);
                            edtEmail.setText(email);
                            edtPassword.setText(password);
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
                        Toast.makeText(ModificacionClienteActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
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
                imagenCliente.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}