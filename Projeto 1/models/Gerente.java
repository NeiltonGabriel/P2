package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.Facade;

import java.util.List;

import static br.ufal.ic.p2.wepayu.Facade.trabalhadores;

public class Gerente {

    public static Empregado buscar(String chave){
        for (Empregado a : trabalhadores){
            if (chave.equals(a.getId())){
                return a;
            }
        }
        return null;
    }

    public static void direcionar(String nome, String endereco, String tipo, String salario, String id){

        switch (tipo) {
            case "assalariado" -> {
                try {
                    Assalariado novo = new Assalariado(nome, endereco, tipo, salario, id);
                    Facade.adicionarEmpregado(novo);
                }catch(EmpregadoNaoExisteException _){

                }
            }
            case "horista" -> {
                try{
                    Horista novo = new Horista(nome, endereco, tipo, salario, id);
                    Facade.adicionarEmpregado(novo);
                }catch (EmpregadoNaoExisteException _){

                }
            }
            case "comissionado" -> throw new TipoNaoAplicavelException();
            default -> throw new TipoInvalidoException();
        }
    }

    public static void direcionar(String nome, String endereco, String tipo, String salario, String id, String comissao){
        if (!tipo.equals("comissionado")) throw new TipoNaoAplicavelException();

        try{
            if (comissao.isEmpty()) throw new ComissaoNaoPodeSerNula();
            double aux = Double.parseDouble(comissao.replace(",", "."));
            if (aux < 0) throw new ComissaoDeveSerNaoNegativa();

            Comissionado novo = new Comissionado(nome, endereco, tipo, salario, id, comissao);
            Facade.adicionarEmpregado(novo);

        }catch(EmpregadoNaoExisteException | NumberFormatException e){
            if (e instanceof EmpregadoNaoExisteException) System.out.println(e.getMessage());
            else throw new NumberFormatException("Comissao deve ser numerica.");
        }
    }

    public static String atributo(String emp, String atributo) throws EmpregadoNaoExisteException{

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
            Comissionado aux = (Comissionado) atual;
            return aux.getComissao();
        }
        else throw new AtributoNaoExisteException();
    }

    public static String empregadoPorNome(String nome, int indice, List<Empregado> trabalhadores) throws EmpregadoNaoExisteException{

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

    public static void remover(String emp) throws EmpregadoNaoExisteException{

        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (trabalhadores.isEmpty()) throw new EmpregadoNaoExisteException();

        Empregado demitido = buscar(emp);

        if (demitido == null) throw new EmpregadoNaoExisteException();
        trabalhadores.remove(demitido);
    }
}
