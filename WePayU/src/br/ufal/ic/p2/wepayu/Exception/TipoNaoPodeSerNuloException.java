package br.ufal.ic.p2.wepayu.Exception;

public class TipoNaoPodeSerNuloException extends RuntimeException {
    public TipoNaoPodeSerNuloException() {
        super("Tipo nao pode ser nulo.");
    }
}
