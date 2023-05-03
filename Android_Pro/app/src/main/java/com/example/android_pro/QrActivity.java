package com.example.android_pro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QrActivity extends AppCompatActivity {

    TextView idQR;
    TextView txtDni;
    TextView txtNombre;
    TextView txtIdPedido;
    TextView txtFechaPedido;
    TextView txtDetallesPedido;

    RequestQueue requestQueue;

    private static final String urlConsultarPedido = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_qr.php?id_perfil=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        idQR = findViewById(R.id.txtIdQr);

        txtDni = findViewById(R.id.txtDni);
        txtNombre = findViewById(R.id.txtNombre);
        txtIdPedido = findViewById(R.id.txtIdPedido);
        txtFechaPedido = findViewById(R.id.txtFechaPedido);
        txtDetallesPedido = findViewById(R.id.txtDetallesPedido);

        Button btnVolver = findViewById(R.id.btn_volver);

        requestQueue = Volley.newRequestQueue(this);

        new IntentIntegrator(this).initiateScan();

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String datos = result.getContents();
        idQR.setText(datos);

        consultarPedido();
    }

    private void consultarPedido() {
        String URL = urlConsultarPedido + idQR.getText().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String idPedido = response.getString("id_pedido");
                            String fechaPedido = response.getString("fecha_pedido");
                            String dni = response.getString("dni_nie");
                            String nombre = response.getString("nombre");
                            String apellidos = response.getString("apellidos");

                            JSONArray detalles = response.getJSONArray("detalles");
                            String detallesPedido = "";
                            for (int i = 0; i < detalles.length(); i++) {
                                JSONObject detalle = detalles.getJSONObject(i);
                                String tipoProducto = detalle.getString("tipo_producto");
                                String nombreProducto = detalle.getString("nombre_producto");
                                String cantidad = detalle.getString("cantidad");
                                detallesPedido += tipoProducto + " - " + nombreProducto + "\t\tx" + cantidad + "\n";
                            }

                            txtDni.setText(dni);
                            txtNombre.setText(nombre + " " + apellidos);
                            txtIdPedido.setText(idPedido);
                            txtFechaPedido.setText(fechaPedido);
                            txtDetallesPedido.setText(detallesPedido);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QrActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

}