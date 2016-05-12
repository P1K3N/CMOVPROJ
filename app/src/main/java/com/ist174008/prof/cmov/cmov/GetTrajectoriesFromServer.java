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
    private ArrayList<LatLng> course;

    public GetTrajectoriesFromServer(TrajectoriesActivity activity) {
        this.trajActv=activity;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected ArrayList<ArrayList<LatLng>>  doInBackground(String... inputString) {
        try {
            Socket socket = new Socket("10.0.2.2", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "Show Trajectories");

            message.put("Username", inputString[0]);

            outBound.writeObject(message.toString());

            str =  (String) inBound.readObject();

            if(str == null) {
                socket.close();
                Log.v(TAG, "Wrong username or password...");
            }

            message = new JSONObject(str);

            numberOfTrajectories = message.getInt("Trajectories");
            Log.v(TAG, "Number of trajs:" + numberOfTrajectories);

            if(numberOfTrajectories == 0){
                socket.close();
            }else{
                for(int i=0; i<numberOfTrajectories;i++){
                    int nOfHops= message.getInt("Locations");
                    Log.v(TAG, "Number of hops:" + nOfHops);

                    for(int j=0;j<nOfHops;j++){
                        course.add(new LatLng(message.getDouble("Latitude" + j),message.getDouble("Longitude" + j)));
                    }
                    trajectories.add(course);
                }
            }
            socket.close();

        } catch (Throwable e) {
            Log.v(TAG, "fail " + e.getMessage());
        }

        return trajectories;
    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(ArrayList<ArrayList<LatLng>>  result) {
        ListView list = trajActv.getListView();


        if (result != null) {
            this.trajActv.setTrajectories(result);
            ArrayList<String> listStr=new ArrayList<>();

            for(int i=0;i<numberOfTrajectories;i++) {
                listStr.add("Trajectory " + i+1);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(trajActv.getApplicationContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1,listStr);

            list.setAdapter(adapter);

        }else{
            String[] val = {"No trajectories to show"};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(trajActv.getApplicationContext(),R.layout.list_black_text,R.id.list_content,val);
            list.setAdapter(adapter);
        }
    }
}