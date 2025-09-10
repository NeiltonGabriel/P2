package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.ComissaoDeveSerNaoNegativa;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNaoPodeSerNula;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;

public class Comissionado extends Assalariado{

    private String comissao;

    public Comissionado(){

    }

    public Comissionado(String nome, String endereco, String tipo, String salario, String id, String comissao) throws EmpregadoNaoExisteException {

        super(nome, endereco, tipo, salario, id);
        this.comissao = comissao;
    }

    public String getComissao() {
        if (comissao == null) return "";
        else if (comissao.contains(",")) return comissao;
        else return String.format("%d,00", Integer.parseInt(comissao));
    }

    public void setComissao(String comissao) {
        this.comissao = comissao;
    }

    @Override
    public String toString(){
        return String.format(this.getId() + "\nNome: " + this.getNome() + "\nSalário: " + this.getSalario()
                + "\nTipo: " + this.getTipo() + "\nEndereço: " + this.getEndereco() + "\nComissão: " + this.getComissao() + "\n");
    }
}
