package br.edu.ufca.audiencias.models;

import br.edu.ufca.audiencias.models.enums.TipoVinculoAdvogado;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MembroEscritorio {

    private Long id;
    private Advogado advogado;
    private TipoVinculoAdvogado tipoVinculo;

    private BigDecimal percentualSociedade;
    private BigDecimal salarioMensal;

    private LocalDate dataIngresso;
    private LocalDate dataDesligamento;

    public MembroEscritorio() {
    }

    public MembroEscritorio(Advogado advogado, TipoVinculoAdvogado tipoVinculo, LocalDate dataIngresso) {
        this.advogado = advogado;
        this.tipoVinculo = tipoVinculo;
        this.dataIngresso = dataIngresso;
    }

    public boolean isAtivo() {
        return dataDesligamento == null || dataDesligamento.isAfter(LocalDate.now());
    }

    public boolean isSocio() {
        return TipoVinculoAdvogado.SOCIO.equals(tipoVinculo);
    }

    public boolean isEmpregado() {
        return TipoVinculoAdvogado.EMPREGADO.equals(tipoVinculo);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Advogado getAdvogado() {
        return advogado;
    }

    public void setAdvogado(Advogado advogado) {
        this.advogado = advogado;
    }

    public TipoVinculoAdvogado getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(TipoVinculoAdvogado tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    public BigDecimal getPercentualSociedade() {
        return percentualSociedade;
    }

    public void setPercentualSociedade(BigDecimal percentualSociedade) {
        this.percentualSociedade = percentualSociedade;
    }

    public BigDecimal getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(BigDecimal salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    public LocalDate getDataIngresso() {
        return dataIngresso;
    }

    public void setDataIngresso(LocalDate dataIngresso) {
        this.dataIngresso = dataIngresso;
    }

    public LocalDate getDataDesligamento() {
        return dataDesligamento;
    }

    public void setDataDesligamento(LocalDate dataDesligamento) {
        this.dataDesligamento = dataDesligamento;
    }

    @Override
    public String toString() {
        if (advogado == null)
            return "(vínculo sem advogado)";
        String complemento = isSocio()
                ? (percentualSociedade != null ? percentualSociedade.toPlainString() + "%" : "s/ percentual")
                : (salarioMensal != null ? "R$ " + salarioMensal.toPlainString() : "s/ salário");
        return advogado.getNome() + " — " + tipoVinculo.getDescricao() + " (" + complemento + ")";
    }
}
