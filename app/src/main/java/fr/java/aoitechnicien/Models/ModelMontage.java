package fr.java.aoitechnicien.Models;

import java.util.ArrayList;

public class ModelMontage {
    private Integer id;
    private Integer id_sync;
    private Integer fk_category_id;
    private String label;
    private String estimated_time;
    private String createdAt;
    private String deletedAt;

    public ModelMontage(Integer id, Integer id_sync, Integer fk_category_id, String label, String estimated_time, String createdAt, String deletedAt) {
        this.id = id;
        this.id_sync = id_sync;
        this.fk_category_id = fk_category_id;
        this.label = label;
        this.estimated_time = estimated_time;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public Integer getIdSync() { return id_sync; }

    public void setIdSync(Integer id_sync) { this.id_sync = id_sync; }

    public Integer getFkCategoryId() { return fk_category_id; }

    public void setFkCategoryId(Integer fk_category_id) { this.fk_category_id = fk_category_id; }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public String getEstimatedTime() { return estimated_time; }

    public void setEstimatedTime(String estimated_time) { this.estimated_time = estimated_time; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDeletedAt() { return deletedAt; }

    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }
}
