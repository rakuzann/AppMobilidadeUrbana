package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.pc.appmobilidadeurbana.objetos.Utilizador;
import com.example.pc.appmobilidadeurbana.objetos.server;

public class editarLimitacoes extends AppCompatActivity {

    Switch mobilidade,visuais;
    Button btnAtualizar;
    TextView user;

    Utilizador utilizador;

    Boolean userMob = false;
    Boolean userVis = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_limitacoes);

        mobilidade = findViewById(R.id.switchMobilidade);
        visuais = findViewById(R.id.switchVisuais);
        user = findViewById(R.id.textViewUser);
        btnAtualizar = findViewById(R.id.buttonAtualizar2);

        utilizador = (Utilizador) getIntent().getSerializableExtra("utilizador");

        //ver quais as cenas do utilizador atual
        new Thread(){
            public void run (){

                final int[] lim = server.postGetLimitacoesUser(String.valueOf(utilizador.getId()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for(int i=0;i<lim.length;i++){
                            if(lim[i]==1){
                                mobilidade.setChecked(true);
                                userMob=true;
                            }

                            if(lim[i]==2){
                                visuais.setChecked(true);
                                userVis=true;
                            }
                        }

                    }
                });
            }
        }.start();


        user.setText(user.getText().toString()+" "+utilizador.getNome());
    }


    public void atualizar(View view){
        boolean mob,vis;


        //ver o estado dos switches
        if(mobilidade.isChecked())
            mob=true;
        else
            mob=false;

        if(visuais.isChecked())
            vis=true;
        else
            vis=false;


        //comparar o estado dos switches e do user, e ver se é necessario alterar
        if(userMob!=mob){
            if(userMob){
                new Thread(){
                    public void run (){

                        server.postRemLimitacao(String.valueOf(utilizador.getId()),"1");

                    }
                }.start();
            }else{

                new Thread(){
                    public void run (){

                        server.postAddLimitacao(String.valueOf(utilizador.getId()),"1");

                    }
                }.start();
            }
        }

        if(userVis!=vis){
            if(userVis){
                new Thread(){
                    public void run (){

                        server.postRemLimitacao(String.valueOf(utilizador.getId()),"2");

                    }
                }.start();
            }else{
                new Thread(){
                    public void run (){

                        server.postAddLimitacao(String.valueOf(utilizador.getId()),"2");

                    }
                }.start();
            }
        }

        finish();
    }
}
