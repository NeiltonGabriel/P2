package br.ufal.ic.p2.wepayu.Exception;

public class ComissaoDeveSerNumericaException extends RuntimeException {
    public ComissaoDeveSerNumericaException() {
        super("Comissao deve ser numerica.");
    }
}
