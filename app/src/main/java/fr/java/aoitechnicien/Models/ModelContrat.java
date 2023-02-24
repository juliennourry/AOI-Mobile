package fr.java.aoitechnicien.Models;


import java.util.List;

public class ModelContrat {
    private Integer id;
    private String ref;
    private List<ModelCrossThirdpartyContrat> fk_thirdparty_contract;

    public ModelContrat(Integer id, String ref, List<ModelCrossThirdpartyContrat> fk_thirdparty_contract) {
        this.id = id;
        this.ref = ref;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public String getRef() { return ref; }

    public void setRef(String ref) { this.ref = ref; }

    public List<ModelCrossThirdpartyContrat> getCrossThirdpartyContract() { return fk_thirdparty_contract; }

    public void setCrossSiteContrat(ModelContrat getCrossThirdpartyContract) { this.fk_thirdparty_contract = fk_thirdparty_contract; }
}
