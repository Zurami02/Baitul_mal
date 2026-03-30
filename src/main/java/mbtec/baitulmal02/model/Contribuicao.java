package mbtec.baitulmal02.model;

import java.math.BigDecimal;

public class Contribuicao {
    private int idcontribuicao;
    private String contribuinte;
    private BigDecimal valorContribuicao;
    private String data;
    private String observacao;

    public Contribuicao() {
    }

    public Contribuicao(int idcontribuicao, String contribuinte, BigDecimal valorContribuicao, String data, String observacao) {
        this.idcontribuicao = idcontribuicao;
        this.contribuinte = contribuinte;
        this.valorContribuicao = valorContribuicao != null ? valorContribuicao : BigDecimal.ZERO;
        this.data = data;
        this.observacao = observacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getIdcontribuicao() {
        return idcontribuicao;
    }

    public void setIdcontribuicao(int idcontribuicao) {
        this.idcontribuicao = idcontribuicao;
    }

    public String getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(String contribuinte) {
        this.contribuinte = contribuinte;
    }

    public BigDecimal getValorContribuicao() {
        return valorContribuicao != null ? valorContribuicao: BigDecimal.ZERO;
    }

    public void setValorContribuicao(BigDecimal valorContribuicao) {
        this.valorContribuicao = valorContribuicao;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Contribuicao{" +
                "idcontribuicao=" + idcontribuicao +
                ", contribuinte='" + contribuinte + '\'' +
                ", valorContribuicao=" + valorContribuicao +
                ", data='" + data + '\'' +
                '}';
    }

    public void setData(String data) {
        this.data = data;
    }
}
