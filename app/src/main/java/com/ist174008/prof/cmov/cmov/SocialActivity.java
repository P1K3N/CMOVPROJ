package com.ist174008.prof.cmov.cmov;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

/**
 * Created by ist174008 on 21/03/2016.
 */
public class SocialActivity extends AppCompatActivity {

    ListView listItems;

    // Defined Array values to show in ListView
    String[] values = new String[] { "Android List View",
            "Adapter implementation",
            "Simple List View In Android",
            "Create List View Android",
            "Android Example",
            "List View Source Code",
            "List View Array Adapter",
            "Android Example List View"
    };


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_social);

        listItems = (ListView) findViewById(R.id.listSocial);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listItems.setAdapter(adapter);


        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) listItems.getItemAtPosition(position);

                Intent intent = new Intent(SocialActivity.this, MessageActivity.class);
                intent.putExtra("contact_name", itemValue);
                startActivity(intent);

            }

        });

    }

}

