package com.ist174008.prof.cmov.cmov;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.List;



public class BookBikeActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_book_bike);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public LatLng getCurrentLocation(){
        Intent intent = getIntent();
        Location location = (Location) intent.getExtras().get("Location");

        if(location !=null) {
            double currentLat = location.getLatitude();
            double currentLong = location.getLongitude();

            LatLng currentPos = new LatLng(currentLat,currentLong);


            Log.d("CurrentLocation", location.toString());
            return currentPos;
        }
        return null;
    }


    @Override
    public void onMapReady(GoogleMap map) {

        mMap=map;

        try {
            map.setMyLocationEnabled(true);
        }catch(SecurityException e) {
            Toast.makeText(getApplicationContext(),"location enable " + e.getMessage(),Toast.LENGTH_LONG).show();
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLocation(), 2));

        List<LatLng>  stationList = ((Global) this.getApplication()).getStations();
        Toast.makeText(getApplicationContext(),"stations for marker" + stationList,Toast.LENGTH_LONG).show();


        Intent intent = getIntent();
        int numberOfStations = (int) intent.getExtras().get("NumberOfStations");

        for (int i = 0 ; i < numberOfStations; i++){

            map.addMarker(new MarkerOptions()
                    .title("Station " + (i+1))
                    .position(stationList.get(i)));
        }

        map.addMarker(new MarkerOptions()
                .title("You are HERE")
                .position(getCurrentLocation()));

        mMap.setOnInfoWindowClickListener(this);
    }



    @Override
    public void onInfoWindowClick(Marker marker) {

        if(marker.getTitle().equals("Station 1")) {
            Intent intent = new Intent(getApplicationContext(), Station1Activity.class);
            startActivity(intent);
        }
        if(marker.getTitle().equals("Station 2")) {
            Intent intent = new Intent(getApplicationContext(), Station2Activity.class); // CHANGE INTENT!!!!
            startActivity(intent);
        }
    }
}

