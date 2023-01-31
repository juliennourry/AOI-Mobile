package fr.java.aoitechnicien;

import android.app.Activity;
import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHelper {
    private static final String BASE_URL = "http://10.100.0.153:8000";
    private static Retrofit retrofit;
    private static InterfaceApi api;
    private static String strToken;
    static SharedHelper sharedhelper;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";


    public static InterfaceApi getApi(String strToken){
        //Log.e("APISYNC", "NOBEARER :: " + strToken);

        Interceptor intercept = new AuthInterceptor(strToken);

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder()
                            .addInterceptor(intercept)
                            .build())
                    .build();
        }
        if(api == null){
            api = retrofit.create(InterfaceApi.class);
        }
        return api;
    }

}



