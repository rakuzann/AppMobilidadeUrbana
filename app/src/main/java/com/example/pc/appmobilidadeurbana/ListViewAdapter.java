package com.example.pc.appmobilidadeurbana;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.pc.appmobilidadeurbana.objetos.Favorito;
import com.example.pc.appmobilidadeurbana.objetos.Utilizador;
import com.example.pc.appmobilidadeurbana.objetos.server;

import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter {
    Utilizador utilizador;
    Context context;
    ArrayList<Favorito> aFav;


    //public constructor
    public ListViewAdapter(Context context, ArrayList<Favorito> aFav) {
        this.context = context;
        this.aFav = aFav;
    }


    @Override
    public int getCount() {

        return aFav.size();
    }

    @Override
    public Object getItem(int position) {

        return aFav.get(position);
    }

    @Override
    public long getItemId(int position) {

        return aFav.get(position).getId();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.listview_favoritos, parent, false);
        }

        // get current item to be displayed
        aFav.get(position);

        // get the TextView for item name and item description
        TextView nomePredefenido = (TextView) convertView.findViewById(R.id.nomePredefenido);
        Button btnIrFavoritos = (Button) convertView.findViewById(R.id.btnIrFavorito) ;
        Button btnRemoverFavorito = (Button) convertView.findViewById(R.id.btnRemoverFavorito) ;


        //sets the text for item name and item description from the current item object
        nomePredefenido.setText( String.valueOf(aFav.get(position).getNome()));
        btnIrFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mudar = new Intent(v.getContext(), MapaFavortios.class);
                mudar.putExtra("lat", aFav.get(position).getLatitude());
                mudar.putExtra("log", aFav.get(position).getLongitude());
                context.startActivity(mudar);
            }
        });
        btnRemoverFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // returns the view for the current row
        return convertView;
    }


}
