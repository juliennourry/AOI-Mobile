package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelItem {

    @SerializedName("id")
    @Expose
    private Integer id;
    private Integer id_sync;
    private String label;
    private String onAt;
    private String uuid;
    private String createdAt;
    private String deletedAt;
    private Integer fk_category;
    private String category;
    private String id_site;
    private Integer access;

    public ModelItem(Integer id, Integer id_sync, String label, String onAt, String uuid, String createdAt, String deletedAt, Integer fk_category, String category, String id_site, Integer access) {
        this.id = id;
        this.id_sync = id_sync;
        this.label = label;
        this.onAt = onAt;
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.fk_category = fk_category;
        this.category = category;
        this.id_site = id_site;
        this.access = access;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdSync() {
        return id_sync;
    }

    public void setIdSync(Integer id_sync) {
        this.id_sync = id_sync;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOnAt() { return onAt;}

    public void setOnAt(String onAt) { this.onAt = onAt; }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getFkCategory() { return fk_category; }

    public void setFkCategory(Integer fk_category) { this.fk_category = fk_category; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public Integer getAccess() { return access; }

    public void setAccess(Integer getAccess) { this.access = access; }

    public String getIdSite() {
        return id_site;
    }

    public void setIdSite(String id_site) {
        this.id_site = id_site;
    }

    /*public ModelTaskDone getTaskDone() { return fk_mountage_step_done; }

    public void setTaskDone(ModelApiItem getTaskDone) { this.fk_mountage_step_done = fk_mountage_step_done; }*/
}
