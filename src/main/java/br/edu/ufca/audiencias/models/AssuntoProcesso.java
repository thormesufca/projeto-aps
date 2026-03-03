package br.edu.ufca.audiencias.models;

public class AssuntoProcesso {

    private Long id;
    private AssuntoCNJ assunto;
    private boolean principal;

    public AssuntoProcesso() {
    }

    public AssuntoProcesso(AssuntoCNJ assunto, boolean principal) {
        this.assunto = assunto;
        this.principal = principal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssuntoCNJ getAssunto() {
        return assunto;
    }

    public void setAssunto(AssuntoCNJ assunto) {
        this.assunto = assunto;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return assunto != null ? assunto.getNome() : "";
    }
}
