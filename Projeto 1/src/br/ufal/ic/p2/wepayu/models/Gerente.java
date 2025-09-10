package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;

import static br.ufal.ic.p2.wepayu.models.Empregado.trabalhadores;

/**
 * Classe responsavel por tratar de operacoes com os empregados
 */
public class Gerente {

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
    public static void direcionar(String nome, String endereco, String tipo, String salario, String id){

        switch (tipo) {
            case "assalariado" -> {
                try {
                    Assalariado novo = new Assalariado(nome, endereco, tipo, salario, id);
                    adicionarEmpregado(novo);
                }catch(EmpregadoNaoExisteException _){

                }
            }
            case "horista" -> {
                try{
                    Horista novo = new Horista(nome, endereco, tipo, salario, id);
                    adicionarEmpregado(novo);
                }catch (EmpregadoNaoExisteException _){

                }
            }
            case "comissionado" -> throw new TipoNaoAplicavelException();
            default -> throw new TipoInvalidoException();
        }
    }

    /**
     * Metodo responsavel por decidir se sera criado um comissionado
     */
    public static void direcionar(String nome, String endereco, String tipo, String salario, String id, String comissao){
        if (!tipo.equals("comissionado")) throw new TipoNaoAplicavelException();

        try{
            if (comissao.isEmpty()) throw new ComissaoNaoPodeSerNula();
            double aux = Double.parseDouble(comissao.replace(",", "."));
            if (aux < 0) throw new ComissaoDeveSerNaoNegativa();

            Comissionado novo = new Comissionado(nome, endereco, tipo, salario, id, comissao);
            adicionarEmpregado(novo);

        }catch(EmpregadoNaoExisteException | NumberFormatException e){
            if (e instanceof EmpregadoNaoExisteException) System.out.println(e.getMessage());
            else throw new NumberFormatException("Comissao deve ser numerica.");
        }
    }

    /**
     * Metodo responsavel por pegar um atributo de um empregado
     */
    public static String atributo(String emp, String atributo) throws EmpregadoNaoExisteException{

        if (!(Empregado.isAcessado())) Empregado.abrir();
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

    /**
     * Metodo responsavel por pegar o id de um empregado baseado no nome do mesmo
     */
    public static String empregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException{

        if (!(Empregado.isAcessado())) Empregado.abrir();
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

        if (!(Empregado.isAcessado())) Empregado.abrir();
        if (emp.isEmpty()) throw new IdentificacaoDoEmpregadoNaoPodeSerNulaException();
        if (trabalhadores.isEmpty()) throw new EmpregadoNaoExisteException();

        Empregado demitido = buscar(emp);

        if (demitido == null) throw new EmpregadoNaoExisteException();
        trabalhadores.remove(demitido);
    }

    /**
     * Metodo responsavel por pegar o id do ultimo empregado da lista de empregados
     */
    public static String getLastId(){
        return (!trabalhadores.isEmpty()) ? trabalhadores.getLast().getId() : "0";
    }

    /**
     * Metodo responsavel por dizer qual o id do novo empregado
     */
    public static String getNextId(){
        return (!trabalhadores.isEmpty()) ? Integer.toString(Integer.parseInt(trabalhadores.getLast().getId()) + 1) : "1";
    }
}
