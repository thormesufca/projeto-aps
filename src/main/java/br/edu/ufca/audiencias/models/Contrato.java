package br.edu.ufca.audiencias.models;

import br.edu.ufca.audiencias.models.enums.TipoValorContrato;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Contrato {

    private Long id;
    private LocalDate dataContratacao;
    private LocalDate dataEncerramento;
    private TipoValorContrato tipoValor = TipoValorContrato.FIXO;
    private BigDecimal valor;
    private String descricao;
    private String observacoes;

    private Cliente cliente;
    private Processo processo;

    public Contrato() {}

    public Contrato(LocalDate dataContratacao, BigDecimal valor, String descricao, Cliente cliente) {
        this.dataContratacao = dataContratacao;
        this.valor = valor;
        this.descricao = descricao;
        this.cliente = cliente;
    }

    public boolean isAtivo() {
        return dataEncerramento == null || dataEncerramento.isAfter(LocalDate.now());
    }

    public String getValorFormatado() {
        if (valor == null) return "-";
        return tipoValor == TipoValorContrato.PERCENTUAL
                ? valor.toPlainString() + "%"
                : "R$ " + valor.toPlainString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(LocalDate dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDate dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public TipoValorContrato getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(TipoValorContrato tipoValor) {
        this.tipoValor = tipoValor != null ? tipoValor : TipoValorContrato.FIXO;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @Override
    public String toString() {
        return "Contrato #" + id + " - " + (descricao != null ? descricao : "Sem descrição") + " (" + getValorFormatado() + ")";
    }
}
