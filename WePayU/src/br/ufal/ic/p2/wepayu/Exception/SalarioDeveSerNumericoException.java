package br.ufal.ic.p2.wepayu.Exception;

public class SalarioDeveSerNumericoException extends RuntimeException {
    public SalarioDeveSerNumericoException() {
        super("Salario deve ser numerico.");
    }
}
