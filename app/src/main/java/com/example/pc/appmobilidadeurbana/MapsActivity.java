package com.example.pc.appmobilidadeurbana;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pc.appmobilidadeurbana.objetos.DirectionsParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SearchView search;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;
    LocationManager lm;
    LatLng myPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


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
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double log = location.getLongitude();
                    myPlace = new LatLng(lat,log);
                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                    try {
                        List<Address> list  = geocoder.getFromLocation(lat,log, 1);
                        String str = list.get(0).getLocality();
                        mMap.addMarker(new MarkerOptions().position(myPlace).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPlace));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
                    } catch (IOException e) {

                    }

                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            });
        } else if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double log = location.getLongitude();
                    myPlace = new LatLng(lat,log);
                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                    try {
                        List<Address> list  = geocoder.getFromLocation(lat,log, 1);
                        String str = list.get(0).getLocality();
                        mMap.addMarker(new MarkerOptions().position(myPlace).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPlace));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
                    } catch (IOException e) {

                    }

                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
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
                pesquisarMapa ();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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


    }


    //Utilizador escolhe uma localização para fazer uma rota desde da sua posição actual ate esta
    public void pesquisarMapa () {

            mMap.clear();

           //Obter dados do destino atraves do nome deste
            String searchPlace = search.getQuery().toString();

            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = new ArrayList<>();

            try {
                list = geocoder.getFromLocationName(searchPlace, 1);
            } catch (IOException e) {

            }


            //Verificar Se existe destino
            if(list.size() >= 1){
                //Obter Destino
                Address address = list.get(0);
                LatLng destino = new LatLng(address.getLatitude(), address.getLongitude());

               //Marcardor destino
                mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));

               //Metodos de calcular a rota
                String url = getRequestUrl(myPlace, destino);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
            }if(list.size() >= 1){
            //Obter Destino
            Address address = list.get(0);
            LatLng destino = new LatLng(address.getLatitude(), address.getLongitude());

            //Marcardor destino
            mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));

            //Metodos de calcular a rota
            String url = getRequestUrl(myPlace, destino);
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);
        }




    }

    public void searchBtn (View v ){
        Intent mudar = new Intent(this, SearchActivity.class);
        startActivity(mudar);
    }
    




        private String getRequestUrl(LatLng origin, LatLng dest) {
            //Value of origin
            String str_org = "origin=" + origin.latitude +","+origin.longitude;
            //Value of destination
            String str_dest = "destination=" + dest.latitude+","+dest.longitude;
            //Set value enable the sensor
            String sensor = "sensor=false";
            //Mode for find direction
            String mode = "mode=driving";
            //Build the full param
            String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
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
            try{
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
            switch (requestCode){
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
                return  responseString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Parse json here
                TaskParser taskParser = new TaskParser();
                taskParser.execute(s);
            }
        }
        public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

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

                        points.add(new LatLng(lat,lon));
                    }

                    polylineOptions.addAll(points);
                    polylineOptions.width(15);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.geodesic(true);
                }

                if (polylineOptions!=null) {
                    mMap.addPolyline(polylineOptions);
                } else {
                    Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
                }

            }
        }

}
