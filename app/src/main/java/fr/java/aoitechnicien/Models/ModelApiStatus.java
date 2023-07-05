package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelApiStatus {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String version;
    private String createdAt;


    public ModelApiStatus(int id, String version, String createdAt) {
        this.id = id;
        this.version = version;
        this.createdAt = createdAt;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
