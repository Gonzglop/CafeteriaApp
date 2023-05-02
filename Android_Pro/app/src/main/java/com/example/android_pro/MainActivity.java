package com.example.android_pro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edtUsuario, edtPassword;
    Button btnLogin, btnRegistrar;
    String usuario, password,idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsuario = (EditText) findViewById(R.id.lbl1);
        edtPassword = (EditText) findViewById(R.id.lbl2);
        btnLogin = (Button) findViewById(R.id.btn1);
        btnRegistrar = (Button) findViewById(R.id.btn_registrar);

        recuperarPreferencias();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = edtUsuario.getText().toString();
                password = edtPassword.getText().toString();

                if (!usuario.isEmpty() && !password.isEmpty()) {
                    validarUsuario("https://micafeteriaapp.000webhostapp.com/android_mysql/validar_cliente.php");
                } else {
                    Toast.makeText(MainActivity.this, "Introduce el usuario y la contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegistroClienteActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void validarUsuario(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        idCliente = jsonObject.getString("id_cliente");
                        Toast.makeText(MainActivity.this, "ID: " + idCliente, Toast.LENGTH_LONG).show();

                        guardarPreferencias(); // Guardar el id_cliente en las preferencias

                        Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                        //intent.putExtra("idCliente", idCliente);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> parametros = new HashMap<>();
                parametros.put("email_cliente", usuario);
                parametros.put("password_cliente", password);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void guardarPreferencias(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("usuario", usuario);
        editor.putString("password", password);
        editor.putString("idCliente", idCliente); // Guardar el id_cliente en las preferencia
        editor.putBoolean("sesion", true);
        editor.commit();
    }

    private void recuperarPreferencias(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        edtUsuario.setText(preferences.getString("usuario",""));
        edtPassword.setText(preferences.getString("password",""));
    }
}