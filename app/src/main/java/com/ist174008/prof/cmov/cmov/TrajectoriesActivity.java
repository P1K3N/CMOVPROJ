package com.ist174008.prof.cmov.cmov;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TrajectoriesActivity extends ListActivity {


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_trajectories);

        ListView listTraj = getListView();


        String mail = ((Global) this.getApplication()).getUser();
        String password = ((Global) this.getApplication()).getPassword();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);

        listTraj.setAdapter(adapter);



        new GetTrajectoriesFromServer(this).execute(mail, password);

        listTraj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) getListView().getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"oi fatty " + itemValue,Toast.LENGTH_LONG).show();
            }
        });
    }
}

