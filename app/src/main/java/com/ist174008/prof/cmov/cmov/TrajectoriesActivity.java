package com.ist174008.prof.cmov.cmov;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.ListView;
;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class TrajectoriesActivity extends ListActivity {

    private static final String TAG = "TrajectoriesActivity";
    private static final String NO_TRAJS = "No trajectories to show";

    private ArrayList<ArrayList<LatLng>>  trajectories = new ArrayList<>();
    private String mail;
    private String password;
    private int numberOfTrajectories;


    public void setTrajectories(ArrayList<ArrayList<LatLng>> trajectories){
      ((Global) this.getApplication()).setTrajectories(trajectories);
    }

    @Override
    public void onResume(){
        super.onResume();

        //send trajectories to server
        Integer nOfTrajs= ((Global) this.getApplication()).getNumberOfTrajectories();
        if(nOfTrajs !=0) {
            new SendTrajectoriesToServer().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    mail,
                    nOfTrajs.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_trajectories);
        Log.d(TAG, "ENTER trajectories ");

        new GetTrajectoriesFromServer(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mail);

        ListView listTraj = getListView();

        mail = ((Global) this.getApplication()).getUser();
        password = ((Global) this.getApplication()).getPassword();
        numberOfTrajectories = ((Global) this.getApplication()).getNumberOfTrajectories();
        this.trajectories = ((Global) this.getApplication()).getTrajectories();

        ArrayList<String> listStr=new ArrayList<>();

        if(trajectories == null || trajectories.isEmpty()) {
            String[] val = {NO_TRAJS};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getApplicationContext(), R.layout.list_black_text, R.id.list_content, val);
            listTraj.setAdapter(adapter);
        }else {
            for (int i = 0; i < numberOfTrajectories; i++) {
                listStr.add("Trajectory " + i + 1);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getApplicationContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, listStr);

            listTraj.setAdapter(adapter);
        }
        listTraj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) getListView().getItemAtPosition(position);
                Log.d(TAG, "CLICKED LIST " + itemValue);

                if(!itemValue.equals(NO_TRAJS)) {
                    Intent intent = new Intent(view.getContext(), BookBikeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putParcelableArrayListExtra("TrajectoriesForMap", trajectories.get(position));
                    intent.putExtra("ActionTrajs", "myMethod");

                    Log.d(TAG, "traj in pos " + trajectories.get(position));

                    view.getContext().startActivity(intent);
                }
            }
        });
    }
}