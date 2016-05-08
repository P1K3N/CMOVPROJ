package com.ist174008.prof.cmov.cmov;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.Channel;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.GroupInfoListener;


public class SocialActivity extends AppCompatActivity implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {


    public static final String TAG = "msgsender";

    private SimWifiP2pManager mManager = null;
    private Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private TextView mTextInput;
    private TextView mTextOutput;
    private SimWifiP2pBroadcastReceiver mReceiver;
    //private IntentFilter filter = new IntentFilter();




    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(mReceiver, filter);
        guiUpdateInitState();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_social);


        guiSetButtonListeners();
        guiUpdateInitState();

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }



    /* private void connectWifiOn(){
        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

        // spawn the chat server background task
        new IncommingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

    }*/
    private OnClickListener listenerInRangeButton = new OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                mManager.requestPeers(mChannel, SocialActivity.this);
            } else {
                Toast.makeText(v.getContext(), "Service not bound",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    private OnClickListener listenerInGroupButton = new OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                mManager.requestGroupInfo(mChannel, SocialActivity.this);
            } else {
                Toast.makeText(v.getContext(), "Service not bound",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };


    private OnClickListener listenerConnectButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.idConnectButton).setEnabled(false);

            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
            intent.putExtra("UsefulText", mTextInput.getText());

            new OutgoingCommTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    mTextInput.getText().toString());

            if (mTextOutput.getText().toString().equals("S")) {
                startActivity(intent);
            }
        }
    };

    private OnClickListener listenerWifiOnButton = new View.OnClickListener(){
        @Override
        public void onClick(View v){

            Intent intent = new Intent(v.getContext(), SimWifiP2pService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;

            // spawn the chat server background task
            new IncommingCommTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);

            guiUpdateDisconnectedState();
        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

	/*
	 * Asynctasks implementing message exchange
	 */

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            sendBroadcastIntent(values[0]);
            mTextOutput.append(values[0] + "\n");
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mTextOutput.setText("Connecting...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(getString(R.string.port)));
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                guiUpdateDisconnectedState();
                mTextOutput.setText(result);
            } else {
                findViewById(R.id.idConnectButton).setEnabled(false);
                mTextInput.setHint("");
                mTextInput.setText("");
                mTextOutput.setText("S");
            }
        }
    }

    /*
	 * Listeners associated to Termite
	 */

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
        }

        // display list of devices in range
        new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {

        // compile list of network members
        StringBuilder peersStr = new StringBuilder();
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null)?"??":device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
        }

        // display list of network members
        new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Network")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void sendBroadcastIntent(String s){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction("com.ist174008.prof.cmov.cmov.MsgReceived");
        intent.putExtra("Msg", s);
        sendBroadcast(intent);
    }


    private void guiSetButtonListeners() {

        findViewById(R.id.idConnectButton).setOnClickListener(listenerConnectButton);
        findViewById(R.id.idInRangeButton).setOnClickListener(listenerInRangeButton);
        findViewById(R.id.idInGroupButton).setOnClickListener(listenerInGroupButton);
        findViewById(R.id.WifiOn).setOnClickListener(listenerWifiOnButton);
    }

    private void guiUpdateInitState() {

        mTextInput = (TextView) findViewById(R.id.editText1);
        mTextInput.setHint("type remote virtual IP (192.168.0.0/16)");
        mTextInput.setEnabled(false);

        mTextOutput = (TextView) findViewById(R.id.editText2);
        mTextOutput.setEnabled(false);
        mTextOutput.setText("");

        findViewById(R.id.idConnectButton).setEnabled(false);
        findViewById(R.id.WifiOn).setEnabled(true);

        findViewById(R.id.idInRangeButton).setEnabled(false);
        findViewById(R.id.idInGroupButton).setEnabled(false);
    }

    private void guiUpdateDisconnectedState() {

        mTextInput.setEnabled(true);
        mTextInput.setHint("type remote virtual IP (192.168.0.0/16)");
        mTextOutput.setEnabled(true);
        mTextOutput.setText("");

        findViewById(R.id.idConnectButton).setEnabled(true);

        findViewById(R.id.WifiOn).setEnabled(false);
        findViewById(R.id.idInRangeButton).setEnabled(true);
        findViewById(R.id.idInGroupButton).setEnabled(true);
    }
}

