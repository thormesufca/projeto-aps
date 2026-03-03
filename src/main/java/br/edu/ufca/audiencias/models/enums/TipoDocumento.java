package br.edu.ufca.audiencias.models.enums;

public enum TipoDocumento {
    PETICAO("Petição"),
    SENTENCA("Sentença"),
    RECURSO("Recurso"),
    CONTRATO("Contrato"),
    PROCURACAO("Procuração"),
    LAUDO("Laudo Pericial"),
    OUTRO("Outro");

    private final String descricao;

    TipoDocumento(String descricao) {
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
