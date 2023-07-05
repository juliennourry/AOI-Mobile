package fr.java.aoitechnicien.Requester;

import java.util.List;

import fr.java.aoitechnicien.Models.ModelApiBonIntervention;
import fr.java.aoitechnicien.Models.ModelApiCategory;
import fr.java.aoitechnicien.Models.ModelApiDemand;
import fr.java.aoitechnicien.Models.ModelApiItem;
import fr.java.aoitechnicien.Models.ModelApiItemReduce;
import fr.java.aoitechnicien.Models.ModelApiMaintTask;
import fr.java.aoitechnicien.Models.ModelApiMaintTaskDone;
import fr.java.aoitechnicien.Models.ModelApiNote;
import fr.java.aoitechnicien.Models.ModelApiStatus;
import fr.java.aoitechnicien.Models.ModelApiTiers;
import fr.java.aoitechnicien.Models.ModelMaintenance;
import fr.java.aoitechnicien.Models.ModelApiMountage;
import fr.java.aoitechnicien.Models.ModelApiMountageDone;
import fr.java.aoitechnicien.Models.ModelApiOfftime;
import fr.java.aoitechnicien.Models.ModelApiRMFrequency;
import fr.java.aoitechnicien.Models.ModelApiSite;
import fr.java.aoitechnicien.Models.ModelApiUser;
import fr.java.aoitechnicien.Models.ModelAuth;
import fr.java.aoitechnicien.Models.ModelIntervention;
import fr.java.aoitechnicien.Models.ModelMountageDone;
import fr.java.aoitechnicien.Models.ModelOfftime;
import fr.java.aoitechnicien.Models.ModelTaskDone;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InterfaceApi {
    @GET("/api/statuses")
    Call<List<ModelApiStatus>> getStatus();
    @GET("/api/users")
    Call<List<ModelApiUser>> getUsers();
    @GET("/api/item_sites")
    Call<List<ModelApiItem>> getItems();
    @GET("/api/item_site_reduces")
    Call<List<ModelApiItemReduce>> getItemReduces();
    @GET("/api/offtimes")
    Call<List<ModelApiOfftime>> getOfftime();
    @GET("/api/maint_tasks")
    Call<List<ModelApiMaintTask>> getTasks();
    @GET("/api/maint_task_dones")
    Call<List<ModelApiMaintTaskDone>> getTaskDone();
    @GET("/api/categories")
    Call<List<ModelApiCategory>> getFrequency();
    @GET("/api/real_madrid_frequencies")
    Call<List<ModelApiRMFrequency>> getRMFrequency();
    @GET("/api/mountage_steps")
    Call<List<ModelApiMountage>> getMountage();
    @GET("/api/mountage_step_dones")
    Call<List<ModelApiMountageDone>> getMountageDone();
    @GET("/api/mountage_step_dones")
    Call<List<ModelApiMountageDone>> getMountageDoneControl(@Query("itemsite") int itemsite, @Query("mountagestep") int mountagestep);
    @GET("/api/sites/{id}")
    Call<ModelApiSite> getSite(@Path("id") int idSite);
    @GET("/api/interventions")
    Call<List<ModelApiBonIntervention>> getBonIntervention();
    @GET("/api/demands")
    Call<List<ModelApiDemand>> getDemand();
    @GET("/api/notes")
    Call<List<ModelApiNote>> getNote();
    @GET("/api/thirdparties")
    Call<List<ModelApiTiers>> getTiers();

    @POST("/api/intervention_vouchers")
    Call<ModelIntervention> createIntervention(@Body RequestBody body);
    @POST("/auth")
    Call<ModelAuth> auth(@Body RequestBody body);
    @POST("/api/offtimes")
    Call<ModelOfftime> createOfftime(@Body RequestBody body);
    @POST("/api/mountage_step_dones")
    Call<ModelMountageDone> createMountageDone(@Body RequestBody body);
    @POST("/api/maintenance_forms")
    Call<ModelMaintenance> createMaintenanceForm(@Body RequestBody body);
    @POST("/api/maint_task_dones")
    Call<ModelTaskDone> createTaskDone(@Body RequestBody body);
    @POST("/api/real_madrid_frequencies")
    Call<ModelApiRMFrequency> createRMfrequency(@Body RequestBody body);
    @POST("/api/interventions")
    Call<ModelApiBonIntervention> createBonIntervention(@Body RequestBody body);
    @POST("/api/notes")
    Call<ModelApiNote> createNote(@Body RequestBody body);

    @PUT("/api/offtimes/{id}")
    Call<ModelOfftime> updateOfftime(@Path("id") int id, @Body RequestBody body);
    @PUT("/api/mountage_step_dones/{id}")
    Call<ModelMountageDone> updateMountageDone(@Path("id") int id, @Body RequestBody body);
    @PUT("/api/demandes/{id}")
    Call<ModelApiDemand> updateDemande(@Path("id") int id, @Body RequestBody body);

    @PUT("/api/real_madrid_frequencies/{id}")
    Call<ModelApiRMFrequency> updateRMF(@Path("id") int id, @Body RequestBody body);
}
