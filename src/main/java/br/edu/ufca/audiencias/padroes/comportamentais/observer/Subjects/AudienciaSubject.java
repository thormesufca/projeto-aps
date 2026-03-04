package br.edu.ufca.audiencias.padroes.comportamentais.observer.Subjects;

import br.edu.ufca.audiencias.models.Audiencia;
import br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers.AudienciaObserver;

public interface AudienciaSubject {

    void adicionarObservador(AudienciaObserver observer);

    void removerObservador(AudienciaObserver observer);

    void notificarObservadores(Audiencia audiencia, String evento);
}
