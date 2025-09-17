package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsavel por manter os dados do empregado
 */
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



    /**
     * Construtor necessario para usar o XMLEncoder e XMLDecoder
     */
    public Empregado(){
        //Necessário para o XML
    }

    /**
     * Construtor responsavel por criar um objeto da classe Empregado
     */
    public Empregado(String nome, String endereco, String tipo, String salario, String id) throws EmpregadoNaoExisteException{

        if (nome.isEmpty()) throw new NomeNaoPodeSerNuloException();
        if (endereco.isEmpty()) throw new EnderecoNaoPodeSerNuloException();
        if (tipo.isEmpty()) throw new TipoNaoPodeSerNuloException();
        if (salario.isEmpty()) throw new SalarioNaoPodeSerNuloException();

        try{
            double aux = Double.parseDouble(salario.replace(",", "."));
            if (aux < 0) throw new SalarioDeveSerNaoNegativoException();

        }catch(NumberFormatException e){
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
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSalario() {
        if (salario == null) return "";
        else if (salario.contains(",")) return salario;
        else return String.format("%d,00", Integer.parseInt(salario));
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSindicalizado() {
        return sindicalizado;
    }

    public void setSindicalizado(boolean sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getContacorrente() {
        return contacorrente;
    }

    public void setContacorrente(String contacorrente) {
        this.contacorrente = contacorrente;
    }

    @Override
    public String toString(){
        return String.format(this.getId() + "\nNome: " + this.getNome() + "\nSalário: " + this.getSalario()
                + "\nTipo: " + this.getTipo() + "\nEndereço: " + this.getEndereco() + "\n");
    }
}
