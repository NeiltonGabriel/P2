package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoNaoRecebeEmBancoException extends RuntimeException {
    public EmpregadoNaoRecebeEmBancoException() {
        super("Empregado nao recebe em banco.");
    }
}
