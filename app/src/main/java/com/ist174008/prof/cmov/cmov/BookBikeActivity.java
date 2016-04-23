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


/**
 * Created by ist174008 on 21/03/2016.
 */
public class BookBikeActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private  List<Double> stationsLat= new ArrayList<>();
    private  List<Double> stationsLong = new ArrayList<>();
    private int numberOfStations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_book_bike);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new GetStationsFromServer(this).execute();
    }

    public void setStations(List<Double>  stations){
        int listSize = stations.size();
        numberOfStations = listSize/2;

        stationsLat = new ArrayList<>(stations.subList(0,(listSize/2)));
        stationsLong = new ArrayList<>(stations.subList((listSize/2),listSize));
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

        try {
            map.setMyLocationEnabled(true);
        }catch(SecurityException e) {
            Toast.makeText(getApplicationContext(),"location enable " + e.getMessage(),Toast.LENGTH_LONG).show();
        }

        List<LatLng> stations=new ArrayList<>();
        for (int i = 0 ; i < numberOfStations; i++){
            stations.add(new LatLng(stationsLat.get(i), stationsLong.get(i)));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLocation(), 13));

            map.addMarker(new MarkerOptions()
                    .title("Station " + i)
                    .position(stations.get(i)));
        }

        map.addMarker(new MarkerOptions()
                .title("You are HERE")
                .position(getCurrentLocation()));
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        if(marker.getTitle().equals("Station 1")) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
        if(marker.getTitle().equals("Station 2")) {
            Intent intent = new Intent(getApplicationContext(), InfoActivity.class); // CHANGE INTENT!!!!
            startActivity(intent);
        }
        if(marker.getTitle().equals("Station 3")) {
            Intent intent = new Intent(getApplicationContext(), SocialActivity.class);
            startActivity(intent);
        }

        return true;
    }
}

