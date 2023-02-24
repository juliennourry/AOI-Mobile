package fr.java.aoitechnicien;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIMER = 3000;
    TextView textSplash;
    SharedHelper sharedhelper;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String pswKey = "pswKey";

    // -- DB
    private static DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textSplash = findViewById(R.id.text_loader_splash);

        // -- Start service network check info
        textSplash.setText("Lancement du service ...");

        // -- SESSION INFORMATION
        sharedhelper = new SharedHelper(SplashActivity.this, sessionKey);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                textSplash.setText("Analyse des données ...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                // -- START CREATE DB
                databaseHelper = new DatabaseHelper(getBaseContext());
                SQLiteDatabase database = databaseHelper.getWritableDatabase();

                // -- REMOVE DBB HERE
                if (databaseHelper.isTableExists(database, "sync") && databaseHelper.isSync(database) && databaseHelper.isConnect(database, sharedhelper.getParam(loginKey), sharedhelper.getParam(pswKey))) {
                    // table exists
                    Log.i("DEBUG_SPLASH", " -- SYNC OK --");
                    textSplash.setText("Lancement du service de synchronisation ...");

                    //startService(new Intent(getBaseContext(), ServiceNetwork.class));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            intent.putExtra("extra_sync", "true");
                            startActivity(intent);
                            finish();
                        }
                    }, SPLASH_TIMER);
                } else {
                    // -- TO REMOVE !
                    //databaseHelper.onClean(database);

                    textSplash.setText("Connexion à l'application ...");
                    // table does not exist
                    Log.i("DEBUG_SPLASH", " -- SYNC NOT --");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            intent.putExtra("extra_sync", "false");
                            startActivity(intent);
                            finish();
                        }
                    }, SPLASH_TIMER);
                }
                    }
                }, SPLASH_TIMER);

            }
        }, SPLASH_TIMER);

    }
}