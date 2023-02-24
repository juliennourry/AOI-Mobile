package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelApiUser {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String role;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String password;
    private String createdAt;
    private String deletedAt;
    private ModelProfile fk_profile;

    public ModelApiUser(int id,String role, String name, String lastname, String email, String phone, String password, String createdAt, String deletedAt, ModelProfile fk_profile) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.fk_profile = fk_profile;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ModelProfile getProfile() {
        return fk_profile;
    }

    public void setProfile(ModelApiUser getProfile) {
        this.fk_profile = fk_profile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

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
