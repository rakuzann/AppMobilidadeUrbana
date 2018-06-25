package com.example.pc.appmobilidadeurbana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pc.appmobilidadeurbana.objetos.server;

public class addFavorito extends AppCompatActivity {

    EditText nomeFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorito);

        nomeFav = findViewById(R.id.textNomeFavorito);


    }


    public void addFavorito(View view){



        //Log.d("wtf",getIntent().getExtras().getString("lat"));


        final String lat = getIntent().getStringExtra("lat");
        final String log = getIntent().getStringExtra("log");
        final String id = getIntent().getStringExtra("id");


        if(nomeFav.getText().toString().length()>0) {

            new Thread() {
                public void run() {
                    server.postFavorito(lat, log, id, nomeFav.getText().toString());

                    finish();
                }
            }.start();

        }else{
            Toast.makeText(this,"Por favor introduza um nome",Toast.LENGTH_SHORT).show();
        }
    }

}
