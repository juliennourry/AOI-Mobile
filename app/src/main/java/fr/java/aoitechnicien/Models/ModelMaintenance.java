package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelMaintenance {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String createdAt;
    private String deletedAt;
    private String coordinates;
    private String ref;
    private String status;
    private String startDate;
    private String endDate;
    private String fk_user;
    private ModelApiItem fk_itemsite;

    public ModelMaintenance(int id, String createdAt, String deletedAt, String coordinates, String ref, String status, String startDate, String endDate, String fk_user, ModelApiItem fk_itemsite) {
        this.id = id;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.coordinates = coordinates;
        this.ref = ref;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fk_user = fk_user;
        this.fk_itemsite = fk_itemsite;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFk_user() {
        return fk_user;
    }

    public void setFk_user(String fk_user) {
        this.fk_user = fk_user;
    }

    public ModelApiItem getFk_itemsite() {
        return fk_itemsite;
    }

    public void setFk_itemsite(ModelApiItem getFk_itemsite) {
        this.fk_itemsite = fk_itemsite;
    }
}