package fr.java.aoitechnicien.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelApiTiers {

    @SerializedName("id")
    @Expose
    private Integer id;
    private String ref;
    private String label;
    private String customer;
    private String supplier;
    private String email;
    private String phone;
    private String address;
    private String zip;
    private String city;
    private String origin;
    private String createdAt;
    private String deletedAt;

    public ModelApiTiers(int id, String ref, String label, String customer, String supplier, String email, String phone, String address, String zip, String city, String origin, String createdAt, String deletedAt) {
        this.id = id;
        this.ref = ref;
        this.label = label;
        this.customer = customer;
        this.supplier = supplier;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.origin = origin;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }


    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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


    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public String getCustomer() { return customer; }

    public void setCustomer(String customer) { this.customer = customer; }

    public String getSupplier() { return supplier; }

    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getOrigin() { return origin; }

    public void setOrigin(String origin) { this.origin = origin; }

    public String getRef() { return ref; }

    public void setRef(String ref) { this.ref = ref; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getZip() { return zip; }

    public void setZip(String zip) { this.zip = zip; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }
}
