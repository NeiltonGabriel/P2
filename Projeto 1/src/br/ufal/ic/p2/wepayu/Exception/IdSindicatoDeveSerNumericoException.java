package br.ufal.ic.p2.wepayu.Exception;

public class IdSindicatoDeveSerNumericoException extends RuntimeException {
    public IdSindicatoDeveSerNumericoException() {
        super("IdSindicato deve ser numerico.");
    }
}
