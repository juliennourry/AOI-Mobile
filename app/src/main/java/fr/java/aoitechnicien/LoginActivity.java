package fr.java.aoitechnicien;

import static android.text.TextUtils.isEmpty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Models.ModelAuth;
import fr.java.aoitechnicien.Requester.ApiHelper;
import fr.java.aoitechnicien.Requester.ApiSync;
import fr.java.aoitechnicien.Requester.DatabaseHelper;
import fr.java.aoitechnicien.Service.ServiceNetwork;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    SharedHelper sharedhelper;
    ToastHelper toastHelper;
    EditText login;
    EditText psw;
    Button btnConnect;
    ProgressBar progressBar;
    ApiSync apisync;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String urlKey = "urlKey";
    public static final String pswKey = "pswKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        int SPLASH_TIMER = 3000;
        // -- DB
        final DatabaseHelper databaseHelper;
        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(LoginActivity.this);

        // -- SESSION INFORMATION
        sharedhelper = new SharedHelper(LoginActivity.this, sessionKey);
        String extra_sync = getIntent().getStringExtra("extra_sync");


        // -- CONTROL USER INFORMATION
        if (!isEmpty(sharedhelper.getParam(tokenKey)) && !isEmpty(sharedhelper.getParam(loginKey)) && !isEmpty(sharedhelper.getParam(pswKey)) && extra_sync.equals("true")) {


            if(databaseHelper.isConnect(database, sharedhelper.getParam(loginKey).trim(), sharedhelper.getParam(pswKey).trim())){

                sharedhelper.stockParam(loginKey, sharedhelper.getParam(loginKey));
                sharedhelper.stockParam(pswKey, sharedhelper.getParam(pswKey));

                // -- SYNC DATA LOOP
                // -- OLDSERVICE IF USER IS CONNECTED
//                Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
//                intentService.putExtra("login", sharedhelper.getParam(loginKey).trim());
//                intentService.putExtra("password", sharedhelper.getParam(pswKey).trim());
//                startService(intentService);

                // -- HOME PAGE
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                overridePendingTransition(R.anim.slide_out_animation, R.anim.slide_in_animation);
                startActivity(intent);
                finish();

            } else {
                Log.e("DEBUG_LOGIN", " AutoAuth failed ");
            }


        } else {
            Log.i("DEBUG_LOGIN", "STEP::2");
            // -- CONNECT VIEW PAGE
            setContentView(R.layout.activity_login);

            // -- ITEM PAGE
            login = findViewById(R.id.inputLogin);
            if(!sharedhelper.getParam(loginKey).trim().isEmpty()) {
                login.setText(sharedhelper.getParam(loginKey));
            }
            psw = findViewById(R.id.inputPsw);
            btnConnect = findViewById(R.id.btnConnect);
            progressBar = findViewById(R.id.progress_bar);

            // -- ACTION :: CONNEXION
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    progressBar.setVisibility(View.VISIBLE);
                    btnConnect.setVisibility(View.GONE);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("login", login.getText().toString());
                        jsonObject.put("password", psw.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    if(extra_sync.equals("false")) {
                        // -- CONNECT WITH API
                        ApiHelper.getApi("", sharedhelper.getParam("urlKey")).auth(body).enqueue(new Callback<ModelAuth>() {
                            public void onResponse(Call<ModelAuth> call, Response<ModelAuth> response) {
                                if (response.isSuccessful()) {
                                    Log.e("DEBUG_LOGAUTH", String.valueOf(response.body()));
                                    if (!response.body().getAuth().trim().isEmpty()) {
                                        sharedhelper.stockParam(tokenKey, response.body().getAuth());
                                        sharedhelper.stockParam(loginKey, login.getText().toString().trim());
                                        sharedhelper.stockParam(pswKey, psw.getText().toString().trim());
                                        toastHelper.LoadToasted(getResources().getString(R.string.connect_start));


                                        // -- START SERVICE
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                /*Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
                                                intentService.putExtra("extra_sync", "true");
                                                startService(intentService);*/

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(databaseHelper.isSync(database)) {
                                                            progressBar.setVisibility(View.GONE);
                                                            btnConnect.setVisibility(View.VISIBLE);

                                                            // -- SYNC DATA LOOP
                                                            // -- OLDSERVICE AFTER LOGIN BUT DDB IS SYNC
//                                                            Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
//                                                            intentService.putExtra("login", sharedhelper.getParam(loginKey));
//                                                            intentService.putExtra("password", sharedhelper.getParam(pswKey));
//                                                            startService(intentService);

                                                            // -- HOME PAGE
                                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.slide_out_animation, R.anim.slide_in_animation);
                                                            finish();
                                                        } else {
                                                            apisync = new ApiSync(sharedhelper.getParam(tokenKey), getBaseContext());
                                                            apisync.syncUser(sharedhelper.getParam(urlKey), sharedhelper.getParam(tokenKey), new ApiSync.SyncCallback() {
                                                                @Override
                                                                public void onSuccess(boolean success) {
                                                                    if (success) {
                                                                        // -- SYNC DATA LOOP
                                                                        // -- OLDSERVICE AFTER LOGIN BUT DDB IS NOT SYNC
//                                                                        Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
//                                                                        intentService.putExtra("login", sharedhelper.getParam(loginKey));
//                                                                        intentService.putExtra("password", sharedhelper.getParam(pswKey));
//                                                                        startService(intentService);

                                                                        progressBar.setVisibility(View.GONE);
                                                                        btnConnect.setVisibility(View.VISIBLE);

                                                                        // -- HOME PAGE
                                                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                                        startActivity(intent);
                                                                        overridePendingTransition(R.anim.slide_out_animation, R.anim.slide_in_animation);
                                                                        finish();
                                                                    } else {
                                                                        toastHelper.LoadToasted(getString(R.string.cannot_connexion));
                                                                        progressBar.setVisibility(View.GONE);
                                                                        btnConnect.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            });

//                                                            if(apisync.syncUser(sharedhelper.getParam(tokenKey)) && apisync.syncItem()) {
//                                                                // -- SYNC DATA LOOP
//                                                                Intent intentService = new Intent(getBaseContext(), ServiceNetwork.class);
//                                                                intentService.putExtra("login", sharedhelper.getParam(loginKey));
//                                                                intentService.putExtra("password", sharedhelper.getParam(pswKey));
//                                                                startService(intentService);
//
//                                                                progressBar.setVisibility(View.GONE);
//                                                                btnConnect.setVisibility(View.VISIBLE);
//
//                                                                // -- HOME PAGE
//                                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                                                startActivity(intent);
//                                                                overridePendingTransition(R.anim.slide_out_animation, R.anim.slide_in_animation);
//                                                                finish();
//                                                            } else {
//                                                                toastHelper.LoadToasted("Connexion impossible");
//                                                                progressBar.setVisibility(View.GONE);
//                                                                btnConnect.setVisibility(View.VISIBLE);
//                                                            }

                                                        }
                                                    }
                                                }, SPLASH_TIMER);

                                            }
                                        }, SPLASH_TIMER);

                                    } else {
                                        toastHelper.LoadToasted(getResources().getString(R.string.remote_error));
                                        progressBar.setVisibility(View.GONE);
                                        btnConnect.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    toastHelper.LoadToasted(getResources().getString(R.string.connect_error));
                                    progressBar.setVisibility(View.GONE);
                                    btnConnect.setVisibility(View.VISIBLE);
                                }
                            }
                            public void onFailure(Call<ModelAuth> call, Throwable t) {
                                toastHelper.LoadToasted(getResources().getString(R.string.remote_connect_error));
                                progressBar.setVisibility(View.GONE);
                                btnConnect.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        Log.i("DEBUG_LOGIN", "STEP::4");
                        // -- IMPOSSIBLE BECAUSE EXTRA SYNC IS ALWAYS FALSE HERE
                        // -- CONNECT WITH BDD
                        /*Log.e("THREAD_INFO", " BDD YES Connect ");
                        if(databaseHelper.isConnect(database, sharedhelper.getParam(loginKey), sharedhelper.getParam(pswKey))){

                            sharedhelper.stockParam(loginKey, sharedhelper.getParam(loginKey));
                            sharedhelper.stockParam(pswKey, sharedhelper.getParam(pswKey));



                            Log.i("DTB_SYNC", " Auth success ");
                            progressBar.setVisibility(View.GONE);
                            btnConnect.setVisibility(View.VISIBLE);
                        } else {
                            Log.i("DTB_SYNC", " Auth failed ");
                            progressBar.setVisibility(View.GONE);
                            btnConnect.setVisibility(View.VISIBLE);
                        }*/
                    }


                }
            });
        }

    }
}