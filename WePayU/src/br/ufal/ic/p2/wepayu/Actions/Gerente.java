package br.ufal.ic.p2.wepayu.Actions;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Horista;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe responsavel por tratar de operacoes com os empregados
 */
public class Gerente {

    private static String arquivoDados = "dados.xml";
    private static String arquivoAgendas = "agendas.xml";
    public static List<Empregado> trabalhadores = new ArrayList<>();
    public static List<String> agendas = new ArrayList<>(Arrays.asList("mensal $", "semanal 5", "semanal 2 5"));
    private static boolean acessado = false;

    /**
     * Metodos responsavel por buscar um empregado na lista de empregados
     */
    public static Empregado buscar(String chave){
        for (Empregado a : trabalhadores){
            if (chave.equals(a.getId())){
                return a;
            }
        }
        return null;
    }

    /**
     * Metodo responsavel por adicionar um novo empregado a lista de empregados
     */
    public static void adicionarEmpregado(Empregado novo){
        trabalhadores.add(novo);
    }

    /**
     * Metodo responsavel por decidir se sera criado um horista ou assalariado
     */
    public static Empregado direcionar(String nome, String endereco, String tipo, String salario, String id){

        switch (tipo) {
            case "assalariado": {
                try {
                    return new Assalariado(nome, endereco, tipo, salario, id);
                }catch(EmpregadoNaoExisteException e){
                    // Nao deve acontecer
                    return null;
                }
            }
            case "horista": {
                try{
                    return new Horista(nome, endereco, tipo, salario, id);
                }catch (EmpregadoNaoExisteException e){
                    // Nao deve acontecer
                    return null;
                }
            }
            case "comissionado": throw new TipoNaoAplicavelException();
            default: throw new TipoInvalidoException();
        }
    }

    /**
     * Metodo responsavel por decidir se sera criado um comissionado
     */
    public static Empregado direcionar(String nome, String endereco, String tipo, String salario, String id, String comissao){
        if (!tipo.equals("comissionado")) throw new TipoNaoAplicavelException();

        try{
            if (comissao.isEmpty()) throw new ComissaoNaoPodeSerNulaException();
            double aux = Double.parseDouble(comissao.replace(",", "."));
            if (aux < 0) throw new ComissaoDeveSerNaoNegativaException();

            return new Comissionado(nome, endereco, tipo, salario, id, comissao);

        }catch(EmpregadoNaoExisteException | NumberFormatException e){
            if (e instanceof EmpregadoNaoExisteException) {
                System.out.println(e.getMessage());
                return null;
            }
            else throw new ComissaoDeveSerNumericaException();
        }
    }

    /**
     * Metodo responsavel por pegar um atributo de um empregado
     */
    public static String atributo(String emp, String atributo) throws EmpregadoNaoExisteException{

        if (!isAcessado()) abrir();
        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (trabalhadores.isEmpty()) throw new EmpregadoNaoExisteException();

        Empregado atual = buscar(emp);
        if (atual == null) throw new EmpregadoNaoExisteException();

        if (atributo.equals("nome")) return atual.getNome();
        else if (atributo.equals("endereco")) return atual.getEndereco();
        else if (atributo.equals("tipo")) return atual.getTipo();
        else if (atributo.equals("salario")) return atual.getSalario();
        else if (atributo.equals("sindicalizado")) return (atual.isSindicalizado()) ? "true" : "false";
        else if (atributo.equals("comissao")){
            if (!(atual instanceof Comissionado)) throw new EmpregadoNaoEhComissionadoException();
            Comissionado aux = (Comissionado) atual;
            return aux.getComissao();
        }
        else if (atributo.equals("metodoPagamento")) return atual.getMetodo();
        else if (atributo.equals("banco")) {
            String banco = atual.getBanco();
            if (banco.equals("-1")) throw new EmpregadoNaoRecebeEmBancoException();
            return banco;
        }
        else if (atributo.equals("agencia")){
            String agencia = atual.getAgencia();
            if (agencia.equals("-1")) throw new EmpregadoNaoRecebeEmBancoException();
            return agencia;
        }
        else if (atributo.equals("contaCorrente")){
            String conta = atual.getContacorrente();
            if (conta.equals("-1")) throw new EmpregadoNaoRecebeEmBancoException();
            return conta;
        }
        else if (atributo.equals("idSindicato")){
            Sindicato s = Sindicato.buscar(atual.getId(), 2);
            if (s == null) throw new EmpregadoNaoEhSindicalizadoException();
            return s.getId_sindicato();
        }
        else if (atributo.equals("taxaSindical")){
            Sindicato s = Sindicato.buscar(atual.getId(), 2);
            if (s == null) throw new EmpregadoNaoEhSindicalizadoException();
            String volta = s.getTaxa_sindical();
            if (!volta.contains(",")){
                volta = volta.concat(",00");
            }
            return volta;
        }
        else if (atributo.equals("agendaPagamento")) return atual.getAgendaPagamento();
        else throw new AtributoNaoExisteException();
    }

