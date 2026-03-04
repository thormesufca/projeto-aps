package br.edu.ufca.audiencias.padroes.comportamentais.template;

import java.time.LocalDateTime;


public abstract class RelatorioTemplate {

    public final String gerar(Object parametro) {
        StringBuilder sb = new StringBuilder();
        sb.append(gerarCabecalho());
        coletarDados(parametro);
        sb.append(formatarCorpo());
        sb.append(gerarRodape());
        return sb.toString();
    }

    protected String gerarCabecalho() {
        return """
                =============================================================
                    SISTEMA DE GESTÃO DE AUDIÊNCIAS E PROCESSOS JUDICIAIS
                =============================================================
                Gerado em: %s

                """.formatted(LocalDateTime.now().toString().replace("T", " ").substring(0, 19));
    }
    protected abstract void coletarDados(Object parametro);

    protected abstract String formatarCorpo();

    protected String gerarRodape() {
        return "\n====================================================\n" +
               "                     FIM DO RELATÓRIO                 \n" +
               "======================================================\n";
    }
}
