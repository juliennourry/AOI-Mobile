package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelApiItemReduce {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String label;
    private String onAt;
    private String uuid;
    private String createdAt;
    private String deletedAt;
    private ModelCategory fk_category;
    private ModelSite fk_site;

    private String desactivatedAt;

    private String phone;
    //private ModelTaskDone fk_mountage_step_done;

    public ModelApiItemReduce(int id, String label, String onAt, String uuid, String createdAt, String deletedAt, ModelCategory fk_category, ModelSite fk_site, String desactivatedAt, String phone /*, ModelTaskDone fk_mountage_step_done*/) {
        this.id = id;
        this.label = label;
        this.onAt = onAt;
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.fk_category = fk_category;
        this.fk_site = fk_site;
        this.desactivatedAt = desactivatedAt;
        this.phone = phone;
        //this.fk_mountage_step_done = fk_mountage_step_done;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ModelCategory getCategory() { return fk_category; }

    public void setCategory(ModelApiItemReduce getCategory) { this.fk_category = fk_category; }

    public ModelSite getSite() { return fk_site; }

    public void setSite(ModelApiItemReduce getSite) { this.fk_site = fk_site; }

    public String getDesactivatedAt() {
        return desactivatedAt;
    }

    public void setDesactivatedAt(String desactivatedAt) {
        this.desactivatedAt = desactivatedAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /*public ModelTaskDone getTaskDone() { return fk_mountage_step_done; }

    public void setTaskDone(ModelApiItem getTaskDone) { this.fk_mountage_step_done = fk_mountage_step_done; }*/
}