    /**
     * Metodo responsavel por pegar o id de um empregado baseado no nome do mesmo
     */
    public static String empregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException{

        if (!isAcessado()) abrir();
        if (nome.isEmpty()) throw new NomeNaoPodeSerNuloException();
        if (indice <= 0) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();

        int i = 1;

        for (Empregado a : trabalhadores){
            if (nome.equals(a.getNome())){
                if (indice == i) return a.getId();
                else i++;
            }
        }

        throw new NaoHaEmpregadoComEsseNomeException();
    }

    /**
     * Metodo responsavel por remover um empregado da lista de empregados
     */
    public static void remover(String emp) throws EmpregadoNaoExisteException{

        if (!isAcessado()) abrir();
        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (trabalhadores.isEmpty()) throw new EmpregadoNaoExisteException();

        Empregado demitido = buscar(emp);

        if (demitido == null) throw new EmpregadoNaoExisteException();
        trabalhadores.remove(demitido);
    }

    /**
     * Metodo responsavel por alterar atributos gerais do empregado, atualmente
     */
    public static void alterar(String emp, String atributo, String valor)throws IdentificacaoDoMembroNaoPodeSerNulaException{
        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (atributo.isEmpty()) throw new AtributoNaoPodeSerNuloException();
        if (valor.isEmpty()){
            if (atributo.equals("nome")) throw new NomeNaoPodeSerNuloException();
            else if (atributo.equals("endereco")) throw new EnderecoNaoPodeSerNuloException();
            else if (atributo.equals("tipo")) throw new TipoNaoPodeSerNuloException();
            else if (atributo.equals("salario")) throw new SalarioNaoPodeSerNuloException();
            else if (atributo.equals("comissao"))throw new ComissaoNaoPodeSerNulaException();
        }

        Empregado atual = buscar(emp);
        if (atual == null) throw new EmpregadoNaoExisteException();

        switch (atributo) {
            case "nome" -> atual.setNome(valor);
            case "endereco" -> atual.setEndereco(valor);
            case "tipo" -> {
                if (!valor.equals("horista") && !valor.equals("assalariado") && !valor.equals("comissionado")) throw new TipoInvalidoException();
                atual.setTipo(valor);
            }
            case "salario" -> {
                try {
                    double aux = Double.parseDouble(valor.replace(",", "."));
                    if (aux <= 0) throw new SalarioDeveSerNaoNegativoException();
                } catch (NumberFormatException e) {
                    throw new SalarioDeveSerNumericoException();
                }
                atual.setSalario(valor);
            }
            case "sindicalizado" -> {
                if (!valor.equals("true") && !valor.equals("false")) throw new ValorDeveSerTrueOuFalseException();
                atual.setSindicalizado(valor.equals("true"));
                if (valor.equals("false")){
                    Sindicato remover = Sindicato.buscar(emp, 2);
                    while (remover != null) {
                        Sindicato.remover(remover.getId());
                        remover = Sindicato.buscar(emp, 2);
                    }
                }
            }
            case "comissao" -> {
                if (!(atual instanceof Comissionado)) throw new EmpregadoNaoEhComissionadoException();
                try {
                    double aux = Double.parseDouble(valor.replace(",", "."));
                    if (aux <= 0) throw new ComissaoDeveSerNaoNegativaException();
                } catch (NumberFormatException e) {
                    throw new ComissaoDeveSerNumericaException();
                }
                ((Comissionado) atual).setComissao(valor);
            }
            case "metodoPagamento" ->{
                if (!valor.equals("banco") && !valor.equals("correios") && !valor.equals("emMaos")) throw new MetodoDePagamentoInvalidoException();

                atual.setMetodo(valor);
                if (!valor.equals("banco")){
                    atual.setBanco("-1");
                    atual.setAgencia("-1");
                    atual.setContacorrente("-1");
                }
            }
            case "agendaPagamento" ->{
                if (!agendas.contains(valor)) throw new AgendaDePagamentoNaoEstaDisponivelException();
                atual.setAgendaPagamento(valor);
            }
            default -> {
                throw new AtributoNaoExisteException();
            }
        }
    }

