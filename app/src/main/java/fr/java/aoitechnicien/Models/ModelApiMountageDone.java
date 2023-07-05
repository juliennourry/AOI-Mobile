package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelApiMountageDone {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String label;
    private Float doneTime;
    private String doneAt;
    private String fk_mountage_step;
    private String fk_itemsite;
    private String createdAt;


    public ModelApiMountageDone(int id, String label, Float doneTime, String doneAt, String fk_mountage_step, String fk_itemsite, String createdAt) {
        this.id = id;
        this.label = label;
        this.doneTime = doneTime;
        this.doneAt = doneAt;
        this.fk_mountage_step = fk_mountage_step;
        this.fk_itemsite = fk_itemsite;
        this.createdAt = createdAt;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public Float getDoneTime() { return doneTime; }

    public void setDoneTime(Float doneTime) { this.doneTime = doneTime; }

    public String getDoneAt() { return doneAt; }

    public void setDoneAt(String doneAt) { this.doneAt = doneAt; }

    public String getFkMountageStep() { return fk_mountage_step; }

    public void setFkMountageStep(String fk_mountage_step) { this.fk_mountage_step = fk_mountage_step; }

    public String getFkItemsite() { return fk_itemsite; }

    public void setFkItemsite(String fk_itemsite) { this.fk_itemsite = fk_itemsite; }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}
