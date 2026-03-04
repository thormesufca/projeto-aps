package br.edu.ufca.audiencias.padroes.estruturais.decorator;

import java.time.LocalDate;

import br.edu.ufca.audiencias.models.Documento;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.TipoDocumento;

public abstract class DocumentoDecorator extends Documento {

    protected final Documento documentoDecorado;

    public DocumentoDecorator(Documento documentoDecorado) {
        this.documentoDecorado = documentoDecorado;

        this.setId(documentoDecorado.getId());
        this.setTitulo(documentoDecorado.getTitulo());
        this.setDescricao(documentoDecorado.getDescricao());
        this.setCaminhoArquivo(documentoDecorado.getCaminhoArquivo());
        this.setTipo(documentoDecorado.getTipo());
        this.setDataUpload(documentoDecorado.getDataUpload());
        this.setProcesso(documentoDecorado.getProcesso());
    }

    @Override
    public String getDescricaoCompleta() {
        return documentoDecorado.getDescricaoCompleta();
    }

    public Documento getDocumentoDecorado() {
        return documentoDecorado;
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
        documentoDecorado.setId(id);
    }

    @Override
    public void setProcesso(Processo p) {
        super.setProcesso(p);
        documentoDecorado.setProcesso(p);
    }

    @Override
    public void setTipo(TipoDocumento t) {
        super.setTipo(t);
        documentoDecorado.setTipo(t);
    }

    @Override
    public void setDataUpload(LocalDate d) {
        super.setDataUpload(d);
        documentoDecorado.setDataUpload(d);
    }
}
