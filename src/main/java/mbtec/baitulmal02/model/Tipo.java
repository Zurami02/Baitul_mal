package mbtec.baitulmal02.model;

public class Tipo {
    private int idtipo;
    private String descricaoTipo;

    public Tipo() {
    }

    public Tipo(int idtipo, String descricaoTipo) {
        this.idtipo = idtipo;
        this.descricaoTipo = descricaoTipo;
    }

    @Override
    public String toString(){
        return descricaoTipo;
    }

    public int getIdtipo() {
        return idtipo;
    }

    public void setIdtipo(int idtipo) {
        this.idtipo = idtipo;
    }

    public String getDescricaoTipo() {
        return descricaoTipo;
    }

    public void setDescricaoTipo(String descricaoTipo) {
        this.descricaoTipo = descricaoTipo;
    }
}
