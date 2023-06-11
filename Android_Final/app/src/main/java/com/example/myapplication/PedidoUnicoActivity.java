package com.example.myapplication;

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
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PedidoUnicoActivity extends AppCompatActivity {

    DatePicker datePicker;
    Spinner spinnerOpciones;
    RecyclerView recyclerViewProductos1, recyclerViewProductos2, recyclerViewProductos3, recyclerViewProductos4, recyclerViewProductos5;
    EditText edtFecha, edtHora;
    Button btnRealizarPedido, btnFecha, btnHora;
    TextView txtPregunta, txtProgramaSemana, txtEligeFechaHora, txtProductosSeleccionados, txtTotal;

    String idCafeteria, idPerfil, idCliente, horaDB, fechaDB,dineroMonedero;
    int dia, mes, ano, hora, minutos;
    int numProductosSeleccionados;
    double precioTotal , monedero;
    ArrayList<Producto> listaProductosSeleccionados = new ArrayList<>();
    ArrayList<DetallePedido> listaDetallesPedidos = new ArrayList<>();

    RequestQueue requestQueue;
    ProductoAdapter adapter;

    private static final String urlConsultarProductos = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_producto.php?id_cafeteria=";
    private static final String urlInsertarPedido = "https://micafeteriaapp.000webhostapp.com/android_mysql/insertar_pedido.php";
    private static final String urlInsertarDetallePedido = "https://micafeteriaapp.000webhostapp.com/android_mysql/insertar_detalle_pedido.php";
    private static final String urlModificarMonederoCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/modificar_cliente_monedero.php";
    private static final String urlConsultarCliente = "https://micafeteriaapp.000webhostapp.com/android_mysql/consultar_cliente.php?id_cliente=";

    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            numProductosSeleccionados += intent.getIntExtra("cantidad",0);
            precioTotal += intent.getDoubleExtra("precioParcial",0);
            DetallePedido detallePedido = intent.getParcelableExtra("detallePedido");
            String indicacionLista = intent.getStringExtra("indicacionLista");

            if (detallePedido!=null){
                for (DetallePedido detPedido : listaDetallesPedidos) {
                    if (detPedido.getIdProducto() == detallePedido.getIdProducto()) {
                        listaDetallesPedidos.remove(detPedido);
                        break;
                    }
                }
                if (indicacionLista.equals("agregar")){
                    listaDetallesPedidos.add(detallePedido);
                }
            }
            //System.out.println(listaDetallesPedidos);
            txtProductosSeleccionados.setText("Productos seleccionados: "+ numProductosSeleccionados);
            txtTotal.setText("Total: "+ precioTotal + " €");
        }
    };

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
        int idClienteIntent = intentDatosPerfil.getIntExtra("idCliente", 0);
        int idCafeteriaIntent = intentDatosPerfil.getIntExtra("idCafeteria", 0);
        String nombrePerfilIntent = intentDatosPerfil.getStringExtra("nombrePerfil");

        idCafeteria = String.valueOf(idCafeteriaIntent);
        idPerfil = String.valueOf(idPerfilIntent);
        idCliente = String.valueOf(idClienteIntent);

        datePicker = findViewById(R.id.datePicker);
        spinnerOpciones = findViewById(R.id.spinner_tipo_productos);
        recyclerViewProductos1 = findViewById(R.id.recyclerView_productos1);
        recyclerViewProductos2 = findViewById(R.id.recyclerView_productos2);
        recyclerViewProductos3 = findViewById(R.id.recyclerView_productos3);
        recyclerViewProductos4 = findViewById(R.id.recyclerView_productos4);
        recyclerViewProductos5 = findViewById(R.id.recyclerView_productos5);
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

        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1); // Suma 1 día
        int d = c.get(Calendar.DAY_OF_MONTH);
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        fechaDB = String.format("%04d-%02d-%02d", y, m + 1, d);
        edtFecha.setText(d + "/" + (m + 1) + "/" + y);

        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 15);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        horaDB = String.format("%02d:%02d:00", h, min);
        edtHora.setText(h + ":" + min);

        /*
        recyclerViewProductos1.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProductos2.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProductos3.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProductos4.setLayoutManager(new LinearLayoutManager(this));
         */
        requestQueue = Volley.newRequestQueue(this);

        consultarProductos();
        consultarUsuario();

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, 1); // Suma 1 día

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
                        , ano, mes, dia); // Establece la fecha por defecto

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Evita seleccionar fechas anteriores a la actual

                datePickerDialog.show();
            }
        });

        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, 11);
                c.set(Calendar.MINUTE, 15);

                hora = c.get(Calendar.HOUR_OF_DAY);
                minutos = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(PedidoUnicoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        horaDB = String.format("%02d:%02d:00", hourOfDay, minute);
                        edtHora.setText(hourOfDay + ":" + minute);
                    }
                }
                        , hora, minutos, false); // Establece la hora por defecto

                timePickerDialog.show();
            }
        });


        btnRealizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numProductosSeleccionados > 0){
                    crearPedido();

                    for (DetallePedido detallePedido : listaDetallesPedidos) {
                        crearDetallePedido(detallePedido);
                    }
                }else {
                    Toast.makeText(PedidoUnicoActivity.this, "No hay productos seleccionados", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private ArrayList<RecyclerView> recyclerViews;
    private int currentRecyclerViewIndex;

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

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                                    PedidoUnicoActivity.this, android.R.layout.simple_spinner_item, listaTipos
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerOpciones.setAdapter(spinnerAdapter);
                            spinnerOpciones.setSelection(0);
                            spinnerOpciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // Ocultar todos los RecyclerViews
                                    recyclerViewProductos1.setVisibility(View.GONE);
                                    recyclerViewProductos2.setVisibility(View.GONE);
                                    recyclerViewProductos3.setVisibility(View.GONE);
                                    recyclerViewProductos4.setVisibility(View.GONE);
                                    recyclerViewProductos5.setVisibility(View.GONE);

                                    // Mostrar el RecyclerView seleccionado si el índice es válido
                                    if (position >= 0 && position < listaTipos.size()) {
                                        switch (position) {
                                            case 0:
                                                recyclerViewProductos1.setVisibility(View.VISIBLE);
                                                break;
                                            case 1:
                                                recyclerViewProductos2.setVisibility(View.VISIBLE);
                                                break;
                                            case 2:
                                                recyclerViewProductos3.setVisibility(View.VISIBLE);
                                                break;
                                            case 3:
                                                recyclerViewProductos4.setVisibility(View.VISIBLE);
                                                break;
                                            case 4:
                                                recyclerViewProductos5.setVisibility(View.VISIBLE);
                                                break;
                                        }
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                            // Obtener la lista de productos filtrada para cada tipo
                            for (int i = 0; i < listaTipos.size(); i++) {
                                String tipo = listaTipos.get(i);
                                ArrayList<Producto> listaProductosFiltrada = filtrarProductosPorTipo(listaProductos, tipo);
                                ProductoAdapter productoAdapter = new ProductoAdapter(PedidoUnicoActivity.this, listaProductosFiltrada);
                                switch (i) {
                                    case 0:
                                        recyclerViewProductos1.setAdapter(productoAdapter);
                                        recyclerViewProductos1.setLayoutManager(new LinearLayoutManager(PedidoUnicoActivity.this));
                                        break;
                                    case 1:
                                        recyclerViewProductos2.setAdapter(productoAdapter);
                                        recyclerViewProductos2.setLayoutManager(new LinearLayoutManager(PedidoUnicoActivity.this));
                                        break;
                                    case 2:
                                        recyclerViewProductos3.setAdapter(productoAdapter);
                                        recyclerViewProductos3.setLayoutManager(new LinearLayoutManager(PedidoUnicoActivity.this));
                                        break;
                                    case 3:
                                        recyclerViewProductos4.setAdapter(productoAdapter);
                                        recyclerViewProductos4.setLayoutManager(new LinearLayoutManager(PedidoUnicoActivity.this));
                                        break;
                                    case 4:
                                        recyclerViewProductos5.setAdapter(productoAdapter);
                                        recyclerViewProductos5.setLayoutManager(new LinearLayoutManager(PedidoUnicoActivity.this));
                                        break;
                                }
                            }

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

                        if ((monedero-precioTotal)>=0){
                            dineroMonedero = String.valueOf(monedero - precioTotal);
                            ModificarMonedero();

                            Toast.makeText(PedidoUnicoActivity.this, "Pedido creado correctamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(PedidoUnicoActivity.this, "No tienes suficiente dinero...", Toast.LENGTH_SHORT).show();
                        }

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

    private void ModificarMonedero() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlModificarMonederoCliente,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(PedidoUnicoActivity.this, "Transacción realizada correctamente", Toast.LENGTH_SHORT).show();
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
                params.put("id_cliente", idCliente);
                params.put("monedero_cliente", dineroMonedero);

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
                        String saldo;
                        try {
                            saldo = response.getString("monedero_cliente");
                            monedero = Double.parseDouble(saldo);

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
        requestQueue.add(jsonObjectRequest);
    }


    private ArrayList<Producto> filtrarProductosPorTipo(ArrayList<Producto> listaProductos, String tipo) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();
        for (Producto producto : listaProductos) {
            if (producto.getTipo().equals(tipo)) {
                listaFiltrada.add(producto);
            }
        }
        return listaFiltrada;
    }

}