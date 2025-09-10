package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.time.LocalDate;

/**
 * Classe responsavel por tratar todas as operacoes que envolvam o sindicato
 */
public class Sindicato {
    private String id;
    private String id_sindicato;
    private String taxa_sindical;
    private String data;
    private String valor;

    private static String arquivoSind = "sindicato.xml";
    public static List<Sindicato> membros = new ArrayList<Sindicato>();
    private static boolean acessado = false;

    /**
     * Construtor necessario para o uso de XMLEncoder e XMLDecoder
     */
    public Sindicato(){

    }

    /**
     * Construtor responsavel por criar um novo objeto da classe Sindicato
     */
    public Sindicato(String id, String id_sindicato, String taxa_sindical, String data, String valor){
        this.id = id;
        this.id_sindicato = id_sindicato;
        this.taxa_sindical = taxa_sindical;
        this.data = data;
        this.valor = valor;
    }

    /**
     * Metodo responsavel por buscar um empregado no sindicato, podendo utilizar
     * o id do empregado na empresa ou o id do empregado no sindicato
     */
    public static Sindicato buscar(String id, int caminho){

        if (notIsAcessado()) abrir();

        if (caminho == 1) {
            for (Sindicato s : membros) {
                if (s.getId_sindicato().equals(id)) return s;
            }
        }else if (caminho == 2){
            for (Sindicato s : membros){
                if (s.getId().equals(id)) return s;
            }
        }

        return null;
    }

    /**
     * Metodo responsavel por remover um empregado do sindicato
     */
    public static void remover(String id){
        for (Sindicato s : membros){
            if (s.getId().equals(id)) membros.remove(s);
        }
    }

