package br.ufal.ic.p2.wepayu.Exception;

public class ValorDeveSerNaoNuloException extends RuntimeException {
    public ValorDeveSerNaoNuloException() {
        super("Valor deve ser nao nulo.");
    }
}
