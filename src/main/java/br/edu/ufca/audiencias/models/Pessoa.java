package br.edu.ufca.audiencias.models;

import br.edu.ufca.audiencias.models.enums.TipoPessoa;

public abstract class Pessoa {

    private Long id;
    private String nome;
    private String identificador;
    private String telefone;
    private TipoPessoa tipoPessoa;

    public Pessoa() {}

    public Pessoa(String nome, String identificador, String telefone, TipoPessoa tipoPessoa) {
        this.nome = nome;
        this.identificador = identificador;
        this.telefone = telefone;
        this.tipoPessoa = tipoPessoa;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        String strip = identificador == null ? "" : identificador.replaceAll("\\D", "");
        if (strip.length() != 11 && strip.length() != 14) {
            throw new RuntimeException("Identificador deve ter 11 ou 14 digitos");
        }
        this.identificador = strip;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    @Override
    public String toString() {
        return nome + " (" + identificador + ")";
    }
}
