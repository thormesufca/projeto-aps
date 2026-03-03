package br.edu.ufca.audiencias.models;

import java.time.LocalDateTime;

import br.edu.ufca.audiencias.models.enums.TipoMovimentacao;

public class Movimentacao {

    private Long id;
    private String descricao;
    private LocalDateTime dataHora;
    private String responsavel;
    private TipoMovimentacao tipo;

    private Processo processo;

    public Movimentacao() {
    }

    public Movimentacao(String descricao, TipoMovimentacao tipo) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.dataHora = LocalDateTime.now();
    }

    public Movimentacao(String descricao, TipoMovimentacao tipo, String responsavel) {
        this(descricao, tipo);
        this.responsavel = responsavel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @Override
    public String toString() {
        return "[" + dataHora + "] " + tipo + ": " + descricao;
    }
}
