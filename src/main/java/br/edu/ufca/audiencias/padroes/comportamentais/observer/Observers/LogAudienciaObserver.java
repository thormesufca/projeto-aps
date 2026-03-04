package br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.ufca.audiencias.models.Audiencia;

public class LogAudienciaObserver implements AudienciaObserver {

    private final List<String> entradas = new ArrayList<>();

    @Override
    public void atualizar(Audiencia audiencia, String evento) {
        String entrada = String.format("[%s] Audiência ID=%s | Processo=%s | Evento=%s",
                LocalDateTime.now(),
                audiencia.getId() != null ? audiencia.getId() : "novo",
                audiencia.getProcesso() != null ? audiencia.getProcesso().getNumero() : "—",
                evento);
        entradas.add(entrada);
        System.out.println("[LOG] " + entrada);
    }

    public List<String> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public void limpar() {
        entradas.clear();
    }
}
