package br.edu.ufca.audiencias.service;

import br.edu.ufca.audiencias.models.MembroEscritorio;
import br.edu.ufca.audiencias.models.enums.TipoVinculoAdvogado;
import br.edu.ufca.audiencias.repository.EscritorioRepository;

import java.math.BigDecimal;
import java.util.List;

public class EscritorioService {

    private final EscritorioRepository escritorioRepository;

    public EscritorioService() {
        this.escritorioRepository = new EscritorioRepository();
    }

    public MembroEscritorio adicionarMembro(MembroEscritorio membro) {
        validarCampos(membro);
        if (TipoVinculoAdvogado.SOCIO.equals(membro.getTipoVinculo())) {
            validarTeto(null, membro.getPercentualSociedade());
        }
        return escritorioRepository.adicionarMembro(membro);
    }

    public void atualizarMembro(MembroEscritorio membro) {
        validarCampos(membro);
        if (TipoVinculoAdvogado.SOCIO.equals(membro.getTipoVinculo())) {
            validarTeto(membro.getId(), membro.getPercentualSociedade());
        }
        escritorioRepository.atualizarMembro(membro);
    }

    public void removerMembro(Long membroId) {
        escritorioRepository.removerMembro(membroId);
    }

    public List<MembroEscritorio> listarMembros() {
        return escritorioRepository.listarMembros();
    }

    public BigDecimal somarPercentuaisSocios(Long excluirMembroId) {
        return escritorioRepository.somarPercentuaisSocios(excluirMembroId);
    }

    private void validarTeto(Long excluirMembroId, BigDecimal novoPercentual) {
        BigDecimal somaAtual = escritorioRepository.somarPercentuaisSocios(excluirMembroId);
        BigDecimal somaResultante = somaAtual.add(novoPercentual);
        if (somaResultante.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(String.format(
                    "A soma dos percentuais dos sócios não pode ultrapassar 100%%. "
                    + "Percentual já distribuído: %.2f%%. Valor solicitado: %.2f%%.",
                    somaAtual.doubleValue(), novoPercentual.doubleValue()));
        }
    }

    private void validarCampos(MembroEscritorio membro) {
        if (membro.getAdvogado() == null || membro.getAdvogado().getId() == null) {
            throw new IllegalArgumentException("Advogado inválido para o vínculo com o escritório.");
        }
        if (membro.getTipoVinculo() == null) {
            throw new IllegalArgumentException("Tipo de vínculo é obrigatório.");
        }
        switch (membro.getTipoVinculo()) {
            case SOCIO -> {
                if (membro.getPercentualSociedade() == null) {
                    throw new IllegalArgumentException("Sócio deve ter percentual de participação informado.");
                }
                if (membro.getPercentualSociedade().doubleValue() <= 0
                        || membro.getPercentualSociedade().doubleValue() > 100) {
                    throw new IllegalArgumentException("Percentual de participação deve estar entre 0 e 100.");
                }
            }
            case EMPREGADO -> {
                if (membro.getSalarioMensal() == null) {
                    throw new IllegalArgumentException("Empregado deve ter salário mensal informado.");
                }
                if (membro.getSalarioMensal().doubleValue() < 0) {
                    throw new IllegalArgumentException("Salário mensal não pode ser negativo.");
                }
            }
        }
    }
}
