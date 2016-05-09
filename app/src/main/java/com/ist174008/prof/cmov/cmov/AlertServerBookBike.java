package com.ist174008.prof.cmov.cmov;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ist174008 on 13/04/2016.
 */
public class AlertServerBookBike extends AsyncTask<String, Void, String> {

    private static final String TAG = "AlertServerBookBike";


    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... inputString) {
        try {
            Socket socket = new Socket("10.0.2.2", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "Book Bike");
            message.put("Username", inputString[0]);
            message.put("Station", inputString[1]);

            outBound.writeObject(message.toString());

            //boolean response = (boolean) inBound.readObject(); POSSIVEL FODA

            socket.close();

        } catch (Throwable e) {
            Log.v(TAG, "fail" + e.getMessage());
        }
        return null;

    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(String result) {


    }
}