package com.ist174008.prof.cmov.cmov;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ist174008 on 14/04/2016.
 */
public class Global extends Application {

    private String user;
    private String password;
    private int points;
    private List<LatLng> stations = new ArrayList<>();

    public List<LatLng> getStations() {
        return stations;
    }

    public void setStations(List<LatLng> stations) {
        this.stations = stations;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
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