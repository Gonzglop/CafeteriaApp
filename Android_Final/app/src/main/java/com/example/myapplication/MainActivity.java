package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText input1 = (EditText) findViewById(R.id.lbl1);
        final EditText input2 = (EditText) findViewById(R.id.lbl2);

        final Button boton = (Button) findViewById(R.id.btn1);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                input1.setAlpha(1);
                input2.setAlpha(1);
                boton.setAlpha(1);
            }
        }, 3000);


        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto1 = String.valueOf(input1.getText());
                String texto2 = String.valueOf(input2.getText());

                if (texto1.equals("")|texto2.equals("")) {
                    Toast.makeText(MainActivity.this, "Introduce usuario y clave", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Hola " + input1.getText() + ", accediendo a la app ", Toast.LENGTH_SHORT).show();

                    Intent actInicio = new Intent(MainActivity.this,InicioActivity.class);
                    actInicio.putExtra("usuario",input1.getText().toString());
                    startActivity(actInicio);
                }
            }
        });
    }
}