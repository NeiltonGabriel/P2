package br.ufal.ic.p2.wepayu.Exception;

public class NaoPodeDarComandosDepoisDeEncerrarSistemaException extends RuntimeException {
    public NaoPodeDarComandosDepoisDeEncerrarSistemaException() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}
