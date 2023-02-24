package fr.java.aoitechnicien.Requester;

import java.util.List;

import fr.java.aoitechnicien.Models.ModelApiItem;
import fr.java.aoitechnicien.Models.ModelApiSite;
import fr.java.aoitechnicien.Models.ModelApiUser;
import fr.java.aoitechnicien.Models.ModelAuth;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InterfaceApi {
    @POST("/auth")
    Call<ModelAuth> auth(@Body RequestBody body);

    @GET("/api/users")
    Call<List<ModelApiUser>> getUsers();
    //Call<ModelApiUser> getUser();

    @GET("/api/item_sites")
    Call<List<ModelApiItem>> getItems();

    @GET("/api/sites/{id}")
    Call<ModelApiSite> getSite(@Path("id") int idSite);
}
