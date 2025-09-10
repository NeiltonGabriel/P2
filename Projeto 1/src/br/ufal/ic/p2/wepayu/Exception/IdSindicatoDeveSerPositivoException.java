package br.ufal.ic.p2.wepayu.Exception;

public class IdSindicatoDeveSerPositivoException extends RuntimeException {
    public IdSindicatoDeveSerPositivoException() {
        super("IdSindicato deve ser positivo.");
    }
}
