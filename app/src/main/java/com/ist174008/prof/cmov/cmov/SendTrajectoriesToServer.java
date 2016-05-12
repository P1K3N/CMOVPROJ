package com.ist174008.prof.cmov.cmov;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ist174008 on 13/04/2016.
 */
public class SendTrajectoriesToServer extends AsyncTask<String, Void, Void> {

    private static final String TAG = "SendTraj";
    private ArrayList<ArrayList<LatLng>>  trajectories = new ArrayList<>();

    @Override
    protected void onPreExecute() {}

    @Override
    protected Void doInBackground(String... inputString) {
        try {
            Log.v(TAG, "SEND TRAJS TO SERVER");
            int j = Integer.parseInt(inputString[1]); // Number of trajectories to send

            Socket socket = new Socket("10.0.2.2", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "New Trajectory");

            message.put("Username", inputString[0]);

            message.put("Locations", j);

            outBound.writeObject(message.toString());

            boolean ack = (boolean) inBound.readObject();

            if(!ack) {
                socket.close();
                Log.v(TAG, "fail" );
            }

            for(int i = 0; i < j; i++){
                for(LatLng coord: trajectories.get(i)){
                    message.put("Latitude", coord.latitude);
                    message.put("Longitude", coord.longitude);
                    outBound.writeObject(message.toString());
                    inBound.readObject();

                }
                Log.v(TAG, "SENDING Trajs");
            }

            ack = (boolean) inBound.readObject();

            if(!ack) {
                socket.close();
                Log.v(TAG, "fail");
            }

            socket.close();

        } catch (Throwable e) {
            Log.v(TAG, "fail" + e.getMessage());
        }

        return null;

    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(Void result) {}
}