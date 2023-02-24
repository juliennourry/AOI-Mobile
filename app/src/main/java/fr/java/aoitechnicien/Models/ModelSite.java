package fr.java.aoitechnicien.Models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ModelSite {
    private Integer id;
    private String label;
    private String contrat;
    private String tiers;
    private ArrayList<String> fk_user;

    public ModelSite(Integer id, String label, String contrat, String tiers, ArrayList<String> fk_user) {
        this.id = id;
        this.label = label;
        this.fk_user = fk_user;
        this.contrat = contrat;
        this.tiers = tiers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public ArrayList<String> getUser() {
        return fk_user;
    }

    public void setUser(ArrayList<String> fk_user) {
        this.fk_user = fk_user;
    }

    public String getContrat() { return contrat; }

    public void setContrat(String contrat) { this.contrat = contrat; }

    public String getTiers() { return tiers; }

    public void setTiers(String tiers) { this.tiers = tiers; }
}
