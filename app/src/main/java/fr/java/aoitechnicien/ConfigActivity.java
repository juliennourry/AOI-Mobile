package fr.java.aoitechnicien;

import static android.text.TextUtils.isEmpty;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Models.ModelApiItemReduce;
import fr.java.aoitechnicien.Models.ModelApiMountageDone;
import fr.java.aoitechnicien.Models.ModelApiStatus;
import fr.java.aoitechnicien.Requester.ApiHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigActivity extends AppCompatActivity {

    private static List<ModelApiStatus> dataArrayStatus;
    public static int SPLASH_TIMER = 3000;
    EditText url;
    Button btnConfiguration;
    ProgressBar progressBar;
    ToastHelper toastHelper;
    String urlSync;

    // -- SESSION INFORMATION
    SharedHelper sharedhelper;
    public static final String sessionKey = "sessionKey";
    public static final String urlKey = "urlKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);

        url = findViewById(R.id.inputUrl);
        btnConfiguration = findViewById(R.id.btnConfiguration);
        progressBar = findViewById(R.id.progress_bar);

        // -- DB
        final DatabaseHelper databaseHelper;
        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        // -- TOAST PREPARED
        toastHelper = new ToastHelper(ConfigActivity.this);

        // -- SESSION INFORMATION
        sharedhelper = new SharedHelper(ConfigActivity.this, sessionKey);

        btnConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                btnConfiguration.setVisibility(View.GONE);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(ConfigActivity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                JSONObject jsonObject = new JSONObject();
                Log.e("DEBUG_CONFIGURATION", "::1::");
                try {
                    if(url.getText().toString().length() == 0){
                        toastHelper.LoadToasted(getResources().getString(R.string.url_not_found));
                        progressBar.setVisibility(View.GONE);
                        btnConfiguration.setVisibility(View.VISIBLE);
                    } else {

                            jsonObject.put("url", url.getText().toString());

                            if (!url.getText().toString().startsWith("http://") && !url.getText().toString().startsWith("https://")) {
                                // Add "https://" to the URL
                                urlSync = "https://" + url.getText().toString();
                            } else {
                                urlSync = url.getText().toString();
                            }

                        if(isValidURL(urlSync)){
                            if (!urlSync.endsWith("/")) {
                                // Append a slash to the base URL
                                urlSync = urlSync + "/";
                            }
                            Log.e("DEBUG_CONFIGURATION", "::2::");
                            if(!isEmpty(urlSync)){
                                Log.e("DEBUG_CONFIGURATION", "::3::");
                                ApiHelper.getApiRequest("", urlSync).getStatus().enqueue(new Callback<List<ModelApiStatus>>() {
                                    public void onResponse(Call<List<ModelApiStatus>> call, Response<List<ModelApiStatus>> response_status) {
                                        Log.e("DEBUG_CONFIGURATION", "::4::");
                                        if (response_status.isSuccessful()) {
                                            Log.e("DEBUG_CONFIGURATION", "::5::");
                                            dataArrayStatus = response_status.body();
                                            for (ModelApiStatus status : dataArrayStatus) {
                                                toastHelper.LoadToasted(getResources().getString(R.string.url_working));
                                                Log.e("DEBUG_CONFIGURATION", status.getVersion().toString());
                                                Boolean insertAPI = databaseHelper.insertAPI(database, urlSync);

                                                sharedhelper.stockParam(urlKey, urlSync);
                                                if(insertAPI){
                                                    toastHelper.LoadToasted(getResources().getString(R.string.url_insert));
                                                    new Handler().postDelayed(() -> {

                                                        Intent intent = new Intent(ConfigActivity.this, LoginActivity.class);
                                                        intent.putExtra("extra_sync", "false");
                                                        startActivity(intent);
                                                        finish();
                                                    }, SPLASH_TIMER);
                                                } else {
                                                    toastHelper.LoadToasted(getResources().getString(R.string.url_not_insert));
                                                }
                                            }


                                            progressBar.setVisibility(View.GONE);
                                            btnConfiguration.setVisibility(View.VISIBLE);
                                        } else {
                                            Log.e("DEBUG_CONFIGURATION", "::7::");
                                            toastHelper.LoadToasted(getResources().getString(R.string.url_not_working));
                                            progressBar.setVisibility(View.GONE);
                                            btnConfiguration.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    public void onFailure(Call<List<ModelApiStatus>> call, Throwable t) {
                                        Log.e("DEBUG_CONFIGURATION", "::6::");
                                        Log.e("DEBUG_CONFIGURATION", t.toString());
                                        toastHelper.LoadToasted(getResources().getString(R.string.url_not_working));
                                        progressBar.setVisibility(View.GONE);
                                        btnConfiguration.setVisibility(View.VISIBLE);
                                    }
                                });

                            } else {
                                toastHelper.LoadToasted(getResources().getString(R.string.url_not_found));

                                progressBar.setVisibility(View.GONE);
                                btnConfiguration.setVisibility(View.VISIBLE);
                            }
                        } else {
                            toastHelper.LoadToasted(getResources().getString(R.string.url_not_valid));

                            progressBar.setVisibility(View.GONE);
                            btnConfiguration.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static boolean isValidURL(String urlString) {
        Log.e("DEBUG_URL", urlString);
        try {
            URL url = new URL(urlString);
            url.toURI(); // This checks for the URI syntax
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}