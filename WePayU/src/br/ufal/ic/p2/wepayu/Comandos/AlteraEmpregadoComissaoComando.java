package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Gerente;
import br.ufal.ic.p2.wepayu.models.Empregado;

public class AlteraEmpregadoComissaoComando implements Comando {
    private final String emp;
    private final String atributo;
    private final String valor;
    private final String comissao;
    private Empregado oldEmpregado;

    public AlteraEmpregadoComissaoComando(String emp, String atributo, String valor, String comissao) {
        this.emp = emp;
        this.atributo = atributo;
        this.valor = valor;
        this.comissao = comissao;
        this.oldEmpregado = Gerente.buscar(emp);
    }

    @Override
    public void execute() {
        Gerente.alterarComissao(emp, atributo, valor, comissao);
    }

    @Override
    public void undo() {
        Gerente.remover(emp);
        Gerente.adicionarEmpregado(oldEmpregado);
    }
}