package br.edu.ufca.audiencias.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Escritorio {

    private String nome;
    private String cnpj;
    private String telefone;
    private String email;
    private String endereco;
    private LocalDate dataFundacao;

    private List<MembroEscritorio> membros = new ArrayList<>();

    public Escritorio() {
    }

    public void addMembro(MembroEscritorio membro) {
        this.membros.add(membro);
    }

    public List<MembroEscritorio> getMembrosAtivos() {
        return membros.stream().filter(MembroEscritorio::isAtivo).toList();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        String strip = cnpj == null ? "" : cnpj.replaceAll("\\D", "");
        if (!strip.isEmpty() && strip.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve ter 14 dígitos");
        }
        this.cnpj = strip.isEmpty() ? null : strip;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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

    public LocalDate getDataFundacao() {
        return dataFundacao;
    }

    public void setDataFundacao(LocalDate dataFundacao) {
        this.dataFundacao = dataFundacao;
    }

    public List<MembroEscritorio> getMembros() {
        return membros;
    }

    public void setMembros(List<MembroEscritorio> membros) {
        this.membros = membros;
    }

    @Override
    public String toString() {
        return nome + (cnpj != null ? " (CNPJ: " + cnpj + ")" : "");
    }
}
