package com.ist174008.prof.cmov.cmov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText editMail;
    private EditText editPassword;
    private Global g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);
        guiSetButtonListeners();

        editMail = (EditText) findViewById(R.id.editTextEmail);
        editPassword = (EditText) findViewById(R.id.editTextPassword);
        g = ((Global)getApplicationContext());

    }

    private OnClickListener btnLOGIN2 = new OnClickListener() {
        public void onClick(View v){

            String mail = editMail.getText().toString();
            String password = editPassword.getText().toString();

            g.setUser(mail);
            g.setPassword(password);

            new LoginToServer().execute(mail,password);
            Intent intent = new Intent(v.getContext(), HomeActivity.class);
            startActivity(intent);

        }
    };

    private void guiSetButtonListeners() {

        findViewById(R.id.btnLOGIN2).setOnClickListener(btnLOGIN2);

    }
}
