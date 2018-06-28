package com.example.pc.appmobilidadeurbana;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pc.appmobilidadeurbana.objetos.DirectionsParser;
import com.example.pc.appmobilidadeurbana.objetos.Paragem;
import com.example.pc.appmobilidadeurbana.objetos.server;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.util.HashMap;
import java.util.List;

public class SearchMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;
    LatLng origem, destino;

    //Variaveis de desenhar rota de autocarros
    double latAnterior = 0;
    double logAnterior = 0;
    float distanciaOriginal = 40000;
    int idProximaParagem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        //Array para localizar dois pontos funcionar
        listPoints = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Activar Localizador Automatico do Google
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);

        desenharRotaSearch();

    }


    public void desenharRotaSearch(){

        apanharParagens ();
        mMap.clear();


        //Carregar Dados do destino
        String getDestino = getIntent().getStringExtra("destino");
        Geocoder geocoder = new Geocoder(SearchMapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(getDestino, 1);
        } catch (IOException e) {
        }
        if (list.size() >= 1) {
            //Obter Destino
            Address address = list.get(0);
            destino = new LatLng(address.getLatitude(), address.getLongitude());
            //Marcardor destino
            mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));

        }


        //Carregar Dados da Origem
        String getOrigem = getIntent().getStringExtra("origem");
        Geocoder geocoderOrigem = new Geocoder(SearchMapActivity.this);
        List<Address> listOrigem = new ArrayList<>();
        try {
            listOrigem = geocoderOrigem.getFromLocationName(getOrigem, 1);
        } catch (IOException e) {
        }
        if (listOrigem.size() >= 1) {
            //Obter Origem
            Address address = listOrigem.get(0);
            origem = new LatLng(address.getLatitude(), address.getLongitude());
            //Marcardor Origem
            mMap.addMarker(new MarkerOptions().position(origem).title("Origem"));
        }


        //Mover Mapa Para o Origem
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origem));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));


        //Marcado paragem mais proxima do utilizador
        paragemProximaMim(origem);
        LatLng nearMe = new LatLng(paragemMaisProximaLatX, paragemMaisProximaLog);
        String idParagem1 = idParagemMaisProxima;
        double nearMeLatitude = paragemMaisProximaLatX;
        double nearMeLongitude = paragemMaisProximaLog;

        //Metodos calular rota a pe do origem
        String urlOrigem = getRequestUrlWalking(origem, nearMe);
        SearchMapActivity.TaskRequestDirectionsWalking taskRequestDirectionsOrigem = new SearchMapActivity.TaskRequestDirectionsWalking();
        taskRequestDirectionsOrigem.execute(urlOrigem);


        //Marcardor da paragem mais proxima do  destino
        paragemProximaMim(destino);
        LatLng nearDestino = new LatLng(paragemMaisProximaLatX, paragemMaisProximaLog);
        mMap.addMarker(new MarkerOptions().position(destino).title("DESTINO"));
        String idParagem2 = idParagemMaisProxima;


        //Metodos calular rota a pe do destion
        String urlDestino = getRequestUrlWalking(nearDestino, destino);
        SearchMapActivity.TaskRequestDirectionsWalking taskRequestDirectionsDestino = new SearchMapActivity.TaskRequestDirectionsWalking();
        taskRequestDirectionsDestino.execute(urlDestino);

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
                    mMap.addMarker(new MarkerOptions()
                            .position(latLog)
                            .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                }


                //Desenhar no Mapa
                String urlx = getRequestUrl(latLogAnterior, latLog);
                SearchMapActivity.TaskRequestDirections taskRequestDirectionsx = new SearchMapActivity.TaskRequestDirections();
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
            mMap.addMarker(new MarkerOptions()
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
                    mMap.addMarker(new MarkerOptions()
                            .position(latLog)
                            .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                }


                //Desenhar no Mapa
                String urlx = getRequestUrl(latLogAnterior, latLog);
                SearchMapActivity.TaskRequestDirections taskRequestDirectionsx = new SearchMapActivity.TaskRequestDirections();
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
                    mMap.addMarker(new MarkerOptions()
                            .position(latLog)
                            .title(todasParagensRotaCerta.get(i).getNome() + " Horario: " + todasParagensRotaCerta.get(i).getHorario()));
                }


                //Desenhar no Mapa
                String urlx = getRequestUrlRota(latLogAnterior, latLog);
                SearchMapActivity.TaskRequestDirectionsRota taskRequestDirectionsx = new SearchMapActivity.TaskRequestDirectionsRota();
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



    //Metodos que utilizam a base dados
    public void apanharParagens (){

        Thread t = new Thread(){
            public void run (){
                todasParagensMapa = server.postHttpGetAllParagens();
            }
        };

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                mMap.addMarker(new MarkerOptions()
                        .position(maisProxima)
                        .title(todasParagensMapa.get(x).getNome())
                );

                paragemMaisProximaLatX = todasParagensMapa.get(x).getLatitude();
                paragemMaisProximaLog = todasParagensMapa.get(x).getLongitude();
                idParagemMaisProxima = String.valueOf(todasParagensMapa.get(x).getId());
            }
        }


    }





    //Metodos de calcular a rota

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
                    mMap.setMyLocationEnabled(true);
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
            SearchMapActivity.TaskParser taskParser = new SearchMapActivity.TaskParser();
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
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();

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
            SearchMapActivity.TaskParserRota taskParser = new SearchMapActivity.TaskParserRota();
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
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();

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
            SearchMapActivity.TaskParserWalking taskParser = new SearchMapActivity.TaskParserWalking();
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
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();

            }

        }
    }

}
