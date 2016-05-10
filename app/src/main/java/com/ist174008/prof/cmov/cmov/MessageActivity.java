package com.ist174008.prof.cmov.cmov;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;


public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = true;
    private SimWifiP2pSocket mCliSocket = null;
    private IntentFilter  filterMSG;


    private  BroadcastReceiver receiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "IN RECEIVER!!!");

            String Msg1 = intent.getStringExtra("Msg");

            Toast.makeText(MessageActivity.this, "RECEIVED THE MSG " + Msg1, Toast.LENGTH_SHORT).show();
            receiveChatMessage(false, Msg1);

            if(action.equals("com.ist174008.prof.cmov.cmov.MsgReceived")) {
                String Msg2 = intent.getStringExtra("Msg");

                Toast.makeText(MessageActivity.this, "RECEIVED THE MSG " + Msg2, Toast.LENGTH_SHORT).show();
                receiveChatMessage(false, Msg2);
            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(MessageActivity.this, "DESTROYYYYYYED MessageCtvt", Toast.LENGTH_SHORT).show();
       unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filterMSG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_message);

        filterMSG = new IntentFilter("com.ist174008.prof.cmov.cmov.MsgReceived");
        this.registerReceiver(receiver, filterMSG);

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {


                    Intent intent = getIntent();
                    String ip = intent.getExtras().getString("IP");

                    new SendCommTask().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            ip, chatText.getText().toString());

                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = getIntent();
                String ip = intent.getExtras().getString("IP");

                new SendCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        ip, chatText.getText().toString());

                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }

    public boolean receiveChatMessage(boolean side, String MsgReceived){
        chatArrayAdapter.add(new ChatMessage(side, MsgReceived));
        return true;
    }

    public class SendCommTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            Log.d(TAG, "SENDCOMMTASK started: " + "send to ip:" +msg[0] + "msg to send:" + msg[1] );
            try {
                if(mCliSocket == null) {
                    mCliSocket = new SimWifiP2pSocket(msg[0],
                            Integer.parseInt(getString(R.string.port)));
                }

                mCliSocket.getOutputStream().write((msg[1] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();

            } catch (UnknownHostException e) {
                Log.d(TAG, "Unknown Host: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "IO Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {}
    }
}
