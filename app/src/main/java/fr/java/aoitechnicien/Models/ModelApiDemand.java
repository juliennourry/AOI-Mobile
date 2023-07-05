package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelApiDemand {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String reference;
    private String title;
    private String description;
    private String origin;

    private String origin_phone;
    private String origin_mail;
    private String state;
    private String doneAt;
    private Integer doneTime;
    private String suspendedAt;
    private String suspendedReason;
    private String[] fk_contactthirdparty;
    private String[] fk_itemsite;
    private String[] fk_site;
    private String[] fk_thirdparty;
    private String[] fk_user;
    private String createdAt;
    private String deletedAt;


    public ModelApiDemand(int id, String reference, String title, String description, String origin, String origin_phone, String origin_mail, String state, String doneAt, Integer doneTime,  String suspendedAt, String suspendedReason, String[] fk_contactthirdparty, String[] fk_itemsite, String[] fk_site, String[] fk_thirdparty, String[] fk_user, String createdAt, String deletedAt) {
        this.id = id;
        this.reference = reference;
        this.title = title;
        this.description = description;
        this.origin = origin;
        this.origin_phone = origin_phone;
        this.origin_mail = origin_mail;
        this.state = state;
        this.doneAt = doneAt;
        this.doneTime = doneTime;
        this.suspendedAt = suspendedAt;
        this.suspendedReason = suspendedReason;
        this.fk_contactthirdparty = fk_contactthirdparty;
        this.fk_itemsite = fk_itemsite;
        this.fk_site = fk_site;
        this.fk_thirdparty = fk_thirdparty;
        this.fk_user = fk_user;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginPhone() {
        return origin_phone;
    }

    public void setOriginPhone(String origin_phone) {
        this.origin_phone = origin_phone;
    }

    public String getOriginMail() {
        return origin_mail;
    }

    public void setOriginMail(String origin_mail) {
        this.origin_mail = origin_mail;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(String doneAt) {
        this.doneAt = doneAt;
    }

    public Integer getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Integer doneTime) {
        this.doneTime = doneTime;
    }

    public String getSuspendedAt() {
        return suspendedAt;
    }

    public void setSuspendedAt(String suspendedAt) {
        this.suspendedAt = suspendedAt;
    }

    public String getSuspendedReason() {
        return suspendedReason;
    }

    public void setSuspendedReason(String suspendedReason) { this.suspendedReason = suspendedReason; }

    public String[] getFkContactthirdparty() { return fk_contactthirdparty; }

    public void setFkContactthirdparty(String[] fk_contactthirdparty) { this.fk_contactthirdparty = fk_contactthirdparty; }

    public String[] getFkItemsite() { return fk_itemsite; }

    public void setFkItemsite(String[] fk_itemsite) { this.fk_itemsite = fk_itemsite; }

    public String[] getFkSite() { return fk_site; }

    public void setFkSite(String[] fk_site) { this.fk_site = fk_site; }

    public String[] getFkThirdparty() { return fk_thirdparty; }

    public void setFkThirdparty(String[] fk_thirdparty) { this.fk_thirdparty = fk_thirdparty; }

    public String[] getFkUser() { return fk_user; }

    public void setFkUser(String[] fk_user) { this.fk_user = fk_user; }

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


}
