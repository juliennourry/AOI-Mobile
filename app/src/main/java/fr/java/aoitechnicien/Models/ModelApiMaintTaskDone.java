package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelApiMaintTaskDone {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String label;
    private String doneAt;
    private String createdAt;
    private String deletedAt;
    private String fk_realmadridfrequency;
    private String fk_maintenanceform;
    private ModelApiMaintTask fk_mainttask;

    public ModelApiMaintTaskDone(int id, String label, String doneAt, String createdAt, String deletedAt, String fk_realmadridfrequency, String fk_maintenanceform, ModelApiMaintTask fk_mainttask) {
        this.id = id;
        this.label = label;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.fk_realmadridfrequency = fk_realmadridfrequency;
        this.fk_maintenanceform = fk_maintenanceform;
        this.fk_mainttask = fk_mainttask;
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

    public String getDoneAt() { return doneAt; }

    public void setDoneAt(String doneAt) { this.doneAt = doneAt; }

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

    public String getFkRealmadridfrequency() { return fk_realmadridfrequency; }

    public void setFkRealmadridfrequency(String fk_realmadridfrequency) { this.fk_realmadridfrequency = fk_realmadridfrequency; }

    public String getFkMaintenanceform() { return fk_maintenanceform; }

    public void setFkMaintenanceform(String fk_maintenanceform) { this.fk_maintenanceform = fk_maintenanceform; }

    public ModelApiMaintTask getFkMainttask() { return fk_mainttask; }

    public void setFkMainttask(ModelApiMaintTask fk_mainttask) { this.fk_mainttask = fk_mainttask; }
}
