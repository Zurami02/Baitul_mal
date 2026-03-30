package mbtec.baitulmal02.model;

import java.math.BigDecimal;

public class ContaPrincipal {
    private int idcontaPrincipal;
    private BigDecimal saldo_atual;

    public ContaPrincipal() {
    }

    public ContaPrincipal(int idcontaPrincipal, BigDecimal saldo_atual) {
        this.idcontaPrincipal = idcontaPrincipal;
        this.saldo_atual = saldo_atual != null ? saldo_atual : BigDecimal.ZERO;
    }

    public int getIdcontaPrincipal() {
        return idcontaPrincipal;
    }

    public void setIdcontaPrincipal(int idcontaPrincipal) {
        this.idcontaPrincipal = idcontaPrincipal;
    }

    public BigDecimal getSaldo_atual() {
        return saldo_atual != null ? saldo_atual : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "ContaPrincipal{" +
                "idcontaPrincipal=" + idcontaPrincipal +
                ", saldo_atual=" + saldo_atual +
                '}';
    }

    public void setSaldo_atual(BigDecimal saldo_atual) {
        this.saldo_atual = saldo_atual;
    }
}
