package br.ufal.ic.p2.wepayu.Exception;

public class ValorDeveSerNumericoException extends RuntimeException {
    public ValorDeveSerNumericoException() {
        super("Valor deve ser numerico.");
    }
}
