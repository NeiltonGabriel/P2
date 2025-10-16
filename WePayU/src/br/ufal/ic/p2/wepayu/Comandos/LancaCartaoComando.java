package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Horarios;

public class LancaCartaoComando implements Comando {
    private final String emp;
    private final String data;
    private final String horas;

    public LancaCartaoComando(String emp, String data, String horas) {
        this.emp = emp;
        this.data = data;
        this.horas = horas;
    }

    @Override
    public void execute() {
        Horarios.lancaCartao(emp, data, horas);
    }

    @Override
    public void undo() {
        if (!Horarios.cartoes.isEmpty()) {
            Horarios.cartoes.removeLast();
        }
    }
}