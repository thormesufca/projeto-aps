package br.edu.ufca.audiencias.models.enums;

public enum TipoVinculoAdvogado {
    SOCIO("Sócio"),
    EMPREGADO("Empregado");

    private final String descricao;

    TipoVinculoAdvogado(String descricao) {
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
