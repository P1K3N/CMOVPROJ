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
    private int points;
    private LatLng myLoc;
    private int numberOfStation;
    private ArrayList<ArrayList<LatLng>> trajectories = new ArrayList<>();

    private boolean userNearBike = false;
    private boolean nearStation1 = false;
    private boolean nearStation2 = false;
    private String bookStation = "no";
    private boolean pickedBike = false;
    private boolean biking = false;
    private String sessionToken;


    public ArrayList<ArrayList<LatLng>> getTrajectories() {
        return trajectories;
    }

    public void setTrajectories(ArrayList<ArrayList<LatLng>> trajectories) {
        this.trajectories = trajectories;
    }

    public int getNumberOfStation() {
        return numberOfStation;
    }

    public void setNumberOfStation(int numberOfStation) {
        this.numberOfStation = numberOfStation;
    }

    public void addTrajectories(ArrayList<LatLng> courses) {
        this.trajectories.add(courses);
    }

    public int getNumberOfTrajectories() {
        return this.trajectories.size();
    }

    public boolean isBiking() {
        return biking;
    }

    public void setBiking(boolean biking) {
        this.biking = biking;
    }

    public boolean hasPickedBike() {
        return pickedBike;
    }

    public void setPickedBike(boolean pickedBike) {
        this.pickedBike = pickedBike;
    }

    public String getBookStation() {
        return bookStation;
    }

    public void setBookStation(String bookStation) {
        this.bookStation = bookStation;
    }

    public boolean isNearStation1() {
        return nearStation1;
    }

    public void setNearStation1(boolean nearStation1) {
        this.nearStation1 = nearStation1;
    }

    public boolean isNearStation2() {
        return nearStation2;
    }

    public void setNearStation2(boolean nearStation2) {
        this.nearStation2 = nearStation2;
    }

    public boolean isUserNearBike() {
        return userNearBike;
    }

    public void setUserNearBike(boolean userNearBike) {
        this.userNearBike = userNearBike;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int points) {
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


    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String u) {
        this.sessionToken = u;}
}