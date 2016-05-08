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
public class GetStationsFromServer extends AsyncTask<String, Void, List<Double>> {

    private static final String TAG = "GetStations";
    private HomeActivity activity;
    private List<String> name = new ArrayList<>();
    private List<Double> longitude = new ArrayList<>();
    private List<Double> latitude = new ArrayList<>();
    private List<String> bike = new ArrayList<>();

    public GetStationsFromServer(HomeActivity act) {
        this.activity=act;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected List<Double> doInBackground(String... inputString) {
        try {
            Socket socket = new Socket("192.168.1.80", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "List Stations");

            outBound.writeObject(message.toString());

            String response = (String) inBound.readObject();

            message = new JSONObject(response);

            int numberOfStations = message.getInt("Stations");

            if(numberOfStations == 0) {
                socket.close();

                throw new SecurityException("No available station...");
            }

            outBound.writeObject("");

            ArrayList<JSONObject> jsonArray = new ArrayList<>();

            for(int i = 0; i < numberOfStations; i++) {
                response = (String) inBound.readObject();

                message = new JSONObject(response);

                jsonArray.add(message);

                outBound.writeObject("");
            }

            boolean ack = (boolean) inBound.readObject();

            if(!ack) {
                socket.close();
                throw new SecurityException("Failed to show Stations...");
            }

            for (JSONObject json : jsonArray) {
                name.add(json.getString("Name"));
                latitude.add(json.getDouble("Latitude"));
                longitude.add(json.getDouble("Longitude"));
                bike.add(json.getString("Bikes"));
            }

            socket.close();

        } catch (Throwable e) {
            Log.v(TAG, "fail" + e.getMessage());
        }

        List<Double> newList = new ArrayList<>(latitude);
        newList.addAll(longitude);

        return newList;

    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(List<Double> result) {

        this.activity.setStations(result);

    }
}