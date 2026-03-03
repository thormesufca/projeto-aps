package br.edu.ufca.audiencias.models.enums;

public enum TipoAudiencia {
    INSTRUCAO("Instrução"),
    CONCILIACAO("Conciliação"),
    JULGAMENTO("Julgamento"),
    PRELIMINAR("Preliminar");

    private final String descricao;

    TipoAudiencia(String descricao) {
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
