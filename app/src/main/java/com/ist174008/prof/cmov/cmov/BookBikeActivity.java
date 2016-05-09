package com.ist174008.prof.cmov.cmov;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
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

import java.util.List;



public class BookBikeActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static final String TAG = "BookBike";

    private GoogleMap mGoogleMap;
    private  String userName;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;


    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_book_bike);

        // Setup Location manager and receiver
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET
            },10 );
            return;
        }

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


    @Override
    public void onMapReady(GoogleMap map) {

        mGoogleMap=map;

        buildGoogleApiClient();
        try {
            mGoogleMap.setMyLocationEnabled(true);
        }catch (SecurityException e){
            Toast.makeText(getApplicationContext(),"Permission denied?",Toast.LENGTH_SHORT).show();
        }

        List<LatLng>  stationList = ((Global) this.getApplication()).getStations();
        Toast.makeText(getApplicationContext(),"stations for marker" + stationList,Toast.LENGTH_LONG).show();


        Intent intent = getIntent();
        int numberOfStations = (int) intent.getExtras().get("NumberOfStations");

        for (int i = 0 ; i < numberOfStations; i++){

           mGoogleMap.addMarker(new MarkerOptions()
                    .title("Station " + (i+1))
                    .position(stationList.get(i)));
        }

            mGoogleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(37.35, -122.0))
                    .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
                    .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
                    .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
                    .add(new LatLng(37.35, -122.0)) // Closes the polyline.
                    .width(3)
                    .color(Color.BLUE));



        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        userName = ((Global) this.getApplication()).getUser();

        if(marker.getTitle().equals("Station 1")) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            Toast.makeText(getApplicationContext(),"Bike Successfully Booked",Toast.LENGTH_SHORT).show();

            new AlertServerBookBike().execute(userName,"Station 1");

            startActivity(intent);
        }
        if(marker.getTitle().equals("Station 2")) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            Toast.makeText(getApplicationContext(),"Bike Successfully Booked",Toast.LENGTH_SHORT).show();

            new AlertServerBookBike().execute(userName, "Station 2");

            startActivity(intent);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
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

       // Toast.makeText(getApplicationContext(),"DISTANCE1111: " + mLastLocation.distanceTo(location),Toast.LENGTH_LONG).show();

        /*float[] distance={0.0f};
        Location.distanceBetween(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude(),
                location.getLatitude(),
                location.getLongitude(),
                distance);*/

        float points=mLastLocation.distanceTo(location);

        ((Global) this.getApplication()).addPoints(points);
        Toast.makeText(getApplicationContext(),"DISTANCE22: " + points,Toast.LENGTH_LONG).show();

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                .title("You are Here")
                .position(latLng));

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(4));

        LatLng thisLoc = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());

        ((Global) this.getApplication()).setMyLoc(thisLoc);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public void onProviderEnabled(String provider) {

    }


    public void onProviderDisabled(String provider) {

    }
}

