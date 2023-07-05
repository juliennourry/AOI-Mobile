package fr.java.aoitechnicien.Requester;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import fr.java.aoitechnicien.ConfigActivity;
import fr.java.aoitechnicien.Function.SharedHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.database.sqlite.SQLiteDatabase;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class ApiHelper {
    private static final String BASE_URL = "https://portail.ascenseurs-oi.re";//"http://10.100.0.153:8000";
    private static Retrofit retrofit;
    private static InterfaceApi api;
    private static String strToken;
    static SharedHelper sharedhelper;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String urlKey = "urlKey";





    public static InterfaceApi getApi(String strToken, String Url){

        Interceptor intercept = new AuthInterceptor(strToken);

        //if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .addInterceptor(intercept)
                            .build())
                    .build();
        //}
        //if(api == null){
            api = retrofit.create(InterfaceApi.class);
        //}
        return api;
    }

    public static InterfaceApi getApiRequest(String strToken, String Url){

        Interceptor intercept = new AuthInterceptor(strToken);

        //if(retrofit == null){
        retrofit = new Retrofit.Builder()
                .baseUrl(Url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .addInterceptor(intercept)
                        .build())
                .build();
        //}
        //if(api == null){
        api = retrofit.create(InterfaceApi.class);
        //}
        return api;
    }

}



