package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelApiMaintTask {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String label;
    private String createdAt;
    private String deletedAt;
    private String fk_frequency;
    private ModelMaintTaskCategory fk_mainttaskcategory;

    public ModelApiMaintTask(int id, String label, String createdAt, String deletedAt, String fk_frequency, ModelMaintTaskCategory fk_mainttaskcategory) {
        this.id = id;
        this.label = label;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.fk_frequency = fk_frequency;
        this.fk_mainttaskcategory = fk_mainttaskcategory;
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

    public String getFkFrequency() { return fk_frequency; }

    public void setFkFrequency(String fk_frequency) { this.fk_frequency = fk_frequency; }

    public ModelMaintTaskCategory getFkMainttaskcategory() { return fk_mainttaskcategory; }

    public void setFkMainttaskcategory(ModelMaintTaskCategory fk_mainttaskcategory) { this.fk_mainttaskcategory = fk_mainttaskcategory; }
}
