package br.edu.ufca.audiencias.padroes.criacionais.factory;

import java.time.LocalDate;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.Cliente;
import br.edu.ufca.audiencias.models.Movimentacao;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.FaseProcesso;
import br.edu.ufca.audiencias.models.enums.StatusProcesso;
import br.edu.ufca.audiencias.models.enums.TipoMovimentacao;
import br.edu.ufca.audiencias.models.enums.TipoProcesso;

public class ProcessoTrabalhistaFactory implements ProcessoFactory {

    @Override
    public Processo criarProcesso(String numero, Cliente cliente, Advogado advogado) {
        Processo processo = new Processo();
        processo.setNumero(numero);
        processo.setTipo(TipoProcesso.TRABALHISTA);
        processo.setStatus(StatusProcesso.ATIVO);
        processo.setFase(FaseProcesso.CONHECIMENTO);
        processo.setDataAbertura(LocalDate.now());
        processo.setCliente(cliente);
        processo.setAdvogadoResponsavel(advogado);

        String responsavel = advogado != null ? advogado.getNome() : "Sistema";
        Movimentacao abertura = new Movimentacao(
                "Processo Trabalhista iniciado — Reclamação Trabalhista protocolada",
                TipoMovimentacao.DESPACHO, responsavel);
        processo.registrarMovimentacao(abertura);
        return processo;
    }
}
