package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Gerente;
import br.ufal.ic.p2.wepayu.models.Empregado;

public class CriarEmpregadoComando implements Comando {
    private final Empregado employee;

    public CriarEmpregadoComando(Empregado employee) {
        this.employee = employee;
    }

    @Override
    public void execute() {
        Gerente.adicionarEmpregado(employee);
    }

    @Override
    public void undo() {
        Gerente.remover(employee.getId());
    }
}