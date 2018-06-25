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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pontos_favoritos);







        lstView = findViewById(R.id.listView);

        utilizador = (Utilizador) getIntent().getSerializableExtra("utilizador");



        new Thread(){
            public void run (){


                final ArrayList<Favorito> aFav  = server.postHttpGetFavoritos(String.valueOf(utilizador.getId()));


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                       //Apresentar dados na list view
                        ArrayList<String> dados = new ArrayList<>();
                        for(int i=0; i < aFav.size(); i++) {
                            dados.add(aFav.get(i).getLatitude().toString());
                        }

                        ListViewAdapter adapter = new ListViewAdapter(PontosFavoritosActivity.this, aFav);
                        lstView.setAdapter(adapter);
                    }
                });


            }
        }.start();




    }
}
