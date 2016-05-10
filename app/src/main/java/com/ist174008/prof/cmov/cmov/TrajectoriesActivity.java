package com.ist174008.prof.cmov.cmov;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class TrajectoriesActivity extends ListActivity {

    private ArrayList<LatLng> traj = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_trajectories);

        ListView listTraj = getListView();

        String mail = ((Global) this.getApplication()).getUser();
        String password = ((Global) this.getApplication()).getPassword();

        new GetTrajectoriesFromServer(this).execute(mail, password);

        listTraj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) getListView().getItemAtPosition(position);

                Intent intent = new Intent(view.getContext(), BookBikeActivity.class);
               // intent.setClassName("com.mypackage", "com.mypackage.MainActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("TrajectoriesForMap", traj);
                intent.putExtra("Trajectory Name", itemValue);

                startActivity(intent);
            }
        });
    }


    public void setTrajectories(List<LatLng> trajectories){


    }
}

