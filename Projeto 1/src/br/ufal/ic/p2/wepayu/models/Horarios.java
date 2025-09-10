package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Classe responsavel por tratar as operacoes relacionadas as horas
 */
public class Horarios {

    private String dia;
    private String id;
    private String horas;

    private static String arquivohoras = "horas.xml";
    private static boolean acessado = false;

    private static DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static List<Horarios> cartoes = new ArrayList<>();

    /**
     * Construtor necessario para usar o XMLEncoder e XMLDecoder
     */
    public Horarios(){

    }

    /**
     * Construtor responsavel por criar um objeto da classe Horarios
     */
    public Horarios(String id, String dia, String horas){
        this.id = id;
        this.dia = dia;
        this.horas = horas;
    }

    /**
     * Metodo responsavel por lancar um cartao de horas de um empregado em uma data
     */
    public static void lancaCartao(String emp, String data, String hora) throws EmpregadoNaoExisteException{

        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        Empregado atual = Gerente.buscar(emp);
        if (atual == null) throw new EmpregadoNaoExisteException();

        if (!(atual instanceof Horista)) throw new EmpregadoNaoEhHoristaException();

        LocalDate datacorreta;
        try{
            datacorreta = LocalDate.parse(data, formatador);
        }catch(DateTimeParseException e){

            try{
                datacorreta = arrumar(data);
            }catch(DataInvalidaException t){
                throw new DataInvalidaException();
            }
        }

        double horascorretas;
        try{
            horascorretas = Double.parseDouble(hora.replace(",", "."));
            if (horascorretas <= 0) throw new HorasDevemSerPositivasException();
        }catch (NumberFormatException e){
            throw new HorasDevemSerNumericaException();
        }

        String[] partes = datacorreta.toString().split("-");
        String certa = partes[2].concat("/" + partes[1] + "/" + partes[0]);
        Horarios novo = new Horarios(emp, certa, Double.toString(horascorretas));
        cartoes.add(novo);
    }

