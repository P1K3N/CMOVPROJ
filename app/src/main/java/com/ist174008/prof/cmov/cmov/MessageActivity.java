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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;


public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static final String ACTION_MSGREC = "com.ist174008.prof.cmov.cmov.MsgReceived";
    private static final String ACTION_POINTS = "com.ist174008.prof.cmov.cmov.PointsRcv";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = true;
    private SimWifiP2pSocket mCliSocket = null;
    private IntentFilter  filterMSG;
    private int pointsOfUser;


    @Override
    public void onPause() {
        super.onPause();
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

        filterMSG = new IntentFilter(ACTION_MSGREC);
        registerReceiver(receiver, filterMSG);
        pointsOfUser=((Global) this.getApplication()).getPoints();


        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if(!updatePoints()){
                        return false;
                    }
                    Intent intent= getIntent();
                    String ip= intent.getExtras().getString("IP");

                    new SendCommTask().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            ip,chatText.getText().toString());

                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {

                if(updatePoints()) {
                    Intent intent= getIntent();
                    String ip= intent.getExtras().getString("IP");

                    new SendCommTask().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            ip, chatText.getText().toString());
                    sendChatMessage();
                }
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

    public boolean  updatePoints(){
        //Points(2323323)
        if(chatText.getText().toString().startsWith("Points(")) {

            int start = chatText.getText().toString().indexOf("(");
            int finish = chatText.getText().toString().indexOf(")");
            String pointsToSend = chatText.getText().toString().substring(start + 1, finish);

            if(!pointsToSend.matches("[0-9]+") ){
                Toast.makeText(MessageActivity.this, "Can't send text", Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(MessageActivity.this, "Points to send: " + pointsToSend, Toast.LENGTH_SHORT).show();

            pointsOfUser=((Global) this.getApplication()).getPoints();

            int newPoints = pointsOfUser - Integer.parseInt(pointsToSend);
            if(newPoints >= 0) {
                ((Global) this.getApplication()).setPoints(newPoints);
                Toast.makeText(MessageActivity.this, "You have now " + newPoints + "Points", Toast.LENGTH_SHORT).show();

                return true;
            }else{
                Toast.makeText(MessageActivity.this, "Not Enough Points to send", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private  BroadcastReceiver receiver=new BroadcastReceiver(){

        @Override
        public void onReceive(Context context,Intent intent){
            Log.d(TAG,"INSIDE ON RECEIVE!");

            String action= intent.getAction();
            if(action.equals(ACTION_MSGREC)) {
                String Msg = intent.getExtras().getString("Msg");
                receiveChatMessage(false, Msg);
            }
        }

    };

    public class SendCommTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                if(mCliSocket==null) {
                    mCliSocket = new SimWifiP2pSocket(msg[0], Integer.parseInt(getString(R.string.port)));
                }
                mCliSocket.getOutputStream().write((msg[1] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();
                mCliSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {}
    }
}