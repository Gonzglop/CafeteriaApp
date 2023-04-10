package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        ImageView imgQR = findViewById(R.id.imagen_qr);
        TextView nombreQR = findViewById(R.id.txtNombreQr);
        TextView IdQR = findViewById(R.id.txtIdQr);
        Button btnVolver = findViewById(R.id.btn_volver);

        Intent intent = getIntent();
        int idPerfil = intent.getIntExtra("idPerfil", 0);
        String nombreApellidos = intent.getStringExtra("nombreApellidos");

        nombreQR.setText(nombreApellidos);
        IdQR.setText(String.valueOf(idPerfil));

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(String.valueOf(idPerfil), BarcodeFormat.QR_CODE,720,720);

            imgQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}