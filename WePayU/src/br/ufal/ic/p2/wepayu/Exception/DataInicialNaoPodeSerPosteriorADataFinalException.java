package br.ufal.ic.p2.wepayu.Exception;

public class DataInicialNaoPodeSerPosteriorADataFinalException extends RuntimeException {
    public DataInicialNaoPodeSerPosteriorADataFinalException() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
