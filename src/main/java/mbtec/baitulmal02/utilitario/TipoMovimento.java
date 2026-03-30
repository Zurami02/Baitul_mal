package mbtec.baitulmal02.utilitario;

public enum TipoMovimento {
    ENTRADA("Entrada"),
    SAIDA("Saida");

    private final String descricao;

    TipoMovimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static void main(String[] args) {
        String tipo = TipoMovimento.SAIDA.descricao;
        TipoMovimento tipoMovimento;

        if (tipo.equalsIgnoreCase("Entrada")){
            System.out.println("Tipo :"+tipo);
        }else{
            System.out.println("Tipo :"+ TipoMovimento.SAIDA.descricao);
        }

    }
}
