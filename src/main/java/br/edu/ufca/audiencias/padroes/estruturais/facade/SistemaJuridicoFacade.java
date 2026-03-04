package br.edu.ufca.audiencias.padroes.estruturais.facade;

import br.edu.ufca.audiencias.models.*;
import br.edu.ufca.audiencias.models.enums.*;
import br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers.AdvogadoNotificadorObserver;
import br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers.AudienciaObserver;
import br.edu.ufca.audiencias.padroes.comportamentais.observer.Observers.LogAudienciaObserver;
import br.edu.ufca.audiencias.padroes.comportamentais.observer.Subjects.GerenciadorAudienciasSubject;
import br.edu.ufca.audiencias.padroes.comportamentais.strategy.EstrategiaBuscaProcesso;
import br.edu.ufca.audiencias.padroes.comportamentais.template.*;
import br.edu.ufca.audiencias.padroes.estruturais.adapter.NotificadorEmailAdapter;
import br.edu.ufca.audiencias.padroes.estruturais.decorator.*;
import br.edu.ufca.audiencias.config.EscritorioConfig;
import br.edu.ufca.audiencias.models.AssuntoCNJ;
import br.edu.ufca.audiencias.models.AssuntoProcesso;
import br.edu.ufca.audiencias.repository.*;
import br.edu.ufca.audiencias.service.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class SistemaJuridicoFacade {

    private final ClienteService clienteService;
    private final AdvogadoService advogadoService;
    private final ContratoService contratoService;
    private final ProcessoService processoService;
    private final AudienciaService audienciaService;
    private final EscritorioService escritorioService;

    private final GerenciadorAudienciasSubject gerenciadorAudiencias;
    
    private final ProcessoRepository processoRepository;
    private final ClienteRepository clienteRepository;
    private final AudienciaRepository audienciaRepository;
    private final InteressadoRepository interessadoRepository;
    private final AdvogadoRepository advogadoRepository;
    private final ContratoRepository contratoRepository;
    private final AssuntoCNJRepository assuntoCNJRepository;
    private final AssuntoProcessoRepository assuntoProcessoRepository;

    public SistemaJuridicoFacade() {
        this.clienteService = new ClienteService();
        this.advogadoService = new AdvogadoService();
        this.contratoService = new ContratoService();
        this.processoService = new ProcessoService();
        this.audienciaService = new AudienciaService();
        this.escritorioService = new EscritorioService();

        this.processoRepository = new ProcessoRepository();
        this.clienteRepository = new ClienteRepository();
        this.audienciaRepository = new AudienciaRepository();
        this.interessadoRepository = new InteressadoRepository();
        this.advogadoRepository = new AdvogadoRepository();
        this.contratoRepository = new ContratoRepository();
        this.assuntoCNJRepository = new AssuntoCNJRepository();
        this.assuntoProcessoRepository = new AssuntoProcessoRepository();

        this.gerenciadorAudiencias = new GerenciadorAudienciasSubject();
        gerenciadorAudiencias.adicionarObservador(new AdvogadoNotificadorObserver());
        gerenciadorAudiencias.adicionarObservador(new LogAudienciaObserver());
        gerenciadorAudiencias.adicionarObservador(new NotificadorEmailAdapter());
    }

    public Cliente salvarCliente(Cliente cliente) {
        return clienteService.salvar(cliente);
    }

    public void atualizarCliente(Cliente cliente) {
        clienteService.atualizar(cliente);
    }

    public void deletarCliente(Long id) {
        clienteService.deletar(id);
    }

    public Cliente buscarClientePorId(Long id) {
        return clienteService.buscarPorId(id);
    }

    public List<Cliente> listarClientes() {
        return clienteService.listarTodos();
    }

    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteService.buscarPorNome(nome);
    }



    public Advogado salvarAdvogado(Advogado advogado) {
        return advogadoService.salvar(advogado);
    }

    public void atualizarAdvogado(Advogado advogado) {
        advogadoService.atualizar(advogado);
    }

    public void deletarAdvogado(Long id) {
        advogadoService.deletar(id);
    }

    public Advogado buscarAdvogadoPorId(Long id) {
        return advogadoService.buscarPorId(id);
    }

    public List<Advogado> listarAdvogados() {
        return advogadoService.listarTodos();
    }



    public Contrato salvarContrato(Contrato contrato) {
        return contratoService.salvar(contrato);
    }

    public void atualizarContrato(Contrato contrato) {
        contratoService.atualizar(contrato);
    }

    public void deletarContrato(Long id) {
        contratoService.deletar(id);
    }

    public List<Contrato> listarContratosPorCliente(Long clienteId) {
        return contratoService.listarPorCliente(clienteId);
    }

    public List<Contrato> listarContratos() {
        return contratoService.listarTodos();
    }

    public int contarClientes()   { return clienteRepository.contar(); }
    public int contarAdvogados()  { return advogadoRepository.contar(); }
    public int contarContratos()  { return contratoRepository.contar(); }
    public int contarProcessos()  { return processoRepository.contar(); }
    public int contarAudiencias() { return audienciaRepository.contar(); }

    public Processo criarProcesso(TipoProcesso tipo, String numero) {
        return processoService.criarProcesso(tipo, numero);
    }

    public List<Processo> buscarProcessosPorNumero(String numero) {
        return processoRepository.buscarPorNumero(numero);
    }

    public void atualizarProcesso(Processo processo) {
        processoService.atualizar(processo);
    }

    public void deletarProcesso(Long id) {
        processoService.deletar(id);
    }

    public Processo buscarProcessoPorId(Long id) {
        return processoService.buscarPorId(id);
    }

    public List<Processo> listarProcessos() {
        return processoService.listarTodos();
    }

    public List<Processo> buscarProcessos(EstrategiaBuscaProcesso estrategia, String termo) {
        return processoService.buscar(estrategia, termo);
    }



    public void adicionarInteressado(Long processoId, Interessado interessado) {
        Processo processo = processoService.buscarPorId(processoId);
        processoService.adicionarInteressado(processo, interessado);
    }

    public void removerInteressado(Long id) {
        processoService.removerInteressado(id);
    }

    public void adicionarTestemunha(Testemunha testemunha, Long interessadoId, Long processoId) {
        Interessado interessado = interessadoRepository.buscarPorId(interessadoId);
        Optional<Processo> processo = processoRepository.buscarPorId(processoId);
        if (processo.isPresent()) {
            processoService.adicionarTestemunha(testemunha, interessado, processo.get());
        }

    }

    public void removerVinculoTestemunha(Long linkId) {
        processoService.removerVinculoTestemunha(linkId);
    }

    public List<Testemunha> buscarTestemunhasPorNome(String nome) {
        return processoService.buscarTestemunhasPorNome(nome);
    }

    public List<Interessado> listarInteressados(Long processoId) {
        return processoService.listarInteressados(processoId);
    }

    public List<Testemunha> listarTestemunhas(Long processoId) {
        return processoService.listarTestemunhas(processoId);
    }

    public Documento adicionarDocumento(Long processoId, Documento documento, boolean assinar, boolean protocolar, boolean urgente) {
        Processo processo = processoService.buscarPorId(processoId);
        Documento doc = documento;
        if (assinar)    doc = new DocumentoAssinadoDecorator(doc);
        if (protocolar) doc = new DocumentoProtocoladoDecorator(doc);
        if (urgente)    doc = new DocumentoUrgenteDecorator(doc);
        return processoService.adicionarDocumento(processo, doc);
    }

    public void removerDocumento(Long id) {
        new DocumentoRepository().deletar(id);
    }

    public List<Documento> listarDocumentos(Long processoId) {
        return processoService.listarDocumentos(processoId);
    }


    public void registrarMovimentacao(Long processoId, Movimentacao movimentacao) {
        Processo processo = processoService.buscarPorId(processoId);
        processoService.registrarMovimentacao(processo, movimentacao);
    }

    public List<Movimentacao> listarMovimentacoes(Long processoId) {
        return processoService.listarMovimentacoes(processoId);
    }

    public Audiencia agendarAudiencia(Long processoId, LocalDateTime dataHora,
                                       String local, TipoAudiencia tipo, String descricao) {
        if (dataHora != null && dataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar audiências para datas no passado.");
        }
        Processo processo = processoService.buscarPorId(processoId);
        Audiencia audiencia = audienciaService.agendar(processo, dataHora, local, tipo, descricao);
        gerenciadorAudiencias.notificarObservadores(audiencia, "AGENDADA");
        return audiencia;
    }

    public void cancelarAudiencia(Long audienciaId, String motivo) {
        Audiencia audiencia = audienciaService.buscarPorId(audienciaId);
        audienciaService.cancelar(audiencia, motivo);
        gerenciadorAudiencias.notificarObservadores(audiencia, "CANCELADA");
    }

    public void remarcarAudiencia(Long audienciaId, LocalDateTime novaDataHora) {
        if (novaDataHora != null && novaDataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível remarcar audiência para uma data no passado.");
        }
        Audiencia audiencia = audienciaService.buscarPorId(audienciaId);
        audiencia.setDataHora(novaDataHora);
        audiencia.setStatus(br.edu.ufca.audiencias.models.enums.StatusAudiencia.AGENDADA);
        audienciaService.atualizar(audiencia);
        gerenciadorAudiencias.notificarObservadores(audiencia, "AGENDADA");
    }

    public void registrarResultadoAudiencia(Long audienciaId, String resultado) {
        Audiencia audiencia = audienciaService.buscarPorId(audienciaId);
        audienciaService.registrarResultado(audiencia, resultado);
        gerenciadorAudiencias.notificarObservadores(audiencia, "REALIZADA");
    }

    public void editarObservacoesAudiencia(Long audienciaId, List<String> observacoes) {
        Audiencia audiencia = audienciaService.buscarPorId(audienciaId);
        audiencia.setObservacoes(observacoes);
        audienciaService.atualizar(audiencia);
    }

    public void deletarAudiencia(Long id) {
        audienciaService.deletar(id);
    }

    public Audiencia buscarAudienciaPorId(Long id) {
        return audienciaService.buscarPorId(id);
    }

    public List<Audiencia> listarAudienciasPorProcesso(Long processoId) {
        return audienciaService.listarPorProcesso(processoId);
    }

    public List<Audiencia> listarAudienciasPorMes(int mes, int ano) {
        return audienciaService.listarPorMes(mes, ano);
    }

    public List<Audiencia> listarAudienciasPorMes(int mes, int ano, boolean comProcesso) {
        return audienciaService.listarPorMes(mes, ano, comProcesso);
    }

    public List<Audiencia> listarTodasAudiencias() {
        return audienciaService.listarTodas();
    }

    
    public String gerarRelatorioProcessosPorCliente(Long clienteId) {
        RelatorioTemplate relatorio = new RelatorioProcessosPorClienteTemplate(
                processoRepository, clienteRepository);
        return relatorio.gerar(clienteId);
    }

    public String gerarRelatorioAudienciasMensais(int mes, int ano) {
        RelatorioTemplate relatorio = new RelatorioAudienciasMensaisTemplate(audienciaRepository);
        return relatorio.gerar(new int[] { mes, ano });
    }
    
    public String gerarRelatorioValoresRecebidosMensais(int mes, int ano) {
        RelatorioTemplate relatorio = new RelatorioValoresMensaisTemplate(this.contratoRepository);
        return relatorio.gerar(new int[] { mes, ano });
    }

    public void adicionarObservadorAudiencia(AudienciaObserver observer) {
        gerenciadorAudiencias.adicionarObservador(observer);
    }

    public void removerObservadorAudiencia(AudienciaObserver observer) {
        gerenciadorAudiencias.removerObservador(observer);
    }


    public List<AssuntoCNJ> buscarAssuntosCNJ(String termo) {
        return assuntoCNJRepository.buscarPorNome(termo);
    }

    public AssuntoProcesso adicionarAssuntoAoProcesso(Long processoId, Long codItem, boolean principal) {
        AssuntoProcesso ap = assuntoProcessoRepository.salvar(processoId, codItem, principal);
        if (principal && ap != null) {
            assuntoProcessoRepository.definirPrincipal(ap.getId(), processoId);
            ap.setPrincipal(true);
        }
        return ap;
    }

    public void removerAssuntoDoProcesso(Long assuntoProcessoId) {
        assuntoProcessoRepository.deletar(assuntoProcessoId);
    }

    public void definirAssuntoPrincipal(Long assuntoProcessoId, Long processoId) {
        assuntoProcessoRepository.definirPrincipal(assuntoProcessoId, processoId);
    }

    public List<AssuntoProcesso> listarAssuntosDoProcesso(Long processoId) {
        return assuntoProcessoRepository.listarPorProcesso(processoId);
    }


    public Escritorio getEscritorio() {
        return EscritorioConfig.carregar();
    }

    public void salvarEscritorio(Escritorio escritorio) {
        EscritorioConfig.salvar(escritorio);
    }

    public MembroEscritorio adicionarMembroEscritorio(MembroEscritorio membro) {
        return escritorioService.adicionarMembro(membro);
    }

    public void atualizarMembroEscritorio(MembroEscritorio membro) {
        escritorioService.atualizarMembro(membro);
    }

    public void removerMembroEscritorio(Long membroId) {
        escritorioService.removerMembro(membroId);
    }

    public List<MembroEscritorio> listarMembrosEscritorio() {
        return escritorioService.listarMembros();
    }

    public java.math.BigDecimal somarPercentuaisSocios(Long excluirMembroId) {
        return escritorioService.somarPercentuaisSocios(excluirMembroId);
    }
}
