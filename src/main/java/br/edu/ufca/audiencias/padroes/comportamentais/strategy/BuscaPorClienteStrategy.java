package br.edu.ufca.audiencias.padroes.comportamentais.strategy;

import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.repository.ProcessoRepository;

import java.util.List;

public class BuscaPorClienteStrategy implements EstrategiaBuscaProcesso {

    @Override
    public List<Processo> buscar(String termo, ProcessoRepository repository) {
        return repository.buscarPorNomeCliente(termo);
    }

    @Override
    public String getLabel() {
        return "Por Cliente";
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
