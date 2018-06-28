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


    //booleans das limitacoes
    boolean user1 = false;
    boolean user2 = false;
    boolean rota1 = false;
    boolean rota2 = false;
    boolean podeAndar = false;

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


                podeAndar = false;

                int[] limRota = server.postGetLimitacoesRota("1");

                for(int i=0;i<limRota.length;i++){
                    if(limRota[i]==1){
                        rota1=true;
                    }

                    if(limRota[i]==2){
                        rota2=true;
                    }
                }

                int[] limUser = server.postGetLimitacoesUser("1");

                for(int i=0;i<limUser.length;i++){
                    if(limUser[i]==1){
                        user1 = true;
                    }

                    if(limUser[i]==2){
                        rota2 = true;
                    }
                }




                if(!user1 && !user2){
                    podeAndar = true;
                }else if(!user1 && user2){
                    if(rota2){
                        podeAndar = true;
                    }
                }else if(user1 && !user2){
                    if(rota1){
                        podeAndar = true;
                    }
                }else if(user1 && user2){
                    if(rota1 && rota2){
                        podeAndar = true;
                    }
                }else{
                    podeAndar = false;
                }


            }
        }.start();


        Log.d("lala",String.valueOf(podeAndar));
        

    }


}
