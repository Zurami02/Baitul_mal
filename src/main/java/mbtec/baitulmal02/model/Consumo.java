package mbtec.baitulmal02.model;

import java.math.BigDecimal;

public class Consumo {
    private int idconsumo;
    private String descricao;
    private BigDecimal valorConsumo;
    private String data;
    private String observacao;

    public Consumo() {
    }

    public Consumo(int idconsumo, String descricao, BigDecimal valorConsumo, String data, String observacao) {
        this.idconsumo = idconsumo;
        this.descricao = descricao;
        this.valorConsumo = valorConsumo != null ? valorConsumo : BigDecimal.ZERO;
        this.data = data;
        this.observacao = observacao;
    }

    public int getIdconsumo() {
        return idconsumo;
    }

    public void setIdconsumo(int idconsumo) {
        this.idconsumo = idconsumo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorConsumo() {
        return valorConsumo != null ? valorConsumo : BigDecimal.ZERO;
    }

    public void setValorConsumo(BigDecimal valorConsumo) {
        this.valorConsumo = valorConsumo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getObservacao() {
        return observacao;
    }

    @Override
    public String toString() {
        return "Consumo{" +
                "idconsumo=" + idconsumo +
                ", descricao='" + descricao + '\'' +
                ", valorConsumo=" + valorConsumo +
                ", data='" + data + '\'' +
                ", observacao='" + observacao + '\'' +
                '}';
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
