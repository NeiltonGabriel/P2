package br.ufal.ic.p2.wepayu.Exception;

public class AtributoNaoPodeSerNuloException extends RuntimeException {
    public AtributoNaoPodeSerNuloException() {
        super("Atributo nao pode ser nulo.");
    }
}
