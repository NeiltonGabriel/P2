package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSindicatoNaoPodeSerNulaException extends RuntimeException {
    public TaxaSindicatoNaoPodeSerNulaException() {
        super("Taxa sindical nao pode ser nula.");
    }
}
