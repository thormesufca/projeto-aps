package br.edu.ufca.audiencias.padroes.estruturais.adapter;


public class NotificadorEmailLegado {

    public void enviarEmail(String destinatario, String assunto, String corpo) {
        System.out.printf("[EMAIL LEGADO] Para: %s%n", destinatario);
        System.out.printf("[EMAIL LEGADO] Assunto: %s%n", assunto);
        System.out.printf("[EMAIL LEGADO] Corpo: %s%n%n", corpo);
    }

    public boolean verificarConexaoSMTP() {
        return true;
    }
}
