package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelApiNote {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String description;
    private String fk_demand;
    private String fk_user;
    private String createdAt;
    private String deletedAt;
    @SerializedName("private")
    private String privated;

    public ModelApiNote(int id, String description, String fk_demand, String fk_user, String createdAt, String deletedAt, String privated) {
        this.id = id;
        this.description = description;
        this.fk_demand = fk_demand;
        this.fk_user = fk_user;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.privated = privated;
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

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getFkDemand() { return fk_demand; }

    public void setFkDemand(String fk_demand) { this.fk_demand = fk_demand; }

    public String getFkUser() { return fk_user; }

    public void setFkUser(String fk_user) { this.fk_user = fk_user; }

    public String getPrivated() { return privated; }

    public void setPrivated(String privated) { this.privated = privated; }
}
