package br.ufal.ic.p2.wepayu.Exception;

public class AgendaDePagamentoNaoEstaDisponivelException extends RuntimeException {
    public AgendaDePagamentoNaoEstaDisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }
}
