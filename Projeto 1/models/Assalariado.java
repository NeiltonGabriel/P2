package br.ufal.ic.p2.wepayu.models;

public class Assalariado extends Empregado{

    private String liquido;

    public Assalariado(String nome, String endereco, String tipo, String salario, String id){
        super(nome, endereco, tipo, salario, id);
        this.liquido = salario;
    }

    public String getLiquido() {
        return liquido;
    }

    public void setLiquido(String liquido) {
        this.liquido = liquido;
    }
}
