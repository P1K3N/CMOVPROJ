package com.ist174008.prof.cmov.cmov;


import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GetTrajectoriesFromServer extends AsyncTask<String, Void, List<LatLng>> {

    private static final String TAG = "GetTraj";

    private TrajectoriesActivity trajActv;
    private String str;
    private int numberOfTrajectories;
    private List<LatLng> traj = new ArrayList<>();

    public GetTrajectoriesFromServer(TrajectoriesActivity activity) {
        this.trajActv=activity;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected List<LatLng> doInBackground(String... inputString) {
        try {
            Socket socket = new Socket("10.0.2.2", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "Show Trajectories");

            message.put("Username", inputString[0]);

            outBound.writeObject(message.toString());

            str =  (String) inBound.readObject();

            //message = new JSONObject(str);

            // int numberOfTrajectories = message.getInt("Number Trajectories");

            /*
            if(numberOfTrajectories == 0){
                socket.close();
            }else{
                for(int i=0; i<numberOfTrajectores;i++){


                    LatLng traj(i) = new LatLng(message.getString("Latitude"),message.getString("Longitude"));

                }
            }

             */

            if(str == null) {
                socket.close();
                Log.v(TAG, "Wrong username or password...");
                throw new SecurityException("Wrong username or password...");
            }

            socket.close();

        } catch (Throwable e) {
            Log.v(TAG, "fail" + e.getMessage());
        }

        return traj;
    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(List<LatLng> result) {
        ListView list = trajActv.getListView();
        //this.trajActv.setTrajectories();

        if (result != null) {
            ArrayList<String> listStr=new ArrayList<>();

            for(int i=0;i<numberOfTrajectories;i++) {
                 listStr.add("Trajectory " + i+1);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(trajActv.getApplicationContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1,listStr);

            list.setAdapter(adapter);
            //adapter.add(result);

        }else{
            String[] val = {"No trajectories to show"};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(trajActv.getApplicationContext(),R.layout.list_black_text,R.id.list_content,val);
            list.setAdapter(adapter);
        }
    }
}