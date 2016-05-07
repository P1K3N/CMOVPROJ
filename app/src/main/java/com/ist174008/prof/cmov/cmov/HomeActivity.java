package com.ist174008.prof.cmov.cmov;



import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;




public  class HomeActivity extends AppCompatActivity implements LocationListener {

    private  List<LatLng> finalStations = new ArrayList<>();
    private int numberOfStations;
    private Location actualLocation;

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        guiSetButtonListeners();

        // Setup Location manager and receiver
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET
            },10 );
            return;
        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);

        new GetStationsFromServer(this).execute();
    }


    public void setStations(List<Double> stations){
        int listSize = stations.size();
        numberOfStations = listSize/2;

        List<Double> stationsLat;
        List<Double> stationsLong;

        stationsLat = new ArrayList<>(stations.subList(0,(listSize/2)));
        stationsLong = new ArrayList<>(stations.subList((listSize/2),listSize));


        for (int i = 0 ; i < numberOfStations; i++) {
            finalStations.add(new LatLng(stationsLat.get(i), stationsLong.get(i)));
        }

        ((Global) this.getApplication()).setStations(finalStations);
    }


    /* Button listeners
    */


    private View.OnClickListener listenerBookBike= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BookBikeActivity.class);
                intent.putExtra("Location", actualLocation);
                intent.putExtra("NumberOfStations",numberOfStations);

                startActivity(intent);
            }
        };


    public void onSocial(View view) {
        Intent intent = new Intent(this, SocialActivity.class);
        startActivity(intent);

    }

    public void onUserInfo(View view) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);

    }

    public void onTrajectories(View view) {
        Intent intent = new Intent(this, TrajectoriesActivity.class);
        startActivity(intent);

    }

    @Override
    public void onLocationChanged(Location location) {
        actualLocation=location;
        Log.d("GPS", "Location Changed " + location.toString());
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

    private void guiSetButtonListeners(){
        findViewById(R.id.buttonBookBike).setOnClickListener(listenerBookBike);

    }
}


