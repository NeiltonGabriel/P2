package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Actions.Pagamentos;

public class RodaFolhaComando implements Comando {
    private final String data;
    private final String saida;

    public RodaFolhaComando(String data, String saida) {
        this.data = data;
        this.saida = saida;
    }

    @Override
    public void execute() {
        Pagamentos.rodar(data, saida);
    }

    @Override
    public void undo() {
        // Desfazer a folha de pagamento não é um requisito do sistema,
        // então deixamos este método vazio. O importante é que o comando
        // seja registrado para que outras operações possam ser desfeitas
        // na ordem correta.
    }
}