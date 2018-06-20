package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.appmobilidadeurbana.objetos.Utilizador;
import com.example.pc.appmobilidadeurbana.objetos.server;

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
                final Utilizador utilizador = server.postLoginHttp(user.getText().toString(),password.getText().toString());


                if (utilizador != null) {
                    System.out.println(utilizador.getNome());
                    System.out.println(utilizador.getEmail());
                    System.out.println(utilizador.getUsername());
                    System.out.println(utilizador.getPassword());
                }else{
                    Log.d("ew","login fail");
                    return;
                }

                //caso seja perciso alguma alteração no UI, tem de ser aqui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(utilizador.getNome().toString());
                    }
                });



            }
        }.start();


    }


}
