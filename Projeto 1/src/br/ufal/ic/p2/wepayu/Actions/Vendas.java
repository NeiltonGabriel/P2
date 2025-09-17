package br.ufal.ic.p2.wepayu.Actions;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Classe responsavel por tratar de operacoes relacionadas a vendas
 */
public class Vendas {
    private String id;
    private String dia;
    private String valor;

    private static String arquivovendas = "vendas.xml";
    private static boolean acessado = false;

    private static List<Vendas> vendas = new ArrayList<>();

    /**
     * Construtor necessario para o uso de XMLEncoder e XMLDecoder
     */
    public Vendas(){

    }

    /**
     * Construtor responsavel por criar um objeto da classe Vendas
     */
    public Vendas(String id, String data, String valor) throws DataInvalidaException{
        //Não é necessário já que usa o "id" de criarEmpregado
        if (id.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (data.isEmpty()) throw new DataInvalidaException();
        if (valor.isEmpty()) throw new ValorDeveSerNaoNuloException("Valor");

        try{
            double aux = Double.parseDouble(valor.replace(",", "."));
            if (aux <= 0) throw new ValorDeveSerPositivoException();
        }catch(NumberFormatException e){
            throw new ValorDeveSerNumericoException();
        }

        try{
            LocalDate analise = Horarios.arrumar(data);
        }catch(DataInvalidaException e){
            throw new DataInvalidaException();
        }

        Empregado atual = Gerente.buscar(id);
        if (atual == null) throw new EmpregadoNaoExisteException();
        if (!(atual instanceof Comissionado)) throw new EmpregadoNaoEhComissionadoException();

        this.id = id;
        this.dia = data;
        this.valor = valor;
    }

    /**
     * Metodo responsavel por lancar uma nova venda feita por um funcionario no sistema,
     * ao adicionar ela a lista de vendas
     */
    public static void lancar(String emp, String data, String valor) throws DataInvalidaException{
        Vendas novo = new Vendas(emp, data, valor);
        vendas.add(novo);
    }

    /**
     * Metodo responsavel por calcular o valor total das vendas feitas por um funcionario em um intervalo de datas
     */
    public static String vendasFeitas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoEhComissionadoException{

        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (dataInicial.isEmpty()) throw new DataInicialInvalidaException();
        if (dataFinal.isEmpty()) throw new DataFinalInvalidaException();

        Empregado atual = Gerente.buscar(emp);
        if (atual == null) throw new EmpregadoNaoExisteException();
        if (!(atual instanceof Comissionado)) throw new EmpregadoNaoEhComissionadoException();

        LocalDate inicio, fim;

        try{
            inicio = Horarios.arrumar(dataInicial);
        }catch (DataInvalidaException e){
            throw new DataInicialInvalidaException();
        }

        try{
            fim = Horarios.arrumar(dataFinal);
        }catch (DataInvalidaException e){
            throw new DataFinalInvalidaException();
        }

        if (fim.isBefore(inicio)) throw new DataInicialNaoPodeSerPosteriorADataFinalException();

        if (vendas.isEmpty()) return "0,00";

        if (notIsAcessado()) abrir();

        double total = 0;

        for (Vendas v : vendas){
            if(emp.equals(v.id)){
                LocalDate dia_aux = Horarios.arrumar(v.dia);
                if (dia_aux.equals(fim) || dia_aux.isAfter(fim) || dia_aux.isBefore(inicio)) continue;
                else{
                     total += Double.parseDouble(v.valor.replace(",", "."));
                }
            }
        }

        return String.format("%.2f", total);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public static boolean notIsAcessado() {
        return !acessado;
    }

    public static void setAcessado(boolean acessado) {
        Vendas.acessado = acessado;
    }

    /**
     * Metodo responsavel por abrir o arquivo XML, para poder acessar os dados
     */
    public static void abrir(){
        try(FileInputStream input = new FileInputStream(arquivovendas);
            XMLDecoder leitor = new XMLDecoder(input)){
            vendas = (List<Vendas>) leitor.readObject();
            setAcessado(true);
        }catch(IOException | IndexOutOfBoundsException e){
            if (!(e instanceof  IndexOutOfBoundsException)){
                System.out.println("Erro ao abrir o arquivo: " + ((IOException) e).getMessage());
            }
        }
    }

    /**
     * Metodo responsavel por salvar os dados no arquivo XML
     */
    public static void salvar(){
        try(FileOutputStream output = new FileOutputStream(arquivovendas);
            XMLEncoder escritor = new XMLEncoder(output)){
            escritor.writeObject(vendas);

        }catch(IOException e){
            System.out.println("Erro ao abrir o arquivo:" + e.getMessage());
        }
    }

    /**
     * Metodo responsavel por limpar os dados do arquivo XML
     */
    public static void limpar(){
        if (!vendas.isEmpty()){
            vendas.clear();
        }
        //limpar o arquivo XML
        try(FileOutputStream output = new FileOutputStream(arquivovendas);
            XMLEncoder escritor = new XMLEncoder(output)){

        }catch(IOException e){
            System.err.println("Erro ao limpar o arquivo: " + e.getMessage());
        }
    }
}
