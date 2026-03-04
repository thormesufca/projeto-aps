package br.edu.ufca.audiencias.padroes.comportamentais.template;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import br.edu.ufca.audiencias.models.Contrato;
import br.edu.ufca.audiencias.models.enums.TipoValorContrato;
import br.edu.ufca.audiencias.repository.ContratoRepository;

public class RelatorioValoresMensaisTemplate extends RelatorioTemplate {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private final ContratoRepository contratoRepository;
    private List<Contrato> contratos;
    private int mes;
    private int ano;

    public RelatorioValoresMensaisTemplate(ContratoRepository contratoRepository) {
        this.contratoRepository = contratoRepository;
    }

    @Override
    protected void coletarDados(Object parametro) {
        int[] mesAno = (int[]) parametro;
        mes = mesAno[0];
        ano = mesAno[1];
        contratos = contratoRepository.listarRecebidosPorMes(mes, ano);
    }

    @Override
    protected String formatarCorpo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  RELATÓRIO DE VALORES RECEBIDOS — %02d/%04d%n", mes, ano));
        sb.append("  =======================================\n");
        if (contratos == null || contratos.isEmpty()) {
            sb.append("  Nenhum valor recebido neste período.\n");
        } else {
            BigDecimal total = BigDecimal.ZERO;
            for (Contrato c : contratos) {
                if(c.getProcesso() == null){
                    continue;
                }
                String dataFmt = c.getProcesso() != null && c.getProcesso().getDataPagamento() != null
                        ? c.getProcesso().getDataPagamento().format(FMT)
                        : "—";
                BigDecimal valor = BigDecimal.ZERO;
                if (c.getTipoValor() == TipoValorContrato.FIXO) {
                    valor = c.getValor();
                } else {
                    if (c.getProcesso().getValorCondenacao() != null) {
                        valor = c.getProcesso().getHonorariosSucumbenciais().multiply(c.getValor());
                    }
                }
                valor = valor.add(c.getProcesso().getHonorariosSucumbenciais() != null
                        ? c.getProcesso().getHonorariosSucumbenciais()
                        : BigDecimal.ZERO);
                total = total.add(valor);
                sb.append(String.format("  %-16s | %-20s | %-10s | %-15s%n%n",
                        dataFmt,
                        c.getCliente().getNome() != null ? c.getCliente().getNome() : "—",
                        c.getProcesso().getNumero() != null ? c.getProcesso().getNumero() : "—",
                        CURRENCY.format(valor)
                        ));
            }
            sb.append("\n  Total: ").append(CURRENCY.format(total));
        }
        return sb.toString();
    }   
}