    /**
     * Metodo responsavel por alterar o banco de um empregado
     */
    public static void alterarBanco(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws BancoNaoPodeSerNuloException{

        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (atributo.isEmpty()) throw new AtributoNaoPodeSerNuloException();
        if (valor1.isEmpty()) throw new ValorDeveSerNaoNuloException("Valor");
        if (banco.isEmpty()) throw new BancoNaoPodeSerNuloException();
        if (agencia.isEmpty()) throw new AgenciaNaoPodeSerNuloException();
        if (contaCorrente.isEmpty()) throw new ContaCorrenteNaoPodeSerNuloException();

        Empregado atual = buscar(emp);
        if (atual == null) throw new EmpregadoNaoExisteException();

        if (atributo.equals("metodoPagamento")){
            atual.setMetodo(valor1);
            atual.setBanco(banco);
            atual.setAgencia(agencia);
            atual.setContacorrente(contaCorrente);
        }
    }

    /**
     * Metodo responsavel por alterar o atributo de comissao, caso seja comissionado
     * ou salario caso seja horista ou assalariado
     */
    public static void alterarComissao(String emp, String atributo, String valor, String comissao) throws IdentificacaoDoEmpregadoNaoPodeSerNulaException{
        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (atributo.isEmpty()) throw new AtributoNaoPodeSerNuloException();
        if (valor.isEmpty()) throw new ValorDeveSerNaoNuloException("Valor");

        if (!atributo.equals("tipo")) throw new AtributoInvalidoException();

        if(valor.equals("comissionado")) {
            if (comissao.isEmpty()) throw new ComissaoNaoPodeSerNulaException();
            try {
                double aux = Double.parseDouble(comissao.replace(",", "."));
                if (aux <= 0) throw new ComissaoDeveSerNaoNegativaException();
            } catch (NumberFormatException e) {
                throw new ComissaoDeveSerNumericaException();
            }

            Empregado atual = buscar(emp);
            if (atual == null) throw new EmpregadoNaoExisteException();

            Comissionado novo = new Comissionado(atual.getNome(), atual.getEndereco(), "comissionado", atual.getSalario(), atual.getId(), comissao);
            novo.setMetodo(atual.getMetodo());
            novo.setBanco(atual.getBanco());
            novo.setAgencia(atual.getAgencia());
            novo.setContacorrente(atual.getContacorrente());
            novo.setSindicalizado(atual.isSindicalizado());
            remover(emp);
            trabalhadores.add(novo);
        }
        else if (valor.equals("horista") || valor.equals("assalariado")){
            if (comissao.isEmpty()) throw new SalarioNaoPodeSerNuloException();

            try{
                double aux = Double.parseDouble(comissao.replace(",", "."));
                if (aux <= 0) throw new SalarioDeveSerNaoNegativoException();
            }catch(NumberFormatException e){
                throw new SalarioDeveSerNumericoException();
            }

            Empregado atual = buscar(emp);
            if (atual == null) throw new EmpregadoNaoExisteException();

            if (valor.equals("horista")){
                Horista novo = new Horista(atual.getNome(), atual.getEndereco(), valor, comissao, atual.getId());
                novo.setAgencia(atual.getAgencia());
                novo.setBanco(atual.getBanco());
                novo.setContacorrente(atual.getContacorrente());
                novo.setMetodo(atual.getMetodo());
                novo.setSindicalizado(atual.isSindicalizado());
                remover(emp);
                trabalhadores.add(novo);
            }
            else{
                Assalariado novo = new Assalariado(atual.getNome(), atual.getEndereco(), valor, comissao, atual.getId());
                novo.setAgencia(atual.getAgencia());
                novo.setBanco(atual.getBanco());
                novo.setContacorrente(atual.getContacorrente());
                novo.setMetodo(atual.getMetodo());
                novo.setSindicalizado(atual.isSindicalizado());
                remover(emp);
                trabalhadores.add(novo);
            }
        }
    }

    /**
     * Metodo responsavel por criar uma nova agenda de pagamentos
     */
    public static void criarAgendaDePagamentos(String descricao) throws Exception{

        if (agendas.contains(descricao)) throw new AgendaDePagamentoJaExisteException();

        String[] novo = descricao.split(" ");

        if (!novo[0].equals("semanal") && !novo[0].equals("mensal")) throw new DescricaoDeAgendaInvalidaException();

        if (novo.length == 3){
            if (Integer.parseInt(novo[2]) >= 1 && Integer.parseInt(novo[2]) <= 7 && Integer.parseInt(novo[1]) >= 1 && Integer.parseInt(novo[1]) <= 52){
                agendas.add(descricao);
            }
            else throw new DescricaoDeAgendaInvalidaException();
        }
        else if ((novo[0].equals("mensal")) && (novo[1].equals("$") || (Integer.parseInt(novo[1]) >= 1 && Integer.parseInt(novo[1]) <= 28))) agendas.add(descricao);
        else if (novo[0].equals("semanal") && Integer.parseInt(novo[1]) >= 1 && Integer.parseInt(novo[1]) <= 7) agendas.add(descricao);
        else throw new DescricaoDeAgendaInvalidaException();
    }

    /**
     * Metodo responsavel por pegar o id do ultimo empregado da lista de empregados
     */
    public static String getLastId(){
        if (!trabalhadores.isEmpty()){
            Empregado atual = trabalhadores.get(trabalhadores.size() - 1);
            return atual.getId();
        }
        else return "0";
    }

    /**
     * Metodo responsavel por dizer qual o id do novo empregado
     */
    public static String getNextId(){
        if (!trabalhadores.isEmpty()){
            String volta = Integer.toString(Integer.parseInt(getLastId()) + 1);
            return volta;
        }
        else return "1";
    }

    public static boolean isAcessado(){
        return acessado;
    }

    public static void setAcessado(boolean acessado){
        Gerente.acessado = acessado;
    }

    /**
     * Metodo responsavel por abrir o arquivo XML, para poder acessar os dados
     */
    public static void abrir(){

        try(FileInputStream input = new FileInputStream(arquivoDados);
            XMLDecoder leitor = new XMLDecoder(input)){
            trabalhadores = (List<Empregado>) leitor.readObject();
            setAcessado(true);
        }catch(IOException | IndexOutOfBoundsException e){
            if (!(e instanceof  IndexOutOfBoundsException)){
                System.out.println("Erro ao abrir o arquivo: " + ((IOException) e).getMessage());
            }
        }

        try(FileInputStream input = new FileInputStream(arquivoAgendas);
            XMLDecoder leitor = new XMLDecoder(input)){
            agendas = (List<String>) leitor.readObject();
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

        if (!trabalhadores.isEmpty()) trabalhadores.clear();
        if (!agendas.isEmpty()) agendas.clear();
        agendas.add("mensal $");
        agendas.add("semanal 5");
        agendas.add("semanal 2 5");

        try(FileOutputStream output = new FileOutputStream(arquivoDados);
            XMLEncoder escritor = new XMLEncoder(output)){

        }catch(IOException e){
            System.err.println("Erro ao limpar o arquivo: " + e.getMessage());
        }

        try(FileOutputStream output = new FileOutputStream(arquivoAgendas);
            XMLEncoder escritor = new XMLEncoder(output)){

        }catch(IOException e){
            System.err.println("Erro ao limpar o arquivo: " + e.getMessage());
        }
    }

    /**
     * Metodo responsavel por salvar os dados no arquivo XML
     */
    public static void salvar(){
        try(FileOutputStream output = new FileOutputStream(arquivoDados);
            XMLEncoder escritor = new XMLEncoder(output)){
            escritor.writeObject(trabalhadores);

        }catch(IOException e){
            System.out.println("Erro ao abrir o arquivo:" + e.getMessage());
        }

        try(FileOutputStream output = new FileOutputStream(arquivoAgendas);
            XMLEncoder escritor = new XMLEncoder(output)){
            escritor.writeObject(agendas);

        }catch(IOException e){
            System.out.println("Erro ao abrir o arquivo:" + e.getMessage());
        }
    }
}