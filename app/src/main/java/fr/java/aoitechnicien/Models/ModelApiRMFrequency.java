package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelApiRMFrequency {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String startDate;
    private String endDate;
    private String createdAt;
    private String deletedAt;
    private String fk_itemsite;
    private String fk_frequency;

    public ModelApiRMFrequency(int id, String startDate, String endDate, String createdAt, String deletedAt, String fk_itemsite, String fk_frequency) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.fk_itemsite = fk_itemsite;
        this.fk_frequency = fk_frequency;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartDate() { return startDate; }

    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

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

    public String getFkItemsite() { return fk_itemsite; }

    public void setFkItemsite(String fk_itemsite) { this.fk_itemsite = fk_itemsite; }

    public String getFkFrequency() { return fk_frequency; }

    public void setFkFrequency(String fk_frequency) { this.fk_frequency = fk_frequency; }
}
