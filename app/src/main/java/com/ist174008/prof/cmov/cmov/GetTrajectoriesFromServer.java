package com.ist174008.prof.cmov.cmov;


import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class GetTrajectoriesFromServer extends AsyncTask<String, Void, ArrayList<ArrayList<LatLng>> > {

    private static final String TAG = "GetTraj";

    private TrajectoriesActivity trajActv;
    private String str;
    private int numberOfTrajectories;
    private ArrayList<ArrayList<LatLng>> trajectories = new ArrayList<>();
    private ArrayList<LatLng> course= new ArrayList<>();

    public GetTrajectoriesFromServer(TrajectoriesActivity activity) {
        this.trajActv=activity;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected ArrayList<ArrayList<LatLng>>  doInBackground(String... inputString) {
        try {

            Log.d(TAG, "Get Traj from Server");
            Socket socket = new Socket("10.0.2.2", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "Show Trajectories");

            message.put("Username", inputString[0]);

            outBound.writeObject(message.toString());

            String str = (String) inBound.readObject();

            if(str == null) {
                socket.close();
                Log.d(TAG,"fail");
                return null;
            }

            message = new JSONObject(str);

            ArrayList<JSONObject> jsonArray = new ArrayList<>();

            outBound.writeObject("");

            for(int i = 0; i < message.getInt("Trajectories"); i++) {
                str = (String) inBound.readObject();

                jsonArray.add(new JSONObject(str));

                outBound.writeObject("");
            }
            str = (String) inBound.readObject();

            if(str == null) {
                socket.close();
                Log.d(TAG, "Fail ");
                return null;
            }

            for(JSONObject json : jsonArray) {

                for(int i = 0; i < json.getInt("Locations"); i++) {
                    LatLng latlng = new LatLng(json.getDouble("Latitude" + i),json.getDouble("Longitude" + i));
                    course.add(latlng);
                }
                trajectories.add(course);
            }
            socket.close();

        } catch (Throwable e) {
            Log.d(TAG, " Fail ");
            return null;
        }
        return trajectories;
    }



    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(ArrayList<ArrayList<LatLng>>  result) {
        if (result != null) {
            Log.d(TAG, "Should show trajectories");
            this.trajActv.setTrajectories(result);
        }
    }
}