package fr.java.aoitechnicien.Models;


public class ModelCrossThirdpartyContrat {
    private Integer id;
    private ModelThirdParty fk_thirdparty;

    public ModelCrossThirdpartyContrat(Integer id, ModelThirdParty fk_thirdparty) {
        this.id = id;
        this.fk_thirdparty = fk_thirdparty;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public ModelThirdParty getThirdparty() { return fk_thirdparty; }

    public void setThirdparty(ModelCrossThirdpartyContrat getThirdparty) { this.fk_thirdparty = fk_thirdparty; }

}