    /**
     * Metodo responsavel por colocar um novo empregado no sindicato
     */
    public static void colocarSindicato(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws IdentificacaoDoEmpregadoNaoPodeSerNulaException {

        if (emp.isEmpty() || idSindicato.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (atributo.isEmpty()) throw new AtributoNaoPodeSerNuloException();
        if (valor.isEmpty()) throw new ValorDeveSerNaoNuloException();
        if (taxaSindical.isEmpty()) throw new TaxaSindicatoDeveSerNaoNulaException();

        try{
            double aux = Double.parseDouble(taxaSindical.replace(",", "."));
            if (aux <= 0) throw new TaxaSindicalDeveSerPositivaException();
        }catch(NumberFormatException e){
            throw new TaxaSindicalDeveSerNumericaException();
        }

        if (!atributo.equals("sindicalizado")) throw new AtributoInvalidoException();

        if (notIsAcessado()) abrir();

        for (Sindicato s : membros){
            if (s.getId_sindicato().equals(idSindicato)){
                throw new HaOutroEmpregadoComEstaIdentificacaoDeSindicatoException();
            }
        }

        Empregado atual = Gerente.buscar(emp);
        if (atual == null) throw new EmpregadoNaoExisteException();

        if (valor.equals("true")){
            atual.setSindicalizado(true);
            Sindicato novo = new Sindicato(emp, idSindicato,taxaSindical, "0", "0");
            membros.add(novo);
        }else{
            atual.setSindicalizado(false);
            for (Sindicato s : membros){
                if(s.getId_sindicato().equals(idSindicato)){
                    membros.remove(s);
                    break;
                }
            }
        }
    }

    /**
     * Metodo responsavel por alterar atributos gerais do empregado, atualmente
     * apenas retirar alguém do sindicato
     */
    public static void alterar(String emp, String atributo, String valor)throws IdentificacaoDoMembroNaoPodeSerNulaException{

        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (atributo.isEmpty()) throw new AtributoNaoPodeSerNuloException();
        if (valor.isEmpty()) throw new ValorDeveSerNaoNuloException();

        Empregado atual = Gerente.buscar(emp);
        if (atual == null) throw new EmpregadoNaoExisteException();

        if (atributo.equals("sindicalizado")){
            atual.setSindicalizado(valor.equals("true"));
            Sindicato remover = Sindicato.buscar(emp, 2);
            if(remover == null) throw new EmpregadoNaoExisteException();
            remover(remover.id);
        }
    }

    /**
     * Metodo responsavel por lancar a taxa de servico de um empregado em uma data
     */
    public static void lancaTaxaServico(String emp, String data, String valor) throws IdentificacaoDoMembroNaoPodeSerNulaException{
        if (emp.isEmpty()) throw new IdentificacaoDoMembroNaoPodeSerNulaException();
        if (data.isEmpty()) throw new DataInvalidaException();
        if (valor.isEmpty()) throw new ValorDeveSerNaoNuloException();

        try{
            double aux = Double.parseDouble(valor.replace(",", "."));
            if (aux <= 0) throw new ValorDeveSerPositivoException();
        }catch(NumberFormatException e){
            throw new ValorDeveSerNumericoException();
        }

        try{
            LocalDate aux = Horarios.arrumar(data);
        }catch(DataInvalidaException e){
            throw new DataInvalidaException();
        }

        Sindicato atual = Sindicato.buscar(emp, 1);
        if (atual == null) throw new MembroNaoExisteException();
        Sindicato novo = new Sindicato(atual.getId(), atual.getId_sindicato(), atual.getTaxa_sindical(), data, valor);
        membros.add(novo);
    }

    /**
     * Metodo responsavel por calcular o total de taxas de um empregado em um intervalor de datas
     */
    public static String getTaxas(String emp, String dataInicial, String dataFinal) throws IdentificacaoDoMembroNaoPodeSerNulaException{

        if (emp.isEmpty()) throw new IdentificacaoDoMembroNaoPodeSerNulaException();
        if (dataInicial.isEmpty() || dataFinal.isEmpty()) throw new DataInvalidaException();

        LocalDate inicio, fim;

        try{
            inicio = Horarios.arrumar(dataInicial);
        }catch(DataInvalidaException e){
            throw new DataInicialInvalidaException();
        }

        try{
            fim = Horarios.arrumar(dataFinal);
        }catch(DataInvalidaException e){
            throw new DataFinalInvalidaException();
        }

        if (inicio.isAfter(fim)) throw new DataInicialNaoPodeSerPosteriorADataFinalException();

        double total = 0;

        Sindicato atual = Sindicato.buscar(emp, 2);
        if (atual == null) throw new EmpregadoNaoEhSindicalizadoException();

        if(notIsAcessado()) abrir();

        //melhoraria a eficiencia caso "membros" estivesse ordenado
        for (Sindicato s : membros){
            if (s.getId().equals(atual.getId())){
                if (s.data.equals("0")) continue; //necessario para pular o objeto referente a existencia de um empregado no sindicato
                LocalDate dataAtual = Horarios.arrumar(s.data);
                if (dataAtual.isBefore(inicio) || dataAtual.isAfter(fim) || dataAtual.equals(fim)) continue;
                else{
                    total += Double.parseDouble(s.valor.replace(",", "."));
                }
            }
        }
        return String.format("%,.2f", total);
    }

    /**
     * Metodo responsavel por abrir o arquivo XML, para poder acessar os dados
     */
    public static void abrir(){
        try(FileInputStream input = new FileInputStream(arquivoSind);
            XMLDecoder leitor = new XMLDecoder(input)){
            membros = (List<Sindicato>) leitor.readObject();
            setAcessado(true);
        }catch(IOException | IndexOutOfBoundsException e){
            if (!(e instanceof  IndexOutOfBoundsException)){
                System.out.println("Erro ao abrir o arquivo: " + ((IOException) e).getMessage());
            }
        }
    }

    /**
     * Metodo responsavel por limpar os dados do arquivo XML
     */
    public static void limpar(){
        if (!membros.isEmpty()){
            membros.clear();
        }
        //limpar o arquivo XML
        try(FileOutputStream output = new FileOutputStream(arquivoSind);
            XMLEncoder escritor = new XMLEncoder(output)){

        }catch(IOException e){
            System.err.println("Erro ao limpar o arquivo: " + e.getMessage());
        }
    }

    /**
     * Metodo responsavel por salvar os dados no arquivo XML
     */
    public static void salvar(){
        try(FileOutputStream output = new FileOutputStream(arquivoSind);
            XMLEncoder escritor = new XMLEncoder(output)){
            escritor.writeObject(membros);

        }catch(IOException e){
            System.out.println("Erro ao abrir o arquivo:" + e.getMessage());
        }
    }

    public static boolean notIsAcessado(){
        return !acessado;
    }

    public static void setAcessado(boolean acessado){
        Sindicato.acessado = acessado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_sindicato() {
        return id_sindicato;
    }

    public void setId_sindicato(String id_sindicato) {
        this.id_sindicato = id_sindicato;
    }

    public String getTaxa_sindical() {
        return taxa_sindical;
    }

    public void setTaxa_sindical(String taxa_sindical) {
        this.taxa_sindical = taxa_sindical;
    }

    public String getData(){
        return data;
    }

    public void setData(String data){
        this.data = data;
    }

    public String getValor(){
        return valor;
    }

    public void setValor(String valor){
        this.valor = valor;
    }

    @Override
    public String toString(){
        return String.format("id -> " + this.id + "\n" +
                "id sindicato -> " + this.id_sindicato + "\n" +
                "taxa -> " + this.taxa_sindical + "\n" +
                "data -> " + this.data + "\n" +
                "valor -> " + this.valor);
    }
}
