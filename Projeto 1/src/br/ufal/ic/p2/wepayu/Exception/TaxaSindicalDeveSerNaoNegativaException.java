package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSindicalDeveSerNaoNegativaException extends RuntimeException {
    public TaxaSindicalDeveSerNaoNegativaException() {
        super("Taxa sindical deve ser nao-negativa.");
    }
}
