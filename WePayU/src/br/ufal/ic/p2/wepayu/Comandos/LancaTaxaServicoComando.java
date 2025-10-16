package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Sindicato;

public class LancaTaxaServicoComando implements Comando {
    private final String membro;
    private final String data;
    private final String valor;

    public LancaTaxaServicoComando(String membro, String data, String valor) {
        this.membro = membro;
        this.data = data;
        this.valor = valor;
    }

    @Override
    public void execute() {
        Sindicato.lancaTaxaServico(membro, data, valor);
    }

    @Override
    public void undo() {
        if (!Sindicato.membros.isEmpty()) {
            for (int i = Sindicato.membros.size() - 1; i >= 0; i--) {
                Sindicato s = Sindicato.membros.get(i);
                if (s.getId_sindicato().equals(membro) && s.getData().equals(data) && s.getValor().equals(valor)) {
                    Sindicato.membros.remove(i);
                    break;
                }
            }
        }
    }
}