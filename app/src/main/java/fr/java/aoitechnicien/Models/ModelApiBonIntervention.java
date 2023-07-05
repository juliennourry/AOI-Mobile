package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelApiBonIntervention {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String reference;
    private String title;
    private String description;
    private String state;
    private String doneAt;
    private Integer doneTime;
    private String origin;
    private String type;
    private String signataire;
    private String signature;
    private String fk_demand;
    private String fk_note;
    private String fk_user;
    private String fk_thirdparty;
    private String createdAt;
    private String deletedAt;


    public ModelApiBonIntervention(int id, String reference, String title, String description, String state, String doneAt, Integer doneTime, String origin, String type, String signataire, String signature, String fk_demand, String fk_note, String fk_user, String fk_thirdparty, String createdAt, String deletedAt) {
        this.id = id;
        this.reference = reference;
        this.title = title;
        this.description = description;
        this.state = state;
        this.doneAt = doneAt;
        this.doneTime = doneTime;
        this.origin = origin;
        this.type = type;
        this.signataire = signataire;
        this.signature = signature;
        this.fk_demand = fk_demand;
        this.fk_note = fk_note;
        this.fk_user = fk_user;
        this.fk_thirdparty = fk_thirdparty;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() { return reference; }

    public void setReference(String reference) { this.reference = reference; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getDoneAt() { return doneAt; }

    public void setDoneAt(String doneAt) { this.doneAt = doneAt; }

    public Integer getDoneTime() { return doneTime; }

    public void setDoneTime(Integer doneTime) { this.doneTime = doneTime; }

    public String getOrigin() { return origin; }

    public void setOrigin(String origin) { this.origin = origin; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getSignataire() { return signataire; }

    public void setSignataire(String signataire) { this.signataire = signataire; }

    public String getSignature() { return signature; }

    public void setSignature(String signature) { this.signature = signature; }

    public String getFkDemand() {
        if(fk_demand == null) {
            return "";
        }
        return fk_demand;
    }

    public void setFkDemand(String fk_demand) { this.fk_demand = fk_demand; }

    public String getFkNote() {
        if(fk_note == null) {
            return "";
        }
        return fk_note;
    }

    public void setFkNote(String fk_note) { this.fk_note = fk_note; }

    public String getFkUser() {
        if(fk_user == null) {
            return "";
        }
        return fk_user;
    }

    public void setFkUser(String fk_user) { this.fk_user = fk_user; }

    public String getFkThirdparty() {
        if(fk_thirdparty == null) {
            return "";
        }
        return fk_thirdparty;
    }

    public void setFkThirdparty(String fk_thirdparty) { this.fk_thirdparty = fk_thirdparty; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDeletedAt() { return deletedAt; }

    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }
}
