package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegistroClienteActivity extends AppCompatActivity {

    EditText edtDni, edtNombre, edtApellidos, edtDireccion, edtTelefono, edtEmail, edtPassword, edtImagen;
    Button btnInsertar;

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
        edtImagen = (EditText)findViewById(R.id.imagen_cliente);

        btnInsertar = (Button)findViewById(R.id.btn_insertar_cliente);

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
                String imagen = edtImagen.getText().toString().trim();

                crearUsuario(dni, nombre, apellidos, direccion, telefono, email, password, imagen);

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
}