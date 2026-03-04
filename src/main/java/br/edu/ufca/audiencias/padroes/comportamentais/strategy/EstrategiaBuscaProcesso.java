package br.edu.ufca.audiencias.padroes.comportamentais.strategy;

import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.repository.ProcessoRepository;

import java.util.List;


public interface EstrategiaBuscaProcesso {

    List<Processo> buscar(String termo, ProcessoRepository repository);
    String getLabel();
}
