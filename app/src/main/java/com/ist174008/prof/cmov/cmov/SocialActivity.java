package com.ist174008.prof.cmov.cmov;



import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class SocialActivity extends ListActivity {


    public static final String TAG = "Social";
    public static final String NO_IP="No friends near you :(";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_social);

        ListView list = getListView();

        Intent intent = getIntent();
        ArrayList<String> names;

        names = (ArrayList<String>) intent.getSerializableExtra("ForList");
        Log.d(TAG,"LIST??" + names);

        if(!names.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    R.layout.list_black_text,R.id.list_content, names);

            list.setAdapter(adapter);
        }else {
            String[] unfortunate = {NO_IP};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    R.layout.list_black_text, R.id.list_content, unfortunate);

            list.setAdapter(adapter);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                String itemValue = (String) getListView().getItemAtPosition(position);
                Log.d(TAG,"CLICKED LIST " + itemValue);

                if(!itemValue.equals(NO_IP)) {
                    int start = itemValue.indexOf(")");
                    String ipAddrs = itemValue.substring(start + 1);
                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                    intent.putExtra("IP", ipAddrs);
                    Log.d(TAG, "SENT IP: " + ipAddrs);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume(){
        super.onResume();
    }

}