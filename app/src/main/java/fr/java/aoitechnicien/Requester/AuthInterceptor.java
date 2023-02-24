package fr.java.aoitechnicien.Requester;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private String token;


    public AuthInterceptor(String strtoken) {
        this.token = strtoken;
    }

    public String getToken(){
        return this.token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json");

        Request request = builder.build();
        return chain.proceed(request);
    }
}

