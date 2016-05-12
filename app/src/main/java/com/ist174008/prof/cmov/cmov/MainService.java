package com.ist174008.prof.cmov.cmov;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;


public class MainService extends Service {

    private static final String TAG = "Service";
    private static final String ACTION_MSGRECEIVED ="com.ist174008.prof.cmov.cmov.MsgReceived";
    public static final String SERVICE_FOR_REQUEST_PEERS = "com.ist174008.prof.cmov.cmov.Peers";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private SimWifiP2pManager manager;
    private SimWifiP2pManager.Channel channel;


    private BroadcastReceiver receiver=new BroadcastReceiver(){

        @Override
        public void onReceive(Context context,Intent intent){
            Log.d(TAG,"INSIDE ON RECEIVE SERVICE");

            String action= intent.getAction();
            if(action.equals(ACTION_MSGRECEIVED)) {
                /*String Msg = intent.getExtras().getString("Msg");
                Intent newInt = new Intent();
                newInt.putExtra("Msg", Msg);*/
                Toast.makeText(MainService.this, "You have unread messages in your Social Chat!", Toast.LENGTH_SHORT).show();

            }
        }
    };

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            SimWifiP2pManager.PeerListListener someObject = (SimWifiP2pManager.PeerListListener) msg.obj;

            manager.requestPeers(channel,someObject);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filterMSG = new IntentFilter(ACTION_MSGRECEIVED);
        registerReceiver(receiver, filterMSG);

        HandlerThread thread = new HandlerThread("ServiceStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    public int onStartCommand(Intent intent,int flags,int startId){
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        manager = ((Global) this.getApplication()).getManager();
        channel = ((Global) this.getApplication()).getChannel();

        Log.d(TAG,"MAN: " + manager + "Chan: " + channel );
        if(intent !=null) {
            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job

            if(intent.getAction().equals(SERVICE_FOR_REQUEST_PEERS)){

                Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = startId;
                //msg.obj = intent.getStringExtra("Service");
                mServiceHandler.sendMessage(msg);
            }
        }

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "In onBind");
        return null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
        unregisterReceiver(receiver);
    }
}
