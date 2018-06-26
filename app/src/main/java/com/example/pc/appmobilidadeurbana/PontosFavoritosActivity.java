package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.pc.appmobilidadeurbana.objetos.Favorito;
import com.example.pc.appmobilidadeurbana.objetos.Utilizador;
import com.example.pc.appmobilidadeurbana.objetos.server;

import java.util.ArrayList;

public class PontosFavoritosActivity extends AppCompatActivity {

    //Utilizador
    Utilizador utilizador;

    ListView lstView;

    Double latMyne, logMyne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pontos_favoritos);




        latMyne = getIntent().getDoubleExtra("myLat",0);
        logMyne = getIntent().getDoubleExtra("myLog",0);


        lstView = findViewById(R.id.listView);

        utilizador = (Utilizador) getIntent().getSerializableExtra("utilizador");



        new Thread(){
            public void run (){


                final ArrayList<Favorito> aFav  = server.postHttpGetFavoritos(String.valueOf(utilizador.getId()));


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        ListViewAdapter adapter = new ListViewAdapter(PontosFavoritosActivity.this, aFav,latMyne, logMyne);
                        lstView.setAdapter(adapter);
                    }
                });


            }
        }.start();




    }
}
