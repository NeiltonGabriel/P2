package br.ufal.ic.p2.wepayu.Exception;

public class ComissaoDeveSerNaoNegativaException extends RuntimeException {
    public ComissaoDeveSerNaoNegativaException() {
        super("Comissao deve ser nao-negativa.");
    }
}
