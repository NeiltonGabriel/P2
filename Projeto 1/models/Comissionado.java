package br.ufal.ic.p2.wepayu.models;

public class Comissionado extends Empregado{

    private double liquido;;

    public Comissionado(String nome, String endereco, String tipo, double salario, int id){
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
