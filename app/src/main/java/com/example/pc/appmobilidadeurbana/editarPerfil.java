package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pc.appmobilidadeurbana.objetos.Paragem;
import com.example.pc.appmobilidadeurbana.objetos.Utilizador;
import com.example.pc.appmobilidadeurbana.objetos.server;

import java.util.ArrayList;

public class editarPerfil extends AppCompatActivity {

    EditText nome,username,password,email;
    Button submit;

    Utilizador utilizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        utilizador = (Utilizador) getIntent().getSerializableExtra("utilizador");

        nome = findViewById(R.id.editTextNome);
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        email = findViewById(R.id.editTextEmail);

        submit = findViewById(R.id.buttonSubmit);

        nome.setText(utilizador.getNome());
        username.setText(utilizador.getUsername());
        password.setText(utilizador.getPassword());
        email.setText(utilizador.getEmail());
    }

    public void atualizar(View view){

        new Thread(){
            public void run (){

                server.postUpdateUser(String.valueOf(utilizador.getId()),username.getText().toString(), password.getText().toString(),nome.getText().toString(), email.getText().toString());

            }
        }.start();

        finish();
    }
}
