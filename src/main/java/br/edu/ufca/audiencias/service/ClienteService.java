package br.edu.ufca.audiencias.service;

import br.edu.ufca.audiencias.models.Cliente;
import br.edu.ufca.audiencias.models.Contrato;
import br.edu.ufca.audiencias.repository.ClienteRepository;
import br.edu.ufca.audiencias.repository.ContratoRepository;

import java.util.List;

public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContratoRepository contratoRepository;

    public ClienteService() {
        this.clienteRepository = new ClienteRepository();
        this.contratoRepository = new ContratoRepository();
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.salvar(cliente);
    }

    public void atualizar(Cliente cliente) {
        clienteRepository.atualizar(cliente);
    }

    public void deletar(Long id) {
        clienteRepository.deletar(id);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + id));
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.buscarPorNome(nome);
    }

    public List<Contrato> listarContratosPorCliente(Long clienteId) {
        return contratoRepository.listarPorCliente(clienteId);
    }
}
