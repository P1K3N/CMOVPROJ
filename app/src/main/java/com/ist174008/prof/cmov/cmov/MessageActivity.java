package com.ist174008.prof.cmov.cmov;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;


public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = true;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_message);

        onMsgReceived();

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Intent intent = getIntent();
                    String str = (String) intent.getExtras().get("tt");

                    Toast.makeText(getApplicationContext(), "INTENT " + str, Toast.LENGTH_LONG).show();
                    new SendCommTask().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            str);

                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();

                Intent intent = getIntent();
                String str = (String) intent.getExtras().get("tt");

                Toast.makeText(getApplicationContext(), "INTENT " + str, Toast.LENGTH_LONG).show();
                new SendCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        str);
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

    public void onMsgReceived(){
        filterMSG = new IntentFilter("com.ist174008.prof.cmov.cmov.MsgReceived");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String Msg =  intent.getExtras().getString("Msg");
                receiveChatMessage(false,Msg);
            }
        };
        registerReceiver(receiver, filterMSG);
    }

    public class SendCommTask extends AsyncTask<String, Void, Void> {

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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {}
    }
}
