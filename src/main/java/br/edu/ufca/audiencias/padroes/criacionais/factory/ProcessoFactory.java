package br.edu.ufca.audiencias.padroes.criacionais.factory;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.Cliente;
import br.edu.ufca.audiencias.models.Processo;


public interface ProcessoFactory {

    Processo criarProcesso(String numero, Cliente cliente, Advogado advogado);
}
