package com.ist174008.prof.cmov.cmov;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.LocationRequest;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;



public class BookBikeActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static final String TAG = "BookBikeActivity";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;

    private GoogleMap mGoogleMap;
    private String userName= ((Global) this.getApplication()).getUser();

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    IntentFilter filter = new IntentFilter();
    private boolean biking =false;
    private List<LatLng> newTrajectory = new ArrayList<>();


    private int timer=0;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String trajName = intent.getExtras().getString("Trajectory Name");
            List<LatLng> traj = intent.getExtras().getParcelable("TrajectoriesForMap");

            //updateMap(trajName,traj);
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active -> LIMITATION !
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(updateReceiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_book_bike);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        filter = new IntentFilter();
        filter.addAction("UPDATE_MAP"); // ??
        registerReceiver(updateReceiver, filter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public void updateMap(String trajName, List<LatLng> traj1) {

        for (int i = 0; i < traj1.size() - 1; i++) {
            LatLng src = traj1.get(i);
            LatLng dest = traj1.get(i + 1);


            mGoogleMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude)
                    ).width(2).color(Color.BLUE).geodesic(true)
            );
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mGoogleMap = map;

        buildGoogleApiClient();
        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "Permission denied?", Toast.LENGTH_SHORT).show();
        }

        List<LatLng> stationList = ((Global) this.getApplication()).getStations();

        Intent intent = getIntent();
        int numberOfStations = (int) intent.getExtras().get("NumberOfStations");

        for (int i = 0; i < numberOfStations; i++) {

            mGoogleMap.addMarker(new MarkerOptions()
                    .title("Station " + (i + 1))
                    .position(stationList.get(i)));
        }
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {


        if (marker.getTitle().equals("Station 1")) {
            Toast.makeText(getApplicationContext(), "Bike Successfully Booked", Toast.LENGTH_SHORT).show();

            new AlertServerBookBike().execute(userName, "Station 1");
            ((Global) this.getApplication()).setBookStation("Station 1");
        }
        if (marker.getTitle().equals("Station 2")) {
            Toast.makeText(getApplicationContext(), "Bike Successfully Booked", Toast.LENGTH_SHORT).show();

            new AlertServerBookBike().execute(userName, "Station 2");
            ((Global) this.getApplication()).setBookStation("Station 2");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        Log.i(TAG, "IF FALSE nogood " + (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onConnectionSuspended(int k) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Changed " + location.toString());

        biking = ((Global) this.getApplication()).isBiking();



        if (mLastLocation != null) {

            if(biking){
                //new CreateTrajectory(mLastLocation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                newTrajectory.add(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                // When does trajectory start or stop?
                Log.d(TAG, "Adding to trajectories");
            }


            // TESTE DISTANCE FUNCTION !!!!
            float[] distance = {0.0f};

            Location.distanceBetween(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    location.getLatitude(),
                    location.getLongitude(),
                    distance);

            Toast.makeText(getApplicationContext(), "DISTANCE: " + distance[0], Toast.LENGTH_LONG).show();
            ((Global) this.getApplication()).addPoints(distance[0] / 1000);
            //
        }

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4));
        // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(6));

        LatLng thisLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        ((Global) this.getApplication()).setMyLoc(thisLoc);

        List<LatLng> stationList = ((Global) this.getApplication()).getStations();


        // Distance to stations
        float[] distanceStation1 = {0.0f};
        float[] distanceStation2 = {0.0f};
        Location.distanceBetween(stationList.get(0).latitude, stationList.get(0).longitude, thisLoc.latitude, thisLoc.longitude, distanceStation1);
        Location.distanceBetween(stationList.get(1).latitude, stationList.get(1).longitude, thisLoc.latitude, thisLoc.longitude, distanceStation2);

        // Near Stations ?
        if(distanceStation1[0] <= 20){
            ((Global) this.getApplication()).setNearStation1(true);
        }else{
            ((Global) this.getApplication()).setNearStation1(false);
        }

        if(distanceStation2[0] <= 20){
            ((Global) this.getApplication()).setNearStation2(true);
        }else{
            ((Global) this.getApplication()).setNearStation1(false);
        }

        // Near Bikes in a Station ?
        boolean nearBike = ((Global) this.getApplication()).isUserNearBike();
        String bookedStation = ((Global) this.getApplication()).getBookStation();
        boolean nearStation1 = ((Global) this.getApplication()).isNearStation1();
        boolean nearStation2 = ((Global) this.getApplication()).isNearStation2();


        // Pick up bike Station 1
        if (nearBike && (bookedStation.equals("Station 1"))) {
            if(nearStation1) {
                // Biking
                ((Global) this.getApplication()).setPickedBike(true);
                ((Global) this.getApplication()).setBiking(true);
                ((Global) this.getApplication()).setBookStation("no");

                new NotifyBikePickUp().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR);
                Log.d(TAG, "Picked up bike");
            }
        }

        // Pick up bike Station 2
        if (nearBike && (bookedStation.equals("Station 2"))) {
            if(nearStation2) {

                ((Global) this.getApplication()).setPickedBike(true);
                ((Global) this.getApplication()).setBookStation("no");
                ((Global) this.getApplication()).setBiking(true);

                new NotifyBikePickUp().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR);
                Log.d(TAG, "Picked up bike Stat2");
            }
        }

        boolean hasBike = ((Global) this.getApplication()).hasPickedBike();
        nearBike = ((Global) this.getApplication()).isUserNearBike();
        bookedStation = ((Global) this.getApplication()).getBookStation();

        //Drop Off Bike
        if(nearBike && hasBike) {
            if(nearStation1 && bookedStation.equals("no")){

                new NotifyBikeDropOff().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        userName,
                        "Station 1");

                ((Global) this.getApplication()).setPickedBike(false);
                ((Global) this.getApplication()).setBiking(false);
            }

            if(nearStation2 && bookedStation.equals("no")){

                new NotifyBikeDropOff().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        userName,
                        "Station 2");

                ((Global) this.getApplication()).setPickedBike(false);
                ((Global) this.getApplication()).setBiking(false);
            }

            Log.d(TAG, "Drop off bike");
        }
    }

   /* public class CreateTrajectory extends AsyncTask<String, Void, String> {
        private Location location;

        public CreateTrajectory(Location loc){
            this.location = loc;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(final String... inputString) {
            Log.d(TAG, "Biking background " );

            new Thread(new Runnable() {

                public void run() {

                    while (biking) {

                        newTrajectory.add(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));


                        try{
                            Thread.sleep(10000);
                        }catch (InterruptedException e){
                            Log.d(TAG,"Biking " + e.getMessage());
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }).start();

         return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        protected void onPostExecute(String result) {

        }
    }*/


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public void onProviderEnabled(String provider) {

    }


    public void onProviderDisabled(String provider) {

    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET
                }, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied, please enable GPS location", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

