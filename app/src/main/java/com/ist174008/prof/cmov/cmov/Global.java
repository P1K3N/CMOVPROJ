package com.ist174008.prof.cmov.cmov;

import android.app.Application;

/**
 * Created by ist174008 on 14/04/2016.
 */
public class Global extends Application {

    private String user;
    private String password;
    private int points;

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