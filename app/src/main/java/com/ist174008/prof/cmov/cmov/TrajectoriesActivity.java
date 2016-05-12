package com.ist174008.prof.cmov.cmov;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class TrajectoriesActivity extends ListActivity {

    private static final String TAG = "TrajectoriesActivity";

    private ArrayList<ArrayList<LatLng>>  trajectories = new ArrayList<>();

    public void setTrajectories(ArrayList<ArrayList<LatLng>>  trajectories){
        this.trajectories = trajectories;
        ((Global) this.getApplication()).setTrajectories(trajectories);
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_trajectories);

        ListView listTraj = getListView();

        String mail = ((Global) this.getApplication()).getUser();
        String password = ((Global) this.getApplication()).getPassword();

        //send trajectories to server
        Integer nOfTrajs= ((Global) this.getApplication()).getNumberOfTrajectories();
        new SendTrajectoriesToServer().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                mail,
                nOfTrajs.toString());

        //get trajectories from server
        new GetTrajectoriesFromServer(this).execute(mail, password);

        listTraj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(view.getContext(), BookBikeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putParcelableArrayListExtra("TrajectoriesForMap", trajectories.get(position));
                intent.putExtra("ActionTrajs","myMethod");

                Log.v(TAG, "traj in pos " + trajectories.get(position));

                view.getContext().startActivity(intent);
            }
        });
    }
}