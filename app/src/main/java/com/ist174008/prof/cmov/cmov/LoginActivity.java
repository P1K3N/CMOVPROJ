package com.ist174008.prof.cmov.cmov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);
        guiSetButtonListeners();
        new ConnectServer().execute("string");

    }



    private OnClickListener btnLOGIN2 = new OnClickListener() {
        public void onClick(View v){

            new ConnectServer().execute("string222222222222222222222");
            Intent intent = new Intent(v.getContext(), HomeActivity.class);
            startActivity(intent);

        }
    };





    private void guiSetButtonListeners() {

        findViewById(R.id.btnLOGIN2).setOnClickListener(btnLOGIN2);

    }
}
