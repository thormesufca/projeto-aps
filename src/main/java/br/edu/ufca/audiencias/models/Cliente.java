package br.edu.ufca.audiencias.models;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufca.audiencias.models.enums.TipoPessoa;

public class Cliente extends Pessoa {

    private String email;
    private String endereco;
    private List<Contrato> contratos = new ArrayList<>();

    public Cliente() {
        super();
    }

    public Cliente(String nome, String identificador, String telefone,
            TipoPessoa tipoPessoa, String email, String endereco) {
        super(nome, identificador, telefone, tipoPessoa);
        this.email = email;
        this.endereco = endereco;
    }

    public void adicionarContrato(Contrato contrato) {
        contratos.add(contrato);
        contrato.setCliente(this);
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return getNome();
    }
}
