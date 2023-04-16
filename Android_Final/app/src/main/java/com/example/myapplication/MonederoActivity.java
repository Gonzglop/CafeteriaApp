package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MonederoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monedero);

        Spinner spinnerMetodosPago = findViewById(R.id.spinnerMetodoPago);

        List<String> metodosPagoList = new ArrayList<>();
        metodosPagoList.add("Tarjeta de crédito");
        metodosPagoList.add("Tarjeta de débito");
        metodosPagoList.add("PayPal");
        metodosPagoList.add("Bizum");
        metodosPagoList.add("Transferencia");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, metodosPagoList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMetodosPago.setAdapter(spinnerAdapter);
    }
}