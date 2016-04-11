package com.ist174008.prof.cmov.cmov;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;



public class MessageActivity extends AppCompatActivity {

    private EditText textMessage;
    private EditText pointsMessage;
    private TextView contactName;
    private TextView sendPoints;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_message);
        guiSetButtonListeners();


        textMessage= (EditText) findViewById(R.id.editTextMessage);
        pointsMessage = (EditText) findViewById(R.id.editTextPoints);
        contactName= (TextView) findViewById(R.id.textViewContactName);
        sendPoints = (TextView) findViewById(R.id.textViewPoints);

        getContactName();

    }

    public void getContactName(){
        Bundle bundle = getIntent().getExtras();
        int value = bundle.getInt("contact_name");

        contactName.append("Send a Message to " + value);
        sendPoints.append("Send some points too :)");

    }


    private OnClickListener btnSendMsg = new OnClickListener() {
        public void onClick(View v){

        }
    };


    private void guiSetButtonListeners() {

        findViewById(R.id.btnSendMsg).setOnClickListener(btnSendMsg);

    }

}
