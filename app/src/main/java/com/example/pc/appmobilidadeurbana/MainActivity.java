package com.example.pc.appmobilidadeurbana;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.appmobilidadeurbana.objetos.Utilizador;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_map:
                Toast.makeText(this, "nav_map", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_account:
                //Intent i = new Intent(this, Register.class);
                //startActivity(i);
                Toast.makeText(this, "nav_account", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "nav_settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_taxi:
                Toast.makeText(this, "nav_taxi", Toast.LENGTH_SHORT).show();
                break;

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(nToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void btnTeste(View view){
        Intent mudar = new Intent(this, testes.class);
        startActivity(mudar);
    }
}
