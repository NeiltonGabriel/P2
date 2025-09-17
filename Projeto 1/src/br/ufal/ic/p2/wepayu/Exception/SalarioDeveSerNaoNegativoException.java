package br.ufal.ic.p2.wepayu.Exception;

public class SalarioDeveSerNaoNegativoException extends RuntimeException {
    public SalarioDeveSerNaoNegativoException() {
        super("Salario deve ser nao-negativo.");
    }
}
