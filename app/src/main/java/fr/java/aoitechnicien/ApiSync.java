package fr.java.aoitechnicien;

import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiSync {

    private String token;
    private static final String BASE_URL = "http://10.100.0.153:8000";
    private static Retrofit retrofit;
    private static InterfaceApi api;

    public ApiSync(String strToken) {
        this.token = strToken;
    }

    public Boolean syncUser() {
        //Log.e("APISYNC", token);
        getApiSync().getUser().enqueue(new Callback<ModelApiUser>() {
            public void onResponse(Call<ModelApiUser> call, Response<ModelApiUser> response) {
                if (response.isSuccessful()) {

                    //Log.e("APIRESPONSE", response.toString());
                    Log.e("API_SYNC_DATA", response.body().getUsers());

                }
            }

            public void onFailure(Call<ModelApiUser> call, Throwable t) {
                Log.e("APISYNC", t.toString());
            }
        });
        return true;
    }

    public Boolean syncItem() {
        return true;
    }

    public InterfaceApi getApiSync(){

        Interceptor intercept = new AuthInterceptor(token);

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
