package fr.java.aoitechnicien;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIMER = 3000;
    TextView textSplash;
    SharedHelper sharedhelper;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
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
        textSplash.setText(getResources().getString(R.string.open_service));

        // -- SESSION INFORMATION
        sharedhelper = new SharedHelper(SplashActivity.this, sessionKey);

        new Handler().postDelayed(() -> {


            textSplash.setText(getResources().getString(R.string.data_analysis));
            new Handler().postDelayed(() -> {
        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(getBaseContext());

        // -- !! To remove, use it to clean the dev database
        // -- databaseHelper.deleteDatabase(getBaseContext());

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- REMOVE DBB HERE
        if (databaseHelper.isTableExists(database, "sync") && databaseHelper.isSync(database) && databaseHelper.isConnect(database, sharedhelper.getParam(loginKey), sharedhelper.getParam(pswKey)) && databaseHelper.hasUrl(database)) {
            // -- TO REMOVE !
            //databaseHelper.debugAction(database);

            // table exists
            Log.i("DEBUG_SPLASH", " -- SYNC OK --");
            textSplash.setText(getResources().getString(R.string.start_sync));

            //startService(new Intent(getBaseContext(), ServiceNetwork.class));
            new Handler().postDelayed(() -> {

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("extra_sync", "true");
                startActivity(intent);
                finish();
            }, SPLASH_TIMER);
        } else {
            // -- TO REMOVE !
            //databaseHelper.onClean(database);

            textSplash.setText(getResources().getString(R.string.connect));
            // table does not exist
            Log.i("DEBUG_SPLASH", " -- SYNC NOT --");

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, ConfigActivity.class);
                intent.putExtra("extra_sync", "false");
                startActivity(intent);
                finish();
            }, SPLASH_TIMER);
        }
            }, SPLASH_TIMER);

        }, SPLASH_TIMER);

    }
}