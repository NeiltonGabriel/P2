package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;

public class Horista extends Empregado{

    public Horista(){

    }

    public Horista(String nome, String endereco, String tipo, String salario, String id) throws EmpregadoNaoExisteException {
        super(nome, endereco, tipo, salario, id);
    }



    @Override
    public String toString(){
        return String.format(this.getId() + "\nNome: " + this.getNome() + "\nSalário: " + this.getSalario()
                + "\nTipo: " + this.getTipo() + "\nEndereço: " + this.getEndereco() + "\n");
    }

}
