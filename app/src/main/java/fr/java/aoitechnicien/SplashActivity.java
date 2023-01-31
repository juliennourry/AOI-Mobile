package fr.java.aoitechnicien;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIMER = 3000;
    TextView textSplash;

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
        Log.e("NETWORK INFO ::SN ::", "LOAD::SERVICE");

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
                if (databaseHelper.isTableExists(database, "sync") && databaseHelper.isSync(database)) {
                    // table exists
                    Log.i("THREAD INFO", " -- SYNC OK --");
                    textSplash.setText("Lancement du service de synchronisation ...");

                    //startService(new Intent(getBaseContext(), ServiceNetwork.class));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            intent.putExtra("extra_sync", "false");
                            startActivity(intent);
                            finish();
                        }
                    }, SPLASH_TIMER);
                } else {
                    textSplash.setText("Première connexion à l'application ...");
                    // table does not exist
                    Log.i("THREAD INFO", " -- SYNC NOT --");

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