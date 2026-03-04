package br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.Audiencia;

public class AdvogadoNotificadorObserver implements AudienciaObserver {

    @Override
    public void atualizar(Audiencia audiencia, String evento) {
        Advogado advogado = audiencia.getProcesso() != null
                ? audiencia.getProcesso().getAdvogadoResponsavel()
                : null;

        String nomeAdv = advogado != null ? advogado.getNome() : "Advogado não atribuído";
        String oab = (advogado != null && !advogado.getInscricoesOab().isEmpty())
                ? advogado.getInscricoesOab().get(0).toString() : "—";
        String numProcesso = audiencia.getProcesso() != null
                ? audiencia.getProcesso().getNumero()
                : "—";

        System.out.printf("[NOTIFICAÇÃO] Advogado: %s (OAB: %s) | Processo: %s | Audiência '%s' → %s em %s%n",
                nomeAdv, oab, numProcesso,
                audiencia.getDescricao(), evento, audiencia.getDataHora());
    }
}
