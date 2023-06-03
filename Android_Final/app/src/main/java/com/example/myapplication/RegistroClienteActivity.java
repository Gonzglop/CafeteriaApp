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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistroClienteActivity extends AppCompatActivity {

    EditText edtDni, edtNombre, edtApellidos, edtDireccion, edtTelefono, edtEmail, edtPassword;
    Button btnInsertar, btnElegirImagen;
    ImageView imagenCliente;

    final int PICK_IMAGE_REQUEST = 1;

    Bitmap bitmap;
    RequestQueue requestQueue;

    private static final String urlInsertarCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/insertar_cliente.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cliente);

        requestQueue = Volley.newRequestQueue(this);

        edtDni = (EditText)findViewById(R.id.dni_cliente);
        edtNombre = (EditText)findViewById(R.id.nombre_cliente);
        edtApellidos = (EditText)findViewById(R.id.apellidos_cliente);
        edtDireccion = (EditText)findViewById(R.id.direccion_cliente);
        edtTelefono = (EditText)findViewById(R.id.telefono_cliente);
        edtEmail = (EditText)findViewById(R.id.email_cliente);
        edtPassword = (EditText)findViewById(R.id.password_cliente);
        imagenCliente = (ImageView)findViewById(R.id.imagen_cliente);

        btnInsertar = (Button)findViewById(R.id.btn_insertar_cliente);
        btnElegirImagen = (Button)findViewById(R.id.btnElegirImagen);


        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dni = edtDni.getText().toString().trim();
                String nombre = edtNombre.getText().toString().trim();
                String apellidos = edtApellidos.getText().toString().trim();
                String direccion = edtDireccion.getText().toString().trim();
                String telefono = edtTelefono.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String imagen = "";
                if (bitmap!=null){
                    imagen = getStringImagen(bitmap);
                }

                if (!dni.isEmpty() && !email.isEmpty()&& !password.isEmpty()&& !nombre.isEmpty()&& !apellidos.isEmpty()) {
                    crearUsuario(dni, nombre, apellidos, direccion, telefono, email, password, imagen);
                } else {
                    Toast.makeText(RegistroClienteActivity.this, "Faltan campos obligatorios por rellenar: (*)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnElegirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

    }



    private void crearUsuario(String dni, String nombre, String apellidos, String direccion, String telefono, String email, String password, String imagen) {

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlInsertarCliente,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegistroClienteActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(RegistroClienteActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
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