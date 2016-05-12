package com.ist174008.prof.cmov.cmov;




import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;


public  class HomeActivity extends AppCompatActivity implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private  List<LatLng> finalStations = new ArrayList<>();

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private SimWifiP2pSocket sock =null;

    private SimWifiP2pBroadcastReceiver mReceiver;
    private IntentFilter filter = new IntentFilter();

    private ArrayList<String> listInGroup= new ArrayList<>();
    private ArrayList<String> listInRange=new ArrayList<>();

    public static final String TAG = "HomeActivity";
    public static final String SERVICE_FOR_REQUEST_PEERS = "com.ist174008.prof.cmov.cmov.Peers";

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stopService()
        Toast.makeText(HomeActivity.this, "DESTORYED HOME ACTIVITY", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver,filter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        guiSetButtonListeners();

        new GetStationsFromServer(this).execute();
        String userName = ((Global) this.getApplication()).getUser();
        ((Global) this.getApplication()).setPoints(10.0d);

        //new GetPointsFromServer(this).execute(userName);
        //new SendPointsToServer().execute(userName, points); // (int) points then to string

        // WIfi direct ON
        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        // register broadcast receiver
        filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        /*((Global) this.getApplication()).setChannel(mChannel);
        ((Global) this.getApplication()).setManager(mManager);

        Intent serviceIntent = new Intent();
        startService(serviceIntent);*/

        new PeerUpdate(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // spawn the chat server background task
        new IncommingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setStations(List<Double> stations){
        int listSize = stations.size();
        int numberOfStations = listSize/2;

        List<Double> stationsLat;
        List<Double> stationsLong;

        stationsLat = new ArrayList<>(stations.subList(0,(listSize/2)));
        stationsLong = new ArrayList<>(stations.subList((listSize/2),listSize));


        for (int i = 0 ; i < numberOfStations; i++) {
            finalStations.add(new LatLng(stationsLat.get(i), stationsLong.get(i)));
        }

        ((Global) this.getApplication()).setStations(finalStations);

    }

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
                    Log.d(TAG,"sock incommingTask " + mSrvSocket);
                    sock = mSrvSocket.accept();
                    try {
                        Log.d(TAG, "INSIDE TRY !!");

                        CommunicationThread commThread = new CommunicationThread();
                        new Thread(commThread).start();

                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d(TAG,"Error reading socket:");
                    }
                } catch (IOException e) {
                    Log.d(TAG,"ERROR IO EXCEPTION" + e.getMessage());
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {}
    }

    public class CommunicationThread implements Runnable {

        private BufferedReader input;

        public void run() {
            try {
                Log.d(TAG,"sock thread " + sock);
                this.input = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                String st = input.readLine();
                sendBroadcastIntent(st);


            } catch (IOException e) {
                Log.d(TAG,"ERROR IO EXCEPTION 3 " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public class PeerUpdate extends AsyncTask<Void, String, Void> {
        private HomeActivity activity;

        public PeerUpdate(HomeActivity act) {
            this.activity=act;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "Peer Update started");

            new Thread(new Runnable() {
                public void run() {

                    while (true) {

                        if(mManager !=null && mChannel !=null) {
                            mManager.requestPeers(mChannel, activity);
                            mManager.requestGroupInfo(mChannel, activity);
                            Log.d(TAG, "Peer Updated!!");
                        }
                        try{
                            Thread.sleep(10000);
                        }catch (InterruptedException e){
                            Log.d(TAG,"NO MORE PEER UPDATES??");
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }).start();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {}

    }


    public void sendBroadcastIntent(String message){
        Log.d(TAG, "IN SENDING BROADCAST INTENT!!!");

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction("com.ist174008.prof.cmov.cmov.MsgReceived");
        intent.putExtra("Msg", message);

        if(message.startsWith("Points:")) {
            String[] Points =message.split(":");
            String numberOfPoints= Points[1];
            double numberOfPointsF= Double.parseDouble(numberOfPoints);
            ((Global) this.getApplication()).addPoints(numberOfPointsF);
        }
        sendBroadcast(intent);
    }

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
	 * Listeners associated to Termite
	 */

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {

        listInRange.clear();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "(" + device.deviceName + ")" + device.getVirtIp();
            //peersStr.append(devstr);

            // add devices to list
            listInRange.add(devstr);
            Log.d(TAG, "LIST ADDED RANGE" + listInRange);

            // bike in range
            if(device.getVirtIp() == null){
                ((Global) this.getApplication()).setUserNearBike(true);

                Toast.makeText(HomeActivity.this, "BIKE IN RANGE ", Toast.LENGTH_SHORT).show();
            }else{
                ((Global) this.getApplication()).setUserNearBike(false);
                Toast.makeText(HomeActivity.this, "BIKE GONE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {

        //StringBuilder peersStr = new StringBuilder();
        listInGroup.clear();

        // compile list of network members
        StringBuilder peersStr = new StringBuilder();


        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "(" + deviceName + ")" + device.getVirtIp();


            String devstr2 = "" + deviceName + " (" +
                    ((device == null)?"??":device.getVirtIp()) + ")\n";

            peersStr.append(devstr2);

            // add contacts to list
            if(device.getVirtIp() !=null) {
                listInGroup.add(devstr);
                Log.d(TAG, "LIST ADDED  GROUP" + listInGroup);
            }
        }
    }

    /* Button listeners
    */
    private View.OnClickListener listenerBookBike= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, BookBikeActivity.class);
            startActivity(intent);
        }
    };

    public void onSocial(View view) {
        if(mBound) {
            mManager.requestPeers(mChannel, this);
            mManager.requestGroupInfo(mChannel, this);
            Log.d(TAG,"REQUESTED");
        }
        Intent intent = new Intent(this, SocialActivity.class);
        intent.putExtra("ForList", listInGroup);
        startActivity(intent);
    }

    public void onUserInfo(View view) {
        if(mBound) {
            mManager.requestPeers(mChannel, this);
            mManager.requestGroupInfo(mChannel, this);
            Log.d(TAG,"REQUESTED");
        }
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void onTrajectories(View view) {
        Intent intent = new Intent(this, TrajectoriesActivity.class);
        startActivity(intent);
    }

    private void guiSetButtonListeners(){
        findViewById(R.id.buttonBookBike).setOnClickListener(listenerBookBike);
    }
}