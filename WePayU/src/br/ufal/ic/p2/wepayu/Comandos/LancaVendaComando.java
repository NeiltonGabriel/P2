package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Vendas;

public class LancaVendaComando implements Comando {
    private final String emp;
    private final String data;
    private final String valor;

    public LancaVendaComando(String emp, String data, String valor) {
        this.emp = emp;
        this.data = data;
        this.valor = valor;
    }

    @Override
    public void execute() {
        Vendas.lancar(emp, data, valor);
    }

    @Override
    public void undo() {
        if (!Vendas.vendas.isEmpty()) {
            Vendas.vendas.removeLast();
        }
    }
}