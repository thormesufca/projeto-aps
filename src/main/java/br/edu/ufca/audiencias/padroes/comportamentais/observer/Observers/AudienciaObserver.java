package br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers;

import br.edu.ufca.audiencias.models.Audiencia;


public interface AudienciaObserver {
    void atualizar(Audiencia audiencia, String evento);
}
