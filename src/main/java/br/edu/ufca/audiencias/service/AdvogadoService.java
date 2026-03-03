package br.edu.ufca.audiencias.service;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.repository.AdvogadoRepository;

import java.util.List;

public class AdvogadoService {

    private final AdvogadoRepository advogadoRepository;

    public AdvogadoService() {
        this.advogadoRepository = new AdvogadoRepository();
    }

    public Advogado salvar(Advogado advogado) {
        return advogadoRepository.salvar(advogado);
    }

    public void atualizar(Advogado advogado) {
        advogadoRepository.atualizar(advogado);
    }

    public void deletar(Long id) {
        advogadoRepository.deletar(id);
    }

    public Advogado buscarPorId(Long id) {
        return advogadoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Advogado não encontrado: " + id));
    }

    public List<Advogado> listarTodos() {
        return advogadoRepository.listarTodos();
    }
}
