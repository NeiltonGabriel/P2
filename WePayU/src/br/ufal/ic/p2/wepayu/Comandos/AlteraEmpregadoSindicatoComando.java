package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Gerente;
import br.ufal.ic.p2.wepayu.Actions.Sindicato;
import br.ufal.ic.p2.wepayu.models.Empregado;

public class AlteraEmpregadoSindicatoComando implements Comando {
    private final String emp;
    private final String atributo;
    private final String valor;
    private final String idSindicato;
    private final String taxaSindical;
    private final boolean oldSindicalizado;
    private Sindicato oldSindicato;

    public AlteraEmpregadoSindicatoComando(String emp, String atributo, String valor, String idSindicato, String taxaSindical) {
        this.emp = emp;
        this.atributo = atributo;
        this.valor = valor;
        this.idSindicato = idSindicato;
        this.taxaSindical = taxaSindical;

        Empregado e = Gerente.buscar(emp);
        assert e != null;
        this.oldSindicalizado = e.isSindicalizado();
        if (this.oldSindicalizado) {
            this.oldSindicato = Sindicato.buscar(emp, 2);
        }
    }

    @Override
    public void execute() {
        Sindicato.alterarSindicato(emp, atributo, valor, idSindicato, taxaSindical);
    }

    @Override
    public void undo() {
        Empregado e = Gerente.buscar(emp);
        assert e != null;
        e.setSindicalizado(oldSindicalizado);
        Sindicato.remover(emp);
        if (oldSindicato != null) {
            Sindicato.membros.add(oldSindicato);
        }
    }
}