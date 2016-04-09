package com.ist174008.prof.cmov.cmov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);
        guiSetButtonListeners();

    }


    private OnClickListener btnLOGIN2 = new OnClickListener() {
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), HomeActivity.class);
            startActivity(intent);
        }
    };


    private void guiSetButtonListeners() {

        findViewById(R.id.btnLOGIN2).setOnClickListener(btnLOGIN2);

    }
}
