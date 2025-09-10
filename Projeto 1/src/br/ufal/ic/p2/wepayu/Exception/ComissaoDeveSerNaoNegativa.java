package br.ufal.ic.p2.wepayu.Exception;

public class ComissaoDeveSerNaoNegativa extends RuntimeException {
    public ComissaoDeveSerNaoNegativa() {
        super("Comissao deve ser nao-negativa.");
    }
}
