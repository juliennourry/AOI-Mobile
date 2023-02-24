package fr.java.aoitechnicien.Service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Requester.ApiSync;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

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

    // -- DEFAULT CONTROL
    public static boolean c_auth = true;
    public static boolean c_user = false;
    public static boolean c_item = false;
    public static String dateSync;


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

    public static boolean isStartedThread(Context context, String login, String password) {
        String token = null;



        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        dateSync = databaseHelper.getSyncDTB(database, "createdAt");
        ApiSync apisync = new ApiSync("0", context);

        t = new Thread() {
            @Override
            public void run() {

                isReturnThread = false;
                if(!isStartedThread){
                    isStartedThread = true;

                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(2000);
                            isReturnThread = true;

                            if(c_auth) {
                                c_auth = false;

                                // -- LOOP GET AUTH TOKEN
                                do {
                                    apisync.syncAuth(login, password);
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "token") == null);
                                databaseHelper.verifyAndInsert(database, databaseHelper.getSyncDTB(database, "token"));
                                c_user = true;
                                c_item = true;
                            }

                            if(databaseHelper.getSyncDTB(database, "token").length() > 200 && c_user && c_item) {
                                c_user = false;
                                c_item = false;

                                // -- SYNC USER
                                do {
                                    apisync.syncUser(databaseHelper.getSyncDTB(database, "token"));
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC ITEM
                                do {
                                    apisync.syncAppareil(databaseHelper.getSyncDTB(database, "token"), login);
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC INTERVENTIONS
                                do {
                                    Log.d("SYNC", "Syncing interventions!");
                                    apisync.syncInterventions(databaseHelper.getSyncDTB(database, "token"));
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- END SYNC
                                dateSync = databaseHelper.getSyncDTB(database, "createdAt");
                                c_auth = true;

                                // -- EDIT TO ALTER THE DELAY
                                Thread.sleep(10000);

                            }

                                /*if ((token = ApiSync.syncAuth(context, login, password)) != null) {

                                    Thread.sleep(2000);
                                    // -- FOLLOW PROCESS SYNC
                                    if(c_user){
                                        Log.i("THREAD INFO", "-- USER LOOP --");
                                        Thread.sleep(2000);
                                        c_user = false;
                                        ApiSync.syncUser(token);
                                        Thread.sleep(2000);
                                        Log.i("THREAD INFO", "-- RESTART LOOP --");
                                        c_user = c_auth = true;
                                    }
                                } else {
                                    Log.e("THREAD INFO", "-- ERROR LOOP --");
                                    c_auth = true;
                                }*/


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
        }
        return true;
    }

}
