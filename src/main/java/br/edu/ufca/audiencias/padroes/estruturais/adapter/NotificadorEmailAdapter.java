package br.edu.ufca.audiencias.padroes.estruturais.adapter;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.Audiencia;
import br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers.AudienciaObserver;


public class NotificadorEmailAdapter implements AudienciaObserver {

    private final NotificadorEmailLegado emailLegado;

    public NotificadorEmailAdapter() {
        this.emailLegado = new NotificadorEmailLegado();
    }

    @Override
    public void atualizar(Audiencia audiencia, String evento) {
        if (!emailLegado.verificarConexaoSMTP()) {
            System.out.println("[ADAPTER] SMTP indisponível. E-mail não enviado.");
            return;
        }

        Advogado adv = audiencia.getProcesso() != null
                ? audiencia.getProcesso().getAdvogadoResponsavel()
                : null;

        String destinatario = adv != null && adv.getEmail() != null
                ? adv.getEmail()
                : "sem-email@escritorio.com";

        String assunto = "Audiência " + evento + " — Processo "
                + (audiencia.getProcesso() != null ? audiencia.getProcesso().getNumero() : "—");

        String corpo = construirCorpo(audiencia, evento, adv);

        emailLegado.enviarEmail(destinatario, assunto, corpo);
    }

    private String construirCorpo(Audiencia audiencia, String evento, Advogado adv) {
        return String.format("""
                Prezado(a) %s,

                Informamos que a audiência abaixo foi marcada como: %s

                  Processo  : %s
                  Audiência : %s
                  Data/Hora : %s
                  Local     : %s

                Atenciosamente,
                Sistema de Gestão Jurídica""",
                adv != null ? adv.getNome() : "Advogado(a)",
                evento,
                audiencia.getProcesso() != null ? audiencia.getProcesso().getNumero() : "—",
                audiencia.getDescricao(),
                audiencia.getDataHora() != null ? audiencia.getDataHora() : "—",
                audiencia.getLocal() != null ? audiencia.getLocal() : "—");
    }
}
