package fr.java.aoitechnicien;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ServiceNetwork extends Service {

    final static String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    NotificationManager manager ;
    TextView textSplash;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*new Timer().scheduleAtFixedRate(new TimerTask()
    {
        @Override
        public void run() {
        method(); // call your method
    }
    }, 0, 100000);*/



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("NETWORK INFO ::SN ::", "START::SERVICE");
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        BroadcastReceiver receiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                    //check internet connection
                    if (!ConnexionHelper.isConnectedOrConnecting(context)) {
                        if (context != null) {
                            boolean show = false;
                            if (ConnexionHelper.lastNoConnectionTs == -1) {//first time
                                show = true;
                                ConnexionHelper.lastNoConnectionTs = System.currentTimeMillis();
                            } else {
                                if (System.currentTimeMillis() - ConnexionHelper.lastNoConnectionTs > 1000) {
                                    show = true;
                                    ConnexionHelper.lastNoConnectionTs = System.currentTimeMillis();
                                }
                            }

                            if (show && ConnexionHelper.isOnline) {
                                ConnexionHelper.isOnline = false;
                                Log.e("NETWORK INFO :: lost ::","Connection lost");
                                ConnexionHelper.isStopedThread(context);
                                Toast.makeText(context, "Service Disconnected", Toast.LENGTH_SHORT).show();
                                //manager.cancelAll();
                            }
                        }
                    } else {
                        Log.e("NETWORK INFO ::SN ::","Connected");
                        ConnexionHelper.isStartedThread(context);
                        Toast.makeText(context, "Service Connected", Toast.LENGTH_SHORT).show();
                        // Perform your actions here
                        ConnexionHelper.isOnline = true;
                    }
                }
            }
        };
        registerReceiver(receiver,filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
