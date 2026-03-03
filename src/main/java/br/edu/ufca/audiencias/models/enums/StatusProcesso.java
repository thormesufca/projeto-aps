package br.edu.ufca.audiencias.models.enums;

public enum StatusProcesso {
    ATIVO("Ativo"),
    ARQUIVADO("Arquivado"),
    ENCERRADO("Encerrado"),
    SUSPENSO("Suspenso");

    private final String descricao;

    StatusProcesso(String descricao) {
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
