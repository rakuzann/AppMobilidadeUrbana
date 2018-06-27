package com.example.pc.appmobilidadeurbana;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;


import com.example.pc.appmobilidadeurbana.objetos.DirectionsParser;
import com.example.pc.appmobilidadeurbana.objetos.Favorito;
import com.example.pc.appmobilidadeurbana.objetos.Paragem;
import com.example.pc.appmobilidadeurbana.objetos.Utilizador;
import com.example.pc.appmobilidadeurbana.objetos.server;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    //Variaveis Mapa
    private GoogleMap mMapa;
    SearchView search;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;
    LocationManager lm;
    LatLng myPlace,destino;

    //Variaveis do NavigationDrawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle nToggle;

    //Variaveis de verificação
    boolean verificarPonto = false;
    int markerMap = 0;


    //Variaveis Adicionar favoritos
    double latitudeFav, longitudeFav;


    //Variaveis de desenhar rota de autocarros
    double latAnterior = 0;
    double logAnterior = 0;
    float distanciaOriginal = 40000;
    int idProximaParagem;
    int getIdProximaParagemDestino;

    //Variavies das paragens mais proximas da origem e do destino
    double paragemMaisProximaLatX, paragemMaisProximaLog;
    String idParagemMaisProxima;

   //array que vai buscar todas as paragens do mapa
    ArrayList<Paragem> todasParagensMapa;
    ArrayList<Paragem> todasParagensRotaCerta;
    int [] rotasDeParagem;
    ArrayList<Paragem> paragemComuns;


   //Boolean que vai indicar se duas paragens pertecenm a mesma rota
    int memaRota;


    //Utilizador
    Utilizador utilizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        apanharParagens ();


        utilizador = (Utilizador) getIntent().getSerializableExtra("utilizador");


        //Navigation Drawer
        //------------- cenas da action bar -------------------

        mDrawerLayout = findViewById(R.id.drawer_layouts);
        NavigationView navigationView = findViewById(R.id.nav_viewr);
        navigationView.setNavigationItemSelectedListener(this);

        nToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();


        //-----------------------------------------------------
        // Configurações Do Mapa

        //Configurar Location Manager e Fazer Catch Obrigatorio
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Codigo para obter nossa localização (Tenta Usar a Net e o GPS )
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double log = location.getLongitude();
                    myPlace = new LatLng(lat, log);
                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                    try {
                        List<Address> list = geocoder.getFromLocation(lat, log, 1);
                        String str = list.get(0).getLocality();
                        mMapa.addMarker(new MarkerOptions().position(myPlace).title(str));
                        mMapa.moveCamera(CameraUpdateFactory.newLatLng(myPlace));
                        mMapa.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
                    } catch (IOException e) {

                    }

                    lm.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        } else if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double log = location.getLongitude();
                    myPlace = new LatLng(lat, log);
                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                    try {
                        List<Address> list = geocoder.getFromLocation(lat, log, 1);
                        String str = list.get(0).getLocality();
                        mMapa.addMarker(new MarkerOptions().position(myPlace).title(str));
                        mMapa.moveCamera(CameraUpdateFactory.newLatLng(myPlace));
                        mMapa.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
                    } catch (IOException e) {

                    }

                    lm.removeUpdates(this);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }


        /*
        Codigo de configuração da barra de procura
        onQuerryTextChange actualiza sempre que é escrito algo na barra, mesmo que seja so uma letra
        OnquerryTextSubmit so actualzia quando clicamos no search
        */
        search = findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pesquisarMapa();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        //Array para localizar dois pontos funcionar
        listPoints = new ArrayList<>();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);


    }


    //NavigationDrawer Metodos

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
                Intent x = new Intent(this, InfoActivity.class);
                startActivity(x);
                break;
            case R.id.nav_account:
                Intent acc = new Intent(this, editarPerfil.class);
                acc.putExtra("utilizador",utilizador);
                startActivity(acc);
                break;
            case R.id.nav_settings:
                Intent lim = new Intent(this, editarLimitacoes.class);
                lim.putExtra("utilizador",utilizador);
                startActivity(lim);
                break;
            case R.id.nav_taxi:
                Intent i = new Intent(this, TaxiActivity.class);
                startActivity(i);
                break;
            case R.id.nav_favorite:
                Intent fav = new Intent(this, PontosFavoritosActivity.class);
                fav.putExtra("utilizador",utilizador);
                fav.putExtra("myLat",myPlace.latitude);
                fav.putExtra("myLog",myPlace.longitude);
                startActivity(fav);
                break;


        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (nToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }


    //Configuração dos Mapas Metodos


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMapa = googleMap;

        mMapa.getUiSettings().setZoomControlsEnabled(true);

        //Activar Localizador Automatico do Google
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMapa.setMyLocationEnabled(true);


        //Obter localização atraves de click no mapa

        mMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                clickMapRota(latLng);
            }
        });



    }



    //Metodos

    public void searchBtn(View v) {
        Intent mudar = new Intent(this, SearchActivity.class);
        startActivity(mudar);
    }
    public void addFavoritos(View v) {

        final double lat = latitudeFav;
        final double log = longitudeFav;

        if (!verificarPonto) {
            Toast.makeText(this, "ERRO", Toast.LENGTH_SHORT).show();
        } else {


            if(utilizador!=null){
                Intent mudar = new Intent(this, addFavorito.class);
                mudar.putExtra("lat", String.valueOf(lat));
                mudar.putExtra("log", String.valueOf(log));
                mudar.putExtra("id", String.valueOf(utilizador.getId()));
                startActivity(mudar);
            } else{
                Toast.makeText(this,"É necessário o login!",Toast.LENGTH_SHORT).show();
            }




        }


    }

    //Metodos que utilizam a base dados
    public void apanharParagens (){

        new Thread(){
            public void run (){


                final ArrayList<Paragem> todasParagens = server.postHttpGetAllParagens();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        todasParagensMapa = todasParagens;
                    }
                });


            }
        }.start();
    }
    public void verficarMesmaRota (String id1, String id2){
        final String idRouta1 = id1;
        final String idRouta2 = id2;

        Thread t = new Thread(){
            public void run (){
                memaRota = server.mesmaRota( idRouta1,idRouta2);

            }
        };

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void obterRotaCerta(String id, String tempo){
        final String idRota = id;
        final String tempoSistema = tempo;
        Thread th = new Thread(){
            public void run (){
                todasParagensRotaCerta = server.postHttpGetParagens(idRota,tempoSistema);
            }
        };

        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void getRotasDaParagem (String id){
        final String idParagem = id;
        Thread t = new Thread(){
            public void run (){

                rotasDeParagem = server.postGetRotasFromParagem(idParagem);
            }
        };

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void getParagemDeDuasRotas(String id1, String id2){
        final String idRota1 = id1;
        final String idRota2 = id2;

        Thread t = new Thread(){
            public void run (){
                paragemComuns = server.postHttpGetParagensDuasRotas(idRota1,idRota2);
            }
        };

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




    public void paragemProximaMim (final LatLng ponto){

        distanciaOriginal = 40000;


        //Calcular Paragem mais proxima de Nos

        for (int i = 0; i < todasParagensMapa.size(); i++){

            double lat = todasParagensMapa.get(i).getLatitude();
            double log = todasParagensMapa.get(i).getLongitude();


            Location locationB = new Location("point B");
            locationB.setLatitude(lat);
            locationB.setLongitude(log);

            Location locationA = new Location("point A");
            locationA.setLatitude(ponto.latitude);
            locationA.setLongitude(ponto.longitude);

            float distancia = locationA.distanceTo(locationB);

            if (distanciaOriginal > distancia) {
                distanciaOriginal = distancia;
                idProximaParagem = todasParagensMapa.get(i).getId();

            }

        }

        for (int x = 0; x < todasParagensMapa.size(); x++){

            double lat = todasParagensMapa.get(x).getLatitude();
            double log = todasParagensMapa.get(x).getLongitude();
            LatLng maisProxima = new LatLng(lat,log);
            if(idProximaParagem == todasParagensMapa.get(x).getId()){
                mMapa.addMarker(new MarkerOptions()
                                .position(maisProxima)
                                .title(todasParagensMapa.get(x).getNome())
                );

                paragemMaisProximaLatX = todasParagensMapa.get(x).getLatitude();
                paragemMaisProximaLog = todasParagensMapa.get(x).getLongitude();
                idParagemMaisProxima = String.valueOf(todasParagensMapa.get(x).getId());
            }
        }


    }




    //Utilizador escolhe uma localização para fazer uma rota desde da sua posição actual ate esta
    public void pesquisarMapa() {

        //Boolean de adicionar favoritos
        verificarPonto = false;
        mMapa.clear();
        mMapa.addMarker(new MarkerOptions().position(myPlace).title("Origem"));


        //Obter dados do destino atraves do nome deste
        String searchPlace = search.getQuery().toString() + " Castelo Branco";
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchPlace, 1);
        } catch (IOException e) {}


        //Verificar Se existe destino
        if (list.size() >= 1) {

            //Boolean Para edicar se podemos adicionar locar aos favoritos
            verificarPonto = true;

            //Marcado paragem mais proxima do utilizador
            paragemProximaMim(myPlace);
            LatLng nearMe = new LatLng(paragemMaisProximaLatX, paragemMaisProximaLog);
            String idParagem1 = idParagemMaisProxima;
            double nearMeLatitude = paragemMaisProximaLatX;
            double nearMeLongitude = paragemMaisProximaLog;

            //Metodos calular rota a pe do origem
            String urlOrigem = getRequestUrlWalking(myPlace, nearMe);
            TaskRequestDirectionsWalking taskRequestDirectionsOrigem = new TaskRequestDirectionsWalking();
            taskRequestDirectionsOrigem.execute(urlOrigem);


            //Obter Destino
            Address address = list.get(0);
            LatLng destino = new LatLng(address.getLatitude(), address.getLongitude());

            //Marcardor da paragem mais proxima do  destino
            paragemProximaMim(destino);
            LatLng nearDestino = new LatLng(paragemMaisProximaLatX, paragemMaisProximaLog);
            mMapa.addMarker(new MarkerOptions().position(destino).title("DESTINO"));
            String idParagem2 = idParagemMaisProxima;


            //Metodos calular rota a pe do destion
            String urlDestino = getRequestUrlWalking(nearDestino, destino);
            TaskRequestDirectionsWalking taskRequestDirectionsDestino = new TaskRequestDirectionsWalking();
            taskRequestDirectionsDestino.execute(urlDestino);


            //Dados para variaveis dos Favoritos
            latitudeFav = address.getLatitude();
            longitudeFav = address.getLongitude();


            //Obter Horas do sistema
            int currentHour = Calendar.getInstance().getTime().getHours();
            int currentMinutes = Calendar.getInstance().getTime().getMinutes();
            String horasSistema = String.valueOf(currentHour) +"."+String.valueOf(currentMinutes);




            //Desenhar caminhos do autocarro mapa
            verficarMesmaRota(idParagem1,idParagem2);
            //SE DUAS PARAGENS FIZEREM PARTE DA MESMA ROTA
            if(memaRota != -1){

                //VER SE EXISTEM ROTAS PARA O HORARIO PRESENTE, SE NAO MUDAR PARA O PROXIMO DIA
                obterRotaCerta(String.valueOf(memaRota),horasSistema);
                if(todasParagensRotaCerta.size() == 0) {
                    obterRotaCerta(String.valueOf(memaRota), "0.0");
                }

                //VIR BUSCAR O ID DA PRIMEIRA PARAGEM A DESENHAR
                int aux = 0;
                for (int x = 0; x < todasParagensRotaCerta.size(); x++){
                    if(idParagem1 == String.valueOf(todasParagensRotaCerta.get(x).getId())) {
                        aux = x + 1;
                        break;
                    }
                }


                //DESENHAR O CAMINHO ENTRE PARAGENS
                for (int i = aux; i < todasParagensRotaCerta.size(); i++){

                    //COORDENADAS DA PARAGEM INICIAL
                    if(i == aux) {
                        latAnterior = nearMeLatitude;
                        logAnterior = nearMeLongitude;
                    }

                    LatLng latLogAnterior = new LatLng(latAnterior, logAnterior);




                    //SETUP DA NOVA PARAGEM
                    double lat = todasParagensRotaCerta.get(i).getLatitude();
                    double log = todasParagensRotaCerta.get(i).getLongitude();
                    LatLng latLog = new LatLng(lat,log);


                    //MARCAR TODOS OS PONTOS NO MAPA MENOS O DESTINO QUE JA SE ENCONTRA MARCADO
                    if( idParagemMaisProxima  != String.valueOf(todasParagensRotaCerta.get(i).getId())) {
                        mMapa.addMarker(new MarkerOptions()
                                .position(latLog)
                                .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                    }


                    //Desenhar no Mapa
                    String urlx = getRequestUrl(latLogAnterior, latLog);
                    TaskRequestDirections taskRequestDirectionsx = new TaskRequestDirections();
                    taskRequestDirectionsx.execute(urlx);



                    //GUARDAR PARAGEM ANTERIRO
                    latAnterior = todasParagensRotaCerta.get(i).getLatitude();
                    logAnterior = todasParagensRotaCerta.get(i).getLongitude();


                    //PARA O METODO NA PARAGEM DE DESTINO
                    if( Integer.parseInt(idParagemMaisProxima )  == todasParagensRotaCerta.get(i).getId())
                        break;


                }




            }
            //SE AS DUAS PARAGENS NAO FIZEREM PARTE DA MESMA ROTA
            else {

                getRotasDaParagem(idParagem1);
                int [] rotasParagem1 = rotasDeParagem;

                getRotasDaParagem(idParagem2);
                int [] rotasParagem2 = rotasDeParagem;

                int rotaComumOrigem = 0;
                int rotaComumDestino = 0;

                //VERIFICAR QUAIS AS DUAS ROTAS QUE PASSAM NA MESMA PARAGEM
                for(int i = 0; i < rotasParagem1.length; i++){
                    for( int x = 0; x < rotasParagem2.length; x++){
                        getParagemDeDuasRotas(String.valueOf(rotasParagem1[i]) , String.valueOf(rotasParagem2[x]));
                        if(!paragemComuns.isEmpty()){
                            rotaComumOrigem = rotasParagem1[i];
                            rotaComumDestino = rotasParagem2[x];
                            break;
                        }
                    }
                }

                //SE NAO EXISTIR NENHUMA PARAR O METODO
                if(paragemComuns.isEmpty()) {
                    Toast.makeText(this, "Não Existem Rotas Para esse Destino", Toast.LENGTH_SHORT).show();
                    return;
                }


                //DESENHAR MARCADOR DA PARAGEM COMUM
                double latCoumum = paragemComuns.get(0).getLatitude();
                double logComum = paragemComuns.get(0).getLongitude();
                LatLng paragemDoMeio = new LatLng(latCoumum,logComum);
                mMapa.addMarker(new MarkerOptions()
                        .position(paragemDoMeio)
                        .title(paragemComuns.get(0).getNome()));


                //DESENHAR DESDE ORIGEM ATE PARAGEM COMUM



                //VERIFICAR HORARIOS
                obterRotaCerta(String.valueOf(rotaComumOrigem),horasSistema);
                if(todasParagensRotaCerta.size() == 0) {
                    obterRotaCerta(String.valueOf(rotaComumOrigem), "0.0");
                }

                //VIR BUSCAR O ID DA PRIMEIRA PARAGEM A DESENHAR
                int aux = 0;
                for (int x = 0; x < todasParagensRotaCerta.size(); x++){
                    if(idParagem1 == String.valueOf(todasParagensRotaCerta.get(x).getId())) {
                        aux = x + 1;
                        break;
                    }
                }

                //DESENHAR O CAMINHO ENTRE PARAGENS ATE A COMUM
                for (int i = aux; i < todasParagensRotaCerta.size(); i++){

                    //COORDENADAS DA PARAGEM INICIAL
                    if(i == aux) {
                        latAnterior = nearMeLatitude;
                        logAnterior = nearMeLongitude;
                    }

                    LatLng latLogAnterior = new LatLng(latAnterior, logAnterior);




                    //SETUP DA NOVA PARAGEM
                    double lat = todasParagensRotaCerta.get(i).getLatitude();
                    double log = todasParagensRotaCerta.get(i).getLongitude();
                    LatLng latLog = new LatLng(lat,log);


                    //MARCAR TODOS OS PONTOS NO MAPA MENOS O DESTINO QUE JA SE ENCONTRA MARCADO
                    if( paragemComuns.get(0).getId()  != todasParagensRotaCerta.get(i).getId()) {
                        mMapa.addMarker(new MarkerOptions()
                                .position(latLog)
                                .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                    }


                    //Desenhar no Mapa
                    String urlx = getRequestUrl(latLogAnterior, latLog);
                    TaskRequestDirections taskRequestDirectionsx = new TaskRequestDirections();
                    taskRequestDirectionsx.execute(urlx);



                    //GUARDAR PARAGEM ANTERIRO
                    latAnterior = todasParagensRotaCerta.get(i).getLatitude();
                    logAnterior = todasParagensRotaCerta.get(i).getLongitude();


                    //PARA O METODO NA PARAGEM DE DESTINO
                    if( paragemComuns.get(0).getId()  == todasParagensRotaCerta.get(i).getId())
                        break;


                }

//--------------------------------------------------------------------------------------------------------------------

                //DESENHAR DESDE PARAGEM COMUM ATE DESTINO

                //VERIFICAR HORARIOS
                obterRotaCerta(String.valueOf(rotaComumDestino),horasSistema);
                if(todasParagensRotaCerta.size() == 0) {
                    obterRotaCerta(String.valueOf(rotaComumDestino), "0.0");
                }

                //VIR BUSCAR O ID DA PRIMEIRA PARAGEM A DESENHAR
                int auxDest = 0;
                for (int x = 0; x < todasParagensRotaCerta.size(); x++){
                    if(idParagem1 == String.valueOf(todasParagensRotaCerta.get(x).getId())) {
                        auxDest = x + 1;
                        break;
                    }
                }


                //DESENHAR O CAMINHO ENTRE PARAGENS ATE A COMUM
                for (int i = auxDest; i < todasParagensRotaCerta.size(); i++){

                    //COORDENADAS DA PARAGEM INICIAL
                    if(i == auxDest) {
                        latAnterior = paragemComuns.get(0).getLatitude();
                        logAnterior = paragemComuns.get(0).getLongitude();
                    }

                    LatLng latLogAnterior = new LatLng(latAnterior, logAnterior);




                    //SETUP DA NOVA PARAGEM
                    double lat = todasParagensRotaCerta.get(i).getLatitude();
                    double log = todasParagensRotaCerta.get(i).getLongitude();
                    LatLng latLog = new LatLng(lat,log);


                    //MARCAR TODOS OS PONTOS NO MAPA MENOS O DESTINO QUE JA SE ENCONTRA MARCADO
                    if( Integer.parseInt(idParagemMaisProxima )  != todasParagensRotaCerta.get(i).getId()) {
                        mMapa.addMarker(new MarkerOptions()
                                .position(latLog)
                                .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                    }


                    //Desenhar no Mapa
                    String urlx = getRequestUrlRota(latLogAnterior, latLog);
                    TaskRequestDirectionsRota taskRequestDirectionsx = new TaskRequestDirectionsRota();
                    taskRequestDirectionsx.execute(urlx);



                    //GUARDAR PARAGEM ANTERIRO
                    latAnterior = todasParagensRotaCerta.get(i).getLatitude();
                    logAnterior = todasParagensRotaCerta.get(i).getLongitude();


                    //PARA O METODO NA PARAGEM DE DESTINO
                    if( Integer.parseInt(idParagemMaisProxima )  == todasParagensRotaCerta.get(i).getId())
                        break;


                }





            }



        }


    }

    //METODO PARA LONGCLICK FUNCIONAR
    public void clickMapRota(LatLng destino){

        //Boolean de adicionar favoritos
        verificarPonto = false;
        mMapa.clear();
        mMapa.addMarker(new MarkerOptions().position(myPlace).title("Origem"));


            //Boolean Para edicar se podemos adicionar locar aos favoritos
            verificarPonto = true;

            //Marcado paragem mais proxima do utilizador
            paragemProximaMim(myPlace);
            LatLng nearMe = new LatLng(paragemMaisProximaLatX, paragemMaisProximaLog);
            String idParagem1 = idParagemMaisProxima;
            double nearMeLatitude = paragemMaisProximaLatX;
            double nearMeLongitude = paragemMaisProximaLog;

            //Metodos calular rota a pe do origem
            String urlOrigem = getRequestUrlWalking(myPlace, nearMe);
            TaskRequestDirectionsWalking taskRequestDirectionsOrigem = new TaskRequestDirectionsWalking();
            taskRequestDirectionsOrigem.execute(urlOrigem);


            //Marcardor da paragem mais proxima do  destino
            paragemProximaMim(destino);
            LatLng nearDestino = new LatLng(paragemMaisProximaLatX, paragemMaisProximaLog);
            mMapa.addMarker(new MarkerOptions().position(destino).title("DESTINO"));
            String idParagem2 = idParagemMaisProxima;


            //Metodos calular rota a pe do destion
            String urlDestino = getRequestUrlWalking(nearDestino, destino);
            TaskRequestDirectionsWalking taskRequestDirectionsDestino = new TaskRequestDirectionsWalking();
            taskRequestDirectionsDestino.execute(urlDestino);


            //Dados para variaveis dos Favoritos
            latitudeFav = destino.latitude;
            longitudeFav = destino.longitude;


            //Obter Horas do sistema
            int currentHour = Calendar.getInstance().getTime().getHours();
            int currentMinutes = Calendar.getInstance().getTime().getMinutes();
            String horasSistema = String.valueOf(currentHour) +"."+String.valueOf(currentMinutes);




            //Desenhar caminhos do autocarro mapa
            verficarMesmaRota(idParagem1,idParagem2);
            //SE DUAS PARAGENS FIZEREM PARTE DA MESMA ROTA
            if(memaRota != -1){

                //VER SE EXISTEM ROTAS PARA O HORARIO PRESENTE, SE NAO MUDAR PARA O PROXIMO DIA
                obterRotaCerta(String.valueOf(memaRota),horasSistema);
                if(todasParagensRotaCerta.size() == 0) {
                    obterRotaCerta(String.valueOf(memaRota), "0.0");
                }

                //VIR BUSCAR O ID DA PRIMEIRA PARAGEM A DESENHAR
                int aux = 0;
                for (int x = 0; x < todasParagensRotaCerta.size(); x++){
                    if(idParagem1 == String.valueOf(todasParagensRotaCerta.get(x).getId())) {
                        aux = x + 1;
                        break;
                    }
                }


                //DESENHAR O CAMINHO ENTRE PARAGENS
                for (int i = aux; i < todasParagensRotaCerta.size(); i++){

                    //COORDENADAS DA PARAGEM INICIAL
                    if(i == aux) {
                        latAnterior = nearMeLatitude;
                        logAnterior = nearMeLongitude;
                    }

                    LatLng latLogAnterior = new LatLng(latAnterior, logAnterior);




                    //SETUP DA NOVA PARAGEM
                    double lat = todasParagensRotaCerta.get(i).getLatitude();
                    double log = todasParagensRotaCerta.get(i).getLongitude();
                    LatLng latLog = new LatLng(lat,log);


                    //MARCAR TODOS OS PONTOS NO MAPA MENOS O DESTINO QUE JA SE ENCONTRA MARCADO
                    if( idParagemMaisProxima  != String.valueOf(todasParagensRotaCerta.get(i).getId())) {
                        mMapa.addMarker(new MarkerOptions()
                                .position(latLog)
                                .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                    }


                    //Desenhar no Mapa
                    String urlx = getRequestUrl(latLogAnterior, latLog);
                    TaskRequestDirections taskRequestDirectionsx = new TaskRequestDirections();
                    taskRequestDirectionsx.execute(urlx);



                    //GUARDAR PARAGEM ANTERIRO
                    latAnterior = todasParagensRotaCerta.get(i).getLatitude();
                    logAnterior = todasParagensRotaCerta.get(i).getLongitude();


                    //PARA O METODO NA PARAGEM DE DESTINO
                    if( Integer.parseInt(idParagemMaisProxima )  == todasParagensRotaCerta.get(i).getId())
                        break;


                }




            }
            //SE AS DUAS PARAGENS NAO FIZEREM PARTE DA MESMA ROTA
            else {

                getRotasDaParagem(idParagem1);
                int [] rotasParagem1 = rotasDeParagem;

                getRotasDaParagem(idParagem2);
                int [] rotasParagem2 = rotasDeParagem;

                int rotaComumOrigem = 0;
                int rotaComumDestino = 0;

                //VERIFICAR QUAIS AS DUAS ROTAS QUE PASSAM NA MESMA PARAGEM
                for(int i = 0; i < rotasParagem1.length; i++){
                    for( int x = 0; x < rotasParagem2.length; x++){
                        getParagemDeDuasRotas(String.valueOf(rotasParagem1[i]) , String.valueOf(rotasParagem2[x]));
                        if(!paragemComuns.isEmpty()){
                            rotaComumOrigem = rotasParagem1[i];
                            rotaComumDestino = rotasParagem2[x];
                            break;
                        }
                    }
                }

                //SE NAO EXISTIR NENHUMA PARAR O METODO
                if(paragemComuns.isEmpty()) {
                    Toast.makeText(this, "Não Existem Rotas Para esse Destino", Toast.LENGTH_SHORT).show();
                    return;
                }


                //DESENHAR MARCADOR DA PARAGEM COMUM
                double latCoumum = paragemComuns.get(0).getLatitude();
                double logComum = paragemComuns.get(0).getLongitude();
                LatLng paragemDoMeio = new LatLng(latCoumum,logComum);
                mMapa.addMarker(new MarkerOptions()
                        .position(paragemDoMeio)
                        .title(paragemComuns.get(0).getNome()));


                //DESENHAR DESDE ORIGEM ATE PARAGEM COMUM



                //VERIFICAR HORARIOS
                obterRotaCerta(String.valueOf(rotaComumOrigem),horasSistema);
                if(todasParagensRotaCerta.size() == 0) {
                    obterRotaCerta(String.valueOf(rotaComumOrigem), "0.0");
                }

                //VIR BUSCAR O ID DA PRIMEIRA PARAGEM A DESENHAR
                int aux = 0;
                for (int x = 0; x < todasParagensRotaCerta.size(); x++){
                    if(idParagem1 == String.valueOf(todasParagensRotaCerta.get(x).getId())) {
                        aux = x + 1;
                        break;
                    }
                }

                //DESENHAR O CAMINHO ENTRE PARAGENS ATE A COMUM
                for (int i = aux; i < todasParagensRotaCerta.size(); i++){

                    //COORDENADAS DA PARAGEM INICIAL
                    if(i == aux) {
                        latAnterior = nearMeLatitude;
                        logAnterior = nearMeLongitude;
                    }

                    LatLng latLogAnterior = new LatLng(latAnterior, logAnterior);




                    //SETUP DA NOVA PARAGEM
                    double lat = todasParagensRotaCerta.get(i).getLatitude();
                    double log = todasParagensRotaCerta.get(i).getLongitude();
                    LatLng latLog = new LatLng(lat,log);


                    //MARCAR TODOS OS PONTOS NO MAPA MENOS O DESTINO QUE JA SE ENCONTRA MARCADO
                    if( paragemComuns.get(0).getId()  != todasParagensRotaCerta.get(i).getId()) {
                        mMapa.addMarker(new MarkerOptions()
                                .position(latLog)
                                .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                    }


                    //Desenhar no Mapa
                    String urlx = getRequestUrl(latLogAnterior, latLog);
                    TaskRequestDirections taskRequestDirectionsx = new TaskRequestDirections();
                    taskRequestDirectionsx.execute(urlx);



                    //GUARDAR PARAGEM ANTERIRO
                    latAnterior = todasParagensRotaCerta.get(i).getLatitude();
                    logAnterior = todasParagensRotaCerta.get(i).getLongitude();


                    //PARA O METODO NA PARAGEM DE DESTINO
                    if( paragemComuns.get(0).getId()  == todasParagensRotaCerta.get(i).getId())
                        break;


                }

//--------------------------------------------------------------------------------------------------------------------

                //DESENHAR DESDE PARAGEM COMUM ATE DESTINO

                //VERIFICAR HORARIOS
                obterRotaCerta(String.valueOf(rotaComumDestino),horasSistema);
                if(todasParagensRotaCerta.size() == 0) {
                    obterRotaCerta(String.valueOf(rotaComumDestino), "0.0");
                }

                //VIR BUSCAR O ID DA PRIMEIRA PARAGEM A DESENHAR
                int auxDest = 0;
                for (int x = 0; x < todasParagensRotaCerta.size(); x++){
                    if(idParagem1 == String.valueOf(todasParagensRotaCerta.get(x).getId())) {
                        auxDest = x + 1;
                        break;
                    }
                }


                //DESENHAR O CAMINHO ENTRE PARAGENS ATE A COMUM
                for (int i = auxDest; i < todasParagensRotaCerta.size(); i++){

                    //COORDENADAS DA PARAGEM INICIAL
                    if(i == auxDest) {
                        latAnterior = paragemComuns.get(0).getLatitude();
                        logAnterior = paragemComuns.get(0).getLongitude();
                    }

                    LatLng latLogAnterior = new LatLng(latAnterior, logAnterior);




                    //SETUP DA NOVA PARAGEM
                    double lat = todasParagensRotaCerta.get(i).getLatitude();
                    double log = todasParagensRotaCerta.get(i).getLongitude();
                    LatLng latLog = new LatLng(lat,log);


                    //MARCAR TODOS OS PONTOS NO MAPA MENOS O DESTINO QUE JA SE ENCONTRA MARCADO
                    if( Integer.parseInt(idParagemMaisProxima )  != todasParagensRotaCerta.get(i).getId()) {
                        mMapa.addMarker(new MarkerOptions()
                                .position(latLog)
                                .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                    }


                    //Desenhar no Mapa
                    String urlx = getRequestUrlRota(latLogAnterior, latLog);
                    TaskRequestDirectionsRota taskRequestDirectionsx = new TaskRequestDirectionsRota();
                    taskRequestDirectionsx.execute(urlx);



                    //GUARDAR PARAGEM ANTERIRO
                    latAnterior = todasParagensRotaCerta.get(i).getLatitude();
                    logAnterior = todasParagensRotaCerta.get(i).getLongitude();


                    //PARA O METODO NA PARAGEM DE DESTINO
                    if( Integer.parseInt(idParagemMaisProxima )  == todasParagensRotaCerta.get(i).getId())
                        break;


                }





            }





    }




    //Metodos do algoritmo de desenhar linha de caminhos autocarro

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMapa.setMyLocationEnabled(true);
                }
                break;
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }




            if (polylineOptions != null) {
                mMapa.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
                mMapa.clear();
                return;
            }


        }
    }

    //Metodos do algoritmo de desenhar linha de caminhos a pe

    private String getRequestUrlWalking(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=walking";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirectionWalking(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirectionsWalking extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirectionWalking(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParserWalking taskParser = new TaskParserWalking();
            taskParser.execute(s);
        }
    }

    public class TaskParserWalking extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.GREEN);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMapa.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
                mMapa.clear();
                return;
            }

        }
    }

    //Metodos do algoritomo de desenhar para rota diferente

    private String getRequestUrlRota(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirectionRota(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirectionsRota extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirectionRota(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParserRota taskParser = new TaskParserRota();
            taskParser.execute(s);
        }
    }

    public class TaskParserRota extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMapa.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
                mMapa.clear();
                return;
            }

        }
    }

}
