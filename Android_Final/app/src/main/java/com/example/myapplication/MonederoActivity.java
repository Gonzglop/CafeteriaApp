package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonederoActivity extends AppCompatActivity {
    Button btnRecargarMonedero, btnRecargar, btnRecuperar;
    Spinner spinnerMetodosPago;
    TextView txtSaldo;
    CheckBox recordarDatos;
    EditText edtCantidad,edtTarjeta, edtFechaValidez, edtCodigo;

    String saldo;
    String idCliente;

    RequestQueue requestQueue;


    private static final String urlModificarMonederoCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/modificar_cliente_monedero.php";
    private static final String urlConsultarCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_cliente.php?id_cliente=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monedero);

        requestQueue = Volley.newRequestQueue(this);

        spinnerMetodosPago = findViewById(R.id.spinnerMetodoPago);
        txtSaldo = (TextView) findViewById(R.id.txtSaldo);
        edtCantidad = (EditText) findViewById(R.id.editRecargar);
        edtTarjeta = (EditText) findViewById(R.id.editNumTarjeta);
        edtFechaValidez = (EditText) findViewById(R.id.editFechaValidez);
        edtCodigo = (EditText) findViewById(R.id.editCCV);
        btnRecargarMonedero = (Button) findViewById(R.id.btnRecargarMonedero);
        btnRecargar = (Button) findViewById(R.id.btnRecargar);
        btnRecuperar = (Button) findViewById(R.id.btnRecuperar);
        recordarDatos = (CheckBox) findViewById(R.id.chkRecordarDatosPago);


        List<String> metodosPagoList = new ArrayList<>();
        metodosPagoList.add("Tarjeta de crédito");
        metodosPagoList.add("Tarjeta de débito");
        metodosPagoList.add("PayPal");
        metodosPagoList.add("Bizum");
        metodosPagoList.add("Transferencia");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, metodosPagoList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMetodosPago.setAdapter(spinnerAdapter);

        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        idCliente = preferences.getString("idCliente", "");

        consultarUsuario();


        btnRecargarMonedero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtCantidad.getText().toString().isEmpty()) {
                    double cantidadSaldo = Double.parseDouble(saldo);
                    double cantidadRecarga = Double.parseDouble(String.valueOf(edtCantidad.getText()));

                    if (cantidadRecarga > 0){
                        if (!edtCodigo.getText().toString().isEmpty() && !edtTarjeta.getText().toString().isEmpty() && !edtFechaValidez.getText().toString().isEmpty()){
                            String cantidadTotal = String.valueOf(cantidadSaldo + cantidadRecarga);
                            ModificarMonedero(cantidadTotal);
                        }else{
                            Toast.makeText(MonederoActivity.this, "Faltan datos de pago por introducir", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(MonederoActivity.this, "La recarga debe ser superior a 0€", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MonederoActivity.this, "Introduce la cifra a recargar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ModificarMonedero(String cantidadTotal) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlModificarMonederoCliente,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MonederoActivity.this, "Transacción realizada correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MonederoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id_cliente", idCliente);
                params.put("monedero_cliente", cantidadTotal);

                return params;
            }
        };
        requestQueue.add(stringRequest);
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
                        try {
                            saldo = response.getString("monedero_cliente");
                            txtSaldo.setText(saldo + "€");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MonederoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}