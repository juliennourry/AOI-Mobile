package fr.java.aoitechnicien.Models;


public class ModelCrossSiteContrat {
    private Integer id;
    private ModelContrat fk_contract;

    public ModelCrossSiteContrat(Integer id, ModelContrat fk_contract) {
        this.id = id;
        this.fk_contract = fk_contract;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public ModelContrat getContrat() { return fk_contract; }

    public void setContrat(ModelCrossSiteContrat getContrat) { this.fk_contract = fk_contract; }

}
