package com.example.pc.appmobilidadeurbana;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.pc.appmobilidadeurbana.objetos.Utilizador;

public class MainActivity extends AppCompatActivity {

    TextView textViewUser;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle nToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------saber qual o user que está logged in --------------
        textViewUser = findViewById(R.id.textViewUser);

        Utilizador utilizador = (Utilizador) getIntent().getSerializableExtra("utilizador");

        if(utilizador!=null)
            textViewUser.setText(utilizador.getNome().toString());
        else
            textViewUser.setText("guest");

        //------------- cenas da action bar -------------------

        mDrawerLayout = findViewById(R.id.drawer_layout);
        nToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(nToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

}
