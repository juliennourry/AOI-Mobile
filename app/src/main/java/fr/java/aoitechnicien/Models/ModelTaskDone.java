package fr.java.aoitechnicien.Models;

public class ModelTaskDone {
    private Integer id;
    private String label;
    private String doneAt;
    private String createdAt;
    private String fkRealmadridfrequency;
    private String fkMainttask;
    private String fkMaintenanceform;

    public ModelTaskDone(Integer id, String label, String doneAt, String createdAt, String fkRealmadridfrequency, String fkMainttask, String fkMaintenanceform ) {

        this.id = id;
        this.label = label;
        this.doneAt = doneAt;
        this.createdAt = createdAt;
        this.fkRealmadridfrequency = fkRealmadridfrequency;
        this.fkMainttask = fkMainttask;
        this.fkMaintenanceform = fkMaintenanceform;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(String doneAt) {
        this.doneAt = doneAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFkRealmadridfrequency() {
        return fkRealmadridfrequency;
    }

    public void setFkRealmadridfrequency(String fkRealmadridfrequency) {
        this.fkRealmadridfrequency = fkRealmadridfrequency;
    }

    public String getFkMainttask() {
        return fkMainttask;
    }

    public void setFkMainttask(String fkMainttask) {
        this.fkMainttask = fkMainttask;
    }

    public String getFkMaintenanceform() {
        return fkMaintenanceform;
    }

    public void setFkMaintenanceform(String fkMaintenanceform) {
        this.fkMaintenanceform = fkMaintenanceform;
    }
}
