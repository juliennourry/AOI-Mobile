package fr.java.aoitechnicien;

import static android.text.TextUtils.isEmpty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String pswKey = "pswKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // -- SESSION INFORMATION
        sharedhelper = new SharedHelper(LoginActivity.this, sessionKey);

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(LoginActivity.this);

        // -- CONTROL USER INFORMATION
        if (!isEmpty(sharedhelper.getParam(tokenKey)) && !isEmpty(sharedhelper.getParam(loginKey)) && !isEmpty(sharedhelper.getParam(pswKey))) {

            // -- HOME PAGE
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            overridePendingTransition(R.anim.slide_out_animation, R.anim.slide_in_animation);
            startActivity(intent);
            finish();

        } else {

            // -- CONNECT VIEW PAGE
            setContentView(R.layout.activity_login);

            // -- ITEM PAGE
            login = findViewById(R.id.inputLogin);
            if(sharedhelper.getParam(loginKey).trim().isEmpty()) {
                login.setText(sharedhelper.getParam(loginKey));
            }
            psw = findViewById(R.id.inputPsw);
            btnConnect = findViewById(R.id.btnConnect);

            // -- ACTION :: CONNEXION
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("login", login.getText().toString());
                        jsonObject.put("password", psw.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());


                    /*Log.d("APIRESPONSE", "---------------------------------------");
                    Log.d("APIRESPONSE", login.getText().toString()+"//"+psw.getText().toString());
                    Log.d("APIRESPONSE", "---------------------------------------");*/


                    ApiHelper.getApi().auth(body).enqueue(new Callback<ModelAuth>() {
                        public void onResponse(Call<ModelAuth> call, Response<ModelAuth> response) {
                            //Log.d("APIRESPONSE", response.toString());
                            if (response.isSuccessful()) {
                                if (!response.body().getAuth().trim().isEmpty()) {
                                    sharedhelper.stockParam(tokenKey, response.body().getAuth());
                                    sharedhelper.stockParam(loginKey, login.getText().toString());
                                    sharedhelper.stockParam(pswKey, psw.getText().toString());
                                    //Log.d("APIRESPONSE", sharedhelper.getParam(tokenKey));
                                    toastHelper.LoadToasted("Connexion réussie");

                                    // -- HOME PAGE
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_out_animation, R.anim.slide_in_animation);
                                    finish();
                                } else {
                                    //Log.d("APIRESPONSE", "Token Empty ... ");
                                }
                            } else {
                                toastHelper.LoadToasted("Erreur de connexion, veuillez réessayer ...");
                            }
                        }
                        public void onFailure(Call<ModelAuth> call, Throwable t) {
                            Log.d("APIRESPONSE", t.toString());
                            toastHelper.LoadToasted("Erreur de connexion, veuillez réessayer ...");
                        }
                    });


                }
            });
        }

    }
}