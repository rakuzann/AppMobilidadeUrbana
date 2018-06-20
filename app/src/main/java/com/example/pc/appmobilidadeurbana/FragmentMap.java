package com.example.pc.appmobilidadeurbana;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class FragmentMap extends Fragment implements OnMapReadyCallback {

    MapView mapView;
    private GoogleMap mMap;
    View vView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vView =  inflater.inflate(R.layout.fragment_fragment_map, container, false);
        return vView;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mapView = vView.findViewById(R.id.mapView);
        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng casa  = new LatLng(39.825350, -7.507580);
        mMap.addMarker(new MarkerOptions().position(casa).title("Estou te a ver fdep joao"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(casa));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
    }
}
