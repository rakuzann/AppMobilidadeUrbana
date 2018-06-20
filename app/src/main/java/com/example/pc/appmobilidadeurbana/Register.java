package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pc.appmobilidadeurbana.objetos.server;

public class Register extends AppCompatActivity {

    EditText username,password,nome,email;

    Button btnRegisto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.registUsername);
        password = findViewById(R.id.registPassword);
        nome = findViewById(R.id.registNome);
        email = findViewById(R.id.registEmail);

        btnRegisto = findViewById(R.id.btnRegisto);
    }

    public void registo(View view){

        String user = username.getText().toString();
        String pw = password.getText().toString();
        String nm = nome.getText().toString();
        String mail = email.getText().toString();


        server.postRegisterHttp(user,pw,nm,mail);
    }

}
