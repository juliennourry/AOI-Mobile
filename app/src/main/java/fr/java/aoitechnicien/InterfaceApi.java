package fr.java.aoitechnicien;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface InterfaceApi {
    @POST("/auth")
    Call<ModelAuth> auth(@Body RequestBody body);

    @GET("/api/users")
    Call<ModelApiUser> getUser();
}
