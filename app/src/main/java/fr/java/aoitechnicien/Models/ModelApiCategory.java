package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelApiCategory {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String label;
    private String status;
    private String createdAt;
    private String deletedAt;
    private List<ModelApiFrequency> fk_frequency;


    public ModelApiCategory(int id, String label, String status, String createdAt, String deletedAt, List<ModelApiFrequency> fk_frequency) {
        this.id = id;
        this.label = label;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.fk_frequency = fk_frequency;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

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

    public List<ModelApiFrequency> getFkFrequency() { return fk_frequency; }

    public void setFkFrequency(ModelApiFrequency getFkFrequency) { this.fk_frequency = fk_frequency; }
}
