package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pc.appmobilidadeurbana.objetos.Utilizador;

public class MainActivity extends AppCompatActivity {

    TextView textViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewUser = findViewById(R.id.textViewUser);

        Utilizador utilizador = (Utilizador) getIntent().getSerializableExtra("utilizador");

        textViewUser.setText(utilizador.getNome().toString());
    }
}
