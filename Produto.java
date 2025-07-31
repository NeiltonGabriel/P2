package atividade1;

public class Produto {

	private String nome;
	private double preco;
	private int qtd;
	
	public Produto(String nome, double preco, int qtd) {
		this.nome = nome;
		this.preco = preco;
		this.qtd = qtd;
	}
	
	public void Dados() {
		System.out.print("Dados do produto: ");
		System.out.print(this.GetNome() + ", ");
		System.out.print("$ " + this.GetValor() + ", ");
		System.out.print(this.ValorEmEstoque() + " unidades, ");
		System.out.println("Valor total: $ " + this.GetTotal() + "\n");
	}
	
	public int ValorEmEstoque() {
		return this.qtd;
	}
	
	public void Adicionar(int ad) {
		this.qtd += ad;
	}
	
	public boolean Remover(int rm) {
		if (this.qtd < rm) {
			System.out.println("Todos os produtos " + this.nome + " foram removidos do estoque");
			return false;
		}
		this.qtd -= rm;
		return true;
	}
	
	public String GetNome() {
		return this.nome;
	}
	
	public double GetValor() {
		return this.preco;
	}
	
	public double GetTotal() {
		return (this.preco * this.qtd);
	}

}
