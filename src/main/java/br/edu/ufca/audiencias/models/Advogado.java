package br.edu.ufca.audiencias.models;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufca.audiencias.models.enums.TipoPessoa;

public class Advogado extends Pessoa {

    private List<InscricaoOab> inscricoesOab = new ArrayList<>();
    private List<String> especialidades = new ArrayList<>();
    private String email;

    public Advogado() {
        super();
        setTipoPessoa(TipoPessoa.FISICA);
    }

    public Advogado(String nome, String identificador, String telefone, String email) {
        super(nome, identificador, telefone, TipoPessoa.FISICA);
        this.email = email;
    }

    public List<InscricaoOab> getInscricoesOab() {
        return inscricoesOab;
    }

    public void setInscricoesOab(List<InscricaoOab> inscricoesOab) {
        this.inscricoesOab = inscricoesOab;
    }

    public void addInscricaoOab(InscricaoOab inscricao) {
        this.inscricoesOab.add(inscricao);
    }

    public List<String> getEspecialidades() {
        return this.especialidades;
    }

    public void addEspecialidade(String especialidade) {
        this.especialidades.add(especialidade);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        if (inscricoesOab.isEmpty()) return getNome();
        return getNome() + " (" + inscricoesOab.get(0) + ")";
    }
}
