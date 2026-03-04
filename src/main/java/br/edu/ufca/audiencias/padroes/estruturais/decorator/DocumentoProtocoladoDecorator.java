package br.edu.ufca.audiencias.padroes.estruturais.decorator;

import java.time.LocalDateTime;

import br.edu.ufca.audiencias.models.Documento;

public class DocumentoProtocoladoDecorator extends DocumentoDecorator {

    private String numeroProtocolo;
    private final LocalDateTime dataProtocolo;

    public DocumentoProtocoladoDecorator(Documento documento) {
        super(documento);
        this.dataProtocolo = LocalDateTime.now();
        this.numeroProtocolo = gerarNumeroProtocolo();
    }

    @Override
    public String getDescricaoCompleta() {
        return documentoDecorado.getDescricaoCompleta()
                + ". Protocolado: " + numeroProtocolo;
    }

    private String gerarNumeroProtocolo() {
        return "PROT-" + System.currentTimeMillis();
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public LocalDateTime getDataProtocolo() {
        return dataProtocolo;
    }

    public boolean isProtocolado() {
        return true;
    }
}
