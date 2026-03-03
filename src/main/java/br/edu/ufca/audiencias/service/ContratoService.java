package br.edu.ufca.audiencias.service;

import br.edu.ufca.audiencias.models.Contrato;
import br.edu.ufca.audiencias.repository.ContratoRepository;

import java.util.List;

public class ContratoService {

    private final ContratoRepository contratoRepository;

    public ContratoService() {
        this.contratoRepository = new ContratoRepository();
    }

    public Contrato salvar(Contrato contrato) {
        return contratoRepository.salvar(contrato);
    }

    public void atualizar(Contrato contrato) {
        contratoRepository.atualizar(contrato);
    }

    public void deletar(Long id) {
        contratoRepository.deletar(id);
    }

    public Contrato buscarPorId(Long id) {
        return contratoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado: " + id));
    }

    public List<Contrato> listarPorCliente(Long clienteId) {
        return contratoRepository.listarPorCliente(clienteId);
    }

    public List<Contrato> listarTodos() {
        return contratoRepository.listarTodos();
    }
}
