package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSindicatoDeveSerNaoNulaException extends RuntimeException {
    public TaxaSindicatoDeveSerNaoNulaException() {
        super("Taxa sindicato deve ser nao nula.");
    }
}
