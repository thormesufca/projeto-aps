package br.edu.ufca.audiencias.models;

import br.edu.ufca.audiencias.models.enums.TipoPessoa;

public class Testemunha extends Pessoa {

    private String depoimento;
    private Interessado interessado;
    private Long linkId;

    public Testemunha() {
        super();
    }

    public Testemunha(String nome, String identificador, String telefone, TipoPessoa tipoPessoa) {
        super(nome, identificador, telefone, tipoPessoa);
    }

    public String getDepoimento() {
        return depoimento;
    }

    public void setDepoimento(String depoimento) {
        this.depoimento = depoimento;
    }

    public Interessado getInteressado() {
        return interessado;
    }

    public void setInteressado(Interessado interessado) {
        this.interessado = interessado;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    @Override
    public String toString() {
        return getNome() + " [Testemunha]";
    }
}
