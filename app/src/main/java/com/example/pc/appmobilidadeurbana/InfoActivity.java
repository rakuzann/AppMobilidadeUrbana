package com.example.pc.appmobilidadeurbana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

public class InfoActivity extends AppCompatActivity {


    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setImageResource(R.drawable.mapaautobuses);
    }


    public void btnLinhaVermelha (View v){
        photoView.setImageResource(R.drawable.linhavermelha);
    }

    public void btnLinhaLaranja (View v){
        photoView.setImageResource(R.drawable.linhalaranja);
    }

    public void btnLinhaAzul (View v){
        photoView.setImageResource(R.drawable.linhaazul);
    }

    public void btnMapa (View v){
        photoView.setImageResource(R.drawable.mapaautobuses);
    }
}
