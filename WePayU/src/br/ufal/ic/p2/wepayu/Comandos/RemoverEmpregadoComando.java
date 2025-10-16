package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Gerente;
import br.ufal.ic.p2.wepayu.Actions.Sindicato;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoDoEmpregadoNaoPodeSerNulaException;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.List;
import java.util.stream.Collectors;

public class RemoverEmpregadoComando implements Comando {
    private final String empId;
    private Empregado employee;
    private List<Sindicato> sindicatoMembroInfo;

    public RemoverEmpregadoComando(String empId) {
        this.empId = empId;
    }

    @Override
    public void execute() {
        if (empId == null || empId.isEmpty()) {
            throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        }
        this.employee = Gerente.buscar(empId);
        if (this.employee == null) {
            throw new EmpregadoNaoExisteException();
        }

        this.sindicatoMembroInfo = Sindicato.membros.stream()
                .filter(m -> m.getId().equals(empId))
                .collect(Collectors.toList());

        Gerente.remover(empId);
        Sindicato.remover(empId);
    }

    @Override
    public void undo() {
        if (employee != null) {
            Gerente.adicionarEmpregado(employee);
            if (sindicatoMembroInfo != null && !sindicatoMembroInfo.isEmpty()) {
                Sindicato.membros.addAll(sindicatoMembroInfo);
            }
        }
    }
}