package br.edu.ufca.audiencias.padroes.estruturais.decorator;

import java.time.LocalDate;

import br.edu.ufca.audiencias.models.Documento;

public class DocumentoAssinadoDecorator extends DocumentoDecorator {

    private final LocalDate dataAssinatura;

    public DocumentoAssinadoDecorator(Documento documento) {
        super(documento);
        this.dataAssinatura = LocalDate.now();
    }

    @Override
    public String getDescricaoCompleta() {
        return documentoDecorado.getDescricaoCompleta()
                + ". Assinado em " + dataAssinatura;
    }

    public LocalDate getDataAssinatura() {
        return dataAssinatura;
    }

    public boolean isAssinado() {
        return true;
    }
}
