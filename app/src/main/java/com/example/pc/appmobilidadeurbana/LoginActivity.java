package com.example.pc.appmobilidadeurbana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pc.appmobilidadeurbana.objetos.Utilizador;
import com.example.pc.appmobilidadeurbana.objetos.server;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.etNome);
        password = findViewById(R.id.etPass);

        btnGuest = findViewById(R.id.btnLogInGuest);
    }


    public void btnStart(View v) {

        boolean login = false;


        new Thread() {
            public void run() {
                Utilizador utilizador = server.postLoginHttp(username.getText().toString(), password.getText().toString());


                if (utilizador != null) {

                    Intent mudar = new Intent(LoginActivity.this, MapsActivity.class);
                    mudar.putExtra("utilizador", utilizador);
                    startActivity(mudar);

                } else {
                    //caso seja perciso alguma alteração no UI, tem de ser aqui
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Login Inválido", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        }.start();

    }


    public void test(View view) {
        Intent mudar = new Intent(this, testes.class);
        startActivity(mudar);
    }

    public void rnd(View view) {
        Intent mudar = new Intent(this, testes.class);
        startActivity(mudar);
    }


    public void antonio(View view) {
        Intent mudar = new Intent(this, MapsActivity.class);
        startActivity(mudar);
    }

    public void loginGuest(View view) {
        Intent mudar = new Intent(LoginActivity.this, MapsActivity.class);
        //mudar.putExtra("utilizador", utilizador);
        startActivity(mudar);
    }

    public void criarConta(View view) {
        Intent mudar = new Intent(LoginActivity.this, Register.class);
        startActivity(mudar);
    }
}
