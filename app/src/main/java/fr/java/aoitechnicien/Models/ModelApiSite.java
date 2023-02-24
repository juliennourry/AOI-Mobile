package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelApiSite {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String label;
    private String createdAt;
    private String deletedAt;
    private List<ModelCrossSiteContrat> fk_site_contract;

    public ModelApiSite(int id, String label, String createdAt, String deletedAt, List<ModelCrossSiteContrat> fk_site_contract) {
        this.id = id;
        this.label = label;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.fk_site_contract = fk_site_contract;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

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

    public List<ModelCrossSiteContrat> getCrossSiteContrat() { return fk_site_contract; }

    public void setCrossSiteContrat(ModelApiSite getCrossSiteContrat) { this.fk_site_contract = fk_site_contract; }

}
