package fr.java.aoitechnicien.Requester;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.java.aoitechnicien.Models.ModelApiBonIntervention;
import fr.java.aoitechnicien.Models.ModelApiCategory;
import fr.java.aoitechnicien.Models.ModelApiDemand;
import fr.java.aoitechnicien.Models.ModelApiFrequency;
import fr.java.aoitechnicien.Models.ModelApiItem;
import fr.java.aoitechnicien.Models.ModelApiItemReduce;
import fr.java.aoitechnicien.Models.ModelApiMaintTask;
import fr.java.aoitechnicien.Models.ModelApiMaintTaskDone;
import fr.java.aoitechnicien.Models.ModelApiMountage;
import fr.java.aoitechnicien.Models.ModelApiMountageDone;
import fr.java.aoitechnicien.Models.ModelApiNote;
import fr.java.aoitechnicien.Models.ModelApiOfftime;
import fr.java.aoitechnicien.Models.ModelApiRMFrequency;
import fr.java.aoitechnicien.Models.ModelApiSite;
import fr.java.aoitechnicien.Models.ModelApiTiers;
import fr.java.aoitechnicien.Models.ModelApiUser;
import fr.java.aoitechnicien.Models.ModelAuth;
import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Models.ModelIntervention;
import fr.java.aoitechnicien.Models.ModelMaintTaskCategory;
import fr.java.aoitechnicien.Models.ModelMaintenance;
import fr.java.aoitechnicien.Models.ModelMountageDone;
import fr.java.aoitechnicien.Models.ModelOfftime;
import fr.java.aoitechnicien.Models.ModelTaskDone;
import fr.java.aoitechnicien.Models.ModelApiBonIntervention;
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
    private static final String BASE_URL = "https://portail.ascenseurs-oi.re";//http://10.100.0.153:8000";
    private static Retrofit retrofit;
    private static InterfaceApi api;
    private static List<ModelApiUser> dataArrayList;
    private static List<ModelApiItem> dataArrayItem;
    private static List<ModelApiItemReduce> dataArrayItemReduce;
    private static List<ModelApiOfftime> dataArrayOfftime;
    private static List<ModelApiMountage> dataArrayMountage;
    private static List<ModelApiMountageDone> dataArrayMountageDone;
    private static List<ModelApiCategory> dataArrayCategory;
    private static List<ModelApiFrequency> dataArrayFrequency;
    private static List<ModelApiMaintTask> dataArrayTask;
    private static List<ModelApiMaintTaskDone> dataArrayTaskDone;
    private static List<ModelApiRMFrequency> dataArrayRMFrequency;
    private static List<ModelApiBonIntervention> dataArrayBonIntervention;
    private static List<ModelApiDemand> dataArrayDemand;
    private static List<ModelApiNote> dataArrayNote;
    private static List<ModelApiTiers> dataArrayTiers;
    private static ModelApiSite dataArraySite;
    private static ModelIntervention dataIntervention;
    private static ModelMaintenance dataMaintenance;
    private static ModelOfftime dataOfftime;
    private static ModelMountageDone dataMountageDone;
    private static ModelMaintTaskCategory dataArrayGroups;
    private static ModelTaskDone dataTaskDone;
    private static ModelApiRMFrequency dataRMFrequency;
    private static ModelApiBonIntervention dataBonIntervention;
    private static ModelApiDemand dataDemand;
    private static ModelApiNote dataNote;
    private static ModelApiTiers dataTiers;
    private static SQLiteDatabase database;
    private Boolean control = false;

    // -- DB
    private static DatabaseHelper databaseHelper;

    // -- SESSION INFORMATION
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";

    //CALL BACK RESPONSE
    public interface SyncCallback {
        void onSuccess(boolean success);
    }
    public ApiSync(String strToken, Context context) {

        this.token = strToken;
        this.context = context;

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
    }

