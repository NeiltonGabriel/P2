package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Empregado {
    private String nome;
    private String endereco;
    private String tipo;
    private String salario;
    private String id;
    private boolean sindicalizado;
    private String metodo;
    private String banco;
    private String agencia;
    private String contacorrente;
    private String agendaPagamento;
    private transient LocalDate ultimoPagamento;
    private transient LocalDate dataContratacao;
    private String ultimoPagamentoStr;
    private String dataContratacaoStr;
    private static final transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Empregado() {
        // construtor vazio para XML
    }

    public Empregado(String nome, String endereco, String tipo, String salario, String id, String agendaPagamento) throws EmpregadoNaoExisteException {
        if (nome.isEmpty()) throw new NomeNaoPodeSerNuloException();
        if (endereco.isEmpty()) throw new EnderecoNaoPodeSerNuloException();
        if (tipo.isEmpty()) throw new TipoNaoPodeSerNuloException();
        if (salario.isEmpty()) throw new SalarioNaoPodeSerNuloException();
        try {
            double aux = Double.parseDouble(salario.replace(",", "."));
            if (aux < 0) throw new SalarioDeveSerNaoNegativoException();
        } catch (NumberFormatException e) {
            throw new SalarioDeveSerNumericoException();
        }

        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario.replace(".", ",");
        this.id = id;
        this.metodo = "emMaos";
        this.banco = "-1";
        this.agencia = "-1";
        this.contacorrente = "-1";
        this.agendaPagamento = agendaPagamento;
    }

    public Empregado(Empregado outro) {
        this.nome = outro.nome;
        this.endereco = outro.endereco;
        this.tipo = outro.tipo;
        this.salario = outro.salario;
        this.id = outro.id;
        this.sindicalizado = outro.sindicalizado;
        this.metodo = outro.metodo;
        this.banco = outro.banco;
        this.agencia = outro.agencia;
        this.contacorrente = outro.contacorrente;
        this.agendaPagamento = outro.agendaPagamento;
        this.ultimoPagamento = outro.ultimoPagamento;
        this.dataContratacao = outro.dataContratacao;
        this.ultimoPagamentoStr = outro.ultimoPagamentoStr;
        this.dataContratacaoStr = outro.dataContratacaoStr;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getSalario() {
        if (salario == null) return "";
        if (salario.contains(",")) return salario;
        return String.format("%s,00", salario);
    }
    public void setSalario(String salario) { this.salario = salario; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public boolean isSindicalizado() { return sindicalizado; }
    public void setSindicalizado(boolean sindicalizado) { this.sindicalizado = sindicalizado; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    public String getContacorrente() { return contacorrente; }
    public void setContacorrente(String contacorrente) { this.contacorrente = contacorrente; }
    public String getAgendaPagamento() { return agendaPagamento; }
    public void setAgendaPagamento(String agendaPagamento) { this.agendaPagamento = agendaPagamento; }
    public LocalDate retrieveUltimoPagamento() {
        if (ultimoPagamento == null && ultimoPagamentoStr != null) {
            ultimoPagamento = LocalDate.parse(ultimoPagamentoStr, formatter);
        }
        return ultimoPagamento;
    }

    public void setUltimoPagamento(LocalDate ultimoPagamento) {
        this.ultimoPagamento = ultimoPagamento;
        if (ultimoPagamento != null) {
            this.ultimoPagamentoStr = ultimoPagamento.format(formatter);
        } else {
            this.ultimoPagamentoStr = null;
        }
    }

    public String getUltimoPagamentoStr() {
        return ultimoPagamentoStr;
    }

    public void setUltimoPagamentoStr(String ultimoPagamentoStr) {
        this.ultimoPagamentoStr = ultimoPagamentoStr;
    }

    public LocalDate retrieveDataContratacao() {
        if (dataContratacao == null && dataContratacaoStr != null) {
            dataContratacao = LocalDate.parse(dataContratacaoStr, formatter);
        }
        return dataContratacao;
    }

    public void setDataContratacao(LocalDate dataContratacao) {
        this.dataContratacao = dataContratacao;
        if (dataContratacao != null) {
            this.dataContratacaoStr = dataContratacao.format(formatter);
        } else {
            this.dataContratacaoStr = null;
        }
    }

    public String getDataContratacaoStr() {
        return dataContratacaoStr;
    }

    public void setDataContratacaoStr(String dataContratacaoStr) {
        this.dataContratacaoStr = dataContratacaoStr;
    }

    @Override
    public String toString() {
        return String.format("ID: %s\nNome: %s\nSalário: %s\nTipo: %s\nEndereço: %s\n",
                this.id, this.nome, this.getSalario(), this.tipo, this.endereco);
    }
}