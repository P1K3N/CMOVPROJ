package com.ist174008.prof.cmov.cmov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by ist174008 on 21/03/2016.
 */
public class BookBikeActivity extends Activity {


    private ListView lvStations;
    private ArrayList<String> stations;
    private ArrayAdapter<String> stationsAdapter;

    String[] values = new String[] { "Av.Liberdade",
            "Rossio",
            "Marquês de Pombal",
            "C.C Colombo",
            "Estádio da Luz",
            "Alvalade Xixi",
            "Parque das Nações",
            "Aeroporto",
            "Largo de Camões"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_book_bike);
        Intent intent = getIntent();
        String location = (String)intent.getExtras().get("Location");
        Log.d("Location", location);

        lvStations=(ListView)findViewById(R.id.listBookBike);

        stationsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,values);
        lvStations.setAdapter(stationsAdapter);
        lvStations.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) lvStations.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + position + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });


    }


    }

