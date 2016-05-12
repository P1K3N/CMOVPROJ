package com.ist174008.prof.cmov.cmov;

import android.app.Application;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;


public class Global extends Application {

    private String user;
    private String password;
    private float points;
    private LatLng myLoc;
    private SimWifiP2pManager manager=null;
    private SimWifiP2pManager.Channel channel=null;
    private boolean userNearBike=false;

    public boolean isUserNearBike() {
        return userNearBike;
    }

    public void setUserNearBike(boolean userNearBike) {
        this.userNearBike = userNearBike;
    }

    public SimWifiP2pManager.Channel getChannel() {
        return channel;
    }

    public void setChannel(SimWifiP2pManager.Channel channel) {
        this.channel = channel;
    }

    public SimWifiP2pManager getManager() {
        return manager;
    }

    public void setManager(SimWifiP2pManager mManager) {
        this.manager = mManager;
    }

    private List<LatLng> stations = new ArrayList<>();

    public LatLng getMyLoc() {
        return myLoc;
    }

    public void setMyLoc(LatLng myLoc) {
        this.myLoc = myLoc;
    }

    public List<LatLng> getStations() {
        return stations;
    }

    public void setStations(List<LatLng> stations) {
        this.stations = stations;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public void addPoints(float points) {
        this.points += points;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String u) {
        this.user = u;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password = p;
    }
}