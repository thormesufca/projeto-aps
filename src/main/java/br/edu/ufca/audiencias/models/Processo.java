package br.edu.ufca.audiencias.models;

import br.edu.ufca.audiencias.models.enums.FaseProcesso;
import br.edu.ufca.audiencias.models.enums.StatusProcesso;
import br.edu.ufca.audiencias.models.enums.TipoProcesso;
import br.edu.ufca.audiencias.models.enums.TipoValorContrato;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Processo {

    private Long id;
    private String numero;
    private TipoProcesso tipo;
    private StatusProcesso status;
    private FaseProcesso fase;
    private LocalDate dataAbertura;
    private String orgaoJulgador;
    private String descricao;

    private BigDecimal valorCausa;
    private BigDecimal valorCondenacao;
    private Boolean favoravel;
    private BigDecimal honorariosSucumbenciais;
    private LocalDate dataPagamento;

    private Cliente cliente;
    private Advogado advogadoResponsavel;
    private List<Interessado> interessados = new ArrayList<>();
    private List<Testemunha> testemunhas = new ArrayList<>();
    private List<Audiencia> audiencias = new ArrayList<>();
    private List<Documento> documentos = new ArrayList<>();
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private List<AssuntoProcesso> assuntos = new ArrayList<>();

    public Processo() {
    }

    public Processo(String numero, TipoProcesso tipo, Cliente cliente, Advogado advogado) {
        this.numero = numero;
        this.tipo = tipo;
        this.status = StatusProcesso.ATIVO;
        this.fase = FaseProcesso.CONHECIMENTO;
        this.dataAbertura = LocalDate.now();
        this.cliente = cliente;
        this.advogadoResponsavel = advogado;
    }

    public Processo(String numero, TipoProcesso tipo) {
        this(numero, tipo, null, null);
    }

    public BigDecimal calcularValorGanho(Contrato contrato) {
        if (contrato == null || contrato.getValor() == null)
            return null;

        BigDecimal honorariosContrato;
        if (contrato.getTipoValor() == TipoValorContrato.PERCENTUAL) {
            if (valorCondenacao == null)
                return null;
            honorariosContrato = valorCondenacao
                    .multiply(contrato.getValor())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            honorariosContrato = contrato.getValor();
        }

        BigDecimal sucumb = honorariosSucumbenciais != null ? honorariosSucumbenciais : BigDecimal.ZERO;
        return honorariosContrato.add(sucumb);
    }

    public void adicionarInteressado(Interessado interessado) {
        interessados.add(interessado);
        interessado.setProcesso(this);
    }

    public void adicionarTestemunha(Testemunha testemunha) {
        testemunhas.add(testemunha);
    }

    public void adicionarAudiencia(Audiencia audiencia) {
        audiencias.add(audiencia);
        audiencia.setProcesso(this);
    }

    public void adicionarDocumento(Documento documento) {
        documentos.add(documento);
        documento.setProcesso(this);
    }

    public void registrarMovimentacao(Movimentacao movimentacao) {
        movimentacoes.add(movimentacao);
        movimentacao.setProcesso(this);
    }

    public Optional<Audiencia> getProximaAudiencia() {
        return audiencias.stream()
                .filter(a -> a.getStatus() == br.edu.ufca.audiencias.models.enums.StatusAudiencia.AGENDADA)
                .min(Comparator.comparing(Audiencia::getDataHora));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoProcesso getTipo() {
        return tipo;
    }

    public void setTipo(TipoProcesso tipo) {
        this.tipo = tipo;
    }

    public StatusProcesso getStatus() {
        return status;
    }

    public void setStatus(StatusProcesso status) {
        this.status = status;
    }

    public FaseProcesso getFase() {
        return fase;
    }

    public void setFase(FaseProcesso fase) {
        this.fase = fase;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getOrgaoJulgador() {
        return orgaoJulgador;
    }

    public void setOrgaoJulgador(String orgaoJulgador) {
        this.orgaoJulgador = orgaoJulgador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorCausa() {
        return valorCausa;
    }

    public void setValorCausa(BigDecimal valorCausa) {
        this.valorCausa = valorCausa;
    }

    public BigDecimal getValorCondenacao() {
        return valorCondenacao;
    }

    public void setValorCondenacao(BigDecimal valorCondenacao) {
        this.valorCondenacao = valorCondenacao;
    }

    public Boolean getFavoravel() {
        return favoravel;
    }

    public void setFavoravel(Boolean favoravel) {
        this.favoravel = favoravel;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getHonorariosSucumbenciais() {
        return honorariosSucumbenciais;
    }

    public void setHonorariosSucumbenciais(BigDecimal honorariosSucumbenciais) {
        this.honorariosSucumbenciais = honorariosSucumbenciais;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Advogado getAdvogadoResponsavel() {
        return advogadoResponsavel;
    }

    public void setAdvogadoResponsavel(Advogado advogadoResponsavel) {
        this.advogadoResponsavel = advogadoResponsavel;
    }

    public List<Interessado> getInteressados() {
        return interessados;
    }

    public void setInteressados(List<Interessado> interessados) {
        this.interessados = interessados;
    }

    public List<Testemunha> getTestemunhas() {
        return testemunhas;
    }

    public void setTestemunhas(List<Testemunha> testemunhas) {
        this.testemunhas = testemunhas;
    }

    public List<Audiencia> getAudiencias() {
        return audiencias;
    }

    public void setAudiencias(List<Audiencia> audiencias) {
        this.audiencias = audiencias;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    public List<Movimentacao> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<Movimentacao> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public List<AssuntoProcesso> getAssuntos() {
        return assuntos;
    }

    public void setAssuntos(List<AssuntoProcesso> assuntos) {
        this.assuntos = assuntos;
    }

    @Override
    public String toString() {
        return numero;
    }
}
