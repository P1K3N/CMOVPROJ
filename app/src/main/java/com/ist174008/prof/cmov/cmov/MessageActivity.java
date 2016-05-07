package com.ist174008.prof.cmov.cmov;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;


public class MessageActivity extends AppCompatActivity {

    private EditText textMessage;
    private EditText pointsMessage;
    private TextView contactName;
    private TextView sendPoints;

    private SimWifiP2pSocket mCliSocket = null;
    private BroadcastReceiver receiver;
    private IntentFilter  filterMSG;


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);

        try{
            mCliSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filterMSG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_message);
        guiSetButtonListeners();

        // spawn the chat server background task
        new SendCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);


        textMessage= (EditText) findViewById(R.id.editTextMessage);
        pointsMessage = (EditText) findViewById(R.id.editTextPoints);
        contactName= (TextView) findViewById(R.id.textViewContactName);
        sendPoints = (TextView) findViewById(R.id.textViewPoints);

        onMsgReceived();
    }


    private OnClickListener btnSendMsg = new OnClickListener() {
        public void onClick(View v){

        }
    };
    private OnClickListener listenerSendButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.idSendButton).setEnabled(false);

            Intent intent = getIntent();
            String str = (String) intent.getExtras().get("UsefulText");
            new SendCommTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    str);
        }
    };

    public void onMsgReceived(){
        filterMSG = new IntentFilter("com.ist174008.prof.cmov.cmov.MsgReceived");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String Msg =  intent.getExtras().getString("Msg");
            }
        };
        registerReceiver(receiver, filterMSG);
    }

    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                mCliSocket.getOutputStream().write((msg[0] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {}
    }

    private void guiSetButtonListeners() {
        findViewById(R.id.idSendButton).setOnClickListener(listenerSendButton);
    }
}
