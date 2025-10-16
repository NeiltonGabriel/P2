package br.ufal.ic.p2.wepayu.Exception;

public class IdentificacaoDoEmpregadoDeveSerPositivaException extends RuntimeException {
    public IdentificacaoDoEmpregadoDeveSerPositivaException() {
        super("Identificacao do empregado deve ser positiva.");
    }
}
