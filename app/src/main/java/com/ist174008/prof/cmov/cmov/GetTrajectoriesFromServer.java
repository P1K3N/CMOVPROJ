package com.ist174008.prof.cmov.cmov;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ist174008 on 13/04/2016.
 */
public class GetTrajectoriesFromServer extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetTraj";

    private TrajectoriesActivity trajActv;
    private String str;
    private int numberOfTrajectories;

    public GetTrajectoriesFromServer(TrajectoriesActivity activity) {
        this.trajActv=activity;
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

            message.put("Type", "Show Trajectories");

            message.put("Username", inputString[0]);

            message.put("Password", inputString[1]);

            outBound.writeObject(message.toString());

            str =  (String) inBound.readObject();

            //message = new JSONObject(str);

           // int numberOfTrajectories = message.getInt("Number Trajectories");

            if(str == null) {
                socket.close();
                Log.v(TAG, "Wrong username or password...");
                throw new SecurityException("Wrong username or password...");
            }

            socket.close();

        } catch (Throwable e) {
            Log.v(TAG, "fail" + e.getMessage());
        }

        return str;
    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(String result) {
        ListView list = trajActv.getListView();
        //this.trajActv.setTrajectories();

        if (result != null) {
            ArrayList<String> listStr = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(trajActv.getApplicationContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1,listStr);

            list.setAdapter(adapter);
            adapter.add(result);

        }else{
            String[] val = {"No trajectories to show"};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(trajActv.getApplicationContext(),R.layout.list_black_text,R.id.list_content,val);
            list.setAdapter(adapter);
        }
    }
}