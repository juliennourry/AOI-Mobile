package fr.java.aoitechnicien;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionHelper {
    public static long lastNoConnectionTs = -1;
    public static boolean isOnline = true;
    // -- VAR to Thread
    public static boolean isStartedThread = false;
    public static boolean isReturnThread = false;
    public static Thread t;
    // -- DB
    private static DatabaseHelper databaseHelper;
    // -- SHAREDPREFERENCES
    SharedHelper sharedhelper;
    public static final String textSplashed = "textSplashed";

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String pswKey = "pswKey";

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =(ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static boolean isConnectedOrConnecting(Context context) {
        ConnectivityManager cm =(ConnectivityManager)         context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void updateTextSplash(final String text){
        //TextView textSplash = (TextView) findViewById(R.id.text_loader_splash);

    }

    public static boolean isStartedThread(Context context) {
        Log.i("THREAD INFO", " -- 1 --");

        t = new Thread() {
            @Override
            public void run() {

                Log.i("THREAD INFO", " -- 2 --");
                isReturnThread = false;
                if(!isStartedThread){
                    isStartedThread = true;

                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(15000);
                            isReturnThread = true;
                            Log.i("THREAD INFO", "-- LOOP --");
                            //public void run() {
                            //update here (images or other values)
                            //Toast.makeText(onStartCommand.this, "Service Started", Toast.LENGTH_LONG).show();

                            //}




                        }
                    } catch (InterruptedException e) {
                        isReturnThread = false;
                        Log.i("THREAD INFO", "ERROR : " + e);
                    }

                }
            }
        };
        t.start();
        return isReturnThread;
    }

    public static boolean isStopedThread(Context context) {
        if(t.isAlive()){
            t.interrupt();
            isStartedThread = false;
            Log.i("THREAD INFO", "-- KILL --");
        }
        return true;
    }
}
