package br.edu.ufca.audiencias.padroes.comportamentais.template;

import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.repository.ClienteRepository;
import br.edu.ufca.audiencias.repository.ProcessoRepository;

import java.util.List;

public class RelatorioProcessosPorClienteTemplate extends RelatorioTemplate {

    private final ProcessoRepository processoRepository;
    private final ClienteRepository clienteRepository;
    private List<Processo> processos;
    private String nomeCliente;

    public RelatorioProcessosPorClienteTemplate(ProcessoRepository processoRepository,
            ClienteRepository clienteRepository) {
        this.processoRepository = processoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    protected void coletarDados(Object parametro) {
        Long clienteId = (Long) parametro;
        processos = processoRepository.buscarPorClienteId(clienteId);
        nomeCliente = clienteRepository.buscarPorId(clienteId)
                .map(c -> c.getNome())
                .orElse("Cliente não encontrado");
    }

    @Override
    protected String formatarCorpo() {
        StringBuilder sb = new StringBuilder();
        sb.append("  RELATÓRIO DE PROCESSOS — CLIENTE: ").append(nomeCliente).append("\n");
        sb.append("  =======================================\n");

        if (processos == null || processos.isEmpty()) {
            sb.append("  Nenhum processo encontrado para este cliente.\n");
        } else {
            sb.append(
                    "Número               | Tipo         | Status       | Descricao       | Órgão Julgador                           | Favorável\n");
            for (Processo p : processos) {
                sb.append(String.format("%-20s | %-12s | %-12s | %-15s | %-40s | %-1s%n%n",
                        p.getNumero(),
                        p.getTipo() != null ? p.getTipo().getDescricao() : "—",
                        p.getStatus() != null ? p.getStatus().getDescricao() : "—",
                        p.getFase() != null ? p.getFase().getDescricao() : "—",
                        p.getOrgaoJulgador() != null ? p.getOrgaoJulgador() : "-",
                        p.getFavoravel() != null ? "S" : "N"));
            }

            sb.append("\n  Total: ").append(processos.size()).append(" processo(s)\n");
        }
        return sb.toString();
    }
}
