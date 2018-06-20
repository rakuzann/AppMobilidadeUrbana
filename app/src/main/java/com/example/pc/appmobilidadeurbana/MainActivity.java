package com.example.pc.appmobilidadeurbana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void btnStart (View v){
        Intent mudar = new Intent(this, MapsActivity.class);
        startActivity(mudar);
    }

    public void test(View view){
        Intent mudar = new Intent(this, testes.class);
        startActivity(mudar);
    }
}
