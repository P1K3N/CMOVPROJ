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
    private static final String NO_TRAJS = "No trajectories to show";

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
                Log.d(TAG,"Wrong username");
                return null;
            }

            message = new JSONObject(str);

            ArrayList<JSONObject> jsonArray = new ArrayList<JSONObject>();

            outBound.writeObject("");

            numberOfTrajectories = message.getInt("Trajectories");

            for(int i = 0; i < message.getInt("Trajectories"); i++) {
                str = (String) inBound.readObject();

                jsonArray.add(new JSONObject(str));

                outBound.writeObject("");
            }
            str = (String) inBound.readObject();

            if(str == null) {
                socket.close();
                Log.d(TAG, "Fail after getting trajectories ");
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
            Log.d(TAG, " Fail everything");
            return null;
        }
        return trajectories;
    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(ArrayList<ArrayList<LatLng>>  result) {
        ListView listTraj= trajActv.getListView();

        if (result != null) {
            Log.d(TAG, "Should show trajectories");
            this.trajActv.setTrajectories(result);

            ArrayList<String> listStr=new ArrayList<>();

            for (int i = 0; i < numberOfTrajectories; i++) {
                listStr.add("Trajectory " + i + 1);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(trajActv.getApplicationContext(),
                    R.layout.list_black_text, R.id.list_content, listStr);

            listTraj.setAdapter(adapter);
        }else {
            String[] val = {NO_TRAJS};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(trajActv.getApplicationContext(), R.layout.list_black_text, R.id.list_content, val);
            listTraj.setAdapter(adapter);
        }
    }
}