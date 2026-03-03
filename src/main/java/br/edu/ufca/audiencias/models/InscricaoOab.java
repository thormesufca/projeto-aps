package br.edu.ufca.audiencias.models;

public class InscricaoOab {

    private String estado;
    private String numero;

    public InscricaoOab(String estado, String numero) {
        this.estado = estado;
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "OAB/" + estado + " " + numero;
    }
}
