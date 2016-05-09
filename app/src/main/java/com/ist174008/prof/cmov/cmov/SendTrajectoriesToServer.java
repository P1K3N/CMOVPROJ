package com.ist174008.prof.cmov.cmov;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by ist174008 on 13/04/2016.
 */
public class SendTrajectoriesToServer extends AsyncTask<String, Void, String> {

    private static final String TAG = "SendTraj";

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... inputString) {
        try {
            int j = Integer.parseInt(inputString[1]); // NUMBER OF LOCATIONS OBJECTS TO SEND

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
                message.put("Latitude", 25.672 + (i * 3.4));
                message.put("Longitude", 11.46 / (i + 1));
                outBound.writeObject(message.toString());
                inBound.readObject();
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

        return "response";

    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(String result) {}
}