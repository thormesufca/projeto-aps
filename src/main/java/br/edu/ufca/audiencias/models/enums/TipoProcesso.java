package br.edu.ufca.audiencias.models.enums;

public enum TipoProcesso {
    CIVEL("Cível"),
    CRIMINAL("Criminal"),
    TRABALHISTA("Trabalhista");

    private final String descricao;

    TipoProcesso(String descricao) {
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
