package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelApiMountage {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String label;
    private String fk_category;
    private String createdAt;
    private String deletedAt;
    private Float doneTime;


    public ModelApiMountage(int id, String label, String fk_category, String createdAt, String deletedAt, Float doneTime) {
        this.id = id;
        this.label = label;
        this.fk_category = fk_category;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.doneTime = doneTime;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public String getFkCategory() { return fk_category; }

    public void setFkCategory(String fk_category) { this.fk_category = fk_category; }

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

    public Float getDoneTime() { return doneTime; }

    public void setDoneTime(Float doneTime) { this.doneTime = doneTime; }
}
