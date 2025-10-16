package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Gerente;

public class AlteraEmpregadoAtributoComando implements Comando {
    private final String empId;
    private final String attribute;
    private final String value;
    private final String oldValue;

    public AlteraEmpregadoAtributoComando(String empId, String attribute, String value) {
        this.empId = empId;
        this.attribute = attribute;
        this.value = value;
        this.oldValue = Gerente.atributo(empId, attribute);
    }

    @Override
    public void execute() {
        Gerente.alterar(empId, attribute, value);
    }

    @Override
    public void undo() {
        Gerente.alterar(empId, attribute, oldValue);
    }
}