//    WITH CALLBACK RETURN
//    public static Boolean syncUser(String token) {
//        Log.e("DEBUG_SYNCUSER", "STARTO:: "+token);
//        final Boolean[] retour = {false};
//        ApiHelper.getApi(token).getUsers().enqueue(new Callback<List<ModelApiUser>>() {
//            public void onResponse(Call<List<ModelApiUser>> call, Response<List<ModelApiUser>> response) {
//                if (response.isSuccessful()) {
//                    dataArrayList = response.body();
//                    for (ModelApiUser user : dataArrayList) {
//                        Log.e("DEBUG_INSERTUSER", String.valueOf(user));
//                        databaseHelper.verifyAndInsertUser(database, user);
//                        retour[0] = true;
//                    }
//
//                }
//            }
//
//            public void onFailure(Call<List<ModelApiUser>> call, Throwable t) {
//                Log.e("DEBUG_THREAD_INFO_FAILED_USER", t.toString());
//                retour[0] = false;
//            }
//        });
//        return retour[0];
//    }
    public static void syncUser(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getUsers().enqueue(new Callback<List<ModelApiUser>>() {
            public void onResponse(Call<List<ModelApiUser>> call, Response<List<ModelApiUser>> response) {
                if (response.isSuccessful()) {
                    dataArrayList = response.body();
                    for (ModelApiUser user : dataArrayList) {
                        databaseHelper.verifyAndInsertUser(database, user);
                    }
                    callback.onSuccess(true);
                } else {
                    callback.onSuccess(false);
                }
            }

            public void onFailure(Call<List<ModelApiUser>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_USER", t.toString());
                callback.onSuccess(false);
            }
        });
    }


