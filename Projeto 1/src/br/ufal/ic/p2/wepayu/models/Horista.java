package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;

public class Horista extends Empregado{

    private String dataContratacao;
    private String ultimoPagamento;

    public Horista(){

    }

    public Horista(String nome, String endereco, String tipo, String salario, String id) throws EmpregadoNaoExisteException {
        super(nome, endereco, tipo, salario, id);
        this.dataContratacao = "-1";
        this.ultimoPagamento = "-1";
    }

    public void setDataContratacao(String data){
        this.dataContratacao = data;
    }

    public String getDataContratacao(){
        return this.dataContratacao;
    }

    public String getUltimoPagamento() {
        return ultimoPagamento;
    }

    public void setUltimoPagamento(String ultimoPagamento) {
        this.ultimoPagamento = ultimoPagamento;
    }

    @Override
    public String toString(){
        return String.format(this.getId() + "\nNome: " + this.getNome() + "\nSalário: " + this.getSalario()
                + "\nTipo: " + this.getTipo() + "\nEndereço: " + this.getEndereco() + "\n");
    }

}
