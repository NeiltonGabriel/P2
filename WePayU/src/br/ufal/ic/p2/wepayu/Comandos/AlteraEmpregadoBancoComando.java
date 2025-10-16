package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Gerente;
import br.ufal.ic.p2.wepayu.models.Empregado;

public class AlteraEmpregadoBancoComando implements Comando {
    private final String emp;
    private final String atributo;
    private final String valor1;
    private final String banco;
    private final String agencia;
    private final String contaCorrente;
    private final String oldMetodo;
    private final String oldBanco;
    private final String oldAgencia;
    private final String oldContaCorrente;

    public AlteraEmpregadoBancoComando(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) {
        this.emp = emp;
        this.atributo = atributo;
        this.valor1 = valor1;
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;

        Empregado e = Gerente.buscar(emp);
        assert e != null;
        this.oldMetodo = e.getMetodo();
        this.oldBanco = e.getBanco();
        this.oldAgencia = e.getAgencia();
        this.oldContaCorrente = e.getContacorrente();
    }

    @Override
    public void execute() {
        Gerente.alterarBanco(emp, atributo, valor1, banco, agencia, contaCorrente);
    }

    @Override
    public void undo() {
        if (oldMetodo.equals("banco")) {
            Gerente.alterarBanco(emp, atributo, oldMetodo, oldBanco, oldAgencia, oldContaCorrente);
        } else {
            Gerente.alterar(emp, "metodoPagamento", oldMetodo);
        }
    }
}