//    public static void syncAppareil(String token, String emailUser, final SyncCallback callback) {
//        ApiHelper.getApi(token).getItems().enqueue(new Callback<List<ModelApiItem>>() {
//            public void onResponse(Call<List<ModelApiItem>> call, Response<List<ModelApiItem>> response_item) {
//                if (response_item.isSuccessful()) {
//                    dataArrayItem = response_item.body();
//                    for (ModelApiItem item : dataArrayItem) {
//                        databaseHelper.verifyAndInsertItem(database, item);
//
//                        // -- CHECK USER TO ACCESS THIS APPAREIL
//                        ArrayList<String> user_access = item.getSite().getUser();
//
//                        Boolean user_ctrl = false;
//                        for (String element : user_access) {
//                            String id_user = element.replaceAll("/api/users/", "");
//                            Boolean access = databaseHelper.checkAccessItem(database, id_user, emailUser);
//                            if(access){user_ctrl = true;}
//                        }
//
//
//                        Integer id_item = item.getId();
//                        databaseHelper.updateAccessItem(database, id_item, user_ctrl);
//                        // -- UPDATE SITE ID
//                        syncSite(token, item.getSite().getId());
//
//
//                    }
//                    callback.onSuccess(true);
//
//                }
//            }
//
//            public void onFailure(Call<List<ModelApiItem>> call, Throwable t) {
//                Log.e("DEBUG_THREAD_INFO_FAILED_ITEM", t.toString());
//                callback.onSuccess(false);
//            }
//        });
//    }

    public static void syncAppareil(String url, String token, String emailUser, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getItemReduces().enqueue(new Callback<List<ModelApiItemReduce>>() {
            public void onResponse(Call<List<ModelApiItemReduce>> call, Response<List<ModelApiItemReduce>> response_item) {
                if (response_item.isSuccessful()) {
                    dataArrayItemReduce = response_item.body();
                    for (ModelApiItemReduce item : dataArrayItemReduce) {
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
                        syncSite(url, token, item.getSite().getId());


                    }
                    callback.onSuccess(true);

                }
            }

            public void onFailure(Call<List<ModelApiItemReduce>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_ITEM", t.toString());
                callback.onSuccess(false);
            }
        });
    }


    public static Boolean syncSite(String url, String token, Integer idSite) {
        ApiHelper.getApi(token, url).getSite(idSite).enqueue(new Callback<ModelApiSite>() {
            public void onResponse(Call<ModelApiSite> call, Response<ModelApiSite> response_site) {
                if (response_site.isSuccessful()) {
                    dataArraySite = response_site.body();
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

    public Boolean syncInterventions(String url, String token, final SyncCallback callback) {
        List<JSONObject> interventions = databaseHelper.getInterventions(database, "0");
        for(JSONObject intervention : interventions)
        {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), intervention.toString());
            ApiHelper.getApi(token, url).createIntervention(body).enqueue(new Callback<ModelIntervention>() {
                public void onResponse(Call<ModelIntervention> call, Response<ModelIntervention> response_intervention) {
                    if (response_intervention.isSuccessful()) {
                       dataIntervention = response_intervention.body();

                        try {
                            databaseHelper.updateIntervention(database, Integer.parseInt(intervention.get("tempId").toString()), dataIntervention.getId());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    callback.onSuccess(true);
                }
                public void onFailure(Call<ModelIntervention> call, Throwable t) {
                    Log.e("DEBUG_THREAD_INFO_FAILED_INTER", t.toString());
                    callback.onSuccess(false);
                }
            });
        }
        return true;
    }


    public void syncOfftimeUp(String url, String token, final SyncCallback callback) {
        List<JSONObject> offtimes = databaseHelper.getOfftime(database, "1");
        for(JSONObject offtime : offtimes) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), offtime.toString());
            try {
                if (Integer.parseInt(String.valueOf(offtime.get("tempIdSync"))) == 0) {

                    ApiHelper.getApi(token, url).createOfftime(body).enqueue(new Callback<ModelOfftime>() {
                        public void onResponse(Call<ModelOfftime> call, Response<ModelOfftime> response_offtime) {
                            Log.e("DEBUG_SHOW_RESPONSE_OFFTIME", String.valueOf(response_offtime));
                            if (response_offtime.isSuccessful()) {
                                dataOfftime = response_offtime.body();

                                Log.e("DEBUG_OFFTIME", dataOfftime.getId().toString());
                                try {
                                    databaseHelper.updateOfftime(database, Integer.parseInt(offtime.get("tempId").toString()), dataOfftime.getId());
                                } catch (JSONException e) {
                                    Log.e("DEBUG_OFFTIME_SYNC", e.toString());
                                    throw new RuntimeException(e);
                                }
                            }
                            callback.onSuccess(true);
                        }

                        public void onFailure(Call<ModelOfftime> call, Throwable t) {
                            Log.e("DEBUG_THREAD_INFO_FAILED_OFFTIME", t.toString());
                            callback.onSuccess(false);
                        }
                    });

                } else {

                    ApiHelper.getApi(token, url).updateOfftime(Integer.parseInt(String.valueOf(offtime.get("tempIdSync"))), body).enqueue(new Callback<ModelOfftime>() {
                        public void onResponse(Call<ModelOfftime> call, Response<ModelOfftime> response_offtime) {
                            Log.e("DEBUG_SHOW_RESPONSE_OFFTIME", String.valueOf(response_offtime));
                            if (response_offtime.isSuccessful()) {
                                dataOfftime = response_offtime.body();

                                Log.e("DEBUG_OFFTIME", dataOfftime.getId().toString());
                                try {
                                    databaseHelper.updateOfftime(database, Integer.parseInt(offtime.get("tempId").toString()), dataOfftime.getId());
                                } catch (JSONException e) {
                                    Log.e("DEBUG_OFFTIME_SYNC", e.toString());
                                    throw new RuntimeException(e);
                                }
                            }
                            callback.onSuccess(true);
                        }

                        public void onFailure(Call<ModelOfftime> call, Throwable t) {
                            Log.e("DEBUG_THREAD_INFO_FAILED_OFFTIME", t.toString());
                            callback.onSuccess(false);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void syncOfftimeDown(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getOfftime().enqueue(new Callback<List<ModelApiOfftime>>() {
            public void onResponse(Call<List<ModelApiOfftime>> call, Response<List<ModelApiOfftime>> response_offtime) {
                if (response_offtime.isSuccessful()) {
                    dataArrayOfftime = response_offtime.body();
                    for (ModelApiOfftime offtime : dataArrayOfftime) {
                        databaseHelper.verifyAndInsertOfftime(database, offtime);
                    }
                    callback.onSuccess(true);

                }
            }

            public void onFailure(Call<List<ModelApiOfftime>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_SITE", t.toString());
                callback.onSuccess(false);
            }
        });
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

    public static void syncAuth(String url, String login, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("login", login);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        ApiHelper.getApi("", url).auth(body).enqueue(new Callback<ModelAuth>() {
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

    public static void syncMountage(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getMountage().enqueue(new Callback<List<ModelApiMountage>>() {
            public void onResponse(Call<List<ModelApiMountage>> call, Response<List<ModelApiMountage>> response_mountage) {
                if (response_mountage.isSuccessful()) {
                    dataArrayMountage = response_mountage.body();
                    for (ModelApiMountage mountage : dataArrayMountage) {
                        databaseHelper.verifyAndInsertMountage(database, mountage);
                    }

                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiMountage>> call, Throwable t) {
                Log.e("DEBUG_THREAD_FAILED_MOUNTAGE", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public static void syncMountageDone(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getMountageDone().enqueue(new Callback<List<ModelApiMountageDone>>() {
            public void onResponse(Call<List<ModelApiMountageDone>> call, Response<List<ModelApiMountageDone>> response_mountagedone) {
                if (response_mountagedone.isSuccessful()) {
                    dataArrayMountageDone = response_mountagedone.body();
                    for (ModelApiMountageDone mountagedone : dataArrayMountageDone) {
                        databaseHelper.verifyAndInsertMountageDone(database, mountagedone, true);
                    }

                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiMountageDone>> call, Throwable t) {
                Log.e("DEBUG_THREAD_FAILED_MOUNTAGEDONE", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public void syncMountageDoneUp(String url, String token, final SyncCallback callback) {
        List<JSONObject> mountagedones = databaseHelper.getSendMountageDone(database, "1");
        for(JSONObject mountagedone : mountagedones) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mountagedone.toString());
            try {
                if (Integer.parseInt(String.valueOf(mountagedone.get("tempIdSync"))) == 0) {
                    // -- Control Double
                    control = false;
                    ApiHelper.getApi(token, url).getMountageDoneControl(Integer.parseInt(String.valueOf(mountagedone.get("fkItemsite")).replace("/api/item_sites/", "")), Integer.parseInt(String.valueOf(mountagedone.get("fkMountageStep")).replace("/api/mountage_steps/", ""))).enqueue(new Callback<List<ModelApiMountageDone>>() {
                      public void onResponse(Call<List<ModelApiMountageDone>> call, Response<List<ModelApiMountageDone>> response_control) {
                          if (response_control.isSuccessful()) {
                              dataArrayMountageDone = response_control.body();
                              for (ModelApiMountageDone mountagedone : dataArrayMountageDone) {
                                  control = true;
                              }

                          }
                          callback.onSuccess(true);
                      }

                        public void onFailure(Call<List<ModelApiMountageDone>> call, Throwable t) {
                            Log.e("DEBUG_THREAD_INFO_FAILED_OFFTIME", t.toString());
                            callback.onSuccess(false);
                        }
                      });
                    // --
                    if(!control){
                        ApiHelper.getApi(token, url).createMountageDone(body).enqueue(new Callback<ModelMountageDone>() {
                            public void onResponse(Call<ModelMountageDone> call, Response<ModelMountageDone> response_mountagedone) {
                                if (response_mountagedone.isSuccessful()) {
                                    dataMountageDone = response_mountagedone.body();
                                    try {
                                        databaseHelper.updateMountageDone(database, Integer.parseInt(mountagedone.get("tempId").toString()), dataMountageDone.getId());
                                    } catch (JSONException e) {
                                        Log.e("DEBUG_MOUNTAGEDONE_SYNC", e.toString());
                                        throw new RuntimeException(e);
                                    }
                                }
                                callback.onSuccess(true);
                            }

                            public void onFailure(Call<ModelMountageDone> call, Throwable t) {
                                Log.e("DEBUG_THREAD_INFO_FAILED_OFFTIME", t.toString());
                                callback.onSuccess(false);
                            }
                        });
                    } else {
                        if(databaseHelper.deleteMountageDone(database, String.valueOf(mountagedone.get("tempId")))){
                            Log.e("DEBUG_DELETE_MOUNTAGEDONE", "Element Mountagedone : " + String.valueOf(mountagedone.get("tempId")) + "supprimé");
                            callback.onSuccess(true);
                        }
                    }

                } else {

                    ApiHelper.getApi(token, url).updateMountageDone(Integer.parseInt(String.valueOf(mountagedone.get("tempIdSync"))), body).enqueue(new Callback<ModelMountageDone>() {
                        public void onResponse(Call<ModelMountageDone> call, Response<ModelMountageDone> response_mountagedone) {
                            if (response_mountagedone.isSuccessful()) {
                                dataMountageDone = response_mountagedone.body();
                                try {
                                    databaseHelper.updateMountageDone(database, Integer.parseInt(mountagedone.get("tempId").toString()), dataMountageDone.getId());
                                } catch (JSONException e) {
                                    Log.e("DEBUG_MOUNTAGEDONE_SYNC", e.toString());
                                    throw new RuntimeException(e);
                                }
                            }
                            callback.onSuccess(true);
                        }

                        public void onFailure(Call<ModelMountageDone> call, Throwable t) {
                            Log.e("DEBUG_THREAD_INFO_FAILED_MOUNTAGEDONE", t.toString());
                            callback.onSuccess(false);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void syncFrequency(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getFrequency().enqueue(new Callback<List<ModelApiCategory>>() {
            public void onResponse(Call<List<ModelApiCategory>> call, Response<List<ModelApiCategory>> response_category) {
                if (response_category.isSuccessful()) {
                    dataArrayCategory = response_category.body();
                    for (ModelApiCategory category : dataArrayCategory) {
                        dataArrayFrequency = category.getFkFrequency();
                        for (ModelApiFrequency frequency : dataArrayFrequency) {
                            databaseHelper.verifyAndInsertFrequency(database, frequency, category.getId());
                        }
                    }

                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiCategory>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_FREQUENCY", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public static void syncTask(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getTasks().enqueue(new Callback<List<ModelApiMaintTask>>() {
            public void onResponse(Call<List<ModelApiMaintTask>> call, Response<List<ModelApiMaintTask>> response_task) {
                if (response_task.isSuccessful()) {
                    dataArrayTask = response_task.body();
                    for (ModelApiMaintTask task : dataArrayTask) {
                        databaseHelper.verifyAndInsertTask(database, task);
                        dataArrayGroups = task.getFkMainttaskcategory();
                        databaseHelper.verifyAndInsertGroups(database, dataArrayGroups);

                    }

                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiMaintTask>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_TASK", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public static void syncTaskDone(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getTaskDone().enqueue(new Callback<List<ModelApiMaintTaskDone>>() {
            public void onResponse(Call<List<ModelApiMaintTaskDone>> call, Response<List<ModelApiMaintTaskDone>> response_taskdone) {
                if (response_taskdone.isSuccessful()) {
                    dataArrayTaskDone = response_taskdone.body();
                    for (ModelApiMaintTaskDone taskdone : dataArrayTaskDone) {
                        databaseHelper.verifyAndInsertTaskDone(database, taskdone);
                    }

                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiMaintTaskDone>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_TASK_DONE", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public static void syncRMFrequency(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getRMFrequency().enqueue(new Callback<List<ModelApiRMFrequency>>() {
            public void onResponse(Call<List<ModelApiRMFrequency>> call, Response<List<ModelApiRMFrequency>> response_rmf) {
                if (response_rmf.isSuccessful()) {
                    dataArrayRMFrequency = response_rmf.body();
                    for (ModelApiRMFrequency rmf : dataArrayRMFrequency) {
                        databaseHelper.verifyAndInsertRMFrequency(database, rmf);
                    }
                    // -- POST NEW RMF
                    postRMFrequency(url, token);
                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiRMFrequency>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_RM_FREQUENCY", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public static Boolean postRMFrequency(String url, String token) {
        List<JSONObject> rmfrequencies = databaseHelper.getRMFrequency(database, "0");
        for(JSONObject rmfrequency : rmfrequencies)
        {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), rmfrequency.toString());
            ApiHelper.getApi(token, url).createRMfrequency(body).enqueue(new Callback<ModelApiRMFrequency>() {
                public void onResponse(Call<ModelApiRMFrequency> call, Response<ModelApiRMFrequency> response_rmf) {
                    if (response_rmf.isSuccessful()) {
                        dataRMFrequency = response_rmf.body();
                        try {
                            Boolean sendRMFrequency = databaseHelper.updateRMFrequency(database, Integer.parseInt(rmfrequency.get("tempId").toString()), dataRMFrequency.getId());
                            if(sendRMFrequency) {
                                // -- UPDATE RMF ID_SYNC
                            }
                            // ++ UPDATE FK FREQ (by localRMF) AND FK MAINT (by dataMaintenance.getId())  FOR TASK DONE
                        } catch (JSONException e) {
                            Log.e("DEBUG_RMFREQUENCY_SYNC", e.toString());
                            throw new RuntimeException(e);
                        }
                    }
                }
                public void onFailure(Call<ModelApiRMFrequency> call, Throwable t) {
                    Log.e("DEBUG_THREAD_INFO_FAILED_RMF", t.toString());
                }
            });
        }
        return true;
    }

    public void syncRMFUp(String url, String token, final SyncCallback callback) {
        List<JSONObject> RMFs = databaseHelper.getRMF(database, "1");
        for(JSONObject RMF : RMFs) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), RMF.toString());
            try {
                Log.e("DEBUG_RMFPUT", String.valueOf(RMF.get("tempIdSync")));
                if (Integer.parseInt(String.valueOf(RMF.get("tempIdSync"))) == 0) {

                    // -- CREATE IF ID_SYNC == 0

                } else {

                    ApiHelper.getApi(token, url).updateRMF(Integer.parseInt(String.valueOf(RMF.get("tempIdSync"))), body).enqueue(new Callback<ModelApiRMFrequency>() {
                        public void onResponse(Call<ModelApiRMFrequency> call, Response<ModelApiRMFrequency> response_RMF) {
                            Log.e("DEBUG_SHOW_RESPONSE_RMF", String.valueOf(response_RMF));
                            if (response_RMF.isSuccessful()) {
                                dataRMFrequency = response_RMF.body();

                                try {
                                    databaseHelper.updateRMF(database, Integer.parseInt(RMF.get("tempId").toString()), dataRMFrequency.getId());
                                } catch (JSONException e) {
                                    Log.e("DEBUG_RMF_SYNC", e.toString());
                                    throw new RuntimeException(e);
                                }
                            }
                            callback.onSuccess(true);
                        }

                        public void onFailure(Call<ModelApiRMFrequency> call, Throwable t) {
                            Log.e("DEBUG_THREAD_INFO_FAILED_RMF", t.toString());
                            callback.onSuccess(false);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void syncMaintenances(String url, String token, final SyncCallback callback) {
        List<JSONObject> maintenances = databaseHelper.getMaintenances(database, "0");
        for(JSONObject maintenance : maintenances)
        {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), maintenance.toString());
            ApiHelper.getApi(token, url).createMaintenanceForm(body).enqueue(new Callback<ModelMaintenance>() {
                public void onResponse(Call<ModelMaintenance> call, Response<ModelMaintenance> response_maintenance) {
                    if (response_maintenance.isSuccessful()) {
                        dataMaintenance = response_maintenance.body();
                        try {
                            Boolean sendMaintenance = databaseHelper.updateMaintenance(database, Integer.parseInt(maintenance.get("tempId").toString()), dataMaintenance.getId());
                            if(sendMaintenance) {
                                // -- LOOP FOR SYNC TASK DONE FOR THIS MAINTENANCE
                                Boolean updateTaskDone = databaseHelper.updateTaskDoneRMF(database, maintenance.get("tempId").toString(), String.valueOf(dataMaintenance.getId()));
                                if(updateTaskDone) {
                                    //syncLocalTaskDone(token, maintenance.get("tempId").toString(), String.valueOf(dataMaintenance.getId()));
                                }
                            }
                            // ++ UPDATE FK FREQ (by localRMF) AND FK MAINT (by dataMaintenance.getId())  FOR TASK DONE
                        } catch (JSONException e) {
                            Log.e("DEBUG_MAINTENANCE_SYNC", e.toString());
                            throw new RuntimeException(e);
                        }
                    }
                    callback.onSuccess(true);
                }
                public void onFailure(Call<ModelMaintenance> call, Throwable t) {
                    Log.e("DEBUG_THREAD_INFO_FAILED_MAINT", t.toString());
                    callback.onSuccess(false);
                }
            });
        }
    }

    public Boolean syncLocalTaskDone(String url, String token, String idLocalMaintenance, String idSyncMaintenance, final SyncCallback callback) {
        List<JSONObject> taskdones = databaseHelper.getLocalTaskDone(database, idLocalMaintenance);

        for(JSONObject taskdone : taskdones)
        {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), taskdone.toString());
            ApiHelper.getApi(token, url).createTaskDone(body).enqueue(new Callback<ModelTaskDone>() {
                public void onResponse(Call<ModelTaskDone> call, Response<ModelTaskDone> response_taskdone) {
                    if (response_taskdone.isSuccessful()) {
                        dataTaskDone = response_taskdone.body();
                        try {
                            Boolean sendMaintenance = databaseHelper.updateTaskDone(database, Integer.parseInt(taskdone.get("tempId").toString()), dataTaskDone.getId());
                            if(sendMaintenance) {
                                // -- FINISH SYNC MAINT & TASKDONE
                            }
                        } catch (JSONException e) {
                            Log.e("DEBUG_TASKDONE_SYNC", e.toString());
                            throw new RuntimeException(e);
                        }
                    }
                    callback.onSuccess(true);
                }
                public void onFailure(Call<ModelTaskDone> call, Throwable t) {
                    Log.e("DEBUG_THREAD_INFO_FAILED_INTER", t.toString());
                    callback.onSuccess(false);
                }
            });
        }
        return true;
    }

    public static void syncBonIntervention(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getBonIntervention().enqueue(new Callback<List<ModelApiBonIntervention>>() {
            public void onResponse(Call<List<ModelApiBonIntervention>> call, Response<List<ModelApiBonIntervention>> response_bon) {
                if (response_bon.isSuccessful()) {
                    dataArrayBonIntervention = response_bon.body();
                    for (ModelApiBonIntervention bon : dataArrayBonIntervention) {
                        databaseHelper.verifyAndInsertBonIntervention(database, bon);
                    }
                    // -- POST NEW BON INTERVENTION
                    postBonIntervention(url, token);
                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiBonIntervention>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_BON_INTERVENTION", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public static Boolean postBonIntervention(String url, String token) {
        List<JSONObject> bons = databaseHelper.getBonIntervention(database, "0");
        for(JSONObject bon : bons)
        {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), bon.toString());
            ApiHelper.getApi(token, url).createBonIntervention(body).enqueue(new Callback<ModelApiBonIntervention>() {
                public void onResponse(Call<ModelApiBonIntervention> call, Response<ModelApiBonIntervention> response_bon) {
                    if (response_bon.isSuccessful()) {
                        dataBonIntervention = response_bon.body();
                        try {
                            Boolean sendBonIntervention = databaseHelper.updateBonIntervention(database, Integer.parseInt(bon.get("tempId").toString()), dataBonIntervention.getId());
                            if(sendBonIntervention) {
                                // -- UPDATE RMF ID_SYNC
                            }
                        } catch (JSONException e) {
                            Log.e("DEBUG_BONINTERVENTION_SYNC", e.toString());
                            throw new RuntimeException(e);
                        }
                    }
                }
                public void onFailure(Call<ModelApiBonIntervention> call, Throwable t) {
                    Log.e("DEBUG_THREAD_INFO_FAILED_BONINTERVENTION", t.toString());
                }
            });
        }
        return true;
    }

    public static void syncDemand(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getDemand().enqueue(new Callback<List<ModelApiDemand>>() {
            public void onResponse(Call<List<ModelApiDemand>> call, Response<List<ModelApiDemand>> response_dem) {
                if (response_dem.isSuccessful()) {
                    dataArrayDemand = response_dem.body();
                    for (ModelApiDemand dem : dataArrayDemand) {
                        databaseHelper.verifyAndInsertDemand(database, dem);
                    }
                    // -- TODO
                    //putDemande(url, token);
                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiDemand>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_DEMAND", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public static void syncNote(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getNote().enqueue(new Callback<List<ModelApiNote>>() {
            public void onResponse(Call<List<ModelApiNote>> call, Response<List<ModelApiNote>> response_note) {
                if (response_note.isSuccessful()) {
                    dataArrayNote = response_note.body();
                    for (ModelApiNote note : dataArrayNote) {
                        databaseHelper.verifyAndInsertNote(database, note);
                    }
                    // -- POST NEW NOTE
                    postNote(url, token);
                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiNote>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_NOTE", t.toString());
                callback.onSuccess(false);
            }
        });
    }

    public static Boolean postNote(String url, String token) {
        List<JSONObject> notes = databaseHelper.getNote(database, "0");
        for(JSONObject note : notes)
        {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), note.toString());
            ApiHelper.getApi(token, url).createNote(body).enqueue(new Callback<ModelApiNote>() {
                public void onResponse(Call<ModelApiNote> call, Response<ModelApiNote> response_note) {
                    if (response_note.isSuccessful()) {
                        dataNote = response_note.body();
                        try {
                            Boolean sendNote = databaseHelper.updateNote(database, Integer.parseInt(note.get("tempId").toString()), dataNote.getId());
                            if(sendNote) {
                                // -- NOTHING
                            }
                        } catch (JSONException e) {
                            Log.e("DEBUG_NOTE_SYNC", e.toString());
                            throw new RuntimeException(e);
                        }
                    }
                }
                public void onFailure(Call<ModelApiNote> call, Throwable t) {
                    Log.e("DEBUG_THREAD_INFO_FAILED_NOTE", t.toString());
                }
            });
        }
        return true;
    }

    public static void syncTiers(String url, String token, final SyncCallback callback) {
        ApiHelper.getApi(token, url).getTiers().enqueue(new Callback<List<ModelApiTiers>>() {
            public void onResponse(Call<List<ModelApiTiers>> call, Response<List<ModelApiTiers>> response_tiers) {
                if (response_tiers.isSuccessful()) {
                    dataArrayTiers = response_tiers.body();
                    for (ModelApiTiers tiers : dataArrayTiers) {
                        databaseHelper.verifyAndInsertTiers(database, tiers);
                    }
                }
                callback.onSuccess(true);
            }

            public void onFailure(Call<List<ModelApiTiers>> call, Throwable t) {
                Log.e("DEBUG_THREAD_INFO_FAILED_TIERS", t.toString());
                callback.onSuccess(false);
            }
        });
    }
}
