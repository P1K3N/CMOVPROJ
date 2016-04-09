package com.ist174008.prof.cmov.cmov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ist174008 on 21/03/2016.
 */
public class InfoActivity extends AppCompatActivity {

    private TextView userPoints;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_user_info);
        userPoints = (TextView) findViewById(R.id.textPoints);
        userPoints.setText("You have acumulated 50 Points");

    }

}





