package fr.java.aoitechnicien.Requester;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.java.aoitechnicien.Models.ModelApiItem;
import fr.java.aoitechnicien.Models.ModelApiOfftime;
import fr.java.aoitechnicien.Models.ModelApiSite;
import fr.java.aoitechnicien.Models.ModelApiUser;
import fr.java.aoitechnicien.Models.ModelAuth;
import fr.java.aoitechnicien.Function.SharedHelper;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiSync {

    SharedHelper sharedhelper;
    private Context context;
    private String token;
    private static final String BASE_URL = "http://10.100.0.153:8000";
    private static Retrofit retrofit;
    private static InterfaceApi api;
    private static List<ModelApiUser> dataArrayList;
    private static List<ModelApiItem> dataArrayItem;
    private static List<ModelApiOfftime> dataArrayOfftime;
    private static ModelApiSite dataArraySite;
    private static SQLiteDatabase database;

    // -- DB
    private static DatabaseHelper databaseHelper;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";

    public ApiSync(String strToken, Context context) {

        this.token = strToken;
        this.context = context;

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
    }

    public static Boolean syncUser(String token) {
        ApiHelper.getApi(token).getUsers().enqueue(new Callback<List<ModelApiUser>>() {
            public void onResponse(Call<List<ModelApiUser>> call, Response<List<ModelApiUser>> response) {
                if (response.isSuccessful()) {
                    dataArrayList = response.body();
                    for (ModelApiUser user : dataArrayList) {
                        databaseHelper.verifyAndInsertUser(database, user);
                    }

                }
            }

            public void onFailure(Call<List<ModelApiUser>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_USER", t.toString());
            }
        });
        return true;
    }


    public static Boolean syncAppareil(String token, String emailUser) {
        ApiHelper.getApi(token).getItems().enqueue(new Callback<List<ModelApiItem>>() {
            public void onResponse(Call<List<ModelApiItem>> call, Response<List<ModelApiItem>> response_item) {
                if (response_item.isSuccessful()) {
                    dataArrayItem = response_item.body();
                    for (ModelApiItem item : dataArrayItem) {
                        databaseHelper.verifyAndInsertItem(database, item);

                        // -- CHECK USER TO ACCESS THIS APPAREIL
                        ArrayList<String> user_access = item.getSite().getUser();

                        Boolean user_ctrl = false;
                        for (String element : user_access) {
                            String id_user = element.replaceAll("/api/users/", "");
                            Boolean access = databaseHelper.checkAccessItem(database, id_user, emailUser);
                            if(access){user_ctrl = true;}
                        }

                        Integer id_item = item.getId();
                        databaseHelper.updateAccessItem(database, id_item, user_ctrl);

                        // -- UPDATE SITE ID
                        syncSite(token, item.getSite().getId());


                    }

                }
            }

            public void onFailure(Call<List<ModelApiItem>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_ITEM", t.toString());
            }
        });
        return true;
    }


    public static Boolean syncSite(String token, Integer idSite) {
        ApiHelper.getApi(token).getSite(idSite).enqueue(new Callback<ModelApiSite>() {
            public void onResponse(Call<ModelApiSite> call, Response<ModelApiSite> response_site) {
                Log.e("DEBUG_SHOW_RESPONSE_SITE", String.valueOf(response_site));
                if (response_site.isSuccessful()) {
                    dataArraySite = response_site.body();
                    Log.e("DEBUG_SITE_API", String.valueOf(dataArraySite));
                    /*for (ModelApiSite site : dataArraySite) {*/
                        databaseHelper.verifyAndInsertSite(database, dataArraySite);
                    /*}*/

                }
            }

            public void onFailure(Call<ModelApiSite> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_SITE", t.toString());
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

    public static void syncAuth(String login, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("login", login);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        ApiHelper.getApi("").auth(body).enqueue(new Callback<ModelAuth>() {
            public void onResponse(Call<ModelAuth> call, Response<ModelAuth> response) {
                String retour = null;
                if (response.isSuccessful()) {
                    if (!response.body().getAuth().trim().isEmpty()) {
                        retour = response.body().getAuth();
                        databaseHelper.verifyAndInsert(database, retour);
                    }
                }
            }

            public void onFailure(Call<ModelAuth> call, Throwable t) {
            }
        });
    }

    public static Boolean syncOfftime(String token) {
        ApiHelper.getApi(token).getOfftime().enqueue(new Callback<List<ModelApiOfftime>>() {
            public void onResponse(Call<List<ModelApiOfftime>> call, Response<List<ModelApiOfftime>> response_offtime) {
                Log.e("DEBUG_SHOW_RESPONSE_SITE", String.valueOf(response_offtime));
                if (response_offtime.isSuccessful()) {
                    dataArrayOfftime = response_offtime.body();

                    for (ModelApiOfftime offtime : dataArrayOfftime) {
                        if(databaseHelper.verifyAppareilAccess(database, offtime.getFkItemsite().replace("/api/item_sites/", ""))) {
                            databaseHelper.verifyAndInsertOfftime(database, offtime);
                        }

                        // -- CHECK USER TO ACCESS THIS APPAREIL
                        /*ArrayList<String> user_access = item.getSite().getUser();

                        Boolean user_ctrl = false;
                        for (String element : user_access) {
                            String id_user = element.replaceAll("/api/users/", "");
                            Boolean access = databaseHelper.checkAccessItem(database, id_user, emailUser);
                            if(access){user_ctrl = true;}
                        }

                        Integer id_item = item.getId();
                        databaseHelper.updateAccessItem(database, id_item, user_ctrl);

                        // -- UPDATE SITE ID
                        syncSite(token, item.getSite().getId());*/


                    }

                }
            }

            public void onFailure(Call<List<ModelApiOfftime>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_SITE", t.toString());
            }
        });
        return true;
    }
}
