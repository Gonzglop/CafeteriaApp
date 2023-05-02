package com.example.android_pro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PedidoUnicoActivity extends AppCompatActivity {

    DatePicker datePicker;
    Spinner spinnerOpciones;
    RecyclerView recyclerViewProductos;
    EditText edtFecha, edtHora;
    Button btnRealizarPedido, btnFecha, btnHora;
    TextView txtPregunta, txtProgramaSemana, txtEligeFechaHora, txtProductosSeleccionados, txtTotal;

    String idCafeteria, idPerfil, horaDB, fechaDB;
    int dia, mes, ano, hora, minutos;
    int numProductosSeleccionados;
    double precioTotal;
    ArrayList<Producto> listaProductosSeleccionados = new ArrayList<>();
    ArrayList<DetallePedido> listaDetallesPedidos = new ArrayList<>();

    RequestQueue requestQueue;
    ProductoAdapter adapter;
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Aquí manejas los datos recibidos
            numProductosSeleccionados = intent.getIntExtra("numProductosSeleccionados",0);
            precioTotal = intent.getDoubleExtra("precioTotal",0);
            listaDetallesPedidos = intent.getParcelableArrayListExtra("listaDetallesPedidos");

            txtProductosSeleccionados.setText("Productos seleccionados: "+ numProductosSeleccionados);
            txtTotal.setText("Total: "+ precioTotal + " €");
        }
    };

    private static final String urlConsultarProductos = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_producto.php?id_cafeteria=";
    private static final String urlInsertarPedido = "https://micafeteriaapp.000webhostapp.com/android_mysql/insertar_pedido.php";
    private static final String urlInsertarDetallePedido = "https://micafeteriaapp.000webhostapp.com/android_mysql/insertar_detalle_pedido.php";

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("ProductosSeleccionados"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_unico);

        Intent intentDatosPerfil = getIntent();
        int idPerfilIntent = intentDatosPerfil.getIntExtra("idPerfil", 0);
        int idCafeteriaIntent = intentDatosPerfil.getIntExtra("idCafeteria", 0);
        String nombrePerfilIntent = intentDatosPerfil.getStringExtra("nombrePerfil");


        idCafeteria = String.valueOf(idCafeteriaIntent);
        idPerfil = String.valueOf(idPerfilIntent);

        datePicker = findViewById(R.id.datePicker);
        spinnerOpciones = findViewById(R.id.spinner_tipo_productos);
        recyclerViewProductos = findViewById(R.id.recyclerView_productos);
        btnRealizarPedido = findViewById(R.id.btnRealizarPedido);
        edtFecha = findViewById(R.id.edtFecha);
        edtHora = findViewById(R.id.edtHora);
        btnFecha = findViewById(R.id.btnFecha);
        btnHora = findViewById(R.id.btnHora);

        txtPregunta = findViewById(R.id.txtPregunta);
        txtProgramaSemana = findViewById(R.id.txtProgramaSemana);
        txtEligeFechaHora = findViewById(R.id.txtEligeFechaHora);
        txtProductosSeleccionados = findViewById(R.id.txtProductosSeleccionados);
        txtTotal = findViewById(R.id.txtTotal);

        txtProductosSeleccionados.setText("Productos seleccionados: "+ numProductosSeleccionados);
        txtTotal.setText("Total: "+ precioTotal + " €");

        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = Volley.newRequestQueue(this);

        consultarProductos();


        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                ano = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(PedidoUnicoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        fechaDB = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);

                        edtFecha.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }
                        , dia, mes, ano);
                datePickerDialog.show();
            }
        });

        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                hora = c.get(Calendar.HOUR_OF_DAY);
                minutos = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(PedidoUnicoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        horaDB = String.format("%02d:%02d:00", hourOfDay, minute);
                        edtHora.setText(hourOfDay + ":" + minute);
                    }
                }
                        , hora, minutos,false);
                timePickerDialog.show();
            }
        });

        btnRealizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPedido();
                for (DetallePedido detallePedido : listaDetallesPedidos) {
                    crearDetallePedido(detallePedido);
                }
            }
        });
    }


    private void consultarProductos() {
        String URL = urlConsultarProductos + idCafeteria;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<Producto> listaProductos = new ArrayList<>();
                            ArrayList<String> listaTipos = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject joProducto = response.getJSONObject(i);

                                int idProducto = joProducto.getInt("id_producto");
                                String nombre = joProducto.getString("nombre_producto");
                                String descripcion = joProducto.getString("descripcion_producto");
                                String ingredientes = joProducto.getString("ingredientes_producto");
                                double precio = joProducto.getDouble("precio_producto");
                                String tipo = joProducto.getString("tipo_producto");
                                String imagen = joProducto.getString("imagen_producto");
                                int idCafeteria = joProducto.getInt("id_cafeteria");

                                Producto producto = new Producto(idProducto, nombre, descripcion, ingredientes, precio, tipo, imagen, idCafeteria);
                                listaProductos.add(producto);

                                if (!listaTipos.contains(tipo)) {
                                    listaTipos.add(tipo);
                                }
                            }

                            //adapter = new ProductoAdapter(PedidoUnicoActivity.this, listaProductos);
                            //recyclerViewProductos.setAdapter(adapter);

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                                    PedidoUnicoActivity.this, android.R.layout.simple_spinner_item, listaTipos
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerOpciones.setAdapter(spinnerAdapter);
                            spinnerOpciones.setSelection(0);
                            spinnerOpciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    String tipoSeleccionado = (String) parent.getItemAtPosition(position);
                                    ArrayList<Producto> listaProductosFiltrada = filtrarProductosPorTipo(listaProductos, tipoSeleccionado);
                                    ProductoAdapter adapter = new ProductoAdapter(PedidoUnicoActivity.this, listaProductosFiltrada);
                                    recyclerViewProductos.setAdapter(adapter);

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
                        Toast.makeText(PedidoUnicoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void crearPedido() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlInsertarPedido,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(PedidoUnicoActivity.this, "Pedido creado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PedidoUnicoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id_perfil", idPerfil);
                params.put("id_cafeteria", idCafeteria);
                params.put("fecha_pedido", fechaDB + " " + horaDB);
                params.put("total_pedido", String.valueOf(precioTotal));

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    private void crearDetallePedido(DetallePedido detallePedido) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlInsertarDetallePedido,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(PedidoUnicoActivity.this, "Detalle del pedido creado correctamente", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PedidoUnicoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("cantidad_detalle_pedido", String.valueOf(detallePedido.getCantidadDetallePedido()));
                params.put("id_producto", String.valueOf(detallePedido.getIdProducto()));
                params.put("id_perfil", idPerfil);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    private ArrayList<Producto> filtrarProductosPorTipo(ArrayList<Producto> listaProductos, String tipoSeleccionado) {
        ArrayList<Producto> listaProductosFiltrada = new ArrayList<>();
        for (Producto producto : listaProductos) {
            if (producto.getTipo().equals(tipoSeleccionado)) {
                listaProductosFiltrada.add(producto);
            }
        }
        return listaProductosFiltrada;
    }



}