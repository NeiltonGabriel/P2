package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Actions.*;
import br.ufal.ic.p2.wepayu.Comandos.*;
import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;

public class Facade {
    private boolean encerrado = false;
    private final Pilhas comandos = new Pilhas();

    private void finalizado() {
        if (encerrado) {
            throw new NaoPodeDarComandosDepoisDeEncerrarSistemaException();
        }
    }

    public void zerarSistema() {
        finalizado();
        comandos.execute(new ZerarSistemaComando());
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws IdentificacaoDoEmpregadoNaoPodeSerNulaException {
        finalizado();
        comandos.execute(new AlteraEmpregadoAtributoComando(emp, atributo, valor));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws IdentificacaoDoEmpregadoNaoPodeSerNulaException {
        finalizado();
        comandos.execute(new AlteraEmpregadoSindicatoComando(emp, atributo, valor, idSindicato, taxaSindical));
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws BancoNaoPodeSerNuloException {
        finalizado();
        comandos.execute(new AlteraEmpregadoBancoComando(emp, atributo, valor1, banco, agencia, contaCorrente));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String comissao) throws IdentificacaoDoEmpregadoNaoPodeSerNulaException {
        finalizado();
        comandos.execute(new AlteraEmpregadoComissaoComando(emp, atributo, valor, comissao));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws NomeNaoPodeSerNuloException {
        finalizado();
        String id = Gerente.getNextId();
        Empregado novo = Gerente.direcionar(nome, endereco, tipo, salario, id);
        comandos.execute(new CriarEmpregadoComando(novo));
        return Gerente.getLastId();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws NomeNaoPodeSerNuloException {
        finalizado();
        String id = Gerente.getNextId();
        Empregado novo = Gerente.direcionar(nome, endereco, tipo, salario, id, comissao);
        comandos.execute(new CriarEmpregadoComando(novo));
        return Gerente.getLastId();
    }

    public void encerrarSistema() {
        finalizado();
        Gerente.salvar();
        Horarios.salvar();
        Vendas.salvar();
        Sindicato.salvar();
        encerrado = true;
    }

    public String getAtributoEmpregado(String emp, String atributo) throws EmpregadoNaoExisteException {
        finalizado();
        return Gerente.atributo(emp, atributo);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException {
        finalizado();
        return Gerente.empregadoPorNome(nome, indice);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoEhHoristaException {
        finalizado();
        return Horarios.horasExtras(emp, dataInicial, dataFinal);
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoEhHoristaException {
        finalizado();
        return Horarios.horasNormais(emp, dataInicial, dataFinal);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws IdentificacaoDoMembroNaoPodeSerNulaException {
        finalizado();
        return Sindicato.getTaxas(emp, dataInicial, dataFinal);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoEhComissionadoException {
        finalizado();
        return Vendas.vendasFeitas(emp, dataInicial, dataFinal);
    }

    public void lancaCartao(String emp, String data, String horas) throws EmpregadoNaoEhHoristaException {
        finalizado();
        comandos.execute(new LancaCartaoComando(emp, data, horas));
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws IdentificacaoDoMembroNaoPodeSerNulaException {
        finalizado();
        comandos.execute(new LancaTaxaServicoComando(membro, data, valor));
    }

    public void lancaVenda(String emp, String data, String valor) throws EmpregadoNaoEhComissionadoException {
        finalizado();
        comandos.execute(new LancaVendaComando(emp, data, valor));
    }

    public void removerEmpregado(String emp) throws EmpregadoNaoExisteException {
        finalizado();
        comandos.execute(new RemoverEmpregadoComando(emp));
    }

    public void redo() {
        finalizado();
        comandos.redo();
    }

    public void rodaFolha(String data, String saida) {
        finalizado();
        comandos.execute(new RodaFolhaComando(data, saida));
    }

    public String totalFolha(String data) {
        finalizado();
        return Pagamentos.total(data);
    }

    public void criarAgendaDePagamentos(String descricao) throws Exception {
        finalizado();
        Gerente.criarAgendaDePagamentos(descricao);
    }

    public void undo() {
        finalizado();
        comandos.undo();
    }

    public int getNumeroDeEmpregados() {
        return Gerente.trabalhadores.size();
    }
}