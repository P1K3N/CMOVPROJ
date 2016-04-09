package com.ist174008.prof.cmov.cmov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_book_bike);
        Intent intent = getIntent();
        String location = (String)intent.getExtras().get("Location");
        Log.d("Location", location);


        lvStations = (ListView) findViewById(R.id.listBookBike);
        stations = new ArrayList<String>();
        stationsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, stations);
        lvStations.setAdapter(stationsAdapter);
        stations.add("Av.Liberdade");
        stations.add( "Rossio");
        stations.add("Marquês de Pombal") ;
        stations.add("C.C Colombo");
        stations.add("Estádio da Luz");
        stations.add("Alvalade Xixi");
        stations.add("Parque das Nações");
        stations.add("Aeroporto");

    }


    }

