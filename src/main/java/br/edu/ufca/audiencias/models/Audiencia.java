package br.edu.ufca.audiencias.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufca.audiencias.models.enums.StatusAudiencia;
import br.edu.ufca.audiencias.models.enums.TipoAudiencia;

public class Audiencia {

    private Long id;
    private String descricao;
    private LocalDateTime dataHora;
    private String local;
    private TipoAudiencia tipo;
    private StatusAudiencia status;
    private String resultado;
    private List<String> observacoes = new ArrayList<>();

    private Processo processo;

    public Audiencia() {
    }

    public Audiencia(String descricao, LocalDateTime dataHora, String local, TipoAudiencia tipo) {
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.local = local;
        this.tipo = tipo;
        this.status = StatusAudiencia.AGENDADA;
    }

    public void adicionarObservacao(String observacao) {
        if (observacao != null && !observacao.isBlank()) {
            observacoes.add(observacao);
        }
    }

    public void registrarResultado(String resultado) {
        this.resultado = resultado;
        this.status = StatusAudiencia.REALIZADA;
    }

    public boolean isAgendada() {
        return status == StatusAudiencia.AGENDADA;
    }

    public boolean isRealizada() {
        return status == StatusAudiencia.REALIZADA;
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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public TipoAudiencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoAudiencia tipo) {
        this.tipo = tipo;
    }

    public StatusAudiencia getStatus() {
        return status;
    }

    public void setStatus(StatusAudiencia status) {
        this.status = status;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public List<String> getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(List<String> observacoes) {
        this.observacoes = observacoes;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @Override
    public String toString() {
        return tipo + " - " + (dataHora != null ? dataHora.toString() : "sem data") + " [" + status + "]";
    }
}
