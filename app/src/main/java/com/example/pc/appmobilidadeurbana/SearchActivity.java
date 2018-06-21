package com.example.pc.appmobilidadeurbana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    EditText txtOrigem, txtDestino;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        txtDestino = findViewById(R.id.etDestino);
        txtOrigem = findViewById(R.id.etOrigem);
    }

    public void voltar (View view){
        finish();
    }

    public void mudarRota (View v){
        Intent mudar = new Intent(this, SearchMapActivity.class);
        mudar.putExtra("destino",txtDestino.getText().toString());
        mudar.putExtra("origem",txtOrigem.getText().toString());
        startActivity(mudar);
    }
}
