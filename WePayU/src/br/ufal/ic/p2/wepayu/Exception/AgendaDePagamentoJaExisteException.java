package br.ufal.ic.p2.wepayu.Exception;

public class AgendaDePagamentoJaExisteException extends RuntimeException {
    public AgendaDePagamentoJaExisteException() {
        super("Agenda de pagamentos ja existe");
    }
}
