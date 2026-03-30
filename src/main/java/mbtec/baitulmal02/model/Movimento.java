package mbtec.baitulmal02.model;

import java.math.BigDecimal;

public class Movimento {
    private int idmovimento;
    private String tipo;
    private BigDecimal valor;
    private String data;
    private BigDecimal saldoResultante;
    private Consumo consumo;
    private Contribuicao contribuicao;

    public Movimento() {
    }

    public Movimento(int idmovimento, String tipo, BigDecimal valor, String data, BigDecimal saldoResultante, Consumo consumo, Contribuicao contribuicao) {
        this.idmovimento = idmovimento;
        this.tipo = tipo;
        this.valor = valor;
        this.data = data;
        this.saldoResultante = saldoResultante;
        this.consumo = consumo;
        this.contribuicao = contribuicao;
    }

    public BigDecimal getSaldoResultante() {
        return saldoResultante != null ? saldoResultante : BigDecimal.ZERO;
    }

    public void setSaldoResultante(BigDecimal saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public int getIdmovimento() {
        return idmovimento;
    }

    public void setIdmovimento(int idmovimento) {
        this.idmovimento = idmovimento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Consumo getConsumo() {
        return consumo;
    }

    public void setConsumo(Consumo consumo) {
        this.consumo = consumo;
    }

    @Override
    public String toString() {
        return "Movimento{" +
                "idmovimento=" + idmovimento +
                ", tipo='" + tipo + '\'' +
                ", valor=" + valor +
                ", data='" + data + '\'' +
                ", saldoResultante=" + saldoResultante +
                ", consumo=" + consumo +
                ", contribuicao=" + contribuicao +
                '}';
    }

    public Contribuicao getContribuicao() {
        return contribuicao;
    }

    public void setContribuicao(Contribuicao contribuicao) {
        this.contribuicao = contribuicao;
    }
}
