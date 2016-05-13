package com.ist174008.prof.cmov.cmov;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ist174008 on 21/03/2016.
 */
public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "Info";

    private TextView txtPoints;
    private String userName;

    @Override
    public void onResume(){
        super.onResume();
        updateTxt(((Global) this.getApplication()).getPoints());
        new GetPointsFromServer(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,userName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_user_info);

        txtPoints = (TextView) findViewById(R.id.textPoints);
        guiSetButtonListeners();

        userName = ((Global) this.getApplication()).getUser();

        new GetPointsFromServer(this).execute(userName);
    }

    public void  updateTxt(Integer clientPoints) {
        txtPoints.setText("");
        txtPoints.append("You have accumulated " + clientPoints + " Points");
    }

    public void updateServerPoints(Integer serverPoints){
        int clientPoints = ((Global) this.getApplication()).getPoints();

        if (clientPoints != serverPoints) {
            Log.d(TAG, "Update, client points= " + clientPoints + "Server points= " + serverPoints);
            new SendPointsToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userName, String.valueOf(clientPoints));
        }
    }

    private OnClickListener btnToTraj = new OnClickListener() {
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), TrajectoriesActivity.class);
            startActivity(intent);
        }
    };

    private void guiSetButtonListeners() {
        findViewById(R.id.btnToTraj).setOnClickListener(btnToTraj);
    }
}





