package fr.java.aoitechnicien.Models;

public class ModelCategory {
    private Integer id;
    private String label;

    public ModelCategory(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }
}