    /**
     * Metodo responsavel por calcular o total de horas normais trabalhadas por um empregado
     * em um intervalo de datas
     */
    public static String horasNormais(String id, String dataInicial, String dataFinal) throws EmpregadoNaoEhHoristaException{

        if (notIsAcessado()) abrir();
        if (id.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();


        LocalDate inicio, fim;

        try {
            inicio = LocalDate.parse(dataInicial, formatador);
        }catch(DateTimeParseException e){

            try{
                inicio = arrumar(dataInicial);
            }catch(DataInvalidaException t){
                throw new DataInicialInvalidaException();
            }
        }

        try {
            fim = LocalDate.parse(dataFinal, formatador);
        }catch(DateTimeParseException e){
            try{
                fim = arrumar(dataFinal);
            }catch (DataInvalidaException t){
                throw new DataFinalInvalidaException();
            }
        }

        if (fim.isBefore(inicio)) throw new DataInicialNaoPodeSerPosteriorADataFinalException();

        if (cartoes.isEmpty()) return "0";

        double qtd = 0;

        Empregado atual = Gerente.buscar(id); //Para ver se existe tal empregado
        if (atual == null) throw new EmpregadoNaoExisteException();
        if (!(atual instanceof Horista)) throw new EmpregadoNaoEhHoristaException();

        double aux;
        for (Horarios h : cartoes){
            if (h.id.equals(id)){
                LocalDate dia_aux = LocalDate.parse(h.dia, formatador);
                //ineficiente, mas dá muito trabalho ordenar
                if (dia_aux.isEqual(fim) || dia_aux.isAfter((fim)) || dia_aux.isBefore(inicio)) continue;
                else{
                    aux = Double.parseDouble(h.horas.replace(",", "."));
                    qtd += (aux > 8) ? 8 : aux;
                }
            }
        }

        String volta = Double.toString(qtd);
        return (volta.contains(",")) ? volta.replace(",", ".") : String.format("%.0f", Double.parseDouble(volta));
    }

    /**
     * Metodo responsavel por calcular o total de horas extras trabalhadas por um empregado
     * em um intervalor de datas
     */
    public static String HorasExtras(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoEhHoristaException{

        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (cartoes.isEmpty()) return "0";

        LocalDate inicio, fim;

        try{
            inicio = LocalDate.parse(dataInicial, formatador);
        }catch(DateTimeParseException e){

            try{
                inicio = arrumar(dataInicial);
            }catch (DataInvalidaException t){
                throw new DataInicialInvalidaException();
            }
        }

        try{
            fim = LocalDate.parse(dataFinal, formatador);
        }catch (DateTimeParseException e){

            try{
                fim = arrumar(dataFinal);
            }catch (DataInvalidaException t){
                throw new DataFinalInvalidaException();
            }
        }

        if (fim.isBefore(inicio)) throw new DataInicialNaoPodeSerPosteriorADataFinalException();

        double qtd = 0;

        Empregado atual = Gerente.buscar(emp);
        if (atual == null) throw new EmpregadoNaoExisteException();
        if (!(atual instanceof Horista)) throw new EmpregadoNaoEhHoristaException();

        double aux;
        for (Horarios h : cartoes){
            if (h.id.equals(emp)){
                LocalDate dia_aux = LocalDate.parse(h.dia, formatador);
                //ineficiente, mas dá muito trabalho ordenar
                if (dia_aux.equals(fim) || dia_aux.isAfter(fim) || dia_aux.isBefore(inicio)) continue;
                else{
                    aux = Double.parseDouble(h.horas.replace(",", "."));
                    qtd += (aux > 8) ? aux - 8 : 0;
                }
            }
        }

        String volta = Double.toString(qtd);
        int parteinteira = (int)qtd;
        if (volta.contains(".")) volta = volta.replace(".", ",");
        return (qtd > parteinteira) ? volta : String.format("%d", (int)qtd);
    }

    /**
     * Metodo responsavel por verificar se uma data eh valida e deixa-la na forma dd/MM/yyyy
     */
    public static LocalDate arrumar(String data) throws DataInvalidaException{

        LocalDate datacorreta;
        String[] partes = data.split("/");
        int ano = Integer.parseInt(partes[2]);
        boolean bissexto = ano % 400 == 0 || (ano % 4 == 0 && ano % 100 != 0);

        if (Integer.parseInt(partes[1]) == 2){
            if (bissexto && Integer.parseInt(partes[0]) > 29) throw new DataInvalidaException();
            else if (Integer.parseInt(partes[0]) > 28) throw new DataInvalidaException();
        }

        if (partes[0].length() == 1 || partes[1].length() == 1){

            for (int i = 0; i < 2; i++){
                if (partes[i].length() == 1) partes[i] = "0".concat(partes[i]);
            }

            String novadata = partes[0].concat("/" + partes[1] + "/" + partes[2]);

            try{
                datacorreta = LocalDate.parse(novadata, formatador);
            }catch(DateTimeParseException t){
                throw new DataInvalidaException();
            }
        }
        else if (partes[0].length() == 2 && partes[1].length() == 2){
            try{
                datacorreta = LocalDate.parse(data, formatador);
            }catch(DateTimeParseException t){
                throw new DataInvalidaException();
            }
        }
        else throw new DataInvalidaException();

        return datacorreta;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getInicio(){
        return dia;
    }

    public void setInicio(String inicio){
        this.dia = inicio;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public static boolean notIsAcessado(){
        return !acessado;
    }

    public static void setAcessado(boolean valor){
        acessado = valor;
    }

    /**
     * Metodo responsavel por abrir o arquivo XML, para poder acessar os dados
     */
    public static void abrir(){

        try(FileInputStream input = new FileInputStream(arquivohoras);
            XMLDecoder leitor = new XMLDecoder(input)){
            cartoes = (List<Horarios>) leitor.readObject();
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
        try(FileOutputStream output = new FileOutputStream(arquivohoras);
            XMLEncoder escritor = new XMLEncoder(output)){
            escritor.writeObject(cartoes);

        }catch(IOException e){
            System.out.println("Erro ao abrir o arquivo:" + e.getMessage());
        }
    }

    /**
     * Metodo responsavel por limpar os dados do arquivo XML
     */
    public static void limpar(){
        if (!cartoes.isEmpty()){
            cartoes.clear();
        }
        //limpar o arquivo XML
        try(FileOutputStream output = new FileOutputStream(arquivohoras);
            XMLEncoder escritor = new XMLEncoder(output)){

        }catch(IOException e){
            System.err.println("Erro ao limpar o arquivo: " + e.getMessage());
        }
    }
}
