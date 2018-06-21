package com.example.pc.appmobilidadeurbana;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class TaxiActivity extends AppCompatActivity {

    LocationManager lm;
    LatLng myPlace;
    TextView localAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);


        localAtual = findViewById(R.id.txtDisplay);

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
                    Geocoder geocoder = new Geocoder(TaxiActivity.this);

                    try {
                        List<Address> list = geocoder.getFromLocation(lat, log, 1);
                        String str = list.get(0).getAddressLine(0);
                        String str2 = list.get(0).toString();
                        Log.d("CENAS: ", str2);
                        localAtual.setText(str);
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
                    Geocoder geocoder = new Geocoder(TaxiActivity.this);

                    try {
                        List<Address> list = geocoder.getFromLocation(lat, log, 1);
                        String str = list.get(0).getAddressLine(0);
                        String str2 = list.get(0).toString();
                        Log.d("CENAS: ", str2);

                        localAtual.setText(str);
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
    }

    public void dialPhoneNumber(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "926073934"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
