package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;

public class Facade {

    /**
     * Metodo responsavel por limpar os arquivos XML
     */
    public void zerarSistema(){
        Empregado.limpar();
        Horarios.limpar();
        Vendas.limpar();
        Sindicato.limpar();
    }

    /**
     * Metodo responsavel por fazer alteracoes gerais nos atributos do empregado
     */
    public void alteraEmpregado(String emp, String atributo, String valor) throws IdentificacaoDoEmpregadoNaoPodeSerNulaException{
        Sindicato.alterar(emp, atributo, valor);
    }

    /**
     * Metodo responsavel por tornar um empregado parte do sindicato
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws IdentificacaoDoEmpregadoNaoPodeSerNulaException{
        Sindicato.colocarSindicato(emp, atributo, valor, idSindicato, taxaSindical);
    }

    public void alteraEmpregado(String nome, String tipo, String valor1, String banco, String agencia, String contaCorrente){
        //fazer quando tipo = metodoPagamento e valor = banco
    }

    /**
     * Metodo responsavel por criar um empregado nao comissionado
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws NomeNaoPodeSerNuloException {
        Gerente.direcionar(nome, endereco, tipo, salario, Gerente.getNextId());
        return Gerente.getLastId();
    }

    /**
     * Metodo responsavel por criar um empregado comissionado
     */
    public String criarEmpregado(String nome, String endereco, String tipo , String salario, String comissao) throws NomeNaoPodeSerNuloException{
        Gerente.direcionar(nome, endereco, tipo, salario, Gerente.getNextId(), comissao);
        return Gerente.getLastId();
    }

    /**
     * Metodo responsavel por salvar os dados em seus respectivos arquivos XML
     */
    public void encerrarSistema(){
        //Salvar no arquivo XML
        Empregado.salvar();
        Horarios.salvar();
        Vendas.salvar();
        Sindicato.salvar();
    }

    /**
     * Metodo responsavel por pegar um atributo de um empregado
     */
    public String getAtributoEmpregado(String emp, String atributo) throws EmpregadoNaoExisteException{
        return Gerente.atributo(emp, atributo);
    }

    /**
     * Metodo responsavel por pegar o id de um empregado a partir do nome do mesmo
     */
    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException{
        return Gerente.empregadoPorNome(nome, indice);
    }

    /**
     * Metodo responsavel por calcular a quantidade de horas extras trabalhadas por um empregado em um intervalo de datas
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoEhHoristaException{
        return Horarios.HorasExtras(emp, dataInicial, dataFinal);
    }

    /**
     * Metodo responsavel por calcular a quantidade de horas normais trabalhadas por um empregado em um intervalo de datas
     */
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoEhHoristaException{
        return Horarios.horasNormais(emp, dataInicial, dataFinal);
    }

    /**
     * Metodo responsavel por calcular a taxa de servico de um empregado em um intervalo de datas
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws IdentificacaoDoMembroNaoPodeSerNulaException{
        return Sindicato.getTaxas(emp, dataInicial, dataFinal);
    }

    /**
     * Metodo responsavel por calcular o valor total das vendas feitas por um empregado em um intervalo de datas
     */
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoEhComissionadoException{
        return Vendas.vendasFeitas(emp, dataInicial, dataFinal);
    }

    /**
     * Metodo responsavel por lancar o cartao de horas trabalhadas por um empregado em uma data
     */
    public void lancaCartao(String emp, String data, String horas) throws EmpregadoNaoEhHoristaException{
        Horarios.lancaCartao(emp, data, horas);
    }

    /**
     * Metodo responsavel por lancar a taxa de servico de um empregado em uma data
     */
    public void lancaTaxaServico(String emp, String data, String valor) throws IdentificacaoDoMembroNaoPodeSerNulaException{
        Sindicato.lancaTaxaServico(emp, data, valor);
    }

    /**
     * Metodo responsavel por lancar a venda feita por um empregado em uma data
     */
    public void lancaVenda(String emp, String data, String valor) throws EmpregadoNaoEhComissionadoException{
        Vendas.lancar(emp, data, valor);
    }

    /**
     * Metodo responsavel por remover um empregado da lista de empregados
     */
    public void removerEmpregado(String emp) throws EmpregadoNaoExisteException{
        Gerente.remover(emp);
        Sindicato.remover(emp);

    }

    public void redo(){
        //refazer o último comando?
    }

    public void rodaFolha(String data, String saida){
        //não faço ideia
    }

    public String totalFolha(String data){
        //Sem ideia
        return "";
    }

    public void undo(){
        //desfazer o último comando?
    }

}