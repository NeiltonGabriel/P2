package br.ufal.ic.p2.wepayu.Exception;

public class ValorDeveSerNaoNuloException extends RuntimeException {
    public ValorDeveSerNaoNuloException(String message) {
        super(message + " nao pode ser nulo.");
    }
}
