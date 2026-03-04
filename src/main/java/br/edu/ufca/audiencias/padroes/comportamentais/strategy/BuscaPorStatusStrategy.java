package br.edu.ufca.audiencias.padroes.comportamentais.strategy;

import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.StatusProcesso;
import br.edu.ufca.audiencias.repository.ProcessoRepository;

import java.util.List;

public class BuscaPorStatusStrategy implements EstrategiaBuscaProcesso {

    @Override
    public List<Processo> buscar(String termo, ProcessoRepository repository) {
        try {
            StatusProcesso status = StatusProcesso.valueOf(termo.toUpperCase());
            return repository.buscarPorStatus(status);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    public String getLabel() {
        return "Por Status";
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
