package com.ist174008.prof.cmov.cmov;

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
public class GetPointsFromServer extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetPoints";
    private View rootView;
    private String response;

    public GetPointsFromServer(View v){
        this.rootView=v;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... inputString) {
        try {
            Socket socket = new Socket("10.0.2.2", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "Show Points");

            message.put("Username", inputString[0]);

            message.put("Password", inputString[1]);

            outBound.writeObject(message.toString());

            response = (String) inBound.readObject();

            socket.close();

        } catch (Throwable e) {
            Log.v(TAG, "fail" + e.getMessage());
        }

        return response;

    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(String result) {

        TextView txt = (TextView) rootView.findViewById(R.id.textPoints);
        txt.append("You have accumulated " + result + " Points");
    }
}