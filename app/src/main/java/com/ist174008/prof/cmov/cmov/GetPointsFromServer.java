package com.ist174008.prof.cmov.cmov;

import android.app.Activity;
import android.content.Context;
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
public class GetPointsFromServer extends AsyncTask<String, Void, Integer> {

    private static final String TAG = "GetPoints";
    private InfoActivity infoActivity;
    private Integer serverPoints;
    private String userName;

    public GetPointsFromServer(InfoActivity v){
        this.infoActivity=v;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected Integer doInBackground(String... inputString) {
        try {
            Log.d(TAG, "Getting Points");
            Socket socket = new Socket("10.0.2.2", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "User Info");

            message.put("Username", inputString[0]);

            userName = inputString[0];

            outBound.writeObject(message.toString());

            String response = (String) inBound.readObject();

            if(response == null) {
                socket.close();
                Log.d(TAG, "Wrong username or password...");
            }

            message = new JSONObject(response);

            serverPoints = message.getInt("Score");
            socket.close();

        } catch (Throwable e) {
            Log.d(TAG, "fail" + e.getMessage());
        }

        return serverPoints;

    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(Integer result) {
        if(result !=null) {
            Log.d(TAG, "Display Points");
            infoActivity.updateServerPoints(result);
        }
    }
}