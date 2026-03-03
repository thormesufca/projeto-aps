package br.edu.ufca.audiencias.models;

import java.time.LocalDate;

import br.edu.ufca.audiencias.models.enums.TipoDocumento;

public class Documento {

    private Long id;
    private String titulo;
    private String descricao;
    private Integer sequencial;
    private String caminhoArquivo;
    private TipoDocumento tipo;
    private LocalDate dataUpload;

    private Processo processo;

    public Documento() {
    }

    public Documento(String titulo, String descricao, TipoDocumento tipo, Integer sequencial) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.sequencial = sequencial;
        this.dataUpload = LocalDate.now();
    }

    public String getDescricaoCompleta() {
        return titulo + " [" + (tipo != null ? tipo.getDescricao() : "") + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public TipoDocumento getTipo() {
        return tipo;
    }

    public void setTipo(TipoDocumento tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDate dataUpload) {
        this.dataUpload = dataUpload;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @Override
    public String toString() {
        return getDescricaoCompleta();
    }
}
