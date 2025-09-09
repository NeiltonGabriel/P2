package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;

public class Empregado {
    private String nome;
    private String endereco;
    private String tipo;
    private String salario;
    private String id;
    private boolean sindicalizado;

    public Empregado(){
        //Necess├írio para o XML
    }

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

    @Override
    public String toString(){
        return String.format(this.getId() + "\nNome: " + this.getNome() + "\nSal├írio: " + this.getSalario()
                + "\nTipo: " + this.getTipo() + "\nEndere├ºo: " + this.getEndereco() + "\n");
    }
}
