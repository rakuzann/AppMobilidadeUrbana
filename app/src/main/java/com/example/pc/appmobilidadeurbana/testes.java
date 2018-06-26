package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.appmobilidadeurbana.objetos.Favorito;
import com.example.pc.appmobilidadeurbana.objetos.Paragem;
import com.example.pc.appmobilidadeurbana.objetos.Utilizador;
import com.example.pc.appmobilidadeurbana.objetos.server;

import java.util.ArrayList;

public class testes extends AppCompatActivity {

    EditText user,password;
    TextView textView;

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testes);

        btn = findViewById(R.id.buttonTestes);

        user = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);

        textView = findViewById(R.id.textView);
    }

    public void btn(View view){

        new Thread(){
            public void run (){

                ArrayList<Paragem> arrayPara =  server.postHttpGetParagens(user.getText().toString(),password.getText().toString());


                for(Paragem p:arrayPara){
                    Log.d("NOME",p.getNome());
                    Log.d("LATITUDE",String.valueOf(p.getLatitude()));
                    Log.d("LONGITUDE",String.valueOf(p.getLongitude()));
                    Log.d("HORARIO",String.valueOf(p.getHorario()));
                }


            }
        }.start();


    }


}
