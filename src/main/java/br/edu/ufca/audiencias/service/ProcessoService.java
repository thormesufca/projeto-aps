package br.edu.ufca.audiencias.service;

import br.edu.ufca.audiencias.models.*;
import br.edu.ufca.audiencias.models.enums.TipoProcesso;
import br.edu.ufca.audiencias.padroes.comportamentais.strategy.EstrategiaBuscaProcesso;
import br.edu.ufca.audiencias.padroes.criacionais.factory.*;
import br.edu.ufca.audiencias.repository.*;

import java.util.List;

public class ProcessoService {

    private final ProcessoRepository processoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final InteressadoRepository interessadoRepository;
    private final TestemunhaRepository testemunhaRepository;
    private final DocumentoRepository documentoRepository;

    public ProcessoService() {
        this.processoRepository = new ProcessoRepository();
        this.movimentacaoRepository = new MovimentacaoRepository();
        this.interessadoRepository = new InteressadoRepository();
        this.testemunhaRepository = new TestemunhaRepository();
        this.documentoRepository = new DocumentoRepository();
    }

    public Processo criarProcesso(TipoProcesso tipo, String numero) {
        return criarProcesso(tipo, numero, null, null);
    }

    public Processo criarProcesso(TipoProcesso tipo, String numero,
            Cliente cliente, Advogado advogado) {
        ProcessoFactory factory = switch (tipo) {
            case CIVEL -> new ProcessoCivelFactory();
            case CRIMINAL -> new ProcessoCriminalFactory();
            case TRABALHISTA -> new ProcessoTrabalhistaFactory();
        };

        Processo processo = factory.criarProcesso(numero, cliente, advogado);
        processoRepository.salvar(processo);

        for (Movimentacao mov : processo.getMovimentacoes()) {
            mov.setProcesso(processo);
            movimentacaoRepository.salvar(mov);
        }

        return processo;
    }

    public void atualizar(Processo processo) {
        processoRepository.atualizar(processo);
    }

    public void deletar(Long id) {
        processoRepository.deletar(id);
    }

    public Processo buscarPorId(Long id) {
        Processo p = processoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado: " + id));
        carregarRelacionamentos(p);
        return p;
    }

    public List<Processo> listarTodos() {
        return processoRepository.listarTodos();
    }

    public List<Processo> buscar(EstrategiaBuscaProcesso estrategia, String termo) {
        return estrategia.buscar(termo, processoRepository);
    }

    public void adicionarInteressado(Processo processo, Interessado interessado) {
        interessado.setProcesso(processo);
        interessadoRepository.salvar(interessado);
        processo.getInteressados().add(interessado);
    }

    public void removerInteressado(Long id) {
        interessadoRepository.deletar(id);
    }

    public void adicionarTestemunha(Testemunha testemunha, Interessado interessado, Processo processo) {
        if (testemunha.getId() == null) {
            testemunhaRepository.salvar(testemunha);
        }
        testemunhaRepository.vincularTestemunha(testemunha.getId(), interessado.getId(), processo.getId());
    }

    public void removerVinculoTestemunha(Long linkId) {
        testemunhaRepository.deletarVinculo(linkId);
    }

    public List<Testemunha> buscarTestemunhasPorNome(String nome) {
        return testemunhaRepository.buscarPorNome(nome);
    }

    public Documento adicionarDocumento(Processo processo, Documento documento) {
        documento.setProcesso(processo);
        documentoRepository.salvar(documento);
        processo.getDocumentos().add(documento);
        return documento;
    }

    public void registrarMovimentacao(Processo processo, Movimentacao movimentacao) {
        movimentacao.setProcesso(processo);
        movimentacaoRepository.salvar(movimentacao);
        processo.getMovimentacoes().add(movimentacao);
    }

    public List<Interessado> listarInteressados(Long processoId) {
        return interessadoRepository.listarPorProcesso(processoId);
    }

    public List<Testemunha> listarTestemunhas(Long processoId) {
        return testemunhaRepository.listarPorProcesso(processoId);
    }

    public List<Documento> listarDocumentos(Long processoId) {
        return documentoRepository.listarPorProcesso(processoId);
    }

    public List<Movimentacao> listarMovimentacoes(Long processoId) {
        return movimentacaoRepository.listarPorProcesso(processoId);
    }

    private void carregarRelacionamentos(Processo p) {
        p.setInteressados(interessadoRepository.listarPorProcesso(p.getId()));
        p.setTestemunhas(testemunhaRepository.listarPorProcesso(p.getId()));
        p.setDocumentos(documentoRepository.listarPorProcesso(p.getId()));
        p.setMovimentacoes(movimentacaoRepository.listarPorProcesso(p.getId()));
    }
}
