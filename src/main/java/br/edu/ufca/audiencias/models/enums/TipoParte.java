package br.edu.ufca.audiencias.models.enums;

public enum TipoParte {
    AUTOR("Autor"),
    REU("Réu"),
    PERITO("Perito"),
    TERCEIRO_INTERESSADO("Terceiro Interessado");

    private final String descricao;

    TipoParte(String descricao) {
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
