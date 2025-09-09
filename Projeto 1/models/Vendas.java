package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class Vendas {
    private String id;
    private String dia;
    private String valor;

    private static String arquivovendas = "vendas.xml";
    private static boolean acessado = false;

    private static List<Vendas> vendas = new ArrayList<>();
    //private static DateTimeFormater formatador = new DateTimeFormater("dd/MM)yyyy");
    //talvez seja desnecessário, basta usar o que tem em Horarios.java

    public Vendas(){

    }

    public Vendas(String id, String data, String valor) throws DataInvalidaException{
        //Não é necessário já que usa o "id" de criarEmpregado
        if (id.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (data.isEmpty()) throw new DataInvalidaException();
        if (valor.isEmpty()) throw new ValorDeveSerNaoNuloException();

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

    public static void lancar(String emp, String data, String valor) throws DataInvalidaException{
        Vendas novo = new Vendas(emp, data, valor);
        vendas.add(novo);
    }

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

    public static void salvar(){
        try(FileOutputStream output = new FileOutputStream(arquivovendas);
            XMLEncoder escritor = new XMLEncoder(output)){
            escritor.writeObject(vendas);

        }catch(IOException e){
            System.out.println("Erro ao abrir o arquivo:" + e.getMessage());
        }
    }

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
