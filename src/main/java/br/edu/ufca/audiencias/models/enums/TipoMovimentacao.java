package br.edu.ufca.audiencias.models.enums;

public enum TipoMovimentacao {
    DESPACHO("Despacho"),
    DECISAO("Decisão"),
    SENTENCA("Sentença"),
    RECURSO("Recurso"),
    AUDIENCIA("Audiência"),
    JUNTADA("Juntada de Documento"),
    OUTRO("Outro");

    private final String descricao;

    TipoMovimentacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
