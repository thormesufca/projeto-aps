package br.edu.ufca.audiencias.models.enums;

public enum FaseProcesso {
    CONHECIMENTO("Conhecimento"),
    EXECUCAO("Execução"),
    RECURSAL("Recursal"),
    LIQUIDACAO("Liquidação");

    private final String descricao;

    FaseProcesso(String descricao) {
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
