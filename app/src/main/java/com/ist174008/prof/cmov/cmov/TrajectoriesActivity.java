package com.ist174008.prof.cmov.cmov;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TrajectoriesActivity extends ListActivity {
    private ListView listTraj;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_trajectories);

        listTraj = (ListView) findViewById(R.id.listViewTraj);


        String mail = ((Global) this.getApplication()).getUser();
        String password = ((Global) this.getApplication()).getPassword();

        new GetTrajectoriesFromServer(findViewById(android.R.id.content),this.getApplicationContext()).execute(mail,password);


        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listTraj.setAdapter(adapter);*/

        listTraj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) listTraj.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"oi fatty " + itemValue,Toast.LENGTH_LONG).show();
            }
        });
    }
}

