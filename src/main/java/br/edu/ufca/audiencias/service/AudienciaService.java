package br.edu.ufca.audiencias.service;

import br.edu.ufca.audiencias.models.Audiencia;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.StatusAudiencia;
import br.edu.ufca.audiencias.models.enums.TipoAudiencia;
import br.edu.ufca.audiencias.repository.AudienciaRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AudienciaService {

    private final AudienciaRepository audienciaRepository;

    public AudienciaService() {
        this.audienciaRepository = new AudienciaRepository();
    }

    public Audiencia agendar(Processo processo, LocalDateTime dataHora,
                              String local, TipoAudiencia tipo, String descricao) {
        Audiencia audiencia = new Audiencia(descricao, dataHora, local, tipo);
        audiencia.setProcesso(processo);
        audienciaRepository.salvar(audiencia);
        return audiencia;
    }

    public void cancelar(Audiencia audiencia, String motivo) {
        audiencia.setStatus(StatusAudiencia.CANCELADA);
        audiencia.adicionarObservacao("Motivo do cancelamento: " + motivo);
        audienciaRepository.atualizar(audiencia);
    }

    public void registrarResultado(Audiencia audiencia, String resultado) {
        audiencia.registrarResultado(resultado);
        audienciaRepository.atualizar(audiencia);
    }

    public void atualizar(Audiencia audiencia) {
        audienciaRepository.atualizar(audiencia);
    }

    public void deletar(Long id) {
        audienciaRepository.deletar(id);
    }

    public Audiencia buscarPorId(Long id) {
        return audienciaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Audiência não encontrada: " + id));
    }

    public List<Audiencia> listarPorProcesso(Long processoId) {
        return audienciaRepository.listarPorProcesso(processoId);
    }

    public List<Audiencia> listarPorMes(int mes, int ano) {
        return audienciaRepository.listarPorMes(mes, ano);
    }

    public List<Audiencia> listarPorMes(int mes, int ano, boolean comProcesso) {
        return audienciaRepository.listarPorMes(mes, ano, comProcesso);
    }

    public List<Audiencia> listarTodas() {
        return audienciaRepository.listarTodas();
    }
}
