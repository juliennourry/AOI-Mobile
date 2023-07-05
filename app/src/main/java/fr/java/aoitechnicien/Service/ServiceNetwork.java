package fr.java.aoitechnicien.Service;

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

import fr.java.aoitechnicien.R;

public class ServiceNetwork extends Service {

    final static String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    NotificationManager manager ;
    TextView textSplash;
    private ConnexionHelper connexionHelper = new ConnexionHelper();
    private String s_login;
    private String s_password;
    private BroadcastReceiver receiver;

    // -- SHAREDPREFERENCES
    /*SharedHelper sharedhelper;*/
    // -- SESSION INFORMATION
    /*public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String pswKey = "pswKey";*/

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


    public ServiceNetwork() {
        // Default constructor
    }

    public ServiceNetwork(String login, String password) {
        s_login = login;
        s_password = password;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra("login")) {
            s_login = intent.getStringExtra("login");
        }
        if (intent.hasExtra("password")) {
            s_password = intent.getStringExtra("password");
        }

//        connexionHelper = new ConnexionHelper();
        Log.i("DEBUG_NETWORK", "START::SERVICE");
        // Let it continue running until it is stopped.
        //Toast.makeText(this, R.string.loading_service, Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
                Log.e("DEBUG_UNREGISTER", "TRUE");
            } catch (IllegalArgumentException e) {
                Log.e("DEBUG_UNREGISTER", e.toString());
                // receiver was not registered
            }
        }

        receiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                //sharedhelper = new SharedHelper((Activity) context, sessionKey);
                String action = intent.getAction();

                if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                    //check internet connection
                    if (!connexionHelper.isConnectedOrConnecting(context)) {
                        if (context != null) {
                            boolean show = false;
                            if (connexionHelper.lastNoConnectionTs == -1) {//first time
                                show = true;
                                connexionHelper.lastNoConnectionTs = System.currentTimeMillis();
                            } else {
                                if (System.currentTimeMillis() - connexionHelper.lastNoConnectionTs > 1000) {
                                    show = true;
                                    connexionHelper.lastNoConnectionTs = System.currentTimeMillis();
                                }
                            }

                            if (show && connexionHelper.isOnline) {
                                connexionHelper.isOnline = false;
                                Log.e("DEBUG_NETWORK","Connection lost");
                                connexionHelper.isStopedThread();
                                Toast.makeText(context, R.string.service_offline, Toast.LENGTH_SHORT).show();
                                //manager.cancelAll();
                            }
                        }
                    } else {
                        connexionHelper.isStopedThread();
                        connexionHelper.isStartedThread(context, s_login, s_password);
                        //Toast.makeText(context, R.string.service_online, Toast.LENGTH_SHORT).show();
                        // Perform your actions here
                        connexionHelper.isOnline = true;
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
        connexionHelper.isStopedThread();
        try{
            if(receiver!=null)
                unregisterReceiver(receiver);

        }catch(Exception e){}
        //Toast.makeText(this, "Service stoppé", Toast.LENGTH_LONG).show();
    }
}
