package br.edu.ufca.audiencias.padroes.estruturais.decorator;

import br.edu.ufca.audiencias.models.Documento;

public class DocumentoUrgenteDecorator extends DocumentoDecorator {

    private String justificativaUrgencia;

    public DocumentoUrgenteDecorator(Documento documento) {
        super(documento);
    }

    public DocumentoUrgenteDecorator(Documento documento, String justificativa) {
        super(documento);
        this.justificativaUrgencia = justificativa;
    }

    @Override
    public String getDescricaoCompleta() {
        return "! URGENTE — " + documentoDecorado.getDescricaoCompleta();
    }

    public boolean isUrgente() {
        return true;
    }

    public String getJustificativaUrgencia() {
        return justificativaUrgencia;
    }

    public void setJustificativaUrgencia(String justificativa) {
        this.justificativaUrgencia = justificativa;
    }
}
