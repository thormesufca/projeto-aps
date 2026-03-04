package br.edu.ufca.audiencias.padroes.comportamentais.observer.Subjects;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufca.audiencias.models.Audiencia;
import br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers.AudienciaObserver;


public class GerenciadorAudienciasSubject implements AudienciaSubject {

    private final List<AudienciaObserver> observadores = new ArrayList<>();

    @Override
    public void adicionarObservador(AudienciaObserver observer) {
        if (!observadores.contains(observer)) {
            observadores.add(observer);
        }
    }

    @Override
    public void removerObservador(AudienciaObserver observer) {
        observadores.remove(observer);
    }

    @Override
    public void notificarObservadores(Audiencia audiencia, String evento) {
        for (AudienciaObserver obs : observadores) {
            obs.atualizar(audiencia, evento);
        }
    }

    public int getTotalObservadores() {
        return observadores.size();
    }
}
