package br.ufal.ic.p2.wepayu.models;

public class Assalariado extends Empregado{

    private double liquido;

    public Assalariado(String nome, String endereco, String tipo, double salario, int id){
        super(nome, endereco, tipo, salario, id);
        setLiquido(salario);
    }

    public double getLiquido() {
        return liquido;
    }

    public void setLiquido(double liquido) {
        this.liquido = liquido;
    }
}
