package br.edu.ufca.audiencias.padroes.comportamentais.template;

import br.edu.ufca.audiencias.models.Audiencia;
import br.edu.ufca.audiencias.repository.AudienciaRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioAudienciasMensaisTemplate extends RelatorioTemplate {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AudienciaRepository audienciaRepository;
    private List<Audiencia> audiencias;
    private int mes;
    private int ano;

    public RelatorioAudienciasMensaisTemplate(AudienciaRepository audienciaRepository) {
        this.audienciaRepository = audienciaRepository;
    }

    @Override
    protected void coletarDados(Object parametro) {
        int[] mesAno = (int[]) parametro;
        mes = mesAno[0];
        ano = mesAno[1];
        audiencias = audienciaRepository.listarPorMes(mes, ano);
    }

    @Override
    protected String formatarCorpo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  RELATÓRIO DE AUDIÊNCIAS — %02d/%04d%n", mes, ano));
        sb.append("  =======================================\n");

        if (audiencias == null || audiencias.isEmpty()) {
            sb.append("  Nenhuma audiência encontrada para este período.\n");
        } else {
            for (Audiencia a : audiencias) {
                String dataFmt = a.getDataHora() != null ? a.getDataHora().format(FMT) : "—";
                sb.append(String.format("  %-16s | %-14s | %-10s | Processo: %s%n",
                        dataFmt,
                        a.getTipo() != null ? a.getTipo().getDescricao() : "—",
                        a.getStatus() != null ? a.getStatus().getDescricao() : "—",
                        a.getProcesso() != null && a.getProcesso().getNumero() != null
                                ? a.getProcesso().getNumero() : "—"));
                if (a.getDescricao() != null) {
                    sb.append("    └ ").append(a.getDescricao()).append("\n");
                }
                if (a.getResultado() != null && !a.getResultado().isBlank()) {
                    sb.append("    ↳ Resultado: ").append(a.getResultado()).append("\n");
                }
            }
            sb.append("\n  Total: ").append(audiencias.size()).append(" audiência(s)\n");
        }
        return sb.toString();
    }
}
