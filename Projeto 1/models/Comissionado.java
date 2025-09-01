package br.ufal.ic.p2.wepayu.models;

public class Comissionado extends Empregado{

    private String liquido;
    private String comissao;

    public Comissionado(String nome, String endereco, String tipo, String salario, String id, String comissao){
        super(nome, endereco, tipo, salario, id);
        this.liquido = salario;
        this.comissao = comissao;
    }

    public String getLiquido() {
        return liquido;
    }

    public void setLiquido(String liquido) {
        this.liquido = liquido;
    }

    public String getComissao() {
        if (comissao.contains(",")) return comissao;
        else return String.format("%d,00", Integer.parseInt(comissao));
    }

    public void setComissao(String comissao) {
        this.comissao = comissao;
    }
}
