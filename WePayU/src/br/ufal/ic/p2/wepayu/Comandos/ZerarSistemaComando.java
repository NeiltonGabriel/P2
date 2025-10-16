package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.*;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.ArrayList;
import java.util.List;

public class ZerarSistemaComando implements Comando {
    private List<Empregado> oldTrabalhadores;
    private List<String> oldAgendas;
    private List<Horarios> oldCartoes;
    private List<Vendas> oldVendas;
    private List<Sindicato> oldMembros;

    @Override
    public void execute() {
        // Salva o estado atual
        oldTrabalhadores = new ArrayList<>(Gerente.trabalhadores);
        oldAgendas = new ArrayList<>(Gerente.agendas);
        oldCartoes = new ArrayList<>(Horarios.cartoes);
        oldVendas = new ArrayList<>(Vendas.vendas);
        oldMembros = new ArrayList<>(Sindicato.membros);

        // Executa a ação
        Gerente.limpar();
        Horarios.limpar();
        Vendas.limpar();
        Sindicato.limpar();
    }

    @Override
    public void undo() {
        // Restaura o estado anterior
        Gerente.trabalhadores = oldTrabalhadores;
        Gerente.agendas = oldAgendas;
        Horarios.cartoes = oldCartoes;
        Vendas.vendas = oldVendas;
        Sindicato.membros = oldMembros;
    }
}