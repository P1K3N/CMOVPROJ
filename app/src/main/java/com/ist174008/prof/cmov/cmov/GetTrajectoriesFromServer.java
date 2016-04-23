package com.ist174008.prof.cmov.cmov;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by ist174008 on 13/04/2016.
 */
public class GetTrajectoriesFromServer extends AsyncTask<String, Void, String[]> {

    private static final String TAG = "GetTraj";
    private View rootView;
    private Context mContext;
    private String[] str; // POSSIVEL FODA COM O STRING[] ou STRING

    public GetTrajectoriesFromServer(View v,Context cont) {
        this.rootView=v;
        this.mContext=cont;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected String[] doInBackground(String... inputString) {
        try {
            Socket socket = new Socket("10.0.2.2", 6000);

            ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

            JSONObject message = new JSONObject();

            message.put("Type", "Show Trajectories");

            message.put("Username", inputString[0]);

            message.put("Password", inputString[1]);


            outBound.writeObject(message.toString());

            str =  (String[]) inBound.readObject();

            if(str == null) {
                socket.close();
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
    protected void onPostExecute(String[] result) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1,android.R.id.text1,result);
        ListView listTra = (ListView) rootView.findViewById(R.id.listViewTraj);

        listTra.setAdapter(adapter);

        adapter.addAll(result);
    }
}