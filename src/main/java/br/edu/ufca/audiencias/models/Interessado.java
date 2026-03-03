package br.edu.ufca.audiencias.models;

import br.edu.ufca.audiencias.models.enums.TipoParte;
import br.edu.ufca.audiencias.models.enums.TipoPessoa;


public class Interessado extends Pessoa {

    private TipoParte tipoParte;
    private Processo processo;
    private Advogado advogado;

    public Interessado() {
        super();
    }

    public Interessado(String nome, String identificador, String telefone, TipoPessoa tipoPessoa, TipoParte tipoParte) {
        super(nome, identificador, telefone, tipoPessoa);
        this.tipoParte = tipoParte;
    }

    public TipoParte getTipoParte() {
        return tipoParte;
    }

    public void setTipoParte(TipoParte tipoParte) {
        this.tipoParte = tipoParte;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Advogado getAdvogado() {
        return advogado;
    }

    public void setAdvogado(Advogado advogado) {
        this.advogado = advogado;
    }

    @Override
    public String toString() {
        return getNome() + " [" + (tipoParte != null ? tipoParte.getDescricao() : "") + "]";
    }
}
