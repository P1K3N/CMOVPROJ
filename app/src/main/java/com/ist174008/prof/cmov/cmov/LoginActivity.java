package com.ist174008.prof.cmov.cmov;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    private EditText editMail;
    private EditText editPassword;
    private Global g;
    private SharedPreferences settings;

    private static final String TAG = "LoginToServer";


    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(),"PAUSED",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(),"STOPPED",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /* if the user has already login, start the home activity */
        if(settings.getBoolean("connected", false)) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        // initialize log of connection to false
        settings = getSharedPreferences("mySharedPref", 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("connected", false);
        editor.apply();

        guiSetButtonListeners();

        editMail = (EditText) findViewById(R.id.editTextEmail);
        editPassword = (EditText) findViewById(R.id.editTextPassword);
        g = ((Global) getApplicationContext());
    }


    private OnClickListener btnLOGIN2 = new OnClickListener() {
        public void onClick(View v) {

            String mail = editMail.getText().toString();
            String password = editPassword.getText().toString();

            g.setUser(mail);
            g.setPassword(password);

            new LoginToServer(getApplicationContext()).execute(mail, password, "Login User");
        }
    };

    private OnClickListener btnRegister = new OnClickListener() {
        public void onClick(View v) {

            String mail = editMail.getText().toString();
            String password = editPassword.getText().toString();

            g.setUser(mail);
            g.setPassword(password);

            new LoginToServer(getApplicationContext()).execute(mail, password, "Register");
        }
    };

    private void guiSetButtonListeners() {

        findViewById(R.id.btnLOGIN).setOnClickListener(btnLOGIN2);
        findViewById(R.id.buttonRegister).setOnClickListener(btnRegister);

    }

    public class LoginToServer extends AsyncTask<String, Void, Boolean> {

        private Context mActivity;
        private boolean response;

        public LoginToServer(Context activity){
            this.mActivity = activity;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected Boolean doInBackground(String... inputString) {
            try {
                Socket socket = new Socket("192.168.1.80", 6000);

                ObjectOutputStream outBound = new ObjectOutputStream(socket.getOutputStream());

                ObjectInputStream inBound = new ObjectInputStream(socket.getInputStream());

                JSONObject message = new JSONObject();

                message.put("Type", inputString[2]);

                message.put("Username", inputString[0]);

                message.put("Password", inputString[1]);

                outBound.writeObject(message.toString());

                response = (boolean) inBound.readObject();

                socket.close();


            } catch (Throwable e) {
                Log.v(TAG, "fail " + e.getMessage());
                return false;
            }
            return response;
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("connected", true);
                editor.apply();

                startActivity(intent);
            }else{
                Toast.makeText(mActivity,"Invalid Username/Password Combination",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
