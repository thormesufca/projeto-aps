package br.edu.ufca.audiencias.models;

public class AssuntoCNJ {

    private Long codItem;
    private Long codItemPai;
    private String nome;

    public AssuntoCNJ() {
    }

    public AssuntoCNJ(Long codItem, Long codItemPai, String nome) {
        this.codItem = codItem;
        this.codItemPai = codItemPai;
        this.nome = nome;
    }

    public Long getCodItem() {
        return codItem;
    }

    public void setCodItem(Long codItem) {
        this.codItem = codItem;
    }

    public Long getCodItemPai() {
        return codItemPai;
    }

    public void setCodItemPai(Long codItemPai) {
        this.codItemPai = codItemPai;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
