package fr.java.aoitechnicien;

import java.util.Map;

public class ModelApiUser {
    private int id;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String password;
    private String created_at;
    private String deleted_at;
    private String users;

    public ModelApiUser(int id, String name, String lastname, String email, String phone, String password, String created_at, String deleted_at) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.created_at = created_at;
        this.deleted_at = deleted_at;
    }

    public String getUsers() { return users; }

    public void setUsers(String data) { this.users = data; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getDeletedAt() {
        return deleted_at;
    }

    public void setDeletedAt(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}
