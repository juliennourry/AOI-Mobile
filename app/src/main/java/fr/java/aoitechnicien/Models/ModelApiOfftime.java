package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelApiOfftime {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String startAt;
    private String endAt;
    private String status;
    private String createdAt;
    private String fk_itemsite;

    public ModelApiOfftime(int id, String startAt, String endAt, String status, String createdAt, String fk_itemsite) {
        this.id = id;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.createdAt = createdAt;
        this.fk_itemsite = fk_itemsite;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartAt() { return startAt; }

    public void setStartAt(String startAt) { this.startAt = startAt; }

    public String getEndAt() { return endAt; }

    public void setEndAt(String endAt) { this.endAt = endAt; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getFkItemsite() { return fk_itemsite; }

    public void setFkItemsite(String fkItemsite) { this.fk_itemsite = fk_itemsite; }
}
