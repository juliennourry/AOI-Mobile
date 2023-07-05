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

        final Integer[] Counted = {0};

        t = new Thread() {
            @Override
            public void run() {

                isReturnThread = false;
                if(!isStartedThread){
                    isStartedThread = true;
                    c_auth = true;
                    try {
                        while (isStartedThread) {
                            Thread.sleep(2000);
                            isReturnThread = true;


                            if(c_auth) {
                                c_auth = false;
                                Counted[0]++;
                                Log.e("DEBUG_LOOP_THREAD", "COUNT::" + String.valueOf(Counted[0]));

                                // -- LOOP GET AUTH TOKEN
                                do {
                                    apisync.syncAuth(databaseHelper.getApiUrl(database, "url"), login, password);
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
                                    apisync.syncUser(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback() {

                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {

                                                Log.i("DEBUG_API_THREAD", "SYNCUSER:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCUSER:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC ITEM
                                do {
                                    apisync.syncAppareil(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), login, new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {

                                                Log.i("DEBUG_API_THREAD", "SYNCAPPAREIL:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCAPPAREIL:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC OFFTIME UP
                                do {
                                    apisync.syncOfftimeUp(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCOFFTIMEUP:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCOFFTIMEUP:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC OFFTIME DOWN
                                do {
                                    apisync.syncOfftimeDown(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCOFFTIMEDOWN:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCOFFTIMEDOWN:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC INTERVENTIONS
                                do {
                                    apisync.syncInterventions(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCINTERVENTION:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCINTERVENTION:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC MOUNTAGE
                                do {
                                    apisync.syncMountage(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCMOUNTAGE:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCMOUNTAGE:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC MOUNTAGE DONE DOWN
                                do {
                                    apisync.syncMountageDone(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCMOUNTAGEDONE:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCMOUNTAGEDONE:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC MOUNTAGE DONE UP
                                do {
                                    apisync.syncMountageDoneUp(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCMOUNTAGEDONEUP:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCMOUNTAGEDONEUP:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC FREQUENCY by CATEGORY
                                do {
                                    apisync.syncFrequency(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCFREQUENCY:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCFREQUENCY:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC TASK and GROUPS
                                do {
                                    apisync.syncTask(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCTASK:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCTASK:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC TASKDONE
                                do {
                                    apisync.syncTaskDone(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCTASKDONE:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCTASKDONE:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC TASKDONE UP
                                do {
                                    apisync.syncLocalTaskDone(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), "0", "0", new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCTASKDONEPOST:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCTASKDONEPOST:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC RMFREQUENCY
                                do {
                                    apisync.syncRMFrequency(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCRMFREQUENCY:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCRMFREQUENCY:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC RMF UP
                                do {
                                    apisync.syncRMFUp(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCRMFUP:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCRMFUP:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC MAINTENANCE UP ONLY
                                do {
                                    apisync.syncMaintenances(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCMAINTENANCE:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCMAINTENANCE:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC BON INTERVENTION
                                do {
                                    apisync.syncBonIntervention(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCBONINTERVENTION:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCBONINTERVENTION:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC DEMANDE
                                do {
                                    apisync.syncDemand(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCDEMAND:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCDEMAND:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC NOTE
                                do {
                                    apisync.syncNote(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCNOTE:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCNOTE:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- SYNC Tiers
                                do {
                                    apisync.syncTiers(databaseHelper.getApiUrl(database, "url"), databaseHelper.getSyncDTB(database, "token"), new ApiSync.SyncCallback(){
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                Log.i("DEBUG_API_THREAD", "SYNCTIERS:: SUCCESS");
                                            } else {
                                                Log.i("DEBUG_API_THREAD", "SYNCTIERS:: FAIL");
                                            }
                                        }
                                    });
                                    Thread.sleep(2000);
                                } while (databaseHelper.getSyncDTB(database, "createdAt").equals(dateSync));

                                // -- END SYNC
                                dateSync = databaseHelper.getSyncDTB(database, "createdAt");
                                c_auth = true;

                                // -- EDIT TO ALTER THE DELAY
                                Thread.sleep(5000);

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
                        Log.i("DEBUG_THREAD", "ERROR : " + e);
                    }

                }
            }
        };
        t.start();
        return isReturnThread;
    }

    public static boolean isStopedThread() {
        if(t != null){
            if(t.isAlive()){
                t.interrupt();
                isStartedThread = false;
            }
        }
        return true;
    }


    public static boolean isAliveThread() {
        if(t != null){
            if(t.isAlive()){
                return true;
            }
        }
        return false;
    }

}
