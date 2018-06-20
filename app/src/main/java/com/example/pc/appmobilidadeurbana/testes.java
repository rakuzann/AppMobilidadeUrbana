package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                Utilizador aaa = server.postLoginHttp("joaosousa","joaosousa");


                if (aaa != null) {
                    System.out.println(aaa.getNome());
                }

                textView.setText(aaa.getNome().toString());


            }
        }.start();






        //Toast.makeText(this, utilizador.getNome().toString(), Toast.LENGTH_SHORT).show();
    }
}
